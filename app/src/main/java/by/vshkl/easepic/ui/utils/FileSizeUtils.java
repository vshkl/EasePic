package by.vshkl.easepic.ui.utils;


import java.util.Locale;

public class FileSizeUtils {

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf((si ? "kMGTPE" : "KMGTPE").charAt(exp-1));
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
