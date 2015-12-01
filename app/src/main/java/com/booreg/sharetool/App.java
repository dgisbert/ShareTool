package com.booreg.sharetool;

import android.app.Application;
import android.content.Context;

import com.booreg.sharetool.model.Tool;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * ShareTool Application class
 */

public class App extends Application
{
    public static final String TOOL_OBJECT = "TOOL_OBJECT";

    private static final String APPLICATION_ID = "2QePHKyrZMo0v7Q6grxWepfQxUqaSqbx0AMAAExI";
    private static final String CLIENT_ID = "iJU5W1WBweD8k9CQOfkTL3d6AIE2dg2eWl9Ay017";

    private static App instance;

    @Override
    public void onCreate()
    {
        super.onCreate();

        instance = this;

        // Parse initialization

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Tool.class);
        Parse.initialize(this, APPLICATION_ID, CLIENT_ID);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    //*****************************************************************************************************************
    // Public classes
    //*****************************************************************************************************************

    /**
     * Returns a static object representing the application
     */

    public static App getInstance() { return instance; }

    /**
     * Returns a static object representing the context of the application
     */

    public static Context getContext() { return instance.getApplicationContext(); }
}
