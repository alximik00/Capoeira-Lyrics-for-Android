//package com.alximik.capoeiralyrics.db;
//
//import android.database.sqlite.SQLiteOpenHelper;
//import com.alximik.capoeiralyrics.R;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//import com.alximik.capoeiralyrics.entities.Favourite;
//import com.alximik.capoeiralyrics.entities.Song;
//
//import com.j256.ormlite.android.apptools.OpenHelperManager;
//import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
//import com.j256.ormlite.dao.BaseDaoImpl;
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.support.ConnectionSource;
//import com.j256.ormlite.table.TableUtils;
//
//import java.sql.SQLException;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.support.ConnectionSource;
//import com.j256.ormlite.table.TableUtils;
//
//
//import java.io.*;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by IntelliJ IDEA.
// *
// * @author alximik
// * @since 29.06.12 7:16
// */
//public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
//
//    private static DatabaseHelper instance = null;
//
//    private static String DATABASE_PATH = "/data/data/com.alximik.capoeiralyrics/databases/";
//    private static final String DATABASE_NAME = "cap_lyr.db";
//    private static final int DATABASE_VERSION = 2;
//
//
//    private Dao<Song, Long> songDao = null;
//    private Dao<Favourite, Integer> favouritesDao = null;
//
//
//    public static DatabaseHelper sharedInstance(Context context){
//        return new DatabaseHelper(context);
//    }
//
//
//
//
//    private DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
//
//
//    }
//
//
//
//
//    public Dao<Song, Long> getSongsDao() throws SQLException {
//        if (songDao == null) {
//            songDao = BaseDaoImpl.createDao(getConnectionSource(), Song.class);
//        }
//        return songDao;
//    }
//
//    public Dao<Favourite, Integer> getFavouritesDao() throws SQLException {
//        if (favouritesDao == null) {
//            favouritesDao = BaseDaoImpl.createDao(getConnectionSource(), Favourite.class);
//        }
//        return favouritesDao;
//    }
//
//
//    public void close() {
//        super.close();
//        favouritesDao = null;
//        songDao = null;
//    }
//
//    //////////////////////////////// Implement database SQLiteOpenHelper
//
//    @Override
//    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
//
//        try {
//            TableUtils.createTable(getConnectionSource(), Song.class);
//            TableUtils.createTable(getConnectionSource(), Favourite.class);
//
//            // load songs
//
//            Log.i("", "Database create");
//        } catch (Exception e) {
//            Log.e("", "Failed to create DB", e );
//        } finally {
//
//        }
//
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//
//        onCreate(db, connectionSource);
//
//    }
//
//
//
//    private static InputStream openFileId(Context context, int fileId) {
//        InputStream stream = context.getResources().openRawResource(fileId);
//        if (stream == null) {
//            throw new IllegalStateException("Could not find object config file with id " + fileId);
//        }
//        return stream;
//    }
//
//
//}
