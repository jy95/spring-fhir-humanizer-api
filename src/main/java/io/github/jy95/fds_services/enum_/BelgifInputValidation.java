package io.github.jy95.fds_services.enum_;

import lombok.*;

@Getter
@AllArgsConstructor
public enum BelgifInputValidation {
    SCHEMA_VIOLATION("urn:problem-type:belgif:input-validation:schemaViolation"),
    INVALID_INPUT("urn:problem-type:belgif:input-validation:invalidInput"),
    UNKNOWN_INPUT("urn:problem-type:belgif:input-validation:unknownInput");

    private final String type;
}
