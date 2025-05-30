package com.nexxserve.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyfirstConfig {
    @Bean
	public MyFirstClass myFirstClass() {
		return new MyFirstClass();
	}
}
