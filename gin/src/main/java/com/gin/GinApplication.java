package com.gin;

import com.misaka.annotation.KirScan;
import com.misaka.annotation.EnableRateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@KirScan
@EnableRateLimiter
@SpringBootApplication
public class GinApplication {
    public static void main(String[] args) {
        SpringApplication.run(GinApplication.class, args);
    }
}
