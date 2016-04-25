package com.materialnotes.util;

/**
 * Class with String methods and constants.
 **/
public final class Strings {

    /** Empty string "". */
    public static final String EMPTY = "";

    /** Constructor. not invoke. */
    private Strings() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Looks if the string is null {@code null} or empty.
     *
     * @param str the string
     * @return {@code true} if {@code null} or empty; otherwise {@code false}.
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}