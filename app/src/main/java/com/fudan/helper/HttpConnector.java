package com.fudan.helper;

import android.os.AsyncTask;
import android.os.Handler;

import org.json.JSONException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by leiwe on 2018/3/11.
 * Thank you for reading, everything gonna to be better.
 */

public class HttpConnector {
    /**
     * HttpTask is an AsyncTask which we can connect the server on another thread.
     * the task could be cancelled if time out.
     * it will trigger a listener when the task stops.
     * how to trigger the listener: send value -1 if getting exception or time out , or send value 1 if successful.
     */
    static class HttpTask extends AsyncTask<Void,Void,Void>
    {
        HttpListener mListener;
        Request request;
        /**
         * flag : refresh the UI if true
         * I am so tired now, so this function will be finished later.
         */
        boolean flag;
        boolean done = false;
        String responseData = "";


        HttpTask(HttpListener listener, Request request, boolean flag){
            this.mListener = listener;
            this.request = request;
            this.flag = flag;
        }

        protected void onPreExecute() {
            if (flag){
                //
            }
        }

        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                responseData = response.body().string();
                done = true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (flag){

            }
            if (! done){
                try {
                    mListener.onHttpFinish(-1,"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    mListener.onHttpFinish(1,responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            try {
                mListener.onHttpFinish(-1,"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * execute the task. the task would be cancelled if time out
     * @param s : a HttpTask
     */
    private static void executeTask(final HttpTask s){
        s.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (! s.done){
                    s.cancel(true);
                }
            }
        },2000);
    }

    /**
     * get public key from the server
     * @param listener listener
     */
    public static void getKey(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("api/get-key"))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to login
     * @param number myNumber
     * @param code   identify code
     * @param listener listener
     */
    public static void login(String number, String code, HttpListener listener ){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .add("code",code)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/login"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to trigger the verification system.
     * @param number myNumber
     * @param listener listener
     */
    public static void identifyCode(String number, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/identify-code"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to send feedback message.
     * @param number myNumber
     * @param plaintext feedback content
     * @param listener listener
     */
    public static void myFeedback(String number,String plaintext, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .add("plaintext",plaintext)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/feedback"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    //ToDo something needs to be changed later
    /** BC
     * use GET to check new version.
     * @param versionNumber versionNumber
     * @param listener listener
     */
    public static void checkNew(int versionNumber,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("api/check-new?number="+versionNumber))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use GET to download the new version.
     * @param listener listener
     */
    public static void downloadNew(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("api/download-new"))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }


    /**
     * use POST to send data of location and sos
     * @param num myNumber
     * @param lat latitude
     * @param lng longitude
     * @param sos state of c
     * @param state state of b&c
     * @param listener listener
     */
    public static void sendLocation( String num, double lat, double lng,int sos,int state, HttpListener listener ) {
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("latitude",lat+"")
                .add("longitude",lng+"")
                .add("sos",sos+"")
                .add("state",state+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/get-help"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /**
     * * upload my location, and down the information which I need
     * @param num myNumber of B
     * @param lat latitude
     * @param lng longitude
     * @param target target number of C
     * @param state  myState of B
     * @param listener listener
     */
    public static void downInformation(String num, double lat, double lng, String target,int state, HttpListener listener ) {
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("latitude",lat+"")
                .add("longitude",lng+"")
                .add("target",target)
                .add("state",state+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/get-details"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /** C
     * use POST to trigger the verification system.
     * @param num  myNumber
     * @param message additional message
     * @param listener listener
     */
    public static void setMessage(String num, String message,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("message",message)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/set-message"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /**B
     *  use POST to send feedback message.
     * @param num myNumber
     * @param target target number
     * @param phoneState if has connect with the target
     * @param listener listener
     */
    public static void reportPhoneState(String num,String target, int phoneState, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .add("phonestate",phoneState+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/report-phone-state"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /**BC
     * check the state of user B&C
     * @param num myNumber
     * @param target target number
     * @param myState myState
     * @param phoneState phoneState
     */
    public static void reportMyStateChanged(String num, String target, int myState,int phoneState ,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .add("state",myState+"")
                .add("phonestate",phoneState+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/report-state-changed"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * report other's bad behavior to the backend
     * @param num myNumber
     * @param target target number
     * @param listener listener
     */
    public static void reportWrong(String num,String target, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/report-wrong"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** C
     * check whether being warned
     * @param num myNumber
     * @param listener listener
     */
    public static void checkWrong(String num,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("api/check-wrong?number="+num))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /** B
     * report that you have finished the sos help
     * @param num myNumber
     * @param target target number
     * @param listener listener
     */
    public static void reportFinish(String num,String target, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/report-finish"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * get the your  score from the backend
     * @param num myNumber
     * @param listener listener
     */
    public static void getScore(String num,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("api/get-score?number="+num))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * add your score
     * @param num myNumber
     * @param score score
     * @param listener listener
     */
    public static void addScore(String num,int score, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("score",score+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("api/add-score"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /**
     * B&C
     * check your current user level
     * @param num myNumber
     * @param listener listener
     */
    public static void checkLevel(String num,HttpListener listener){
        Request request=new Request.Builder()
                .url(formatURL("api/check-level?number="+num))
                .build();
        final HttpTask s=new HttpTask(listener,request,true);
        executeTask(s);
    }

    //ToDo WebSocket connect PushService need to be finished
    /**
     * hhe formulation of URL
     * @param command additional URL element
     * @return the string of URL
     */
    private static String formatURL(String command){
        return "http://118.89.111.214:2333/" + command;
    }
}
