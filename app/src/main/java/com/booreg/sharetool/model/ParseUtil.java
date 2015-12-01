package com.booreg.sharetool.model;

import android.location.Location;
import android.util.Log;

import com.booreg.common.android.BackgroundTaskListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Offers methods for accessing data through Parse
 */

public class ParseUtil
{
    private static final String TAG = ParseUtil.class.getSimpleName();

    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    private static class ParseCallbackTool implements FindCallback<Tool>
    {
        BackgroundTaskListener<List<Tool>> backgroundTaskListener;

        @Override
        public void done(List<Tool> toolList, ParseException e)
        {
            if (toolList != null)
            {
                try
                {
                    Tool.unpinAll();
                    Tool.pinAll(Tool.class.getSimpleName(), toolList);

                    if (backgroundTaskListener != null) backgroundTaskListener.onFinish(toolList);
                }
                catch (ParseException e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        public ParseCallbackTool(BackgroundTaskListener<List<Tool>> backgroundTaskListener)
        {
            this.backgroundTaskListener = backgroundTaskListener;
        }
    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Retrieves tools from Parse given the filters specified by parameters
     */

    public static void searchTools(BackgroundTaskListener<List<Tool>> backgroundTaskListener, String text, Number dist, Number pric, Location location)
    {
        ParseQuery<Tool> query = ParseQuery.getQuery(Tool.class);

        // Text filter

        if (!StringUtils.isBlank(text))
        {
            ParseQuery<Tool> nameQuery = ParseQuery.getQuery(Tool.class).whereContains(Tool.NAME, text);
            ParseQuery<Tool> dscrQuery = ParseQuery.getQuery(Tool.class).whereContains(Tool.DSCR, text);

            query = ParseQuery.or(Arrays.asList(nameQuery, dscrQuery));
        }

        // Distance filter

        if (dist != null && location != null) query.whereWithinKilometers(Tool.POSI, new ParseGeoPoint(location.getLatitude(), location.getLongitude()), dist.doubleValue());

        // Price filter

        if (pric != null) query.whereLessThanOrEqualTo(Tool.PRIC, pric);

        // User id filter

        query.whereNotEqualTo(Tool.OWID, ParseUser.getCurrentUser());

        query.findInBackground(new ParseCallbackTool(backgroundTaskListener));
    }

    /**
     * Retrieves all tools from local databasse
     */

    public static List<Tool> getAllTools()
    {
        List<Tool> toolList = null;
        ParseQuery<Tool> query = ParseQuery.getQuery(Tool.class);
        query.fromLocalDatastore();
        try
        {
            toolList = query.find();
        }
        catch (ParseException e)
        {
            Log.e(TAG, "ParseException: " + e.getMessage());
        }
        return toolList;
    }
}
