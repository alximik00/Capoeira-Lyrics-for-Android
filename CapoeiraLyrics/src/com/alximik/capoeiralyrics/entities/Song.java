package com.alximik.capoeiralyrics.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.utils.JU;
import com.alximik.capoeiralyrics.utils.SU;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 27.06.12 19:00
 */

public class Song implements Parcelable, Serializable {

    private long id;

     private String title;
     private String author;
     private String text;
     private String titleNorm;
     private String authorNorm;
     private String textNorm;
     private String engText;
     private String rusText;
     private String videoUrl;

    private boolean favourite = false;

    public Song() {
    }

    public Song(long id, String title, String author, String text, String engText, String rusText, boolean favourite, String videoUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.text = text;
        this.engText = engText;
        this.rusText = rusText;
        this.videoUrl = videoUrl;
        this.favourite = favourite;
        this.titleNorm = SU.deAccent(this.title);
        this.authorNorm = SU.deAccent(this.author);
        this.textNorm = SU.deAccent(this.text);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleNorm() {
        return titleNorm;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorNorm() {
        return authorNorm;
    }

    public String getText() {
        return text;
    }

    public String getTextNorm() {
        return textNorm;
    }

    public String getEngText() {
        return engText;
    }

    public String getRusText() {
        return rusText;
    }



    public String getVideoUrl() {
        return videoUrl;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite){
        this.favourite = favourite;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleNorm = SU.deAccent(this.title);
    }

    public void setAuthor(String author) {
        this.author = author;
        this.authorNorm = SU.deAccent(this.author);
    }

    public void setText(String text) {
        this.text = text;
        this.textNorm = SU.deAccent(this.text);
    }

    public void setEngText(String engText) {
        this.engText = engText;
    }

    public void setRusText(String rusText) {
        this.rusText = rusText;
    }



    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }


    public static Song fromJson(JSONObject jsonObject) {
        return new Song(
                JU.getLongSafe(jsonObject, "ID"),
                JU.getStringSafe(jsonObject, "Name"),
                JU.getStringSafe(jsonObject, "Artist"),
                JU.getStringSafe(jsonObject, "Text"),
                JU.getStringSafe(jsonObject, "EngText"),
                JU.getStringSafe(jsonObject, "RusText"),
                false,
                JU.getStringSafe(jsonObject, "VideoUrl")
        );
    }
     
    
    public static Song findById(List<Song> songs, long id) {
        for(Song song : songs) {
            if (song.id == id) {
                return song;
            }
        }
        return null;
    }



    //////// Implement Parcelable
    @Override
    public int describeContents() {
        return 0xBADF00D8;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString( title );
        parcel.writeString( author );
        parcel.writeString( text );
        parcel.writeString( engText );
        parcel.writeString( rusText );
        parcel.writeString( videoUrl );

        parcel.writeString(titleNorm);
        parcel.writeString(authorNorm);
        parcel.writeString(textNorm);
    }

    private Song(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.author = parcel.readString();
        this.text = parcel.readString();
        this.engText = parcel.readString();
        this.rusText = parcel.readString();
        this.videoUrl = parcel.readString();

        this.titleNorm = parcel.readString();
        this.authorNorm = parcel.readString();
        this.textNorm = parcel.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public boolean hasVideo() {
        return !SU.isEmpty(videoUrl);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Song other = (Song) obj;
        if (other.id != this.id)
            return false;
        return true;
    }





}
