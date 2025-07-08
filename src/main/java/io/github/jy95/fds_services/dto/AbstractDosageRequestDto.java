package io.github.jy95.fds_services.dto;

import io.github.jy95.fds_services.enum_.OutputFormat;
import lombok.*;
import io.github.jy95.fds.common.types.DisplayOrder;

import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbstractDosageRequestDto<T> {

    @Singular
    private List<T> dosages;

    @Builder.Default
    private Params params = new Params();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Params {
        @Builder.Default
        private List<Locale> locales = List.of(Locale.ENGLISH);

        @Singular("displayOrder")
        private List<DisplayOrder> displayOrders;

        @Builder.Default
        private String displaySeparator = " - ";

        @Builder.Default
        private OutputFormat outputFormat = OutputFormat.SUMMARY;
    }
}
