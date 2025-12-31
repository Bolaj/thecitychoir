package com.portfolio.thecitychoir.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springCityChoirOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("The City Choir API")
                        .description("API documentation for The City Choir project")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Repository")
                        .url("https://github.com/Bolaj/thecitychoir"));
    }

}
