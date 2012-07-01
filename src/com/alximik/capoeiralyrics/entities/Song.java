package com.alximik.capoeiralyrics.entities;

import com.alximik.capoeiralyrics.utils.JU;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONObject;


/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 27.06.12 19:00
 */
@DatabaseTable(tableName = "songs")
public class Song  {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField private String title;
    @DatabaseField private String author;
    @DatabaseField private String text;
    @DatabaseField private String engText;
    @DatabaseField private String rusText;
    @DatabaseField private String audioUrl;
    @DatabaseField private String videoUrl;

    private boolean favourite = false;

    public Song() {
    }

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


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEngText(String engText) {
        this.engText = engText;
    }

    public void setRusText(String rusText) {
        this.rusText = rusText;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
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
}
