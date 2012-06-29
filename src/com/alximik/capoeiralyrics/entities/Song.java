package com.alximik.capoeiralyrics.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.utils.JU;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
    private String engText;
    private String rusText;
    private String audioUrl;
    private String videoUrl;

    private boolean favourite;

    public Song(long id, String title, String author, String text, String engText, String rusText, String audioUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.text = text;
        this.engText = engText;
        this.rusText = rusText;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getEngText() {
        return engText;
    }

    public String getRusText() {
        return rusText;
    }

    public String getAudioUrl() {
        return audioUrl;
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



    public static Song fromJson(JSONObject jsonObject) {
        return new Song(
                JU.getLongSafe(jsonObject, "ID"),
                JU.getStringSafe(jsonObject, "Name"),
                JU.getStringSafe(jsonObject, "Text"),
                JU.getStringSafe(jsonObject, "Artist"),
                JU.getStringSafe(jsonObject, "EngText"),

                JU.getStringSafe(jsonObject, "RusText"),
                JU.getStringSafe(jsonObject, "AudioUrl"),
                JU.getStringSafe(jsonObject, "VideoUrl")
        );
    }
     
    
    public static Song findById(Song[] songs, long id) {
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
        parcel.writeString( audioUrl );
        parcel.writeString( videoUrl );
    }
    
    private Song(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.author = parcel.readString();
        this.text = parcel.readString();
        this.engText = parcel.readString();
        this.rusText = parcel.readString();
        this.audioUrl = parcel.readString();
        this.videoUrl = parcel.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

}
