package com.ecar.ecarservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement // Bật tính năng quản lý transaction của Spring
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Bật tính năng Auditing và chỉ định bean AuditorAware
@EnableJpaRepositories(basePackages = "com.ecar.ecarservice.repositories") // Chỉ định nơi quét các Repository
public class PersistenceConfig {
    @Bean //để Spring biết cách lấy thông tin người dùng hiện tại
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}