package com.marcosmoreiradev.uensbackend.modules.reporte.application.export;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Utilidades de acceso al logo institucional para exportadores de reportes.
 */
final class ReporteLogoSupport {

    static final String LOGO_CLASSPATH = "/assets/logo.png";

    private ReporteLogoSupport() {
    }

    /**
     * Lee el logo institucional desde classpath.
     *
     * @return bytes del logo PNG o {@code null} si no existe/no puede leerse
     */
    static ReporteLogoAsset readLogo() {
        try (InputStream inputStream = ReporteLogoSupport.class.getResourceAsStream(LOGO_CLASSPATH)) {
            if (inputStream == null) {
                return null;
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            inputStream.transferTo(output);
            byte[] bytes = output.toByteArray();
            try (ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes)) {
                var bufferedImage = ImageIO.read(imageStream);
                if (bufferedImage == null) {
                    return null;
                }
                return new ReporteLogoAsset(bytes, bufferedImage.getWidth(), bufferedImage.getHeight());
            }
        } catch (Exception ex) {
            return null;
        }
    }
}

record ReporteLogoAsset(
        byte[] bytes,
        int widthPx,
        int heightPx
) {
    boolean isValid() {
        return bytes != null && bytes.length > 0 && widthPx > 0 && heightPx > 0;
    }
}
