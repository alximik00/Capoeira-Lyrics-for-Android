package com.alximik.capoeiralyrics.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.db.FavouritesStorage;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.markupartist.android.widget.ActionBar;

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
            favourites.clear();
            favourites.addAll(favs);
        } catch (Exception e) {}


        try {
            List<Song> loadedSongs = SongsStorage.loadFavourites(this, favourites);
            setNewSongs( loadedSongs , favourites);
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
                finish();
            }
        });

        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.search;
            }

            @Override
            public void performAction(View view) {
                showSearch();
            }
        });
    }

    protected void doSearch(String text, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, text, searchType, favourites);
            setNewSongs(newContent, favourites);
        } catch (Exception ex) {
            Toast.makeText(this, "Can't load songs, sorry", 3);
        }
    }

    @Override
    protected void onQuickActionSelected(View view, Song song, int actionId) {
        super.onQuickActionSelected(view, song, actionId);

        if (actionId == IdQuickActionUnfav ) {
            songs.remove( song );
            songsAdapter.notifyDataSetChanged();
        }
    }
}
