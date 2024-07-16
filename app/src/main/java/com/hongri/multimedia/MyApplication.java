package com.hongri.multimedia;

import android.app.Application;
import android.content.Context;
import android.media.AudioFormat;
import android.util.Log;

import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.state.RecordConfig;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class MyApplication extends Application {
    private static Context mContext;
    private static MyApplication myApplication;
    public static MyApplication getInstance(){
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        myApplication=this;

    }
    public static  Context getmContext(){
        return mContext;
    }

}
