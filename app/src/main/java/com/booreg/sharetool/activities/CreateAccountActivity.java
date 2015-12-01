package com.booreg.sharetool.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.booreg.common.android.ToastUtil;
import com.booreg.sharetool.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.apache.commons.lang3.StringUtils;

/**
 * Controller class for create account activity
 */

public class CreateAccountActivity extends AppCompatActivity
{
    //*****************************************************************************************************************
    // Private inner classes
    //*****************************************************************************************************************

    /**
     * Listener class called when sign up has finished
     */

    private class SignUpCallbackListener implements SignUpCallback
    {

        @Override
        public void done(ParseException e)
        {
            Context context = CreateAccountActivity.this;

            if (e == null)
            {
                ToastUtil.showLongToast(context, R.string.TXT00023);
                finish();
            }
            else ToastUtil.showLongToast(context, R.string.TXT00022, e.getMessage());
        }
    }

    //*****************************************************************************************************************
    // Protected section
    //*****************************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    //*****************************************************************************************************************
    // Public section
    //*****************************************************************************************************************

    /**
     * Responds to Create account button click creating a new user in Parse
     */

    public void createAccountClick(View view)
    {
        String createAccountUsername        = ((EditText) findViewById(R.id.createAccountUsername)).getText().toString();
        String createAccountEmail           = ((EditText) findViewById(R.id.createAccountEmail)).getText().toString();
        String createAccountPassword        = ((EditText) findViewById(R.id.createAccountPassword)).getText().toString();
        String createAccountConfirmPassword = ((EditText) findViewById(R.id.createAccountConfirmPassword)).getText().toString();

        if      (StringUtils.isBlank(createAccountUsername)) ToastUtil.showLongToast(this, R.string.TXT00018);
        else if (StringUtils.isBlank(createAccountEmail)) ToastUtil.showLongToast(this, R.string.TXT00019);
        else if (StringUtils.isBlank(createAccountPassword)) ToastUtil.showLongToast(this, R.string.TXT00020);
        else if (StringUtils.isBlank(createAccountConfirmPassword)) ToastUtil.showLongToast(this, R.string.TXT00021);
        else
        {
            if (!createAccountPassword.equals(createAccountConfirmPassword)) ToastUtil.showLongToast(this, R.string.TXT00028);
            else
            {
                ParseUser user = new ParseUser();

                user.setUsername(createAccountUsername);
                user.setEmail(createAccountEmail);
                user.setPassword(createAccountPassword);

                user.signUpInBackground(new SignUpCallbackListener());
            }
        }
    }
}
