package by.vshkl.easepic.ui.detector;

public enum Direction {
    NOT_DETECTED,
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Direction get(double angle) {
        if (inRange(angle, 45, 135)) {
            return Direction.UP;
        } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
            return Direction.RIGHT;
        } else if (inRange(angle, 225, 315)) {
            return Direction.DOWN;
        } else {
            return Direction.LEFT;
        }
    }

    private static boolean inRange(double angle, float init, float end) {
        return (angle >= init) && (angle < end);
    }
}
