package com.booreg.sharetool.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.booreg.common.android.BackgroundTaskListener;
import com.booreg.common.android.LocationUtil;
import com.booreg.sharetool.R;
import com.booreg.sharetool.adapters.ToolListAdapter;
import com.booreg.sharetool.model.ParseUtil;
import com.booreg.sharetool.model.Tool;
import com.booreg.sharetool.model.ToolSearchOrder;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Controller class for Search activity of this application. In this case, the Login screen.
 */

public class SearchActivity extends AppCompatActivity
{
    private Spinner     orderList;
    private TextView    longitude;
    private TextView    latitude;
    private ListView    toolListView;

    private EditText    searchText;
    private EditText    dist;
    private EditText    pric;
    private TextView    messagePleaseWait;
    private TextView    noToolsFound;

    private ToolSearchOrder[] toolSearchOrderValues = ToolSearchOrder.values();
    private ToolSearchOrder currentToolSearchOrder = toolSearchOrderValues[0];

    private Location currentLocation;

    private boolean enteringToActivity = true;

    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    private class SearchButtonClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            searchTools(toolSearchOrderValues[orderList.getSelectedItemPosition()]);
        }
    }

    /**
     * Listener class that gets the current Location position and shows the data on screen
     */

    private class CurrentLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) { setCurrentLocation(location); }

        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled (String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    }

    /**
     * Listener class that shows the retrieved tool data on the screen and calculates the distance of each item.
     */

    private class OnToolListRetrieved implements BackgroundTaskListener<List<Tool>>
    {
        ToolSearchOrder                    toolSearchOrder;
        Location                           location;

        @Override
        public void onFinish(List<Tool> toolList)
        {
            populateToolList(SearchActivity.this, getToolListWithDistances(toolList, location, toolSearchOrder));
        }

        /**
         * Constructor
         */

        public OnToolListRetrieved(ToolSearchOrder toolSearchOrder, Location location)
        {
            this.toolSearchOrder = toolSearchOrder;
            this.location        = location;
        }
    }

    /**
     * Listener class activated when user changes the order by spinner option
     */

    private class OrderListSelectionListener implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            currentToolSearchOrder = toolSearchOrderValues[position];

            if (!enteringToActivity) searchTools(currentToolSearchOrder);
            enteringToActivity = false;
        }

        @Override public void onNothingSelected(AdapterView<?> parent) {}
    }

    //*****************************************************************************************************************
    // Private section
    //*****************************************************************************************************************

    private void inititializeVisualComponents()
    {
        orderList         = (Spinner)     findViewById(R.id.orderList);
        longitude         = (TextView)    findViewById(R.id.longitude);
        latitude          = (TextView)    findViewById(R.id.latitude);
        toolListView      = (ListView)    findViewById(R.id.toolListView);

        searchText        = (EditText)    findViewById(R.id.searchText);
        dist              = (EditText)    findViewById(R.id.dist);
        pric              = (EditText)    findViewById(R.id.pric);

        messagePleaseWait = (TextView) findViewById(R.id.messagePleaseWait);
        noToolsFound      = (TextView) findViewById(R.id.noToolsFound);

        orderList.setOnItemSelectedListener(new OrderListSelectionListener());

        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new SearchButtonClickListener());
    }

    /**
     * Search for tools with the given user parameters. At the same time it tries to get the user position.
     */

    private void searchTools(ToolSearchOrder toolSearchOrder)
    {
        // Gets the current position in background

        getCurrentPosition();

        // Executes the tool search

        Number dist = null;
        Number pric = null;

        String distString = this.dist.getText().toString();
        String pricString = this.pric.getText().toString();

        if (!StringUtils.isBlank(distString)) dist = NumberUtils.createNumber(distString);
        if (!StringUtils.isBlank(pricString)) pric = NumberUtils.createNumber(pricString);

        toolListView.setVisibility(View.GONE);
        noToolsFound.setVisibility(View.GONE);
        messagePleaseWait.setVisibility(View.VISIBLE);

        ParseUtil.searchTools(new OnToolListRetrieved(toolSearchOrder, currentLocation), searchText.getText().toString(), dist, pric, currentLocation);
    }

    /**
     * Recalculates the distance of each tool element taking as a base the given Location. The given location can be null.
     * Returns the list of tools ordered depending of the given ToolSearchOrder
     */

    private List<Tool> getToolListWithDistances(List<Tool> toolList, Location location, ToolSearchOrder toolSearchOrder)
    {
        List<Tool> result = toolList;

        // Before populating the tool list obtained, we calculate the distance of each tool.
        // For each tool, we calculate the distance to our position, if it has bee given

        if (location != null)
        {
            for (Tool tool:toolList)
            {
                ParseGeoPoint parseGeoPoint = tool.getPosi();

                float dist = location.distanceTo(LocationUtil.getLocation(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude())) / 1000;

                tool.setDist(dist);
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

            result = new ArrayList<>(set);
        }

        return result;
    }

    /**
     * Populates the data of the listview. This method is synchronized to avoid concurrency problems when this method is called from
     * setCurrentLocation Listener or after retrieving tool searched data
     */

    private synchronized void populateToolList(Context context, List<Tool> toolList)
    {
        // Populate the list on the screen

        ToolListAdapter toolListAdapter  = new ToolListAdapter(context, toolList);

        toolListView.setAdapter(toolListAdapter);
        toolListView.setOnItemClickListener(toolListAdapter);

        messagePleaseWait.setVisibility(View.GONE);

        if (toolList.size() == 0) noToolsFound.setVisibility(View.VISIBLE);
        else toolListView.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the user current location
     */

    private void setCurrentLocation(Location currentLocation)
    {
        this.currentLocation = currentLocation;

        Locale locale = Locale.getDefault();

        this.latitude .setText(String.format(locale, "%.3f", currentLocation.getLatitude()));
        this.longitude.setText(String.format(locale, "%.3f", currentLocation.getLongitude()));

        // We must recalculate here the distance of showed items (if they are visible)

        ListAdapter adapter = toolListView.getAdapter();

        if (adapter != null)
        {
            populateToolList(this, getToolListWithDistances(((ToolListAdapter) adapter).getToolList(), currentLocation, currentToolSearchOrder));
        }
    }

    /**
     * Asks the system Location Service to get the current GPS position.
     */

    private void getCurrentPosition()
    {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
            if (LocationUtil.hasGPS(this) && LocationUtil.isGPSEnabled(this))
            {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Initially we take the last known GPS position. If it's not known, takes to the center of the Universe.

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if   (location == null) setCurrentLocation(LocationUtil.getCenterOfTheUniverseLocation());
                else                    setCurrentLocation(location);

                // Gets the current position of the user

                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new CurrentLocationListener(), null);
            }
            else
            {
                Toast.makeText(this, R.string.TXT00037, Toast.LENGTH_LONG).show();
            }
        }
    }

    //*****************************************************************************************************************
    // Protected section
    //*****************************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        inititializeVisualComponents();

        // Initialize orderList spinner

        orderList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ToolSearchOrder.values()));
    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Sets the current position of the user
     */

    public void setMyPosition(View view)
    {
        getCurrentPosition();
    }

    /**
     * Responds to menu buttons
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.logout: ParseUser.logOut();
                finish();
                break;

            default: break;
        }

        return super.onOptionsItemSelected(item);
    }

}
