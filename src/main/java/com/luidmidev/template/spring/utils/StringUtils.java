package com.luidmidev.template.spring.utils;

import java.text.Normalizer;

public final class StringUtils {


    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String normalice(String string) {
        String normalizedString = Normalizer.normalize(string, Normalizer.Form.NFD);
        return normalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
