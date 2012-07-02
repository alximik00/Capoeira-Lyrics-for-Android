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
import com.alximik.capoeiralyrics.db.FavouritesStorage;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;
import com.markupartist.android.widget.ActionBar;

import java.sql.SQLException;
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
                startActivityForResult(intent, Constants.ID_FAVOURITES);
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
            List<Song> newSongs = SongsStorage.load(this);
            setNewSongs( newSongs, favourites );
        } catch (Exception e) { }

        if (MainApplication.isUpdateAsked())
            return;

        if (songs.size() == 0) {
            showProgress();
            startLoad();
        } else {
            if (!isOnline())
                return;

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

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading songs");
        progressDialog.show();
    }

    private void startLoad() {
        Api.getSongs(new SongsCallback() {
            @Override
            public void onSuccess(final List<Song> songs) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSongsDownloaded(songs);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SongsListActivity.this, "Sorry, couldn't load songs :(", 4);
                Log.wtf(Constants.TAG, "Songs loading:" + error);
            }
        });
    }

    private void onSongsDownloaded(final List<Song> newSongs) {
        setNewSongs(newSongs, favourites);

        // Write songs to storage in background
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
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    protected void doSearch(String text, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, text, searchType);
            setNewSongs(newContent, favourites);
        } catch (Exception ex) {
            Toast.makeText(this, "Can't load songs, sorry", 3);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ID_FAVOURITES ) {

            try {
                Set<Long> newFavs = FavouritesStorage.loadFavourites(this);
                favourites.clear();
                favourites.addAll(newFavs);

                for(Song song: songs) {
                    song.setFavourite( favourites.contains(song.getId()) );
                }
                songsAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
            }
        }
    }
    
}


