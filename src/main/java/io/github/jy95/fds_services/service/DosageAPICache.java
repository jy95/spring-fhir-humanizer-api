package io.github.jy95.fds_services.service;

import java.util.Locale;

public interface DosageAPICache<P, V> {

    /**
     * Provides the fallback creator logic in case of cache miss.
     */
    V getCreator(Locale locale, P params);
}
