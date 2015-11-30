package com.booreg.sharetool.activities;

import android.app.Activity;
import android.os.Bundle;

import com.booreg.sharetool.R;

/**
 * Controller class for Tool activity of this application. In this case, the Login screen.
 */

public class ToolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
    }
}
