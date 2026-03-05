package com.marcosmoreiradev.uensdesktop.common.util;

public final class Strings {

    private Strings() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
