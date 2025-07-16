package io.github.jy95.fds_services.enum_;

import lombok.*;

import java.net.URI;

@Getter
@AllArgsConstructor
public enum BelgifInputValidation {
    SCHEMA_VIOLATION(URI.create("urn:problem-type:belgif:input-validation:schemaViolation")),
    INVALID_INPUT(URI.create("urn:problem-type:belgif:input-validation:invalidInput")),
    UNKNOWN_INPUT(URI.create("urn:problem-type:belgif:input-validation:unknownInput"));

    private final URI type;
}
