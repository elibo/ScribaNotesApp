package com.materialnotes.util;


public final class Strings {

    public static final String EMPTY = "";

    private Strings() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}