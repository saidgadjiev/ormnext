package ru.saidgadjiev.ormnext.core.utils;

/**
 * Utils work with strings.
 *
 * @author Said Gadjiev
 */
public final class NormalizeUtils {

    /**
     * Can't be instantiated.
     */
    private NormalizeUtils() { }

    /**
     * Make lowercase string with underscores where letter is in Uppercase.
     *
     * @param str target string
     * @return normalized string
     */
    public static String normalize(String str) {
        StringBuilder normal = new StringBuilder();

        str.chars().forEach(value -> {
            if (normal.length() > 0 && Character.isUpperCase((char) value)) {
                normal.append("_");
            }
            normal.append(Character.toLowerCase((char) value));
        });

        return normal.toString();
    }
}
