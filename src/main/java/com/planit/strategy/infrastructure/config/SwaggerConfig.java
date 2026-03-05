package com.planit.strategy.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PlanIt Strategy API")
                        .description("AI 기반 학습 전략 생성 서비스")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("PlanIt Team")
                                .email("support@planit.com")));
    }
}
