package com.marcosmoreiradev.uensdesktop.api.client;

/**
 * Raw binary payload returned by endpoints that download files.
 *
 * @param bytes file content as returned by the backend
 * @param fileName optional suggested file name extracted from response headers
 * @param contentType MIME type reported by the backend
 */
public record BinaryPayload(
        byte[] bytes,
        String fileName,
        String contentType) {
}
