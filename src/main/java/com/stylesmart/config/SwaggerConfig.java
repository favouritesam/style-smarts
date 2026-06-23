package com.stylesmart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This annotation marks this class as a Spring Configuration class
@Configuration
public class SwaggerConfig {

    // This annotation defines a bean that Spring will manage
    // This bean configures the OpenAPI documentation for Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StyleSmart API")
                        .version("1.0")
                        .description("API documentation for StyleSmart application"));
    }
}
