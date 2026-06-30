package com.upeu.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

        @Value("${jwt.secret}")
        private String jwtSecret;

        @Value("${jwt.issuer}")
        private String jwtIssuer;

        // 🔐 SECURITY FILTER CHAIN
        @Bean
        SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                                // 🔥 IMPORTANTE: ACTIVAR CORS AQUÍ
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                .securityContextRepository(
                                                NoOpServerSecurityContextRepository.getInstance())

                                .authorizeExchange(exchange -> exchange

                                                .pathMatchers("/auth/**").permitAll()

                                                .pathMatchers(
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()

                                                .anyExchange().authenticated())

                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))

                                .build();
        }

        // 🔐 JWT DECODER
        @Bean
        NimbusReactiveJwtDecoder jwtDecoder() {

                byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);

                SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

                NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();

                decoder.setJwtValidator(
                                JwtValidators.createDefaultWithIssuer(jwtIssuer));

                return decoder;
        }

        // 🔐 ROLES DEL TOKEN
        @Bean
        ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {

                JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
                converter.setAuthoritiesClaimName("roles");
                converter.setAuthorityPrefix("");

                JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
                jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

                return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
        }

        // 🌍 CORS GLOBAL (CLAVE PARA ANGULAR)
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowCredentials(true);
                config.addAllowedOrigin("http://localhost:4200");
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return source;
        }
}