package com.booreg.sharetool.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.booreg.common.android.ToastUtil;
import com.booreg.sharetool.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Controller class for Main activity of this application. In this case, the Login screen.
 */

public class MainActivity extends AppCompatActivity
{
    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    private class LoginListener implements LogInCallback
    {
        @Override
        public void done(ParseUser user, ParseException e)
        {
            Context context = MainActivity.this;

            if (e == null)
            {
                Intent intent = new Intent(context, SearchActivity.class);

                startActivity(intent);
            }
            else
            {
                ToastUtil.showLongToast(context, R.string.TXT00014);
            }
        }
    }

    //*****************************************************************************************************************
    // Private section
    //*****************************************************************************************************************

    private void inititializeVisualComponents()
    {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    //*****************************************************************************************************************
    // Protected section
    //*****************************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inititializeVisualComponents();

    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Makes login into the application and goes to search Activity.
     */

    public void loginClick(View view)
    {
        Intent intent = new Intent(this, SearchActivity.class);

        startActivity(intent);

//        EditText username = (EditText) findViewById(R.id.username);
//        EditText password = (EditText) findViewById(R.id.password);
//
//        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LoginListener());
    }
}
