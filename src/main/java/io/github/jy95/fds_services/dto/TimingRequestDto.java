package io.github.jy95.fds_services.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimingRequestDto {

    @Schema(
            description = "Array of timing objects encoded as HAPI-FHIR JSON",
            example = """
        [
           {
              "repeat":{
                 "frequency":2,
                 "period":1,
                 "periodUnit":"d",
                 "when":[
                    "AC"
                 ],
                 "offset":30,
                 "boundsDuration":{
                    "value":10,
                    "code":"d",
                    "system":"http://hl7.org/fhir/ValueSet/duration-units"
                 }
              }
           }
        ]
        """
    )
    private ArrayNode timings;

    @Builder.Default
    @Schema(
            description = "Optional parameters to control the formatting and output of timings"
    )
    private ParamsDto params = new ParamsDto();
}
