package io.github.jy95.fds_services.dto;

import io.github.jy95.fds_services.enum_.HealthStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthResponseDto {

    HealthStatus status;
}
