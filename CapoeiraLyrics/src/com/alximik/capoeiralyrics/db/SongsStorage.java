package com.alximik.capoeiralyrics.db;

import android.content.Context;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
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

    public static List<Song> load(Context context) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        return  dao.queryBuilder().orderBy("title", true).query();
    }
    
    public  static List<Song> load(Context context, String what, SearchType searchType) throws SQLException {
        return load(context, what, searchType, null);
    }
    
    public static List<Song> load(Context context, String what, SearchType searchType, Set<Long> favourites) throws SQLException {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();

        QueryBuilder<Song,Long> builder = dao.queryBuilder();
        Where<Song, Long> where = builder.orderBy("title", true).where();
        //"title", "author", "engText", "rusText"
        what = "%"+what+"%";

        if (searchType == SearchType.ARTIST) {
            where = where.like("author", what);
        } else if (searchType == SearchType.NAME) {
            where = where.like("title", what);
        } else if (searchType == SearchType.TEXT) {
            where = where.like("engText", what).or().like("rusText", what).or().like("text", what);
        } else if (searchType == SearchType.ALL) {
            where = where.like("engText", what)
                    .or().like("rusText", what)
                    .or().like("text", what)
                    .or().like("author", what)
                    .or().like("title", what);
        }
        List<Song> res = where.query();
        if (favourites != null) {
            List<Song> filtered = new ArrayList<Song>();
            for(Song song: res) {
                if (favourites.contains( song.getId() ) ) {
                    filtered.add( song );
                }
            }
            res = filtered;
        }
        return res;
    }

    public static void save(Context context, List<Song> songs) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        dao.deleteBuilder().delete();
        dao.clearObjectCache();

        for(Song song: songs) {
            dao.create(song);
        }
    }

    public static List<Song> loadFavourites(Context context, Set<Long> favourites) throws SQLException {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        List<Song> songs = new ArrayList<Song>(favourites.size());
        for(long id: favourites) {
            songs.add(dao.queryForId(id));
        }
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song song1) {
                int res =   SU.emptify( song.getTitle() ).compareTo(song1.getTitle());
                if (res != 0)
                    return res;

                return SU.emptify( song.getAuthor() ).compareTo(song1.getAuthor());
            }
        });
        return  songs;
    }
}
