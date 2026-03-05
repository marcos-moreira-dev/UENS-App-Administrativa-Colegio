package com.marcosmoreiradev.uensdesktop.common.util;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class DesktopFileSupport {

    private DesktopFileSupport() {
    }

    public static boolean revealInFileExplorer(Path path) {
        if (path == null) {
            return false;
        }
        Path absolutePath = path.toAbsolutePath().normalize();
        try {
            String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            if (osName.contains("win")) {
                if (Files.isRegularFile(absolutePath)) {
                    new ProcessBuilder("explorer.exe", "/select,", absolutePath.toString()).start();
                    return true;
                }
                Path targetDirectory = Files.isDirectory(absolutePath) ? absolutePath : absolutePath.getParent();
                if (targetDirectory != null) {
                    new ProcessBuilder("explorer.exe", targetDirectory.toString()).start();
                    return true;
                }
                return false;
            }
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            Path targetDirectory = Files.isDirectory(absolutePath) ? absolutePath : absolutePath.getParent();
            if (targetDirectory == null) {
                return false;
            }
            Desktop.getDesktop().open(targetDirectory.toFile());
            return true;
        } catch (IOException | UnsupportedOperationException ignored) {
            return false;
        }
    }
}
