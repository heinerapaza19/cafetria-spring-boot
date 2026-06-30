package com.upeu.producto.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

        @Bean
        public OpenAPI productoOpenApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("producto API")
                                                .description("API REST del microservicio de gestión de productos. Versión actual: v1")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Equipo producto")
                                                                .email("producto@upeu.edu.pe"))
                                                .license(new License()
                                                                .name("Internal Use Only")
                                                                .url("https://upeu.edu.pe")));
        }
}