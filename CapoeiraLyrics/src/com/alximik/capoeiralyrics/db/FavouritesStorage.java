package com.alximik.capoeiralyrics.db;

import android.content.Context;
import com.alximik.capoeiralyrics.entities.Favourite;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 6:45 PM
 */
public class FavouritesStorage {

    public static Set<Long> loadFavourites(Context context) throws SQLException {
        Dao<Favourite,Integer> dao = new DatabaseHelper(context).getFavouritesDao();

        List<Favourite> favs = dao.queryBuilder().orderBy("songId",true).query();

        Set<Long> res = new HashSet<Long>(favs.size());
        for (Favourite fav : favs) {
            res.add(fav.getSongId());
        }
        return res;
    }

    public static void add(Context context, long id) {
        try {
            Dao<Favourite,Integer> dao = new DatabaseHelper(context).getFavouritesDao();
            List<Favourite> found = dao.queryForEq("songId", id);
            if (found.size() > 0) {
                return;
            }  else {
                dao.create(new Favourite(id));
            }
        } catch (SQLException e) {
        }
    }

    public static void remove(Context context, long id) {
        try {
            Dao<Favourite,Integer> dao = new DatabaseHelper(context).getFavouritesDao();
            DeleteBuilder<Favourite, Integer> builder = dao.deleteBuilder();
            builder.where().eq("songId", id);
            dao.delete( builder.prepare() );
        } catch (SQLException e) {
        }
    }
}
