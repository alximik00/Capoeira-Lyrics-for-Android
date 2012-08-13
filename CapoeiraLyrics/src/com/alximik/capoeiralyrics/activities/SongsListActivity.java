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
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;
import com.alximik.capoeiralyrics.network.SongsCountCallback;
import com.markupartist.android.widget.ActionBar;

import java.sql.SQLException;
import java.util.*;


public class SongsListActivity extends BaseListActivity {
    Handler handler = new Handler();

    int mServerSongsCount = 0;
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

        actionBar.setTitle(R.string.app_name);
        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return  R.drawable.reload_icon;
            }

            @Override
            public void performAction(View view) {

                DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
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

                String format = getResources().getString(R.string.msg_pattern_are_you_sure_to_download);
                String value = String.format(format, mServerSongsCount/1000.0);
                new AlertDialog.Builder(SongsListActivity.this)
                        .setTitle(R.string.msg_title_update_available)
                        .setMessage(value)
                        .setPositiveButton(R.string.btn_title_download, onOk)
                        .setNegativeButton(R.string.btn_title_cancel, onCancel)
                        .show();




            }
        });

        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.zoom_icon;
            }

            @Override
            public void performAction(View view) {
                showSearch();
            }
        });

        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.heart_icon;
            }

            @Override
            public void performAction(View view) {
                if(favourites.isEmpty()){
                    Toast.makeText(SongsListActivity.this, R.string.msg_no_favorites, 4).show();
                }else{
                    Intent intent = new Intent(SongsListActivity.this, FavouritesActivity.class);
                    startActivityForResult(intent, Constants.FAVOURITES_INTENT);
                }
            }
        });
    }

    private void setupSongs() {

        try {

            //Set<Long> favs = FavouritesStorage.loadFavourites(this);
            favourites.clear();
            //favourites.addAll(favs);

            List<Song> newSongs = SongsStorage.sharedInstance(this).load(this);

            setNewSongs( newSongs, favourites );


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (MainApplication.isUpdateAsked())
            return;

        if (!isOnline())
            return;



        // background check for server songs count
        Api.getSongsCount(new SongsCountCallback() {
            @Override
            public void onSuccess(final int count) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mServerSongsCount = count;
                        if(songs.size() < count){

                            SongsListActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

                                    String format = getResources().getString(R.string.msg_pattern_update_available);
                                    String value = String.format(format, mServerSongsCount/1000.0);

                                    new AlertDialog.Builder(SongsListActivity.this)
                                            .setTitle(R.string.msg_title_update_available)
                                            .setMessage(value)
                                            .setPositiveButton(R.string.btn_title_download, onOk)
                                            .setNegativeButton(R.string.btn_title_cancel, onCancel)
                                            .show();
                                }
                            });

                        }



                    }
                });
            }

            @Override
            public void onError(String error) {}
        });






        //MainApplication.setUpdateAsked(true);
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.msg_update_progress));
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
            SongsListActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(SongsListActivity.this, R.string.msg_update_failed, 4).show();
                }
            });


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
                    SongsStorage.sharedInstance(SongsListActivity.this).save(SongsListActivity.this,  newSongs);
                } catch (Exception e) {
                    Log.e(Constants.TAG, getString(R.string.msg_update_failed), e);
                }
            }
        };
        saveThread.start();
        if (progressDialog != null)
            progressDialog.dismiss();

        String msg = newSongs.size() + " " +getResources().getString(R.string.msg_update_complete);

        Toast.makeText(SongsListActivity.this, msg, 4).show();
    }


    protected void doSearch(String text, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, text, searchType);
            setNewSongs(newContent, favourites);
        } catch (Exception ex) {
            Toast.makeText(this, R.string.msg_update_failed, 4).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.FAVOURITES_INTENT) {

            {
                //Set<Long> newFavs = FavouritesStorage.loadFavourites(this);
                favourites.clear();
                //favourites.addAll(newFavs);

                for(Song song: songs) {
                    song.setFavourite( favourites.contains(song.getId()) );
                }
                songsAdapter.notifyDataSetChanged();
            }
        }
    }
    
}


