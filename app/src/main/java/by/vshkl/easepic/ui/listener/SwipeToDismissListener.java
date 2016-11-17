package by.vshkl.easepic.ui.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class SwipeToDismissListener implements View.OnTouchListener {

    private final View swipeView;
    private int translationThreshold;
    private OnDismissListener onDismissListener;
    private OnViewMoveListener onViewMoveListener;

    private boolean tracking = false;
    private float startY;

    public SwipeToDismissListener(View swipeView, OnViewMoveListener onViewMoveListener,
                                  OnDismissListener onDismissListener) {
        this.swipeView = swipeView;
        this.onViewMoveListener = onViewMoveListener;
        this.onDismissListener = onDismissListener;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        translationThreshold = view.getHeight() / 4;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Rect hitRect = new Rect();
                swipeView.getHitRect(hitRect);
                if (hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    tracking = true;
                }
                startY = motionEvent.getY();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (tracking) {
                    tracking = false;
                    animateSwipeView(view.getHeight());
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (tracking) {
                    float translationY = motionEvent.getY() - startY;
                    swipeView.setTranslationY(translationY);
                    callMoveListener(translationY, translationThreshold);
                }
                return true;
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------------------

    private void animateSwipeView(int parentHeight) {
        float currentPosition = swipeView.getTranslationY();
        float animateTo = 0.0f;

        if (currentPosition < -translationThreshold) {
            animateTo = -parentHeight;
        } else if (currentPosition > translationThreshold) {
            animateTo = parentHeight;
        }

        final boolean isDismissed = animateTo != 0.0f;
        ObjectAnimator animator = ObjectAnimator.ofFloat(swipeView, "translationY", currentPosition, animateTo);

        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isDismissed) {
                    callDismissListener();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                callMoveListener((float) animation.getAnimatedValue(), translationThreshold);
            }
        });
        animator.start();
    }

    private void callDismissListener() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    private void callMoveListener(float translationY, int translationLimit) {
        if (onViewMoveListener != null) {
            onViewMoveListener.onViewMove(translationY, translationLimit);
        }
    }
}
