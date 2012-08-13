package com.alximik.capoeiralyrics.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextThemeWrapper;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongDao;
import com.alximik.capoeiralyrics.utils.SU;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
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

    public List<Song> load(Context context) throws Exception {




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
    
    public  static List<Song> load(Context context, String what, SearchType searchType) throws SQLException {
        return load(context, what, searchType, null);
    }
    
    public static List<Song> load(Context context, String what, SearchType searchType, Set<Long> favourites) throws SQLException {
//        Dao<Song, Long> dao = DatabaseHelper.sharedInstance(context).getSongsDao();
//
//        QueryBuilder<Song,Long> builder = dao.queryBuilder();
//        Where<Song, Long> where = builder.orderBy("title", true).where();
//        //"title", "author", "engText", "rusText"
//        what = "%"+what+"%";
//
//        if (searchType == SearchType.ARTIST) {
//            where = where.like("authorNorm", what);
//        } else if (searchType == SearchType.NAME) {
//            where = where.like("titleNorm", what);
//        } else if (searchType == SearchType.TEXT) {
//            where = where.like("engText", what).or().like("rusText", what).or().like("textNorm", what);
//        } else if (searchType == SearchType.ALL) {
//            where = where.like("engText", what)
//                    .or().like("rusText", what)
//                    .or().like("textNorm", what)
//                    .or().like("authorNorm", what)
//                    .or().like("titleNorm", what);
//        }
//        List<Song> res = dao.query(where.prepare());
//        if (favourites != null) {
//            List<Song> filtered = new ArrayList<Song>();
//            for(Song song: res) {
//                if (favourites.contains( song.getId() ) ) {
//                    filtered.add( song );
//                }
//            }
//            res = filtered;
//        }
//
//
//        return res;
        return null;
    }

    public void save(Context context, List<Song> songs) throws Exception {

        this.songDao.deleteAll();

        for(Song song: songs) {
            this.songDao.insert(song);
        }
    }

    public static List<Song> loadFavourites(Context context, Set<Long> favourites) throws SQLException {
//        Dao<Song, Long> dao = DatabaseHelper.sharedInstance(context).getSongsDao();
//        List<Song> songs = new ArrayList<Song>(favourites.size());
//        for(long id: favourites) {
//            songs.add(dao.queryForId(id));
//        }
//        Collections.sort(songs, new Comparator<Song>() {
//            @Override
//            public int compare(Song song, Song song1) {
//                int res =   SU.emptify( song.getTitle() ).compareTo(song1.getTitle());
//                if (res != 0)
//                    return res;
//
//                return SU.emptify( song.getAuthor() ).compareTo(song1.getAuthor());
//            }
//        });
//        return  songs;
        return null;
    }

    public static Song findById(Context context, long id) {
//        try {
//            Dao<Song, Long> dao = DatabaseHelper.sharedInstance(context).getSongsDao();
//            return dao.queryForId(id);
//        } catch (SQLException e) {
//            return null;
//        }
        return null;
    }
}
