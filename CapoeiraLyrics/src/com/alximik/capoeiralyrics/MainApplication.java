package com.alximik.capoeiralyrics;

import android.app.Application;
import android.content.Context;
import com.alximik.capoeiralyrics.db.DatabaseHelper;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 4:25 PM
 */
public class MainApplication extends Application {

    private static MainApplication instance;

    private static boolean updateAsked = false;

    public MainApplication() {
        instance = this;

        // create database
        //DatabaseHelper.copyDatabaseFromAssets(getContext());
    }

    public static Context getContext() {
        return instance;
    }

    public static boolean isUpdateAsked() {
        return updateAsked;
    }

    public static void setUpdateAsked(boolean updateAsked) {
        MainApplication.updateAsked = updateAsked;
    }
}
