package com.marcosmoreiradev.uensdesktop.modules.reportes.application;

import java.nio.file.Path;

public record ReporteDownloadResult(
        Path path,
        String fileName,
        String contentType) {
}
