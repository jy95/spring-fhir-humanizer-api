package io.github.jy95.fds_services.dto;

import lombok.*;
import org.hl7.fhir.r4.model.Dosage;

@Data
@EqualsAndHashCode(callSuper = true)
public class R4DosageRequestDto extends AbstractDosageRequestDto<Dosage> {

}
