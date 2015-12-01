package com.booreg.sharetool.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.booreg.common.android.ToastUtil;
import com.booreg.sharetool.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Controller class for first application screen: login
 */

public class LoginActivity extends AppCompatActivity
{
    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    /**
     * Listener class called when login has finished
     */

    private class LogInCallbackListener implements LogInCallback
    {
        @Override
        public void done(ParseUser parseUser, ParseException e)
        {
            Context context = LoginActivity.this;

            if (e == null) goToSearchActivity(context);
            else ToastUtil.showLongToast(context, R.string.TXT00022, e.getMessage());
        }
    }

    /**
     * Goes to starting activity
     */

    private void goToSearchActivity(Context context)
    {
        Intent intent = new Intent(context, SearchActivity.class);

        startActivity(intent);
    }

    //*****************************************************************************************************************
    // Protected section
    //*****************************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If exists a current session it will take the current Parse user.

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) goToSearchActivity(this);
    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Creates a Parse session.
     */

    public void loginClick(View view)
    {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallbackListener());
    }

    /**
     * Goes to account creation activity
     */

    public void createAccountClick(View view)
    {
        Intent intent = new Intent(this, CreateAccountActivity.class);

        startActivity(intent);
    }
}
