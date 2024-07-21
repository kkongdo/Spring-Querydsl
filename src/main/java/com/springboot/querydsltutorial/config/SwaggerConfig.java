package com.springboot.querydsltutorial.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "QueryDSL API 명세서",
                description = "QueryDSL를 학습하기 위한 API 명세서",
                version = "v1"))
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("Spring Boot REST API Specifications")
                .description("Specification")
                .version("1.0.0");
    }
}
