package by.vshkl.easepic.ui.utils;

import android.content.Context;

import net.grandcentrix.tray.AppPreferences;

import by.vshkl.easepic.R;

public class PreferenceUtils {

    public static void storeSortOrder(Context context, String value) {
        (new AppPreferences(context)).put(
                context.getString(R.string.key_sort_order),
                value);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static String getSortOrder(Context context) {
        return (new AppPreferences(context)).getString(
                context.getString(R.string.key_sort_order),
                context.getString(R.string.value_sort_order_by_date));
    }
}
