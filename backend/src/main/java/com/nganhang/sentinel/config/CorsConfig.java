package com.nganhang.sentinel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Cho phép tất cả các nguồn gốc
        config.addAllowedOrigin("*");
        
        // Cho phép tất cả các header
        config.addAllowedHeader("*");
        
        // Cho phép tất cả các phương thức
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
