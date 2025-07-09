package io.github.jy95.fds_services.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageRequestDto {

    // The HAPI-FHIR dosages as JSON Array
    private ArrayNode dosages;

    @Builder.Default
    private ParamsDto params = new ParamsDto();
}
