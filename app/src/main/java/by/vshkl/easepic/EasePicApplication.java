package by.vshkl.easepic;

import android.app.Application;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;

public class EasePicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BigImageViewer.initialize(GlideImageLoader.with(this));
    }
}
