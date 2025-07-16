package io.github.jy95.fds_services.filter;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

// https://www.belgif.be/specification/rest/api-guide/#tracing

@Component
public class BelGovTraceIdFilter implements WebFilter {

    private static final String TRACE_ID_HEADER = "BelGov-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        // Generate a new trace ID
        String newTraceId = UUID.randomUUID().toString();

        // Set new trace ID in response
        response.getHeaders().set(TRACE_ID_HEADER, newTraceId);

        return chain.filter(exchange);
    }
}