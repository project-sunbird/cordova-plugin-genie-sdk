package org.sunbird;

import android.app.Application;


/**
 * Created by swayangjit on 21/4/18.
 */
public class SunbirdApplication extends Application implements ForegroundService.OnForegroundChangeListener{
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(ForegroundService.getInstance());
        ForegroundService.getInstance().registerListener(this);
        GenieService.init(this, "org.sunbird.app");
    }

    @Override
    public void onSwitchForeground() {

    }

    @Override
    public void onSwitchBackground() {

    }
}
