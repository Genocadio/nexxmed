package com.nexxserve.medicine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.nexxserve.medicine.repository")
public class DatabaseConfig {
}