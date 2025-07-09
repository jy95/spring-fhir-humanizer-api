package io.github.jy95.fds_services.dto;

import lombok.*;
import org.hl7.fhir.r5.model.Dosage;

@Data
@EqualsAndHashCode(callSuper = true)
public class R5DosageRequestDto extends AbstractDosageRequestDto<Dosage> {}
