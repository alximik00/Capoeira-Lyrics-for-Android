package com.alximik.capoeiralyrics.db;

import android.database.sqlite.SQLiteOpenHelper;
import com.alximik.capoeiralyrics.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.alximik.capoeiralyrics.entities.Favourite;
import com.alximik.capoeiralyrics.entities.Song;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.DatabaseTableConfigLoader;
import com.j256.ormlite.table.TableUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 29.06.12 7:16
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "cap_lyr.db";
    private static final String TAG = "alx_db";
    private static final int DATABASE_VERSION = 2;

    private Dao<Song, Long> songDao = null;
    private Dao<Favourite, Integer> favouritesDao = null;


    private volatile boolean isOpen = true;
    protected AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        loadConfigs(context);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Song.class);
            TableUtils.createTable(connectionSource, Favourite.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Song.class, true);
            TableUtils.dropTable(connectionSource, Favourite.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (java.sql.SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't upgrade database",e);
        }
    }

    public Dao<Song, Long> getSongsDao() throws SQLException {
        if (songDao == null) {
            songDao = getClassDao(Song.class);
        }
        return songDao;
    }

    public Dao<Favourite, Integer> getFavouritesDao() throws SQLException {
        if (favouritesDao == null) {
            favouritesDao = getClassDao(Favourite.class);
        }
        return favouritesDao;
    }


    public void close() {
        super.close();
        connectionSource.close();
        isOpen = false;
        favouritesDao = null;
        songDao = null;
    }

    //////////////////////////////// Implement database SQLiteOpenHelper

    @Override
    public void onCreate(SQLiteDatabase db) {
        ConnectionSource cs = getConnectionSource();
        /*
           * The method is called by Android database helper's get-database calls when Android detects that we need to
           * create or update the database. So we have to use the database argument and save a connection to it on the
           * AndroidConnectionSource, otherwise it will go recursive if the subclass calls getConnectionSource().
           */
        DatabaseConnection conn = cs.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                cs.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            TableUtils.createTable(cs, Song.class);
            TableUtils.createTable(cs, Favourite.class);
            Log.i(TAG, "Database create");
        } catch (Exception e) {
            Log.e(TAG, "Failed to create DB", e );
        } finally {
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ConnectionSource cs = getConnectionSource();
        DatabaseConnection conn = cs.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                cs.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            TableUtils.dropTable(cs, Song.class, true );
            TableUtils.dropTable(cs, Favourite.class, true );
            onCreate(db);
            Log.i(TAG, "Database updated");
        } catch (Exception e) {
            Log.e(TAG, "Failed to upgrade DB", e );
        } finally {
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        }
    }

    /////////////////////////////////

    private static void loadConfigs(Context context) {
        List<DatabaseTableConfig<?>> list = new ArrayList<DatabaseTableConfig<?>>();

        list.addAll(addConfig(openFileId(context, R.raw.favs_ormlite_config))) ;
        list.addAll(addConfig(openFileId(context, R.raw.songs_ormlite_config)));
        DaoManager.addCachedDatabaseConfigs( list );
    }
    
    private static List<DatabaseTableConfig<?>> addConfig(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream), 4096);
            return DatabaseTableConfigLoader.loadDatabaseConfigFromReader(reader);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load object config file", e);
        } finally {
            try {
                // we close the stream here because we may not get a reader
                stream.close();
            } catch (IOException e) {
                // ignore close errors
            }
        }
    }

    private static InputStream openFileId(Context context, int fileId) {
        InputStream stream = context.getResources().openRawResource(fileId);
        if (stream == null) {
            throw new IllegalStateException("Could not find object config file with id " + fileId);
        }
        return stream;
    }

    private ConnectionSource getConnectionSource() {
        if (!isOpen) {
            // we don't throw this exception, but log it for debugging purposes
            Log.w(TAG, "Getting connectionSource was called after closed", new IllegalStateException());
        }
        return connectionSource;
    }

    private Dao getClassDao(Class clazz) throws SQLException {
        // special reflection fu is now handled internally by create dao calling the database type
        Dao dao = DaoManager.createDao(getConnectionSource(), clazz);
        return dao;
    }

}
