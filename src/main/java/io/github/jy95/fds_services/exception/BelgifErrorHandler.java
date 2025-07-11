package io.github.jy95.fds_services.exception;

import io.github.jy95.fds_services.enum_.BelgifProblemType;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Order  // optional: can specify precedence if needed
@ControllerAdvice
public class BelgifErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationExceptions(WebExchangeBindException ex, ServerWebExchange exchange) {
        BelgifProblemType type = BelgifProblemType.BAD_REQUEST;

        var problem = buildProblemDetail(type, ex.getMessage(), exchange);
        var issues = ex
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    var issue = ProblemDetail.forStatus(400);
                    issue.setType(URI.create("urn:problem-type:belgif:input-validation:invalidInput"));
                    issue.setDetail(fieldError.getDefaultMessage());
                    issue.setProperty("in", "body");
                    issue.setProperty("name", fieldError.getField());
                    return issue;
                })
                .toList();

        problem.setProperty("issues", issues);
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        BelgifProblemType type = resolveProblemTypeFromHttpStatus(ex.getStatusCode());
        String detail = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return buildProblemDetail(type, detail, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, ServerWebExchange exchange) {
        BelgifProblemType type = BelgifProblemType.INTERNAL_SERVER_ERROR;
        return buildProblemDetail(type, ex.getMessage(), exchange);
    }

    private ProblemDetail buildProblemDetail(BelgifProblemType type, String detail, ServerWebExchange exchange) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(type.getStatus());
        problemDetail.setType(URI.create(type.getType()));
        problemDetail.setTitle(type.getTitle());
        problemDetail.setDetail(detail);

        String urnUuid = "urn:uuid:" + UUID.randomUUID();
        problemDetail.setInstance(URI.create(urnUuid));

        // Optional fields for BELGIF
        problemDetail.setProperty("href", type.getHref());
        problemDetail.setProperty("timestamp", Instant.now().toString());
        problemDetail.setProperty("path", exchange.getRequest().getPath().value());

        return problemDetail;
    }

    private BelgifProblemType resolveProblemTypeFromHttpStatus(HttpStatusCode statusCode) {
        return Arrays
                .stream(BelgifProblemType.values())
                .filter(type -> type.getStatus() == statusCode.value())
                .findFirst()
                .orElse(BelgifProblemType.INTERNAL_SERVER_ERROR);
    }
}