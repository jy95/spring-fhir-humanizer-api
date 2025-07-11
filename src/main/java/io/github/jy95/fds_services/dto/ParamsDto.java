package io.github.jy95.fds_services.dto;

import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.enum_.OutputFormat;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Locale;
import io.github.jy95.fds.common.config.FDSConfig;

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
    private static final List<Locale> DEFAULT_LOCALES = List.of(Locale.ENGLISH);
    private static final List<DisplayOrder> DEFAULT_DISPLAY_ORDER = FDSConfig.builder().build().getDisplayOrder();
    private static final String DEFAULT_SEPARATOR = " - ";
    private static final OutputFormat DEFAULT_OUTPUT_FORMAT = OutputFormat.SUMMARY;

    @Schema(
            description = "List of locale codes (BCP 47 format) used to localize the dosage text output",
            example = "[\"en\", \"fr\"]",
            allowableValues = {"en", "fr", "nl", "de"},
            defaultValue = "en"
    )
    @Builder.Default
    private List<Locale> locales = DEFAULT_LOCALES;

    @Schema(
            description = "List specifying the order in which dosage must be displayed.",
            externalDocs = @ExternalDocumentation(
                    description = "Javadoc",
                    url = "https://jy95.github.io/fds/common/apidocs/io/github/jy95/fds/common/types/DisplayOrder.html"
            )
    )

    @Builder.Default
    private List<DisplayOrder> displayOrders = DEFAULT_DISPLAY_ORDER;

    @Schema(
            description = "String used to separate different displayed dosage components.",
            example = " - "
    )
    @Builder.Default
    private String displaySeparator = DEFAULT_SEPARATOR;

    @Builder.Default
    private OutputFormat outputFormat = DEFAULT_OUTPUT_FORMAT;
}
