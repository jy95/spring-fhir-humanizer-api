package io.github.jy95.fds_services.dto;

import io.github.jy95.fds.common.config.FDSConfig;
import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.enum_.OutputFormat;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;

/**
 * Parameters controlling the display and output format of dosage text conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ParamsDto",
        description = "Parameters for dosage text generation."
)
public class ParamsDto {

    // Default values, to avoid recreating them again and again
    private static final FDSConfig DEFAULT_LIB_CONFIG = FDSConfig.builder().build();
    private static final List<Locale> DEFAULT_LOCALES = List.of(Locale.ENGLISH);
    private static final List<DisplayOrder> DEFAULT_DISPLAY_ORDER = DEFAULT_LIB_CONFIG.getDisplayOrder();
    private static final String DEFAULT_SEPARATOR = DEFAULT_LIB_CONFIG.getDisplaySeparator();
    private static final OutputFormat DEFAULT_OUTPUT_FORMAT = OutputFormat.SUMMARY;

    @Schema(
            description = "List of locale codes (BCP 47 format) used to localize the text output",
            example = "[\"en\", \"fr\"]",
            allowableValues = {"en", "fr", "nl", "de", "es", "it", "pt"},
            defaultValue = "[\"en\"]"
    )
    @Builder.Default
    private List<Locale> locales = DEFAULT_LOCALES;

    @Schema(
            description = "List specifying the order in which elements must be displayed.",
            externalDocs = @ExternalDocumentation(
                    description = "Javadoc",
                    url = "https://jy95.github.io/fds/common/apidocs/io/github/jy95/fds/common/types/DisplayOrder.html"
            ),
            example = "[\"BOUNDS_DURATION\"]"
    )
    @Builder.Default
    private List<DisplayOrder> displayOrders = DEFAULT_DISPLAY_ORDER;

    @Schema(
            description = "String used to separate different displayed components.",
            example = " - "
    )
    @Builder.Default
    private String displaySeparator = DEFAULT_SEPARATOR;

    @Builder.Default
    private OutputFormat outputFormat = DEFAULT_OUTPUT_FORMAT;
}
