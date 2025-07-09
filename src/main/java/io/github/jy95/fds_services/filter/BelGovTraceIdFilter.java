package io.github.jy95.fds_services.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// https://www.belgif.be/specification/rest/api-guide/#tracing

@Component
public class BelGovTraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "BelGov-Trace-Id";
    private static final String RELATED_TRACE_ID_HEADER = "BelGov-Related-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Read incoming trace ID if present
        String incomingTraceId = request.getHeader(TRACE_ID_HEADER);

        // Generate a new trace ID (max 36 characters)
        String newTraceId = UUID.randomUUID().toString();

        // Add generated trace ID to the response
        response.setHeader(TRACE_ID_HEADER, newTraceId);

        // If there was an incoming trace ID, set it as related in the response
        if (incomingTraceId != null && !incomingTraceId.isBlank()) {
            response.setHeader(RELATED_TRACE_ID_HEADER, incomingTraceId);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
