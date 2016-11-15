package by.vshkl.easepic;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class EasePicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
