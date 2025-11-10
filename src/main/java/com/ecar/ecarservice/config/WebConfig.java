package com.ecar.ecarservice.config; // Tên package của bạn có thể khác

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // Cho phép tất cả các đường dẫn API ("/api/**")
                .addMapping("/api/**")

                // Cho phép FE chạy ở địa chỉ này được gọi
                .allowedOrigins(
                        "http://localhost:4200",  // Dành cho Angular khi dev
                        "http://localhost:3000"   // Dành cho React khi dev
                        // "https://your-domain.com" // Tên miền thật khi deploy
                )

                // Các phương thức FE được phép dùng
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Cho phép gửi header (ví dụ: header "Authorization" để xác thực)
                .allowedHeaders("*")

                // Cho phép gửi cookie/session
                .allowCredentials(true);
    }
}