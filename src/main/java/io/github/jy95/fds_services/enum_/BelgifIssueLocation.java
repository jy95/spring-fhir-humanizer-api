package io.github.jy95.fds_services.enum_;
import lombok.*;

@Getter
@AllArgsConstructor
public enum BelgifIssueLocation {
    BODY("body"),
    HEADER("header"),
    PATH("path"),
    QUERY("query");

    // The location of the invalid input
    private final String in;
}
