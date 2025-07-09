package io.github.jy95.fds_services.dto;

// https://www.belgif.be/specification/rest/api-guide/#multi-language-descriptions

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalizedStringDto {
    // English translation
    private String en;

    // French translation
    private String fr;

    // Dutch translation
    private String nl;

    // German translation
    private String de;
}
