package com.upeu.producto.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class GatewayInstanciasController {

    private final Environment environment;

    @GetMapping("/instancia")
    public Map<String, String> instancia() {
        return Map.of(
                "servicio", environment.getProperty("spring.application.name", "producto"),
                "instancia", environment.getProperty("local.server.port", "N/A"),
                "host", obtenerHost(),
                "traceId", obtenerTraceId());
    }

    private String obtenerTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null && !traceId.isBlank() ? traceId : "N/A";
    }

    private String obtenerHost() {
        String hostname = environment.getProperty("HOSTNAME");
        if (hostname != null && !hostname.isBlank()) {
            return hostname;
        }

        String computerName = environment.getProperty("COMPUTERNAME");
        if (computerName != null && !computerName.isBlank()) {
            return computerName;
        }

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "desconocido";
        }
    }
}
