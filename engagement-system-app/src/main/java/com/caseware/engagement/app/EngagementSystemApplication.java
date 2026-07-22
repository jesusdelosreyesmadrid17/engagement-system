package com.caseware.engagement.app;

import com.caseware.engagement.client.configuration.EngagementClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EngagementClientConfiguration.class)
public class EngagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(EngagementSystemApplication.class, args);
    }
}
