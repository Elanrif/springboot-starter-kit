package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.config.KeycloakProperties;
import com.elanrif.springbootstarterkit.dto.auth.KeycloakAuthResponse;
import com.elanrif.springbootstarterkit.dto.auth.KeycloakTokenResponse;
import com.elanrif.springbootstarterkit.dto.auth.RegisterDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Login user via ROPC (Resource Owner Password Credentials) grant type
     */
    public KeycloakAuthResponse login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    keycloakProperties.getTokenUrl(),
                    request,
                    KeycloakTokenResponse.class
            );

            KeycloakTokenResponse tokenResponse = response.getBody();
            if (tokenResponse == null) {
                throw new BadRequestException("Failed to get token from Keycloak");
            }

            // Find user in local DB or sync from Keycloak
            User user = userRepository.findByEmail(username)
                    .orElseGet(() -> syncUserFromKeycloak(username));

            return KeycloakAuthResponse.from(tokenResponse, userMapper.toDto(user));
        } catch (HttpClientErrorException e) {
            log.error("Keycloak login error: {}", e.getResponseBodyAsString());
            throw new BadRequestException("Invalid email or password");
        }
    }

    /**
     * Sync user from Keycloak to local database
     */
    private User syncUserFromKeycloak(String email) {
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        String searchUrl = keycloakProperties.getUsersUrl() + "?email=" + email + "&exact=true";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode users = objectMapper.readTree(response.getBody());
            if (users.isArray() && !users.isEmpty()) {
                JsonNode keycloakUser = users.get(0);

                User user = User.builder()
                        .email(keycloakUser.has("email") ? keycloakUser.get("email").asText() : email)
                        .firstName(keycloakUser.has("firstName") ? keycloakUser.get("firstName").asText() : null)
                        .lastName(keycloakUser.has("lastName") ? keycloakUser.get("lastName").asText() : null)
                        .role(UserRole.USER)
                        .isActive(keycloakUser.has("enabled") && keycloakUser.get("enabled").asBoolean())
                        .build();

                log.info("Synced user from Keycloak to local DB: {}", email);
                return userRepository.save(user);
            }

            throw new BadRequestException("User not found in Keycloak");
        } catch (HttpClientErrorException e) {
            log.error("Failed to sync user from Keycloak: {}", e.getResponseBodyAsString());
            throw new BadRequestException("Failed to sync user from Keycloak");
        } catch (Exception e) {
            log.error("Error syncing user from Keycloak: {}", e.getMessage());
            throw new BadRequestException("Failed to sync user from Keycloak");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public KeycloakTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    keycloakProperties.getTokenUrl(),
                    request,
                    KeycloakTokenResponse.class
            );

            KeycloakTokenResponse tokenResponse = response.getBody();
            if (tokenResponse == null) {
                throw new BadRequestException("Failed to refresh token from Keycloak");
            }
            return tokenResponse;
        } catch (HttpClientErrorException e) {
            log.error("Keycloak refresh token error: {}", e.getResponseBodyAsString());
            throw new BadRequestException("Invalid or expired refresh token");
        }
    }

    /**
     * Get admin access token using ROPC with admin credentials
     */
    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getAdmin().getClientId());
        body.add("username", keycloakProperties.getAdmin().getUsername());
        body.add("password", keycloakProperties.getAdmin().getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    keycloakProperties.getAdminTokenUrl(),
                    request,
                    KeycloakTokenResponse.class
            );
            return response.getBody() != null ? response.getBody().accessToken() : null;
        } catch (HttpClientErrorException e) {
            log.error("Keycloak admin token error: {}", e.getResponseBodyAsString());
            throw new BadRequestException("Failed to get admin token");
        }
    }

    /**
     * Create user in Keycloak and login
     */
    public KeycloakAuthResponse createUser(RegisterDto dto) {
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> userRepresentation = new HashMap<>();
        userRepresentation.put("username", dto.email());
        userRepresentation.put("email", dto.email());
        userRepresentation.put("firstName", dto.firstName());
        userRepresentation.put("lastName", dto.lastName());
        userRepresentation.put("enabled", true);
        userRepresentation.put("emailVerified", true);

        // Set password credentials
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", dto.password());
        credentials.put("temporary", false);
        userRepresentation.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRepresentation, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakProperties.getUsersUrl(),
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new BadRequestException("Failed to create user in Keycloak");
            }

            // Login and return tokens (user is already saved by AuthService)
            return this.login(dto.email(), dto.password());

        } catch (HttpClientErrorException e) {
            log.error("Keycloak create user error: {}", e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new BadRequestException("User already exists in Keycloak");
            }
            throw new BadRequestException("Failed to create user in Keycloak: " + e.getMessage());
        }
    }

    /**
     * Update user password in Keycloak
     */
    public void updateUserPassword(String email, String newPassword) {
        String adminToken = getAdminAccessToken();
        String userId = getUserIdByEmail(email, adminToken);

        if (userId == null) {
            throw new BadRequestException("User not found in Keycloak");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", newPassword);
        credentials.put("temporary", false);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(credentials, headers);

        String resetPasswordUrl = keycloakProperties.getUsersUrl() + "/" + userId + "/reset-password";

        try {
            restTemplate.put(resetPasswordUrl, request);
        } catch (HttpClientErrorException e) {
            log.error("Keycloak update password error: {}", e.getResponseBodyAsString());
            throw new BadRequestException("Failed to update password in Keycloak");
        }
    }

    /**
     * Get Keycloak user ID by email
     */
    private String getUserIdByEmail(String email, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        String searchUrl = keycloakProperties.getUsersUrl() + "?email=" + email + "&exact=true";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode users = objectMapper.readTree(response.getBody());
            if (users.isArray() && !users.isEmpty()) {
                return users.get(0).get("id").asText();
            }
            return null;
        } catch (Exception e) {
            log.error("Keycloak get user error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Logout user (invalidate refresh token)
     */
    public void logout(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String logoutUrl = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/logout";

        try {
            restTemplate.postForEntity(logoutUrl, request, Void.class);
        } catch (HttpClientErrorException e) {
            log.error("Keycloak logout error: {}", e.getResponseBodyAsString());
        }
    }
}
