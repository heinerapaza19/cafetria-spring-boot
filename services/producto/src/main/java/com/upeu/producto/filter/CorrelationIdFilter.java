package com.upeu.producto.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter que agrega un Trace ID (Correlation ID) a cada request para facilitar
 * el rastreo de operaciones a través de logs distribuidos.
 *
 * Si el request contiene el header X-Trace-ID, lo utiliza; de lo contrario, genera uno nuevo.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String MDC_TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Obtener o generar Trace ID
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        
        // Agregar al MDC (Mapped Diagnostic Context) para incluir en logs
        MDC.put(MDC_TRACE_ID, traceId);
        
        // Agregar al response header para que el cliente pueda correlacionar
        response.addHeader(TRACE_ID_HEADER, traceId);
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar MDC para evitar leaks en thread pools
            MDC.remove(MDC_TRACE_ID);
        }
    }
}
