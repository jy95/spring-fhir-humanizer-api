package io.github.jy95.fds_services.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
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
    private static final String RELATED_TRACE_ID_HEADER = "BelGov-Related-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Read incoming trace ID if present
        String incomingTraceId = request.getHeaders().getFirst(TRACE_ID_HEADER);

        // Generate a new trace ID
        String newTraceId = UUID.randomUUID().toString();

        // Set new trace ID in response
        response.getHeaders().set(TRACE_ID_HEADER, newTraceId);

        // Set related trace ID if incoming one exists
        if (incomingTraceId != null && !incomingTraceId.isBlank()) {
            response.getHeaders().set(RELATED_TRACE_ID_HEADER, incomingTraceId);
        }

        return chain.filter(exchange);
    }
}