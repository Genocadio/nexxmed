package com.nexxserve.inventoryservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {
    // This enables Spring's @Scheduled annotation support
}
