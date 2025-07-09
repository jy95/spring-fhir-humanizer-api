package io.github.jy95.fds_services.dto;

import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.enum_.OutputFormat;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamsDto {

    @Singular("displayOrder")
    private List<DisplayOrder> displayOrders;

    @Builder.Default
    private String displaySeparator = " - ";

    @Builder.Default
    private OutputFormat outputFormat = OutputFormat.SUMMARY;
}
