package com.ecar.ecarservice.config;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.repositories.AppUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Stream;
/**
 * Class cấu hình chính cho Spring Security.
 * Chịu trách nhiệm về:
 * - Bảo vệ các API endpoint.
 * - Cấu hình đăng nhập bằng Google (OAuth2/OIDC).
 * - Xử lý CORS.
 * - Quản lý việc mã hóa mật khẩu.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    // =================== SECURITY FILTER CHAIN ===================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) throws Exception {
        http
                // Vô hiệu hóa CSRF vì chúng ta đang xây dựng API cho SPA, không dùng session-based form
                .csrf(csrf -> csrf.disable())
                // Áp dụng cấu hình CORS được định nghĩa trong bean corsConfigurationSource()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Định nghĩa các quy tắc phân quyền cho các đường dẫn (URL)
                .authorizeHttpRequests(auth -> auth
                        // Cho phép các đường dẫn này mà không cần xác thực
                        .requestMatchers("/api/ping/**",  "/error", "/").permitAll()
                        // Các đường dẫn OAuth2 nội bộ cũng cần được cho phép
                        .requestMatchers("/", "/login**", "/oauth2/**", "/logout").permitAll()
                        // Tất cả các request API khác đều yêu cầu xác thực
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers("/api/me/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/bookings/**").authenticated()
                        .requestMatchers("/api/service-records").authenticated()  // Cho phép người dùng đã đăng nhập xem lịch sử dịch vụ
                        .requestMatchers("/api/maintenance/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/centers/**", "/api/car-models/**").permitAll() // Hoặc .hasRole("ADMIN")
                        // Bất kỳ request nào khác chưa được định nghĩa ở trên đều yêu cầu phải xác thực
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized: Please log in.\"}");
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .userInfoEndpoint(endpoint -> endpoint.oidcUserService(oidcUserService))
                        // Sau khi đăng nhập Google thành công, chuyển hướng về Frontend
                        .defaultSuccessUrl("http://localhost:4200", true)
                );

        return http.build();
    }
    // =================== PASSWORD ENCODER ===================
    //BCryptPasswordEncoder để mã hóa và so sánh mật khẩu một cách an toàn
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =================== OIDC USER SERVICE (Xử lý Login Google) ===================
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
        String name = oidcUser.getFullName(); // Lấy tên từ Google

        AppUser appUser = appUserRepository.findBySub(sub)
                .map(existingUser -> {
                    // Nếu user đã tồn tại, cập nhật lại tên (phòng trường hợp họ đổi tên)
                    existingUser.setFullName(name);
                    return appUserRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Nếu không có user theo 'sub', thử tìm theo email
                    return appUserRepository.findByEmail(email)
                            .map(existingUser -> {
                                // User đã tồn tại (có thể tạo thủ công), cập nhật 'sub' và 'name' cho họ
                                existingUser.setSub(sub);
                                existingUser.setFullName(name);
                                return appUserRepository.save(existingUser);
                            })
                            .orElseGet(() -> {
                                // User hoàn toàn mới, tạo mới với đầy đủ thông tin
                                AppUser newUser = new AppUser();
                                newUser.setSub(sub);
                                newUser.setEmail(email);
                                newUser.setFullName(name); // Lưu tên
                                newUser.getRoles().add(AppRole.CUSTOMER);
                                return appUserRepository.save(newUser);
                            });
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

    // =================== CORS CONFIGURATION ===================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Cho phép Angular gọi
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // Cho phép tất cả method
        configuration.setAllowedHeaders(List.of("*")); // Cho phép tất cả headers
        configuration.setAllowCredentials(true); // Cho phép gửi cookie/token nếu cần
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // =================== AUTHENTICATION PROVIDER (Cho Login truyền thống) ===================
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder()); // dùng BCryptPasswordEncoder đã khai báo
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authProvider) {
        return authentication -> authProvider.authenticate(authentication);
    }
}