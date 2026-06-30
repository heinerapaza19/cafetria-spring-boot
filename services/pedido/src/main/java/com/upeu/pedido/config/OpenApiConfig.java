package com.upeu.pedido.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI pedidoOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("Pedido Service API")
				.version("v1")
				.description("API para gestion de pedidos de cafeteria"));
	}
}
