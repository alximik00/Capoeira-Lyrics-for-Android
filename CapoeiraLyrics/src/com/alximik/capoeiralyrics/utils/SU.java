package com.alximik.capoeiralyrics.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * StringUtil
 *
 * @author alximik
 * @since 6/28/12 11:30 AM
 */
public class SU {
    public static boolean isEmpty(String s) {
        return s == null || s.length() ==0;
    }

    public static String emptify(String s) {
        if (s == null)
            return  "";
        return s;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
