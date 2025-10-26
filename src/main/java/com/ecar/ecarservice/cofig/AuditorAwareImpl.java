package com.ecar.ecarservice.cofig;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("system"); // Hoặc trả về Optional.empty()
        }

        // Lấy email từ principal
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        return Optional.ofNullable(oidcUser.getEmail());
    }
}