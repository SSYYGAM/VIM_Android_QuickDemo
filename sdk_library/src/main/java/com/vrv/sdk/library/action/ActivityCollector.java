package com.vrv.sdk.library.action;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity统一管理
 */
public class ActivityCollector {

    private static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public final static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing())
                activity.finish();
        }
    }

    public static List<Activity> getAllActivity() {
        return activities;
    }

    public static Activity getFirstActivity() {
        if (activities == null || activities.isEmpty()) {
            return null;
        }
        return activities.get(0);
    }

    public static Activity getCurrentActivity() {
        if (activities == null || activities.isEmpty()) {
            return null;
        }
        return activities.get(activities.size() - 1);
    }

    public static void finishOthers() {
        for (Activity activity : activities) {
/*            if (!(activity instanceof MainActivity)) {
                if (!activity.isFinishing())
                    activity.finish();
            }*/
        }
    }
}
