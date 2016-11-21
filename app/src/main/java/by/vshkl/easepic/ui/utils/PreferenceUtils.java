package by.vshkl.easepic.ui.utils;

import android.content.Context;

import net.grandcentrix.tray.AppPreferences;

public class PreferenceUtils {

    public static void storeSortOrder(Context context, String key, String value) {
        (new AppPreferences(context)).put(key, value);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static String getSortOrder(Context context, String key, String defaultValue) {
        return (new AppPreferences(context)).getString(key, defaultValue);
    }
}
