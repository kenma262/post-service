package org.example.post.security;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {
    public static class UserInfo implements Principal {
        private final String id;
        private final String username;

        public UserInfo(String id, String username) {
            this.id = id;
            this.username = username;
        }

        public String id() {
            return id;
        }

        public String username() {
            return username;
        }

        @Override
        public String getName() {
            return username;
        }

        public static UserInfo fromSecurityContext() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User not authenticated");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                // Extract user information from Keycloak JWT token
                String userId = jwt.getClaimAsString("sub"); // Subject claim contains user ID
                String username = jwt.getClaimAsString("preferred_username"); // Preferred username

                if (username == null) {
                    username = jwt.getClaimAsString("name"); // Fallback to name claim
                }

                if (userId == null || username == null) {
                    throw new RuntimeException("Invalid JWT token: missing user information");
                }

                return new UserInfo(userId, username);
            } else {
                throw new RuntimeException("Unknown principal type: " + principal.getClass());
            }
        }
    }
}
