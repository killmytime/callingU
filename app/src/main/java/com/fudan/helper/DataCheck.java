package com.fudan.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leiwe on 2018/3/30.
 * Thank you for reading, everything gonna to be better.
 */

public class DataCheck {
    /**
     *
     * @param str check the ratify code
     * @return
     */
    public static boolean hasOKLength(String str){
        if (str.length()!=4){
            return false;
        }
        return true;
    }

    /**
     *
     * @param s checkSpecial symbol
     * @return
     */
    public static boolean hasSpecialCharacter(String s){
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        int i;
        char c;
        for (i=0;i<4;i++){
            c=s.charAt(i);
            if (!( (c>='0')&&(c<='9')|| (c>='A')&&(c<='Z')|| (c>='a')&&(c<='z') ) ){
                return false;
            }
        }
        return true;
    }

    /**可能还是需要改一改，基本前端的check还是可以优化一下的，现在就先不搞了
     * @param s check phone number,for a ratify code,check can be easier
     * @return
     */
    public static boolean isPhoneNum(String s){
        // the length should be valid
        if (s == null || s.trim().isEmpty() || s.length()!=11)
            return false;

        // s should have no dot (.)
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        if (b == true)
            return false; // Should not have other character than number, so false if there is
        else
            return true;
    }
}
