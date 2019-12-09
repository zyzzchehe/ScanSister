package alpha.cyber.scansister;

import android.app.Application;

import alpha.cyber.scansister.crash.CrashHandler;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this,true);
    }
}
