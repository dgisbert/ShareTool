package com.booreg.sharetool.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Locale;

/**
 * Controller class for Search activity of this application. In this case, the Login screen.
 */

public class SearchActivity extends Activity
{
    private Spinner     orderList;
    private TextView    longitude;
    private TextView    latitude;
    private ListView    toolListView;

    private EditText    searchText;
    private EditText    dist;
    private EditText    pric;
    private TextView    messagePleaseWait;

    private ToolSearchOrder[] toolSearchOrderValues = ToolSearchOrder.values();

    private Location currentLocation;

    private boolean enteringToActivity = true;

    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    private class YourPositionClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            setCurrentPosition();
        }
    }

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
        public void onLocationChanged(Location location)
        {
            setCurrentLocation(location);
        }

        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled (String provider) {}
        @Override public void onProviderDisabled(String provider) {}
    }

    private class OnToolListRetrieved implements BackgroundTaskListener<List<Tool>>
    {
        @Override
        public void onFinish(List<Tool> toolList)
        {
            ToolListAdapter toolListAdapter  = new ToolListAdapter(SearchActivity.this, toolList);

            toolListView.setAdapter(toolListAdapter);
            toolListView.setOnItemClickListener(toolListAdapter);

            messagePleaseWait.setVisibility(View.GONE);
            toolListView.setVisibility(View.VISIBLE);
        }
    }

    private class OrderListSelectionListener implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            if (!enteringToActivity) searchTools(toolSearchOrderValues[position]);
            enteringToActivity = false;
        }

        @Override public void onNothingSelected(AdapterView<?> parent) {}
    }

    private void searchTools(ToolSearchOrder toolSearchOrder)
    {
        Number dist = null;
        Number pric = null;

        String distString = this.dist.getText().toString();
        String pricString = this.pric.getText().toString();

        if (!StringUtils.isBlank(distString)) dist = NumberUtils.createNumber(distString);
        if (!StringUtils.isBlank(pricString)) pric = NumberUtils.createNumber(pricString);

        toolListView.setVisibility(View.GONE);
        messagePleaseWait.setVisibility(View.VISIBLE);

        ParseUtil.searchTools(new OnToolListRetrieved(), searchText.getText().toString(), dist, pric, currentLocation, toolSearchOrder);
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

        orderList.setOnItemSelectedListener(new OrderListSelectionListener());

        ImageButton yourPosition = (ImageButton) findViewById(R.id.yourPosition);
        yourPosition.setOnClickListener(new YourPositionClickListener());

        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new SearchButtonClickListener());
    }

    /**
     * Moves the camera map to the specified location
     */

    private void setCurrentLocation(Location currentLocation)
    {
        this.currentLocation = currentLocation;

        Locale locale = Locale.getDefault();

        this.latitude .setText(String.format(locale, "%.3f", currentLocation.getLatitude()));
        this.longitude.setText(String.format(locale, "%.3f", currentLocation.getLongitude()));
    }

    /**
     * Asks the system Location Service to set the current GPS position.
     */

    private void setCurrentPosition()
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

                // Once positioned we ask to update the map to the current position

                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new CurrentLocationListener(), null);
            }
            else
            {
                Toast.makeText(this, R.string.TXT00004, Toast.LENGTH_LONG).show();
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
}
