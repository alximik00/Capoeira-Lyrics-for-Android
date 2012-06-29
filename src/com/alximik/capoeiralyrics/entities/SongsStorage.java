package com.alximik.capoeiralyrics.entities;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import com.alximik.capoeiralyrics.MainApplication;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:07 PM
 */
public class SongsStorage {
    public static final String STORE_NAME = "songs_store";
    public static final String SONGS_FIELD = "SONGS";

    public static Song[] load() {
        SharedPreferences store = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
        return deserializeSongsFromString(store.getString(SONGS_FIELD, null));
    }
    
    public static void save(Song[] songs) {
        SharedPreferences settings = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SONGS_FIELD, serializeSongsToString(songs));
        editor.commit();
    }

    private static String serializeSongsToString(Song[] songs) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(out).writeObject(songs);
            byte[] data = out.toByteArray();
            out.close();

            out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Song[] deserializeSongsFromString(String data) {
        try {
            ObjectInputStream inStream = new ObjectInputStream(new Base64InputStream(new ByteArrayInputStream(data.getBytes()), Base64.DEFAULT));
            return (Song[] ) inStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new Song[0];
        }
    }
}
