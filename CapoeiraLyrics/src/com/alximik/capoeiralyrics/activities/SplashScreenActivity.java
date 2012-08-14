package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.db.DaoMaster;
import com.alximik.capoeiralyrics.db.DaoSession;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongDao;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yeti
 * Date: 03.08.12
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */

public class SplashScreenActivity extends Activity {

    Handler handler = new Handler();



    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        // load items for first database start
        List<Song> newSongs = null;
        try {
            newSongs = SongsStorage.sharedInstance(this).load();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        if(newSongs.size() == 0){
            startLoadFromResources();
        }else{
            startSongsList();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // set the content view for your splash screen you defined in an xml file
        setContentView(R.layout.splash_screen);
        findViewById (R.id.splashscreencontainer).invalidate();




    }

    private void startSongsList(){

        Intent intent = new Intent(SplashScreenActivity.this, SongsListActivity.class);
        startActivity(intent);
        finish();
    }


    private void startLoadFromResources() {
        Api.getSongsFromOfflineStorage(getApplicationContext(), new SongsCallback() {
            @Override
            public void onSuccess(final List<Song> songs) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SongsStorage.sharedInstance(SplashScreenActivity.this).save(SplashScreenActivity.this, songs);
                            startSongsList();
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                SplashScreenActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        startSongsList();
                        Toast.makeText(SplashScreenActivity.this, R.string.msg_update_failed, 4).show();

                    }
                });


                Log.wtf(Constants.TAG, "Songs loading:" + error);
            }
        });
    }

}
