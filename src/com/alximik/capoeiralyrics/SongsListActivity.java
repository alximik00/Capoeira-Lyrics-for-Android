package com.alximik.capoeiralyrics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongsStorage;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;
import com.alximik.capoeiralyrics.views.SongsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SongsListActivity extends ListActivity {
    
    Handler handler = new Handler();
    List<Song> songs = new ArrayList<Song>();
    SongsAdapter songsAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list);

        songsAdapter = new SongsAdapter(this, R.id.txt_title, songs);
        getListView().setAdapter(songsAdapter);
        startLoad();
    }

    private void startLoad() {
        Api.getSongs(new SongsCallback() {
            @Override
            public void onSuccess(final Song[] songs) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showConfirmationDialog(songs);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.wtf(Constants.TAG, "Songs loading:" + error);
            }
        });
    }

    private void showConfirmationDialog(final Song[] newSongs) {

        DialogInterface.OnClickListener onOk = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                saveSongs(newSongs);
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
    }

    private void saveSongs(final Song[] newSongs) {
        songs.clear();

        songs.addAll(Arrays.asList(newSongs) );
        songsAdapter.notifyDataSetInvalidated();

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

}


