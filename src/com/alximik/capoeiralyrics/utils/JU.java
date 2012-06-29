package com.alximik.capoeiralyrics.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON Util
 *
 * @author alximik
 * @since 6/28/12 8:06 PM
 */
public class JU {
    public static long getLongSafe(JSONObject object, String fieldName) {
        try {
            return object.getLong(fieldName);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static String getStringSafe(JSONObject object, String fieldName) {
        try {
            return object.getString(fieldName);
        } catch (JSONException e) {
            return null;
        }
    }
}
