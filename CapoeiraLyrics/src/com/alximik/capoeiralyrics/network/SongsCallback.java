package com.alximik.capoeiralyrics.network;

import com.alximik.capoeiralyrics.entities.Song;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:44 PM
 */
public interface SongsCallback {
    void onSuccess(Song[] songs);
    void onError(String error);
}
