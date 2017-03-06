package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class DataUtil {

    public static double[] addArrays(double[] coeff0, double[] coeff1) {
        if(coeff0.length != coeff1.length) {
            throw new UnsupportedOperationException("Row length doesn't match while addition");
        }
        double[] sum = new double[coeff0.length];
        for(int i = 0; i < coeff0.length; i++) {
            sum[i] = coeff0[i] + coeff1[i];
        }
        return sum;
    }


    public static double[] multiplyArrays(double[] values, int factor) {
        double[] multiply = new double[values.length];
        for(int i = 0; i < values.length; i++) {
            multiply[i] = values[i] * factor;
        }
        return multiply;
    }

    public static double mapToInterval(double value, double targetMin, double targetMax) {
        //use linear mapping to map [0, 100] to [targetMin, targetMax]
        return value * (targetMax - targetMin) / 100 + targetMin;
    }

    public static double[][] add2DArray(double[][] coefficients, double[][] coefficients1) {
        double[][] copy = new double[coefficients.length][];
        IntStream.range(0, coefficients.length).forEach(level -> {
            copy[level] = new double[coefficients[level].length];
            IntStream.range(0, coefficients[level].length).forEach(index -> {
                copy[level][index] = coefficients[level][index] + coefficients1[level][index];
            });
        });
        return copy;
    }

    public static double[][] multiply2DArray(double[][] coefficients, int factor) {
        double[][] copy = new double[coefficients.length][];
        IntStream.range(0, coefficients.length).forEach(level -> {
            copy[level] = new double[coefficients[level].length];
            IntStream.range(0, coefficients[level].length).forEach(index -> {
                copy[level][index] = coefficients[level][index] * factor;
            });
        });
        return copy;
    }

    public static <T> ArrayList<T> switchInArrays(T[] currentArray, ArrayList<int[]> splittingPoints) {
        ArrayList<T> switchedList = new ArrayList<>();

        for(int[] point : splittingPoints) {
            Collections.addAll(switchedList, Arrays.copyOfRange(currentArray, point[0], point[1]));
        }

        return switchedList;
    }
}
