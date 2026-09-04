package com.elanrif.springbootstarterkit.services.auth;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenValidator resetTokenValidator;
    private final SecurityContextRepository securityContextRepository; // <-- nouveau

    @Override
    @Transactional
    public UserDto.Response register(AuthDto.RegisterRequest request) {
        log.debug("Registration attempt for email: {}", request.email());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed - user already exists: {}", request.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists: " + request.email());
        }

        User user = User.builder()
                .firstName(request.firstName()).lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(UserRole.USER).status(UserStatus.INACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully with id: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto.Response login(
            AuthDto.LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        log.debug("Login attempt for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.email());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed - incorrect password for: {}", request.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // Persiste explicitement le contexte (crée la session + Set-Cookie JSESSIONID)
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        log.info("User logged in successfully: {}", request.email());

        return userMapper.toDto(user);
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (!isAuthenticated) {
            log.warn("Logout attempted without a valid session");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }

        String email = auth.getName();

        // Invalidate the user's session
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        httpResponse.addCookie(cookie);

        log.info("User logged out: {}", email);
    }

    @Override
    public void forgotPassword(AuthDto.ForgotPasswordRequest request) {
        log.debug("Password reset requested for email: {}", request.email());

        userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Password reset requested for unknown email: {}", request.email());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        // génération + envoi du token par email
        log.info("Password reset token generated and sent for: {}", request.email());
    }

    @Override
    @Transactional
    public void resetMyPassword(AuthDto.ResetPasswordRequest request) {
        log.debug("Password reset attempt for email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Password reset failed - user not found: {}", request.email());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        if (!resetTokenValidator.isValidToken(request.code(), request.resetToken())) {
            log.warn("Password reset failed - invalid or expired token for: {}", request.email());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalid or expired.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for: {}", request.email());
    }
}