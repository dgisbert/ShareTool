package com.booreg.sharetool.activities;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.booreg.common.android.ToastUtil;
import com.booreg.sharetool.R;
import com.booreg.sharetool.model.Tool;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.Locale;

/**
 * Controller class for Tool activity of this application. In this case, the Login screen.
 */

public class ToolActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        Locale locale = Locale.getDefault();

        // To retrieve object in second Activity
        Tool tool = EventBus.getDefault().removeStickyEvent(Tool.class);

        if (tool != null)
        {
            ParseGeoPoint posi = tool.getPosi();

            ((TextView) findViewById(R.id.toolName)).setText(tool.getName());
            ((TextView) findViewById(R.id.toolDscr)).setText(tool.getDscr());
            ((TextView) findViewById(R.id.toolPric)).setText(String.format(locale, "%.2f", tool.getPric().doubleValue()));
            ((TextView) findViewById(R.id.toolCity)).setText(tool.getCity());
            ((TextView) findViewById(R.id.toolDist)).setText(String.format(locale, "%.2f", tool.getDist().doubleValue()));
            ((TextView) findViewById(R.id.toolLat)).setText(String.format(locale, "%.3f", posi.getLatitude()));
            ((TextView) findViewById(R.id.toolLng)).setText(String.format(locale, "%.3f", posi.getLongitude()));

            try
            {
                ((TextView) findViewById(R.id.toolOwidName)).setText(tool.getOwid().fetchIfNeeded().getUsername());
            }
            catch (ParseException e)
            {
                ToastUtil.showLongToast(this, R.string.TXT00034, e.getMessage());
            }
        }
    }
}
