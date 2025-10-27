package com.ecar.ecarservice.cofig;

import com.ecar.ecarservice.enitiies.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.repositories.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/oauth2/**", "/logout").permitAll()
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/bookings/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(endpoint -> endpoint.oidcUserService(oidcUserService))
                        .defaultSuccessUrl("http://localhost:4200", true)
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(AppUserRepository appUserRepository) {
        final OidcUserService delegate = new OidcUserService();

        return userRequest -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            return processOidcUser(appUserRepository, oidcUser);
        };
    }

    @Transactional
    public OidcUser processOidcUser(AppUserRepository appUserRepository, OidcUser oidcUser) {
        String sub = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        AppUser appUser = appUserRepository.findBySub(sub)
                .or(() -> appUserRepository.findByEmail(email))
                .orElseGet(() -> {
                    AppUser newUser = new AppUser();
                    newUser.setSub(sub);
                    newUser.setEmail(email);
                    newUser.getRoles().add(AppRole.CUSTOMER);
                    return appUserRepository.save(newUser);
                });

        if (!appUser.isActive()) {
            throw new RuntimeException("User account is deactivated.");
        }

        var dbAuthorities = appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        var mergedAuthorities = Stream.concat(oidcUser.getAuthorities().stream(), dbAuthorities.stream()).toList();

        return new DefaultOidcUser(mergedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "name");
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}