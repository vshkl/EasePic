package by.vshkl.easepic.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import java.util.List;

import by.vshkl.easepic.R;
import by.vshkl.easepic.ui.listener.OnDismissListener;
import by.vshkl.easepic.ui.listener.OnImageChangeListener;

public class ImageViewer implements OnDismissListener, DialogInterface.OnKeyListener {

    private Builder builder;
    private AlertDialog dialog;
    private ImageViewerView viewer;

    protected ImageViewer(Builder builder) {
        this.builder = builder;
        createDialog();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDismiss() {
        dialog.cancel();
        if (builder.onDismissListener != null) {
            builder.onDismissListener.onDismiss();
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP &&
                !event.isCanceled()) {
            if (viewer.isScaled()) {
                viewer.resetScale();
            } else {
                dialog.cancel();
            }
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void show() {
        if (!builder.uris.isEmpty()) {
            dialog.show();
        }
    }

    public Uri getUri() {
        return viewer.getUri();
    }

    private void createDialog() {
        viewer = new ImageViewerView(builder.context);
        viewer.setUris(builder.uris, builder.startPosition);
        viewer.setOnDismissListener(this);
        viewer.setBackgroundColor(builder.backgroundColor);
        viewer.setBackgroundColor(builder.backgroundColor);
        viewer.setPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (builder.imageChangeListener != null) {
                    builder.imageChangeListener.onImageChange(position);
                }
            }
        });

        dialog = new AlertDialog.Builder(builder.context, getDialogStyle())
                .setView(viewer)
                .setOnKeyListener(this)
                .create();
    }


    private
    @StyleRes
    int getDialogStyle() {
        return builder.shouldStatusBarHide
                ? R.style.AppTheme_Dialog_Fullscreen
                : R.style.AppTheme_Dialog;
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class Builder {

        private Context context;
        private List<Uri> uris;
        private
        @ColorInt
        int backgroundColor = Color.BLACK;
        private int startPosition;
        private OnImageChangeListener imageChangeListener;
        private OnDismissListener onDismissListener;
        private boolean shouldStatusBarHide = true;

        public Builder(Context context, List<Uri> uris) {
            this.context = context;
            this.uris = uris;
        }

        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder setStartPosition(int position) {
            this.startPosition = position;
            return this;
        }

        public Builder setImageChangeListener(OnImageChangeListener imageChangeListener) {
            this.imageChangeListener = imageChangeListener;
            return this;
        }

        public Builder hideStatusBar(boolean shouldHide) {
            this.shouldStatusBarHide = shouldHide;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }

        public ImageViewer build() {
            return new ImageViewer(this);
        }

        public ImageViewer show() {
            ImageViewer dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
