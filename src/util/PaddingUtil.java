package util;

import data.TimeSeries;

import java.util.Arrays;

public class PaddingUtil {

    public static double[] cutToPowerOfTwo(double[] data) {
        int biggestPowerOfTwo = Integer.highestOneBit(data.length - 1);
        return Arrays.copyOfRange(data, 0, biggestPowerOfTwo);
    }

    public static TimeSeries firstXData(TimeSeries data, int number) {
        return new TimeSeries(Arrays.copyOfRange(data.getValues(), 0, number));
    }
}
