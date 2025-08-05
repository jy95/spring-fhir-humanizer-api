package io.github.jy95.fds_services.enum_;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BelgifProblemType {
    BAD_REQUEST(
            "urn:problem-type:belgif:badRequest",
            "https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html",
            "Bad Request",
            400
    ),
    INTERNAL_SERVER_ERROR(
            "urn:problem-type:belgif:internalServerError",
            "https://www.belgif.be/specification/rest/api-guide/problems/internalServerError.html",
            "Internal Server Error",
            500
    ),
    BAD_GATEWAY(
            "urn:problem-type:belgif:badGateway",
            "https://www.belgif.be/specification/rest/api-guide/problems/badGateway.html",
            "Bad Gateway",
            502
    ),
    SERVICE_UNAVAILABLE(
            "urn:problem-type:belgif:serviceUnavailable",
            "https://www.belgif.be/specification/rest/api-guide/problems/serviceUnavailable.html",
            "Service is unavailable",
            504
    );

    // The urn problem type, according Belgif
    private final String type;
    private final String href;
    private final String title;
    private final int status;
}
