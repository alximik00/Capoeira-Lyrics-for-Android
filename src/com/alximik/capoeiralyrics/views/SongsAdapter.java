package com.alximik.capoeiralyrics.views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.Song;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 8:26 PM
 */
public class SongsAdapter extends ArrayAdapter<Song> {
    private Activity context;
    private List<Song> songs;

    public SongsAdapter(Activity context, int textViewResourceId, List<Song> songs) {
        super(context, textViewResourceId, songs);
        this.context = context;
        this.songs = songs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.song_item, parent, false);
        }
        TextView artist = (TextView) convertView.findViewById(R.id.txt_artist);
        artist.setText(songs.get(position).getAuthor());
        
        return convertView;
    }
}
