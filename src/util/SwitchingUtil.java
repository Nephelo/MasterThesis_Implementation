package util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class SwitchingUtil {
    public static double[] switchArray(ArrayList<int[]> splittingPoints, double[] arrayToSwitch) {
        ArrayList<Double> switchedDoubleList = DataUtil.switchInArrays(ArrayUtils.toObject(arrayToSwitch), splittingPoints);
        double[] switchedDouble = new double[arrayToSwitch.length];
        IntStream.range(0, switchedDoubleList.size()).forEach((int index) -> switchedDouble[index] = switchedDoubleList.get(index));
        return switchedDouble;
    }

    public static boolean[] switchArray(ArrayList<int[]> splittingPoints, boolean[] booleanToSwitch) {
        ArrayList<Boolean> switchedBooleanList = DataUtil.switchInArrays(ArrayUtils.toObject(booleanToSwitch), splittingPoints);
        boolean[] switchedBoolean = new boolean[booleanToSwitch.length];
        IntStream.range(0, switchedBooleanList.size()).forEach((int index) -> switchedBoolean[index] = switchedBooleanList.get(index));
        return switchedBoolean;
    }
}
