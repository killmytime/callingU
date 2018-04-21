package com.fudan.helper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leiwe on 2018/3/30.
 * Thank you for reading, everything gonna to be better.
 */

public class DataCheck {
    public static int level;
    /**
     * @param str check the ratify code
     * @return
     */
    public static boolean hasOKLength(String str) {
        if (str.length() != 4) {
            return false;
        }
        return true;
    }


    /**
     * 暂时只支持大陆的号码
     *
     * @param s check phone number,for a ratify code,check can be easier
     * @return
     */
    public static boolean isPhoneNum(String s) {
        return s.matches("^1[0-9]{10}$");
    }

    public static boolean isBuser(String num){
     HttpConnector.checkLevel(num, new HttpListener() {
         @Override
         public void onHttpFinish(int state, String responseData) throws JSONException {
             if (state==-1){
             }
             else {
                 JSONObject jsonObject=new JSONObject(responseData);
                 level=jsonObject.getInt("level");
             }
         }
     });
     return level==2;
    }
}
