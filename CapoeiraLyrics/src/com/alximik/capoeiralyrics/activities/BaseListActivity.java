package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.utils.SU;
import com.alximik.capoeiralyrics.views.SongsAdapter;
import com.makeramen.segmented.SegmentedRadioGroup;
import com.markupartist.android.widget.ActionBar;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

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
public abstract class BaseListActivity extends Activity {

    protected  static final int IdQuickActionFav = 1;
    protected  static final int IdQuickActionUnfav = 2;
    protected  static final int IdQuickActionPlayAudio = 3;
    protected static final int IdQuickActionPlayVideo = 4;

    protected ActionBar actionBar;
    protected SongsAdapter songsAdapter;
    protected ListView listView;
    protected TextView emptyText;

    protected List<Song> songs = new ArrayList<Song>();
    protected Set<Long> favourites = new HashSet<Long>();
    private LinearLayout searchPanel;
    private SegmentedRadioGroup searchTypeRagiogroup;
    private EditText searchTextField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list);

        actionBar = (ActionBar) findViewById(R.id.actionbar);

        songsAdapter = new SongsAdapter(this, R.id.txt_title, songs,favourites);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(songsAdapter);
        emptyText = (TextView) findViewById(android.R.id.empty);

        searchPanel = (LinearLayout) findViewById(R.id.search_panel);
        searchTypeRagiogroup = (SegmentedRadioGroup) findViewById(R.id.radio_search_type);

        searchTextField = (EditText)findViewById(R.id.txt_search);
        searchTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onStartSearch(searchTextField.getText().toString(), getSearchTypeFromRagiogroup() );
                    searchPanel.setVisibility(View.GONE);
                    searchTextField.requestFocus();
                    return true;
                }
                return false;
            }
        });


        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id) {
                Song song = Song.findById(songs, id);
                onSongLongClick(view, song);
                return true;
            }
        });
    }

    protected void onSongLongClick(final View view, final Song song) {
        QuickAction quickActionMenu = new QuickAction(this);
        if (song.isFavourite()) {
            quickActionMenu.addActionItem(new ActionItem(IdQuickActionUnfav, "Unfavorite"));
        } else {
            quickActionMenu.addActionItem(new ActionItem(IdQuickActionFav, "Favorite"));
        }

        if (song.hasVideo()) {
            quickActionMenu.addActionItem(new ActionItem(IdQuickActionPlayVideo, "Play Video") );
        }

        if (song.hasAudio()) {
            quickActionMenu.addActionItem(new ActionItem(IdQuickActionPlayAudio, "Play Audio") );
        }

        quickActionMenu.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                onQuickActionSelected(view, song, actionId);
            }
        });

        quickActionMenu.show(view);
    }

    protected abstract void onQuickActionSelected(View view, Song song, int actionId);

    protected  abstract void onStartSearch(String text, SearchType searchTypeFromRagiogroup);

    protected void showSearch() {
        searchPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_SEARCH){
            showSearch();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchPanel.getVisibility() == View.VISIBLE) {
                searchPanel.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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

    protected void startUrl(Context context, String url) {
        if (SU.isEmpty(url)) {
            return;
        }
    }

    private SearchType getSearchTypeFromRagiogroup() {
        int id = searchTypeRagiogroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rb_all: return SearchType.ALL;
            case R.id.rb_text: return SearchType.TEXT;
            case R.id.rb_name: return SearchType.NAME;
            case R.id.rb_artist: return SearchType.ARTIST;
            default: return SearchType.NONE;
        }
    }
}
