package test;

import android.app.Application;

/**
 * Created by dubo on 2017/9/9.
 */

public class MyApplication extends Application {
    public static String weiXinFlag;//微信相关标识码
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String getWeiXinFlag() {
        return weiXinFlag;
    }

    public static void setWeiXinFlag(String weiXinFlag) {
        MyApplication.weiXinFlag = weiXinFlag;
    }
}
