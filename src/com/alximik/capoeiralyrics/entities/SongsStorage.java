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
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

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

    }
    
    public  static List<Song> load(Context context, String what, SearchType searchType) throws SQLException {
        Dao<Song, Long> dao = new DatabaseHelper(context).getDao();

        QueryBuilder<Song,Long> builder = dao.queryBuilder();
        Where<Song, Long> where = builder.where();
        //"title", "author", "engText", "rusText"

        what = "%"+what+"%";

        if (searchType == SearchType.ARTIST) {
            return where.like("author", what)
                    .query();
        } else if (searchType == SearchType.NAME) {
            return where.like("title", what)
                    .query();
        } else if (searchType == SearchType.TEXT) {
            return where.like("engText", what).or().like("rusText", what).or().like("text", what)
                    .query();
        } else if (searchType == SearchType.ALL) {
            return where.like("engText", what).or().like("rusText", what).or().like("text", what)
                    .or().like("author", what)
                    .or().like("title", what)
                    .query();
        }

        return dao.queryForAll();
    }

    public static void save(Context context, Song[] songs) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getDao();
        dao.deleteBuilder().delete();
        dao.clearObjectCache();

        for(Song song: songs) {
            dao.create(song);
        }

    }
}
