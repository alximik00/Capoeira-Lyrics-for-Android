package com.alximik.capoeiralyrics.utils;

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
}
