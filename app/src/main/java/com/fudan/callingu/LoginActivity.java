package com.fudan.callingu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fudan.helper.BaseActivity;
import com.fudan.helper.HttpConnector;
import com.fudan.helper.HttpListener;
import com.fudan.helper.MyClickableSpan;

import org.json.JSONException;

import okhttp3.Headers;

import static com.fudan.helper.DataCheck.*;


/**
 * Created by FanJin on 2017/1/18.
 */
//ToDo 使用条例暂时好像没有欸，要更新
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    EditText login_num,login_code;
    Button login_submit,back;
    public static Button get_ratify;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TimeCount time;
    private TextView tv;
    private SpannableString str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * check the user state. Skip this activity if isOnline.
         */
        pref= getSharedPreferences("loginStatus",MODE_PRIVATE);
        boolean isOnline=pref.getBoolean("isOnline",false);
        if (isOnline){
            loginFinish();
        }

        if (Build.VERSION.SDK_INT >=21){
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorMain));
        }

        str=new SpannableString(getResources().getString(R.string.login_2));
        MyClickableSpan clickableSpan = new MyClickableSpan(LoginActivity.this);
        ForegroundColorSpan colorSpanA = new ForegroundColorSpan(getResources().getColor(R.color.colorMain));
        str.setSpan(colorSpanA, 16, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str.setSpan(clickableSpan, 16, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setContentView(R.layout.login);
        tv = (TextView) findViewById(R.id.login_tv2);
        tv.setText(str);



        login_num=(EditText) findViewById(R.id.login_num);
        login_code=(EditText) findViewById(R.id.login_code);
        login_submit=(Button) findViewById(R.id.login_submit);
        back=(Button)findViewById(R.id.back_login);
        get_ratify=(Button) findViewById(R.id.get_ratify);
        time = new TimeCount(60000, 1000);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationBoxes()){
                    HttpConnector.login(login_num.getText().toString(), login_code.getText().toString(),
                            new HttpListener() {
                                @Override
                                public void onHttpFinish(int state, String responseData) {
                                    if (state==-1){
                                        Log.e(TAG, "onHttpFinish: -------out time" );
                                        loginError();
                                        Toast.makeText(LoginActivity.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
                                    }else {
                                        if (responseData.equals("")){
                                            editor=pref.edit();
                                            editor.putBoolean("isOnline",true);
                                           // editor.putString("name",login_name.getText().toString());
                                            editor.putString("number",login_num.getText().toString());
                                            editor.apply();
                                            loginFinish();
                                        }else {

                                            Toast.makeText(LoginActivity.this,responseData,Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "onHttpFinish: ---------wrong identify code");
                                            loginError();
                                        }
                                    }
                                }
                            });
                }
            }
        });
        get_ratify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_num.setError(null);
                String snum= login_num.getText().toString();
                View focusView = null;
                /**
                 * cancel : true if there is invalid message.
                 * the value of 'cancel' can conduct View.requestFocus().
                 */
                boolean cancel = false;

                if (TextUtils.isEmpty(snum)){
                    login_num.setError("此项不能为空！");
                    focusView = login_num;
                    cancel = true;
                } else if (!isPhoneNum(snum)){
                    login_num.setError("暂时只支持中国大陆手机号码！");
                    focusView = login_num;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    time.start();// 开始计时
                    HttpConnector.identifyCode(snum,new HttpListener() {
                        @Override
                        public void onHttpFinish(int state, String responseData) {
                            if (state == -1){
                                Toast.makeText(LoginActivity.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this,responseData,Toast.LENGTH_SHORT).show();
                                //
                            }
                        }
                    });
//                    HttpConnector.getKey(new HttpListener() {
//                        @Override
//                        public void onHttpFinish(int state, String responseData) throws JSONException {
//                            if (state==-1){
//                                Toast.makeText(LoginActivity.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(LoginActivity.this,responseData,Toast.LENGTH_SHORT).show();
//                                //
//                            }
//                        }
//                    });
                }
            }
        });
    }

    /**
     * check every boxes
     */
    private boolean validationBoxes(){
        /**
         * reset errors
         */
        login_num.setError(null);
        login_code.setError(null);

        /**
         * Store values at the time of the login attempt.
         */
        String snum= login_num.getText().toString();
        String spwd = login_code.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         * check every boxes
         */
        // Passwords
        if (TextUtils.isEmpty(spwd)){
            login_code.setError("此项不能为空！");
            focusView = login_code;
            cancel = true;
        } else if (!hasOKLength(spwd)){
            login_code.setError("请输入4位验证码！");
            focusView = login_code;
            cancel = true;
        }

        // Phone number
        if (TextUtils.isEmpty(snum)){
            login_num.setError("此项不能为空！");
            focusView = login_num;
            cancel = true;
        } else if (!isPhoneNum(snum)){
            login_num.setError("不存在此手机号码！");
            focusView = login_num;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * jump to MainActivityC if successfully login
     */
    private void loginFinish(){
        Intent intent=new Intent(LoginActivity.this,MainActivityC.class);
        startActivity(intent);
        finish();
    }

    private void loginError(){
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage("登录失败，请输入正确的验证码")
                .setPositiveButton("确定", null)
                .create()
                .show();
    }



}

/**
 * for identifyCode Button's time counting
 */
class TimeCount extends CountDownTimer {

    public TimeCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    @Override
    public void onFinish() {
        // TimeCount finishes.
        LoginActivity.get_ratify.setText("获取验证码");
        LoginActivity.get_ratify.setClickable(true);
        LoginActivity.get_ratify.setBackgroundResource(R.drawable.ratify_press_normal);
    }

    @Override
    public void onTick(long millisUntilFinished) {// being counting now
        LoginActivity.get_ratify.setClickable(false);
        LoginActivity.get_ratify.setText(millisUntilFinished / 1000 + "秒后可以重新发送");
        LoginActivity.get_ratify.setBackgroundResource(R.drawable.ratify_press_fobid);
    }
}
