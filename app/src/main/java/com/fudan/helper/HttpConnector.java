package com.fudan.helper;

import android.os.AsyncTask;
import android.os.Handler;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by leiwe on 2018/3/11.
 * Thank you for reading, everythi
 * ng gonna to be better.
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


        public HttpTask(HttpListener listener,Request request,boolean flag){
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
                mListener.onHttpFinish(-1,"");
            }else {
                mListener.onHttpFinish(1,responseData);
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            mListener.onHttpFinish(-1,"");
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

    public static void getKey(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("get-key"))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }
    /** BC
     * use POST to register
     * @param number
     * @param ciphertext
     * @param listener
     */
    public static void register(String number,String ciphertext,HttpListener listener){
        RequestBody requestBody=new FormBody.Builder()
                .add("number",number)
                .add("ciphertext",ciphertext)
                .build();
        Request request=new Request.Builder()
                .url(formatURL("register"))
                .post(requestBody)
                .build();
        final HttpTask s=new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to login
     * 这里timestamp可能有点问题，后期问询一下
     * @param number
     * @param timestamp
     * @param ciphertext
     * @param listener
     */
    public static void login(String number, String timestamp, String ciphertext, HttpListener listener ){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .add("timestamo",timestamp)
                .add("ciphertext",ciphertext)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("login"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to trigger the verification system.
     * @param number
     * @param listener
     */
    public static void getRatify(String number, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("ratify"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use POST to send feedback message.
     * @param number
     * @param plaintext
     * @param listener
     */
    public static void myFeedback(String number,String plaintext, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",number)
                .add("plaintext",plaintext)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("feedback"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use GET to check new version.
     * @param i
     * @param listener
     */
    public static void checkNew(int i,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("checkNew?version="+i))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** BC
     * use GET to download the new version.
     * @param listener
     */
    public static void downloadNew(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("downloadNew"))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /** 这个是有用的em
     * use GET to download the new version.
     * @param listener
     */
    public static void loadPic(HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("loadPic"))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /**
     * use POST to send data of location and sos
     * @param type
     * @param num
     * @param target
     * @param sos
     * @param lat
     * @param lng
     * @param listener
     */
    public static void sendLocation(int type, String num, String target, int sos, double lat, double lng, HttpListener listener ) {
        RequestBody requestBody = new FormBody.Builder()
                .add("type",type+"")
                .add("number",num)
                .add("target",target)
                .add("sos",sos+"")
                .add("latitude",lat+"")
                .add("longitude",lng+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("UpLocation"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /**
     * upload my location, and down the information which I need
     * @param task: value 0 if in service, value 1 if I want to download all the sos,
     *             value 2 if I want to download detail of one sos.
     * @param type
     * @param num
     * @param target
     * @param sos
     * @param lat
     * @param lng
     * @param listener
     */
    public static void downInformation(int task, int type, String num, String target, int sos, double lat, double lng, HttpListener listener ) {
        String req;
        if (task==0){
            req="BackUp?";
        }
        else if(task==1) {
            req="DownAll?";
        }
        else {
            req="DownOne?";
        }
        RequestBody requestBody = new FormBody.Builder()
                .add("type",type+"")
                .add("number",num)
                .add("target",target)
                .add("sos",sos+"")
                .add("latitude",lat+"")
                .add("longitude",lng+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL(req))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /** C
     * use POST to trigger the verification system.
     * @param num
     * @param message
     * @param listener
     */
    public static void setMessage(String num, String message,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("message",message)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("setMessage"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /**
     *  use POST to send feedback message.
     * @param num
     * @param target
     * @param phoneState
     * @param listener
     */
    public static void reportPhoneState(String num,String target, int phoneState, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .add("phoneState",phoneState+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("reportPhoneState"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /**BC
     * check the state of user B&C
     * @param num
     * @param target
     * @param myState
     * @param phoneState
     */
    public static void reportMyStateChanged(String num, String target, int myState,int phoneState ,HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .add("myState",myState+"")
                .add("phoneState",phoneState+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("reportMyStateChanged"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * report other's bad behavior to the backend
     * @param num
     * @param target
     * @param listener
     */
    public static void reportWrong(String num,String target, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("reportWrong"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** C
     * check whether being warned
     * @param num
     * @param listener
     */
    public static void checkWrong(String num,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("checkWrong?num="+num))
                .build();
        final HttpTask s = new HttpTask(listener,request,false);
        executeTask(s);
    }

    /** B
     * report that you have finished the sos help
     * @param num
     * @param target
     * @param listener
     */
    public static void reportFinish(String num,String target, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("number",num)
                .add("target",target)
                .build();
        Request request = new Request.Builder()
                .url(formatURL("reportFinish"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * get the your total score from the backend
     * @param num
     * @param listener
     */
    public static void getScore(String num,HttpListener listener){
        Request request = new Request.Builder()
                .url(formatURL("getScore?num="+num))
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    /** B
     * add your score
     * @param num
     * @param score
     * @param listener
     */
    public static void addScore(String num,int score, HttpListener listener){
        RequestBody requestBody = new FormBody.Builder()
                .add("num",num)
                .add("score",score+"")
                .build();
        Request request = new Request.Builder()
                .url(formatURL("addScore"))
                .post(requestBody)
                .build();
        final HttpTask s = new HttpTask(listener,request,true);
        executeTask(s);
    }

    static String formatURL(String command){
        //return "http://118.89.104.204:8888/" + command;
        //return "http://100.1.19.33:8888/" + command;
        //return "http://192.168.31.83:8888/" + command;
        return "http://118.89.111.214:8008/" + command;
    }
}
