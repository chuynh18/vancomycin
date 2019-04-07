package yolo.tbv.vancomycin;

public final class UnitTestHelper {
    public static double genRandNumBetween(double lower, double upper) {
        double delta = upper - lower;

        return delta * Math.random() + lower;
    }
}