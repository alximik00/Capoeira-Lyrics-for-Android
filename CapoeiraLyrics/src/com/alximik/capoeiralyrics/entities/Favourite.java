package com.alximik.capoeiralyrics.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 01.07.12 9:48
 */

@DatabaseTable(tableName = "favs")
public class Favourite {
    @DatabaseField(generatedId = true) private int id;
    @DatabaseField(index = true) private long songId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getSongId() { return songId; }
    public void setSongId(long songId) {  this.songId = songId; }

    public Favourite() {
    }
    public Favourite(long songId) {
        this.songId = songId;
    }
}
