package com.upeu.producto.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTraceConfig {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String MDC_TRACE_ID = "traceId";

    @Bean
    public RequestInterceptor traceIdRequestInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get(MDC_TRACE_ID);
            if (traceId != null && !traceId.isBlank()) {
                requestTemplate.header(TRACE_ID_HEADER, traceId);
            }
        };
    }
}
