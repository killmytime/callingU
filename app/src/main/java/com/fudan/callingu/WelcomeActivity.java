package com.fudan.callingu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fudan.helper.BaseActivity;

/**
 * Created by leiwe on 2018/3/30.
 * Thank you for reading, everything gonna to be better.
 */

public class WelcomeActivity extends BaseActivity {
    private static final String TAG="WelcomeActivity";
    private SharedPreferences pref;
    Button tryOut,login,weChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
          check the user state. Skip this activity if isOnline.
         */
        pref= getSharedPreferences("loginStatus",MODE_PRIVATE);
        boolean isOnline=pref.getBoolean("isOnline",false);
        if (isOnline){
            Intent intent=new Intent(WelcomeActivity.this,MainActivityC.class);
            startActivity(intent);
            finish();
        }
        if (Build.VERSION.SDK_INT >=21){
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMain));
        }

        setContentView(R.layout.welcome);


        tryOut=(Button)findViewById(R.id.try_welcome);
        login=(Button)findViewById(R.id.login_welcome);
        weChat=(Button)findViewById(R.id.weChat_welcome);
        tryOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo gengerate a temporary account
                Intent intentTryOut=new Intent(WelcomeActivity.this,MainActivityC.class);
                startActivity(intentTryOut);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLoginActivity=new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intentLoginActivity);
            }
        });
        weChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWeChat=new Intent(WelcomeActivity.this,MainActivityB.class);
                startActivity(intentWeChat);
                finish();
            }
        });
}}
