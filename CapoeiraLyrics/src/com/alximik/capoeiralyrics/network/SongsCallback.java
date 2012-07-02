package com.alximik.capoeiralyrics.network;

import com.alximik.capoeiralyrics.entities.Song;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:44 PM
 */
public interface SongsCallback {
    void onSuccess(List<Song> songs);
    void onError(String error);
}
