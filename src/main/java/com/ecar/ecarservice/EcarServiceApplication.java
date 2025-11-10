package com.ecar.ecarservice;

import com.ecar.ecarservice.config.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcarServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcarServiceApplication.class, args);
    }
}
