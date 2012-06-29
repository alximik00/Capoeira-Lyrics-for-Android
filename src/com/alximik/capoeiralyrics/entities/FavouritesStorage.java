package com.alximik.capoeiralyrics.entities;

import android.content.SharedPreferences;
import com.alximik.capoeiralyrics.MainApplication;
import com.alximik.capoeiralyrics.utils.SU;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 6:45 PM
 */
public class FavouritesStorage {
    public static final String STORE_NAME = "favourites_store";
    public static final String FAVS_FIELD = "FAVS";
    
    public static long [] loadFavourites() {

        SharedPreferences store = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
        String favsString = store.getString(FAVS_FIELD, "");
        String[] favSplitted = favsString.split(";");

        ArrayList<Long> result = new ArrayList<Long>();
        
        for(String fav: favSplitted) {
            if (SU.isEmpty(fav))
                continue;
            
            result.add( Long.parseLong(fav) );
        }

        long[] result2 = new long[result.size()];
        for (int i=0; i<result.size(); i++) {
            result2[i] = result.get(i);
        }
        return result2;
    }

    public static void saveFavourites(long[] favs) {
        if (favs == null)
            favs = new long[0];

        StringBuilder buffer = new StringBuilder(1024);
        for(long fav: favs) {
            buffer.append(fav);
            buffer.append(';');
        }

        SharedPreferences store = MainApplication.getContext().getSharedPreferences(STORE_NAME, 0);
        SharedPreferences.Editor editor = store.edit();
        editor.putString(FAVS_FIELD, buffer.toString());
        editor.commit();
    }

}
