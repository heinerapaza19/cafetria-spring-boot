package com.upeu.categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI catalogoOpenApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("catalogo API")
                                                .description("API REST del microservicio de gestión de categorías. Versión actual: v1")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Equipo catalogo")
                                                                .email("catalogo@upeu.edu.pe"))
                                                .license(new License()
                                                                .name("Internal Use Only")
                                                                .url("https://upeu.edu.pe")));
        }
}
