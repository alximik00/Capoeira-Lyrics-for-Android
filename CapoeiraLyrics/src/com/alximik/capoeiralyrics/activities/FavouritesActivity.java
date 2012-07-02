package com.alximik.capoeiralyrics.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.FavouritesStorage;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongsStorage;
import com.markupartist.android.widget.ActionBar;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

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
            favourites.clear();
            favourites.addAll(favs);
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
                finish();
            }
        });
    }

    protected void onStartSearch(String text, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, text, searchType, favourites);
            setNewSongs(newContent);
        } catch (Exception ex) {
            Toast.makeText(this, "Can't load songs, sorry", 3);
        }
    }

    protected void onQuickActionSelected(View view, Song song, int actionId) {
        if (actionId == IdQuickActionFav && !song.isFavourite()) {
            song.setFavourite(true);
            favourites.add(song.getId());
            view.findViewById(R.id.img_favorite2).setVisibility(View.VISIBLE);
            FavouritesStorage.add(this, song.getId());

        } else if (actionId == IdQuickActionUnfav && song.isFavourite()) {
            Song found = Song.findById(songs, song.getId() );
            if (found != null)
                songs.remove( found );
            
            song.setFavourite(false);
            favourites.remove(song.getId());
            view.findViewById(R.id.img_favorite2).setVisibility(View.GONE);
            FavouritesStorage.remove(this, song.getId());

        } else if (actionId == IdQuickActionPlayAudio) {
            startUrl(this, song.getAudioUrl());
        } else if (actionId == IdQuickActionPlayVideo) {
            startUrl(this,  song.getVideoUrl());
        }
    }
}
