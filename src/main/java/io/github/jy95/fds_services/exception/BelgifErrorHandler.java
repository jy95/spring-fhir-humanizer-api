package io.github.jy95.fds_services.exception;

// Explanations on https://www.belgif.be/specification/rest/api-guide/#error-handling

import io.github.jy95.fds_services.enum_.BelgifProblemType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
public class BelgifErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        BelgifProblemType type = BelgifProblemType.BAD_REQUEST;

        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildProblemDetail(type, String.join("; ", details), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        BelgifProblemType type = resolveProblemTypeFromHttpStatus(ex.getStatusCode());
        String detail = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return buildProblemDetail(type, detail, request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, WebRequest request) {
        BelgifProblemType type = BelgifProblemType.INTERNAL_SERVER_ERROR;
        return buildProblemDetail(type, ex.getMessage(), request);
    }

    private ProblemDetail buildProblemDetail(BelgifProblemType type, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(type.getStatus());
        problemDetail.setType(URI.create(type.getType()));
        problemDetail.setTitle(type.getTitle());
        problemDetail.setDetail(detail);

        String urnUuid = "urn:uuid:" + UUID.randomUUID();
        problemDetail.setInstance(URI.create(urnUuid));

        // Optional fields for BELGIF
        problemDetail.setProperty("href", type.getHref());
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return problemDetail;
    }

    private String getRequestUri(WebRequest request) {
        String desc = request.getDescription(false);
        return desc.startsWith("uri=") ? desc.substring(4) : "/";
    }

    private BelgifProblemType resolveProblemTypeFromHttpStatus(HttpStatusCode statusCode) {
        return Arrays.stream(BelgifProblemType.values())
                .filter(type -> type.getStatus() == statusCode.value())
                .findFirst()
                .orElse(BelgifProblemType.INTERNAL_SERVER_ERROR);
    }
}
