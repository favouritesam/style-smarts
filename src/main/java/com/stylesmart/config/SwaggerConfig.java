package com.stylesmart.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This annotation marks this class as a Spring Configuration class
@Configuration
public class SwaggerConfig {

    // This annotation defines a bean that Spring will manage
    // This bean configures the OpenAPI documentation for Swagger UI
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("StyleSmart API")
                        .version("1.0")
                        .description("API documentation for StyleSmart application"))

                // This applies the security requirement globally to all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // This configures the "Authorize" button to accept a Bearer token
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT Token here. You do NOT need to type 'Bearer ' before it.")));
    }
}