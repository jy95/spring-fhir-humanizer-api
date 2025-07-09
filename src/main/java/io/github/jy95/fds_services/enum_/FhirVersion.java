package io.github.jy95.fds_services.enum_;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FhirVersion {
    R4("r4"),
    R5("r5");

    private final String version;
}
