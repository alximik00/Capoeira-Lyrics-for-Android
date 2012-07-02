package com.alximik.capoeiralyrics.activities;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.MainApplication;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.FavouritesStorage;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongsStorage;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;
import com.markupartist.android.widget.ActionBar;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.util.*;


public class SongsListActivity extends BaseListActivity {
    Handler handler = new Handler();

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionbar();
        setupSongs();
    }

    @Override
    public boolean onSearchRequested() {
        return false;  // don't show the default search box
    }

    private void setupActionbar() {
        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return  R.drawable.refresh;
            }

            @Override
            public void performAction(View view) {
                showProgress();
                startLoad();
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

        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.gray_heart;
            }

            @Override
            public void performAction(View view) {
                Intent intent = new Intent(SongsListActivity.this, FavouritesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupSongs() {
        try {
            Set<Long> favs = FavouritesStorage.loadFavourites(this);
            favourites.clear();
            favourites.addAll(favs);
        } catch (Exception e) {}


        try {
            Song[] newSongs = SongsStorage.load(this);
            for(Song song: newSongs) {
                if (favourites.contains(song.getId())) {
                    song.setFavourite(true);
                }
            }
            setNewSongs( Arrays.asList(newSongs) );
        } catch (Exception e) { }

        if (MainApplication.isUpdateAsked())
            return;

        if (songs.size() == 0) {
            showProgress();
            startLoad();
        } else {
            DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    showProgress();
                    startLoad();
                }
            };

            DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("Updates")
                    .setMessage("New songs updates available! Do you want to load them?")
                    .setPositiveButton("Download", onOk)
                    .setNegativeButton("Cancel", onCancel)
                    .show();
            MainApplication.setUpdateAsked(true);
        }
    }

    protected void onQuickActionSelected(View view, Song song, int actionId) {
        if (actionId == IdQuickActionFav ) {
            song.setFavourite(true);
            favourites.add(song.getId());
            view.findViewById(R.id.img_favorite2).setVisibility(View.VISIBLE);
            FavouritesStorage.add(this, song.getId());

        } else if (actionId == IdQuickActionUnfav ) {
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


    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading songs");
        progressDialog.show();
    }

    private void startLoad() {
        Api.getSongs(new SongsCallback() {
            @Override
            public void onSuccess(final Song[] songs) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSongsLoaded(songs);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.wtf(Constants.TAG, "Songs loading:" + error);
            }
        });
    }

    private void onSongsLoaded(final Song[] newSongs) {
        saveSongs(newSongs);
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void saveSongs(final Song[] newSongs) {
        setNewSongs(Arrays.asList(newSongs));

        Thread saveThread = new Thread() {
            public void run() {
                try {
                    SongsStorage.save(SongsListActivity.this,  newSongs);
                } catch (Exception e) {
                    Log.e(Constants.TAG, "Failed to save songs", e);
                }
            }
        };
        saveThread.start();
    }


    protected void onStartSearch(String text, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, text, searchType);
            setNewSongs(newContent);
        } catch (Exception ex) {
            Toast.makeText(this, "Can't load songs, sorry", 3);
        }
    }

}


