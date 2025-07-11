package io.github.jy95.fds_services.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

/**
 * An immutable DTO representing localized values using ISO 639 language codes.
 */
@Value
@Builder(toBuilder = true)
public class LocalizedDto {

    @Singular("entry")
    @Schema(
            description = "Map of language codes to translated strings.",
            example = """
    {
      "en": "2 times every day - 30 minutes before meal - for 10 days",
      "fr": "2 fois par jour - 30 minutes avant le repas - pendant 10 jours",
      "nl": "2 keer per dag - 30 minuten voor de maaltijd - gedurende 10 dagen",
      "de": "2 mal täglich - 30 Minuten vor der Mahlzeit - für 10 Tage"
    }
    """
    )
    Map<String, String> translations;
}
