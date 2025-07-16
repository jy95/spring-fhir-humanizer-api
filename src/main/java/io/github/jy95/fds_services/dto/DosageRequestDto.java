package io.github.jy95.fds_services.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageRequestDto {

    @Schema(
            description = "Array of dosage objects encoded as HAPI-FHIR JSON",
            example = """
        [{
          "timing": {
            "repeat": {
              "frequency": 2,
              "period": 1,
              "periodUnit": "d",
              "when": [
                "AC"
              ],
              "offset": 30,
              "boundsDuration": {
                "value": 10,
                "code": "d",
                "system": "http://hl7.org/fhir/ValueSet/duration-units"
              }
            }
          }
        }]
        """
    )
    @NotNull
    private List<JsonNode> dosages;

    @Builder.Default
    @Schema(
            description = "Optional parameters to control the formatting and output of dosages"
    )
    private ParamsDto params = new ParamsDto();
}
