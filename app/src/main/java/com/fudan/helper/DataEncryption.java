package com.fudan.helper;

/**
 * Created by leiwe on 2018/3/30.
 * Thank you for reading, everything gonna to be better.
 */

import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;

import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

/**
 * use for the encryption of data
 */
public class DataEncryption {
    public static String registerDataEncryption(String pwd,String ratifycode) throws NoSuchAlgorithmException {
        String s=getMD5(pwd);
        String m=ratifycode+s;
        String publicKey = null;
        HttpConnector.getKey(new HttpListener() {
            @Override
            public void onHttpFinish(int state, String responseData) {
                if (state==-1){
                    Log.e(TAG, "onHttpFinish: -------out time" );
                    //Toast.makeText(LoginActivity.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
                }else {
                    if (responseData.equals("200")){
                        //
                    }else {
                        Log.e(TAG, "onHttpFinish: ---------wrong pwd");
                        //loginError();
                    }
                }
            }
        });
        return "";
    }
    public static String getMD5(String str) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            String tmp= new BigInteger(1, md.digest()).toString(16);
            System.out.println(tmp);
            return tmp.substring(0,10);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        registerDataEncryption("helloworld","0123");
    }
}
