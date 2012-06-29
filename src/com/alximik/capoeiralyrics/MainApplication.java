package com.alximik.capoeiralyrics;

import android.app.Application;
import android.content.Context;
import greendroid.app.GDApplication;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 4:25 PM
 */
public class MainApplication extends Application {

    private static MainApplication instance;

    public MainApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
}
