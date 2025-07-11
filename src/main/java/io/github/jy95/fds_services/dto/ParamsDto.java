package io.github.jy95.fds_services.dto;

import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.enum_.OutputFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

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

    @Schema(
            description = "List specifying the order in which dosage must be displayed.",
            example = "[\"FREQUENCY_FREQUENCY_MAX_PERIOD_PERIOD_MAX\", \"ROUTE\", \"SITE\"]"
    )
    @Singular("displayOrder")
    private List<DisplayOrder> displayOrders;

    @Schema(
            description = "String used to separate different displayed dosage components.",
            example = " - "
    )
    @Builder.Default
    private String displaySeparator = " - ";

    @Builder.Default
    private OutputFormat outputFormat = OutputFormat.SUMMARY;
}
