package io.github.jy95.fds_services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ProblemDetail;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageReponseDto {

    // Result
    @Builder.Default
    List<LocalizedDto> items = List.of();

    // List of issues in the payload
    @Builder.Default
    List<ProblemDetail> issues = List.of();
}
