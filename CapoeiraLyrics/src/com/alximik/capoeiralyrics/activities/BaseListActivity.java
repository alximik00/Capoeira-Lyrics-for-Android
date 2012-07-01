package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.views.SongsAdapter;
import com.markupartist.android.widget.ActionBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 01.07.12 12:33
 */
public class BaseListActivity extends Activity {
    protected ActionBar actionBar;
    protected SongsAdapter songsAdapter;
    protected ListView listView;
    protected TextView emptyText;

    protected List<Song> songs = new ArrayList<Song>();
    protected Set<Long> favourites = new HashSet<Long>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list);

        actionBar = (ActionBar) findViewById(R.id.actionbar);

        songsAdapter = new SongsAdapter(this, R.id.txt_title, songs,favourites);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(songsAdapter);
        emptyText = (TextView) findViewById(android.R.id.empty);

        registerForContextMenu(listView);
        listView.setLongClickable(true);
    }

    protected void setNewSongs(List<Song> newContent) {
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
