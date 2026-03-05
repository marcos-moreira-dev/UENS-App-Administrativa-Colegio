package com.marcosmoreiradev.uensdesktop.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Factory for the Jackson mapper shared by the desktop API client.
 */
public final class JsonMapperFactory {

    private JsonMapperFactory() {
    }

    /**
     * Creates an {@link ObjectMapper} ready to deserialize the backend contracts used by the
     * desktop, including Java time types.
     *
     * @return configured Jackson mapper
     */
    public static ObjectMapper create() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
