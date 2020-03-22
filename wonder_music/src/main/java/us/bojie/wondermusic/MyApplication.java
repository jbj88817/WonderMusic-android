package us.bojie.wondermusic;

import android.app.Application;

import us.bojie.lib_audio.mediaplayer.app.AudioHelper;


public class MyApplication extends Application {

    private static MyApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //音频SDK初始化
        AudioHelper.init(this);
    }

    public static MyApplication getInstance() {
        return mApplication;
    }
}
