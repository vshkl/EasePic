package by.vshkl.easepic.ui.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import by.vshkl.easepic.R;

public class ErrorUtils {

    public enum Error {
        ERROR_GET_ALBUMS,
        ERROR_GET_PICTURES,
        ERROR_NO_PICTURES,
        ERROR_ALBUM_RENAME_FAILED
    }

    @Nullable
    public static String getMessageString(Context context, Error error) {
        String errorMessage = null;

        switch (error) {
            case ERROR_GET_ALBUMS:
                errorMessage = context.getString(R.string.error_get_albums);
                break;
            case ERROR_GET_PICTURES:
                errorMessage = context.getString(R.string.error_get_pictures);
                break;
            case ERROR_NO_PICTURES:
                errorMessage =  context.getString(R.string.error_no_pictures);
                break;
            case ERROR_ALBUM_RENAME_FAILED:
                errorMessage = context.getString(R.string.error_album_rename_failed);
                break;
        }

        return errorMessage;
    }
}
