package com.hoperrush.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hoperrush.R;
import com.hoperrush.hockeyapp.ActivityHockeyApp;
import com.hoperrush.core.socket.SocketHandler;

/**
 * Casperon Technology on 11/26/2015.
 */
public class SignInAndSignUp extends ActivityHockeyApp implements View.OnClickListener
{
    private Button Bt_facebook,Bt_signIn,Bt_signUp;
    public static SignInAndSignUp signInAndSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_signup);
        signInAndSignUp=SignInAndSignUp.this;
        initialize();
    }

    private void initialize() {
        Bt_facebook=(Button)findViewById(R.id.signin_signup_facebook_button);
        Bt_signIn=(Button)findViewById(R.id.signin_signup_signin_button);
        Bt_signUp=(Button)findViewById(R.id.signin_signup_signup_button);

        Bt_facebook.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        Bt_signIn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        Bt_signUp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));

        Bt_facebook.setOnClickListener(this);
        Bt_signIn.setOnClickListener(this);
        Bt_signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==Bt_facebook)
        {

        }
        else if(v==Bt_signIn)
        {
            SocketHandler.getInstance(this).getSocketManager().connect();
            Intent intent=new Intent(SignInAndSignUp.this,LogInPage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        else if(v==Bt_signUp)
        {
            Intent intent=new Intent(SignInAndSignUp.this,RegisterPage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    }
}
