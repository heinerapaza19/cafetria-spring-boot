package com.upeu.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Component
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final Logger log = LoggerFactory.getLogger(TraceIdGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request)
                .build();

        mutatedExchange.getResponse().getHeaders().set(TRACE_ID_HEADER, traceId);

        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        final String traceIdValue = traceId;
        logConTraceId(traceIdValue, "[GATEWAY] Recibida peticion {} {}", method, path);

        return chain.filter(mutatedExchange)
                .doOnSuccess(unused -> logConTraceId(traceIdValue, "[GATEWAY] Respuesta completada {}", path))
                .doOnError(error -> logConTraceId(traceIdValue, "[GATEWAY] Error en ruta {}. Motivo: {}", path,
                        error.getMessage()));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void logConTraceId(String traceId, String pattern, Object... args) {
        MDC.put("traceId", traceId);
        try {
            log.info(pattern, args);
        } finally {
            MDC.remove("traceId");
        }
    }
}
