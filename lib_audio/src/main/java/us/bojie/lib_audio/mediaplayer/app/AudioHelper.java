package us.bojie.lib_audio.mediaplayer.app;

import android.content.Context;

/*
 * @function 唯一与外界通信的帮助类
 */
public final class AudioHelper {

    //SDK全局Context, 供子模块用
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        //初始化本地数据库
    }

    public static Context getContext() {
        return mContext;
    }
}
