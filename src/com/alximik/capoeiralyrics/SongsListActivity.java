package com.alximik.capoeiralyrics;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.entities.SongsStorage;
import com.alximik.capoeiralyrics.network.Api;
import com.alximik.capoeiralyrics.network.SongsCallback;
import com.alximik.capoeiralyrics.views.SongsAdapter;
import com.makeramen.segmented.SegmentedRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SongsListActivity extends Activity {
    
    Handler handler = new Handler();
    List<Song> songs = new ArrayList<Song>();
    SongsAdapter songsAdapter;
    private ProgressDialog progressDialog;
    private ListView listView;
    private TextView emptyText;
    private EditText searchTextField;
    private LinearLayout searchPanel;
    private SegmentedRadioGroup searchType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list);

        songsAdapter = new SongsAdapter(this, R.id.txt_title, songs);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(songsAdapter);

        emptyText = (TextView) findViewById(android.R.id.empty);

        searchPanel = (LinearLayout) findViewById(R.id.search_panel);

        searchType = (SegmentedRadioGroup) findViewById(R.id.radio_search_type);

        searchTextField = (EditText)findViewById(R.id.txt_search);
        searchTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onStartSearch();
                    return true;
                }
                return false;
            }
        });

        setupSongs();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_SEARCH){
            showSearch();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchPanel.getVisibility() == View.VISIBLE) {
                searchPanel.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyUp(keyCode, event);
            }
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean onSearchRequested() {
        return false;  // don't show the default search box
    }

    private void setupSongs() {
        try {
            Song[] newSongs = SongsStorage.load(this);
            setNewSongs( Arrays.asList(newSongs) );
        } catch (Exception e) {
        }

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

    private void showSearch() {
        searchPanel.setVisibility(View.VISIBLE);
    }

    private void onStartSearch() {
        loadSongs(searchTextField.getText().toString(), getSearchType());
        searchPanel.setVisibility(View.GONE);
    }

    private SearchType getSearchType() {
        int id = searchType.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rb_all: return SearchType.ALL;
            case R.id.rb_text: return SearchType.TEXT;
            case R.id.rb_name: return SearchType.NAME;
            case R.id.rb_artist: return SearchType.ARTIST;
            default: return SearchType.NONE;
        }
    }

    private void loadSongs(String what, SearchType searchType) {
        try {
            List<Song> newContent = SongsStorage.load(this, what, searchType);
            setNewSongs(newContent);
        } catch (Exception ex) {
            Toast.makeText(this, "Can't load songs, sorry", 3);
        }
    }

    private void setNewSongs(List<Song> newContent) {
        songs.clear();
        songs.addAll(newContent);
        songsAdapter.notifyDataSetInvalidated();
        if (songs.size() ==0 ) {
            listView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}


