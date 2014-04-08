package com.alximik.capoeiralyrics.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongDao;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author alximik
 * @since 6/28/12 7:07 PM
 */
public class SongsStorage {

    // Most STRANGE code here due to not working in-queries
    private SQLiteDatabase db;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SongDao songDao;


    private Cursor cursor;
    private Context context;

    private static SongsStorage instance;

    public static SongsStorage sharedInstance(Context context){
        if(instance == null){
            instance = new SongsStorage(context);
        }
        return instance;
    }


    private SongsStorage(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "lyricsdb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        songDao = daoSession.getSongDao();

        this.context = context;
    }

    public List<Song> load() throws Exception {

        String textColumn = SongDao.Properties.Title.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(songDao.getTablename(), songDao.getAllColumns(), null, null, null, null, orderBy);

        ArrayList<Song> mArrayList = new ArrayList<Song>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(songDao.readEntity(cursor, 0));
        }

        cursor.close();

        return mArrayList;
    }
    

    
    public List<Song> search(String what, SearchType searchType, boolean searchThruFavs) throws SQLException {

        String textColumn = SongDao.Properties.Title.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        String where = (searchThruFavs)?"(isFavourite = 1)":"(1=1)";
        what = "'%"+what+"%'";

        if (searchType == SearchType.ARTIST) {
            where += " AND (authorNorm like " + what + ")";
        } else if (searchType == SearchType.NAME) {
            where += " AND (titleNorm like " + what+ ")";
        } else if (searchType == SearchType.TEXT) {
            where += " AND (engText like " + what+ " OR rusText like " + what+ " OR textNorm like "+ what+")";
        } else if (searchType == SearchType.ALL) {
            where += " AND (engText like " + what+ " OR rusText like " + what+ " OR textNorm like "+ what+ " OR authorNorm like " + what+ " OR titleNorm like " + what+")";
        }


        cursor = db.query(songDao.getTablename(), songDao.getAllColumns(), where, null, null, null, orderBy);

        ArrayList<Song> mArrayList = new ArrayList<Song>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(songDao.readEntity(cursor, 0));
        }

        cursor.close();

        return mArrayList;

    }

    public void save(List<Song> songs) throws Exception {

        this.songDao.deleteAll();

        for(Song song: songs) {
            this.songDao.insert(song);
        }
    }

    public void update(Song song) throws Exception {
        this.songDao.update(song);
    }

    public List<Song> loadFavourites() throws SQLException {
        String textColumn = SongDao.Properties.Title.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(songDao.getTablename(), songDao.getAllColumns(), "isFavourite == 1", null, null, null, orderBy);

        ArrayList<Song> mArrayList = new ArrayList<Song>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(songDao.readEntity(cursor, 0));
        }

        cursor.close();

        return mArrayList;
    }

    public Song findById(long id) {
        cursor = db.query(songDao.getTablename(), songDao.getAllColumns(), String.format("_id == %d", id), null, null, null, null);

        cursor.moveToFirst();
        Song song = songDao.readEntity(cursor, 0);

        return song;
    }
}
