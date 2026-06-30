package com.upeu.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // clave secreta usada para firmar el token JWT
    private String secret;

    // tiempo de vida del token en milisegundos
    private long expiration;

    // emisor del token (identidad del sistema auth)
    private String issuer;
}