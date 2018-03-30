package com.fudan.helper;

/**
 * Created by leiwe on 2018/3/11.
 * Thank you for reading, everything gonna to be better.
 */

public interface HttpListener {
    /**
     * It would be triggered if HttpTask finished.
     * @param state : values 1 if succeed, -1 if failed.
     * @param responseData : response data from the server.
     */
    void onHttpFinish(int state, String responseData);
}
