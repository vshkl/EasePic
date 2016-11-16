package by.vshkl.easepic.ui.detector;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

abstract public class SwipeDirectionDetector {

    public abstract void onDirectionDetected(Direction direction);

    private int touchSlop;
    private float startX, startY;
    private boolean isDetected;

    public SwipeDirectionDetector(Context context) {
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isDetected) {
                    onDirectionDetected(Direction.NOT_DETECTED);
                }
                startX = startY = 0.0f;
                isDetected = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDetected && getDistance(motionEvent) > touchSlop) {
                    isDetected = true;
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();

                    Direction direction = getDirection(startX, startY, x, y);
                    onDirectionDetected(direction);
                }
                break;
        }
        return false;
    }

    public double getAngle(float x1, float y1, float x2, float y2) {
        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

    public Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    private float getDistance(MotionEvent ev) {
        float distanceSum = 0;

        float dx = (ev.getX(0) - startX);
        float dy = (ev.getY(0) - startY);
        distanceSum += Math.sqrt(dx * dx + dy * dy);

        return distanceSum;
    }
}
