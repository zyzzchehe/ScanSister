package alpha.cyber.scansister.log;

import android.util.Log;

import alpha.cyber.scansister.BuildConfig;

public class LogUtils {
    private static final String TAG = "LogUtils";
    public static void v(String msg){
        if(BuildConfig.DEBUG){
            Log.v(TAG,msg);
        }
    }
    public static void v(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.v(tag,msg);
        }
    }

    public static void d(String msg){
        if(BuildConfig.DEBUG){
            Log.d(TAG,msg);
        }
    }
    public static void d(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.d(tag,msg);
        }
    }

    public static void i(String msg){
        if(BuildConfig.DEBUG){
            Log.i(TAG,msg);
        }
    }
    public static void i(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.i(tag,msg);
        }
    }

    public static void w(String msg){
        if(BuildConfig.DEBUG){
            Log.w(TAG,msg);
        }
    }
    public static void w(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.w(tag,msg);
        }
    }

    public static void e(String msg){
        if(BuildConfig.DEBUG){
            Log.e(TAG,msg);
        }
    }
    public static void e(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }
}
