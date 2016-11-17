package by.vshkl.easepic.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.Picture;
import by.vshkl.easepic.ui.adapter.ImageViewerAdapter;
import by.vshkl.easepic.ui.detector.Direction;
import by.vshkl.easepic.ui.detector.SwipeDirectionDetector;
import by.vshkl.easepic.ui.listener.OnDismissListener;
import by.vshkl.easepic.ui.listener.OnViewMoveListener;
import by.vshkl.easepic.ui.listener.SwipeToDismissListener;

public class ImageViewerView extends RelativeLayout implements OnDismissListener, OnViewMoveListener {

    private View backgroundView;
    private MarqueeToolbar toolbar;
    private MultitouchViewPager vpPager;
    private ViewGroup dismissContainer;
    private ImageViewerAdapter adapter;

    private SwipeDirectionDetector swipeDirectionDetector;
    private GestureDetectorCompat gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private ViewPager.OnPageChangeListener onPageChangeListener;
    private SwipeToDismissListener swipeToDismissListener;
    private OnDismissListener onDismissListener;

    private Direction direction;

    private boolean wasScaled;

    public ImageViewerView(Context context) {
        super(context);
        initialize();
    }

    public ImageViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ImageViewerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageViewerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onActionDown(event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onActionUp(event);
        }

        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        if (direction == null) {
            if (scaleGestureDetector.isInProgress() || event.getPointerCount() > 1) {
                wasScaled = true;
                return vpPager.dispatchTouchEvent(event);
            }
        }

        if (!adapter.isScaled()) {
            swipeDirectionDetector.onTouchEvent(event);
            if (direction != null) {
                switch (direction) {
                    case UP:
                    case DOWN:
                        if (!wasScaled && vpPager.isScrolled()) {
                            return swipeToDismissListener.onTouch(dismissContainer, event);
                        } else break;
                    case LEFT:
                    case RIGHT:
                        return vpPager.dispatchTouchEvent(event);
                }
            }
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    @Override
    public void onViewMove(float translationY, int translationThreshold) {
        float alpha = 1.0f - (1.0f / translationThreshold / 4) * Math.abs(translationY);
        backgroundView.setAlpha(alpha);
    }

    @Override
    public void setBackgroundColor(int color) {
        findViewById(R.id.background_view).setBackgroundColor(color);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void setPictures(List<Picture> pictureList, int startPosition) {
        adapter = new ImageViewerAdapter(getContext(), pictureList);
        vpPager.setAdapter(adapter);
        vpPager.setCurrentItem(startPosition);

//        setupToolbar();
    }

    public void setupToolbar() {
        toolbar.setTitle(adapter.getPicture(vpPager.getCurrentItem()).getName());
    }

    private void initialize() {
        inflate(getContext(), R.layout.dialog_image_viewer, this);

        backgroundView = findViewById(R.id.background_view);

//        toolbar = (MarqueeToolbar) findViewById(R.id.toolbar);

        vpPager = (MultitouchViewPager) findViewById(R.id.vp_pager);

        dismissContainer = (ViewGroup) findViewById(R.id.container);
        swipeToDismissListener = new SwipeToDismissListener(findViewById(R.id.fl_dismiss), this, this);
        dismissContainer.setOnTouchListener(swipeToDismissListener);

        swipeDirectionDetector = new SwipeDirectionDetector(getContext()) {
            @Override
            public void onDirectionDetected(Direction direction) {
                ImageViewerView.this.direction = direction;
            }
        };

        scaleGestureDetector =
                new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener());

        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                onClick(e);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void resetScale() {
        adapter.resetScale();
    }

    public boolean isScaled() {
        return adapter.isScaled();
    }

    public Picture getPicture() {
        return adapter.getPicture(vpPager.getCurrentItem());
    }

    public void setPageChangeListener(ViewPager.OnPageChangeListener pageChangeListener) {
        vpPager.removeOnPageChangeListener(this.onPageChangeListener);
        this.onPageChangeListener = pageChangeListener;
        vpPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(vpPager.getCurrentItem());
    }

    private void onActionDown(MotionEvent event) {
        direction = null;
        wasScaled = false;
        vpPager.dispatchTouchEvent(event);
        swipeToDismissListener.onTouch(dismissContainer, event);
    }

    private void onActionUp(MotionEvent event) {
        swipeToDismissListener.onTouch(dismissContainer, event);
        vpPager.dispatchTouchEvent(event);
    }

    private void onClick(MotionEvent event) {
        super.dispatchTouchEvent(event);
    }
}
