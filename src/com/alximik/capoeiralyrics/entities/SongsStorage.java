package com.alximik.capoeiralyrics.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import com.alximik.capoeiralyrics.MainApplication;
import com.alximik.capoeiralyrics.utils.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:07 PM
 */
public class SongsStorage {
    public static final String STORE_NAME = "songs_store";
    public static final String SONGS_FIELD = "SONGS";

    public static Song[] load(Context context) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getDao();
        List<Song> songs = dao.queryForAll();
        return  songs.toArray(new Song[songs.size()]);

//        SharedPreferences store = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
//        return deserializeSongsFromString(store.getString(SONGS_FIELD, null));
    }
    
    public static void save(Context context, Song[] songs) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getDao();
        dao.deleteBuilder().delete();
        dao.clearObjectCache();

        for(Song song: songs) {
            dao.create(song);
        }

//        SharedPreferences settings = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(SONGS_FIELD, serializeSongsToString(songs));
//        editor.commit();
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
