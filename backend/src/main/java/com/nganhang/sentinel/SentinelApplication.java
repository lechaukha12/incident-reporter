package com.nganhang.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SentinelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelApplication.class, args);
    }

}
