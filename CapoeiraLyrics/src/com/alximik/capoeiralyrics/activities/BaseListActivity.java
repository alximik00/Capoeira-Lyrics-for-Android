package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.R;

import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.entities.SearchType;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.network.ApiConstants;
import com.alximik.capoeiralyrics.utils.SU;
import com.alximik.capoeiralyrics.views.SongsAdapter;
import com.makeramen.segmented.SegmentedRadioGroup;
import com.markupartist.android.widget.ActionBar;
import com.smaato.soma.AdType;
import com.smaato.soma.BannerView;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import com.alximik.capoeiralyrics.network.NetworkConstants;

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
    //protected Set<Long> favourites = new HashSet<Long>();
    private LinearLayout searchPanel;
    private SegmentedRadioGroup searchTypeRagiogroup;
    private EditText searchTextField;
    private BannerView banner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.songs_list);

        actionBar = (ActionBar) findViewById(R.id.actionbar);

        songsAdapter = new SongsAdapter(this, R.id.txt_title, songs);
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
                    doSearch(searchTextField.getText().toString(), getSearchTypeFromRagiogroup());
                    searchPanel.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE );
                    imm.hideSoftInputFromWindow(searchTextField.getWindowToken(), 0);
                    listView.requestFocus();
                    return true;
                }
                return false;
            }
        });


        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id) {
                Song song = songs.get(index);
                onSongLongClick(view, song);
                return true;
            }
        });
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Song song = songs.get(index);
                onSongClicked(view, song);
            }
        });


        banner = (BannerView) findViewById(R.id.banner_view);
        if (banner != null) {
            ApiConstants constants = new NetworkConstants();
            banner.getAdSettings().setPublisherId(constants.getSmaatoPublisherId());
            banner.getAdSettings().setAdspaceId(constants.getSmaatoAdSpace());
            banner.setLocationUpdateEnabled(false);
            banner.getAdSettings().setAdType(AdType.ALL);
            banner.getUserSettings().setKeywordList("Android,Sports,Capoeira");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (banner != null) {
            banner.setAutoReloadEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (banner != null) {
            banner.setAutoReloadEnabled(false);
        }
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


        quickActionMenu.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                onQuickActionSelected(view, song, actionId);
            }
        });

        quickActionMenu.show(view);
    }

    protected void onSongClicked(View view, Song song) {
        Intent intent = new Intent(this, DetailsViewActivity.class);
        intent.putExtra(Constants.SONG_ID, song.getId());
        startActivity(intent);
    }

    protected void onQuickActionSelected(View view, Song song, int actionId) {
        if (actionId == IdQuickActionFav && !song.isFavourite()) {
            song.setFavourite(true);
            try {
                SongsStorage.sharedInstance(this).update(song);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //favourites.add(song.getId());
            view.findViewById(R.id.img_favorite2).setVisibility(View.VISIBLE);
            //FavouritesStorage.add(this, song.getId());

        } else if (actionId == IdQuickActionUnfav && song.isFavourite()) {
            song.setFavourite(false);
            try {
                SongsStorage.sharedInstance(this).update(song);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //favourites.remove(song.getId());
            view.findViewById(R.id.img_favorite2).setVisibility(View.GONE);
            //FavouritesStorage.remove(this, song.getId());

        } else if (actionId == IdQuickActionPlayVideo) {
            startUrl(this,  song.getVideoUrl());
        }

        searchPanel.setVisibility( View.GONE );
    }

    protected  abstract void doSearch(String text, SearchType searchTypeFromRagiogroup);

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

    protected void setNewSongs(List<Song> aSongs) {
        songs.clear();
        songs.addAll(aSongs);

        songsAdapter.notifyDataSetInvalidated();

        if (songs.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    protected void startUrl(Context context, String url) {
        if (!SU.isEmpty(url)) {
            try {
                if (url.contains("youtube")) {
                    url = url.replace("/www.", "/"); // urls with www in youtube couldn't be opened
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url) ));
            } catch (Exception e) {
                Toast.makeText( BaseListActivity.this, "Sorry, can't open the link :(", 3);
            }
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

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
