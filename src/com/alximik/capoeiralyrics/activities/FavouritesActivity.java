package com.alximik.capoeiralyrics.activities;

import android.os.Bundle;
import android.view.View;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.FavouritesStorage;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongsStorage;
import com.markupartist.android.widget.ActionBar;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 01.07.12 12:36
 */
public class FavouritesActivity extends BaseListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionbar();
        setupSongs();
    }

    private void setupSongs() {
        try {
            Set<Long> favs = FavouritesStorage.loadFavourites(this);
            favourites.addAll(favs);
            favourites.clear();
        } catch (Exception e) {}


        try {
            List<Song> loadedSongs = SongsStorage.loadFavourites(this, favourites);
            setNewSongs( loadedSongs );
        } catch (Exception e) { }
    }

    private void setupActionbar() {
        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.arrow_left;
            }

            @Override
            public void performAction(View view) {
            }
        });
    }
}
