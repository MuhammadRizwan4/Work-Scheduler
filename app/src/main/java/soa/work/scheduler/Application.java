package soa.work.scheduler;

import androidx.multidex.MultiDexApplication;

import com.onesignal.OneSignal;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
