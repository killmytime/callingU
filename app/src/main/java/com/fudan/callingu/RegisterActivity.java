package com.fudan.callingu;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fudan.helper.BaseActivity;
import com.fudan.helper.MyClickableSpan;

/**
 * Created by leiwe on 2018/3/13.
 * Thank you for reading, everything gonna to be better.
 */

public class RegisterActivity extends BaseActivity{
    private static final String TAG="RegisterActivity";
    EditText register_name,register_num,register_pwd,register_confirm_pwd;
    Button register_submit,register_sign;
    public static Button get_ratify;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TimeCount time;
    private TextView tv;
    private SpannableString str;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View view=getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMain));
        }


        str=new SpannableString(getResources().getString(R.string.register_2));
        MyClickableSpan clickableSpan=new MyClickableSpan(RegisterActivity.this);
        ForegroundColorSpan colorSpanA=new ForegroundColorSpan(getResources().getColor(R.color.colorMain));
        str.setSpan(colorSpanA,16,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(clickableSpan,16,20,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        /**
         * check the user state. Skip this activity if isOnline
         */
        pref=getSharedPreferences("loginStatus",MODE_PRIVATE);
        boolean isOnline=pref.getBoolean("isOnline",false);
        if (isOnline){
            //\loginFinsh();
        }
        setContentView(R.layout.register);

    }
}

