package io.github.jy95.fds_services.enum_;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Format options for dosage output rendering."
)
public enum OutputFormat {
    @Schema(description = "Concatenate all dosages into a single String.")
    SUMMARY,

    @Schema(description = "Generate one String per dosage entry.")
    DETAILED
}
