package com.alximik.capoeiralyrics.entities;

import android.content.Context;
import com.alximik.capoeiralyrics.activities.FavouritesActivity;
import com.alximik.capoeiralyrics.utils.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:07 PM
 */
public class SongsStorage {

    public static Song[] load(Context context) throws Exception {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        List<Song> songs = dao.queryForAll();
        return  songs.toArray(new Song[songs.size()]);
    }
    
    public  static List<Song> load(Context context, String what, SearchType searchType) throws SQLException {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();

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
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        dao.deleteBuilder().delete();
        dao.clearObjectCache();

        for(Song song: songs) {
            dao.create(song);
        }
    }

    public static List<Song> loadFavourites(Context context, Set<Long> favourites) throws SQLException {
        Dao<Song, Long> dao = new DatabaseHelper(context).getSongsDao();
        List<Song> res = new ArrayList<Song>(favourites.size());
        for(long id: favourites) {
            res.add( dao.queryForId(id) );
        }
        return res;
    }
}