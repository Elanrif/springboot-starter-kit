package com.elanrif.springbootstarterkit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String authServerUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Admin {
        private String clientId;
        private String username;
        private String password;
    }

    public String getTokenUrl() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String getAdminTokenUrl() {
        return authServerUrl + "/realms/master/protocol/openid-connect/token";
    }

    public String getUsersUrl() {
        return authServerUrl + "/admin/realms/" + realm + "/users";
    }
}
