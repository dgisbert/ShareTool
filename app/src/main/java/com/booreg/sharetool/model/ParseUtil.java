package com.booreg.sharetool.model;

import android.location.Location;
import android.util.Log;

import com.booreg.common.android.BackgroundTaskListener;
import com.booreg.common.android.LocationUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
        ToolSearchOrder                    toolSearchOrder;
        Location                           location;

        @Override
        public void done(List<Tool> toolList, ParseException e)
        {
            if (toolList != null)
            {
                try
                {
                    Tool.unpinAll();
                    Tool.pinAll(Tool.class.getSimpleName(), toolList);

                    // For each tool, we calculate the distance to our position, if it has bee given

                    if (location != null)
                    {
                        for (Tool tool:toolList)
                        {
                            ParseGeoPoint parseGeoPoint = tool.getPosi();

                            float dist = location.distanceTo(LocationUtil.getLocation(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude())) / 1000;

                            tool.setDist(dist);
                        }
                    }

                    // Results are ordered after data retrieval by price or distance

                    Set<Tool> set = null;

                    // Order

                    switch(toolSearchOrder)
                    {
                        case BY_PRIC_ASC : set = new TreeSet<>(new Tool.OrderByPricComparator());      break;
                        case BY_PRIC_DESC: set = new TreeSet<>(new Tool.OrderByPricComparator(true));  break;
                        case BY_DIST_ASC : set = new TreeSet<>(new Tool.OrderByDistComparator());      break;
                        case BY_DIST_DESC: set = new TreeSet<>(new Tool.OrderByDistComparator(true));  break;
                    }

                    set.addAll(toolList);

                    if (backgroundTaskListener != null) backgroundTaskListener.onFinish(new ArrayList<>(set));
                }
                catch (ParseException e2)
                {
                    e2.printStackTrace();
                }
            }
        }

        public ParseCallbackTool(BackgroundTaskListener<List<Tool>> backgroundTaskListener, ToolSearchOrder toolSearchOrder, Location location)
        {
            this.backgroundTaskListener = backgroundTaskListener;
            this.toolSearchOrder        = toolSearchOrder;
            this.location               = location;
        }
    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Retrieves tools from Parse given the filters specified by parameters
     */

    public static void searchTools(BackgroundTaskListener<List<Tool>> backgroundTaskListener, String text, Number dist, Number pric, Location location, ToolSearchOrder toolSearchOrder)
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

        query.findInBackground(new ParseCallbackTool(backgroundTaskListener, toolSearchOrder, location));
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
