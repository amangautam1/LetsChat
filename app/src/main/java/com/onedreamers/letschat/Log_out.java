package com.onedreamers.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
public class Log_out extends AppCompatActivity {
    Button button;
    Intent intent;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        button=(Button)findViewById(R.id.btLogIn);
        mainActivity=new MainActivity();
       intent=new Intent(Log_out.this,MainActivity.class);
      /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Start sign in/sign up activity
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER,
                                            AuthUI.FACEBOOK_PROVIDER)
                                    .build(),
                            SIGN_IN_REQUEST_CODE);
                    startActivity(intent);
                }
                else {
                    // User is already signed in, show list of messages

                    startActivity(intent);
                }
            }
        });*/



button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {





    }
});


    }




}
