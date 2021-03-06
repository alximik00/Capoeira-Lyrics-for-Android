package com.alximik.capoeiralyrics.views;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.utils.SU;

import java.util.List;
import java.util.Set;

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

    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setSongs(List<Song> aSongs){
        songs.clear();
        songs.addAll(aSongs);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.song_item, parent, false);
        }
        TextView txtArtist = (TextView) convertView.findViewById(R.id.txt_artist);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
        ImageView imgEn = (ImageView) convertView.findViewById(R.id.img_english);
        ImageView imgRu = (ImageView) convertView.findViewById(R.id.img_russian);
        //ImageView imgAudio = (ImageView) convertView.findViewById(R.id.img_audio);
        ImageView imgVideo = (ImageView) convertView.findViewById(R.id.img_video);

        ImageView imgLogo = (ImageView) convertView.findViewById(R.id.img_logo);



        ImageView imgFav = (ImageView) convertView.findViewById(R.id.img_favorite2);

        Song song = songs.get(position);



        txtArtist.setText( SU.emptify(song.getAuthor()) ) ;
        txtTitle.setText(song.getTitle());

        setVisibilityOnString(imgEn, song.getEngText());
        setVisibilityOnString(imgRu, song.getRusText());


        setVisibilityOnString(imgVideo, song.getVideoUrl());


        int drawableLogo = chooseLogo(song.getAuthor());
        if(drawableLogo == R.drawable.logo_default){
            imgLogo.setScaleType(ImageView.ScaleType.FIT_START);
        }else{
            imgLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }


        imgLogo.setImageDrawable(context.getResources().getDrawable(drawableLogo));
        imgFav.setVisibility( song.isFavourite() ? View.VISIBLE : View.GONE );

        return convertView;
    }

    private void setVisibilityOnString(ImageView view, String text) {
        if (SU.isEmpty(text))
            view.setVisibility( View.GONE );
        else
            view.setVisibility(  View.VISIBLE );
    }

    private int chooseLogo(String artist) {
        if(null == artist) return R.drawable.logo_default;

        artist = artist.toLowerCase();

        if (artist.contains("mestre suassuna") || artist.contains("cordao de ouro") )
            return R.drawable.logo_cdo;

        if (artist.contains("mestre museu") || artist.contains("ficag") )
            return R.drawable.logo_ficag;

        if (artist.contains("mestre barrao") || artist.contains("axe capoeria") )
            return R.drawable.logo_axe;

        if (artist.contains("mestre camisa") || artist.contains("abada capoeira") )
            return R.drawable.logo_abada;

        if (artist.contains("mestre burgues") || artist.contains("grupo muzenza") )
            return R.drawable.logo_muzenza;

        if (artist.contains("mestre mao branca") || artist.contains("capoeira gerais") )
            return R.drawable.logo_gerais;

        if ( artist.contains("jogo de dentro") )
            return R.drawable.logo_sementedojogodeangola;

        if ( artist.contains("mestre acordeon") )
            return R.drawable.logo_uca;

        if ( artist.contains("mundo capoeira") )
            return R.drawable.logo_mundo;

        return R.drawable.logo_default;

    }
}
