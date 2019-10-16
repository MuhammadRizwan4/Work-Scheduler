package soa.work.scheduler.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static soa.work.scheduler.data.Constants.USER_ACCOUNT;

public class PrefManager {

    private static final String PREF_NAME = "work_scheduler";
    private static final String LAST_OPENED_ACTIVITY = "last_opened_activity";
    private static final String IMAGES_VERSION = "images_version";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public int getLastOpenedActivity() {
        return pref.getInt(LAST_OPENED_ACTIVITY, /*default value*/USER_ACCOUNT);
    }

    public void setLastOpenedActivity(int lastOpenedActivity) {
        editor.putInt(LAST_OPENED_ACTIVITY, lastOpenedActivity);
        editor.apply();
    }

    public int getImagesVersion() {
        return pref.getInt(IMAGES_VERSION, 1);
    }

    public void setImagesVersion(int version) {
        editor.putInt(IMAGES_VERSION, version);
        editor.apply();
    }
}
