package util;

import java.util.*;
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

    public static double[] multiplyArrays(double[] values, double[] values1) {
        double[] multiply = new double[values.length];
        for(int i = 0; i < values.length; i++) {
            multiply[i] = values[i] * values1[i];
        }
        return multiply;
    }

    public static double mapToInterval(double value, double targetMin, double targetMax) {
        // use linear mapping to map [0, 100] to [targetMin, targetMax]
        // from: http://stackoverflow.com/questions/12931115/algorithm-to-map-an-interval-to-a-smaller-interval
        // [A , B] -> [a, b]
        // (val - A)*(b-a)/(B-A) + a
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

    public static double[][] multiply2DArray(double[][] coefficients, double[][] coefficients1) {
        double[][] copy = new double[coefficients.length][];
        IntStream.range(0, coefficients.length).forEach(level -> {
            copy[level] = new double[coefficients[level].length];
            IntStream.range(0, coefficients[level].length).forEach(index -> {
                copy[level][index] = coefficients[level][index] * coefficients1[level][index];
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

    public static SortedSet<SortedSet<Integer>> sortedPowerSet(Set<Integer> inputList) {
        Set<Set<Integer>> powerSet = powerSet(inputList);
        SortedSet<SortedSet<Integer>> sortedPowerSet = new TreeSet<>(DataUtil::comparePowerSet);
        powerSet.stream().forEach(integers -> {
            SortedSet<Integer> item = new TreeSet<>(Integer::compareTo);
            item.addAll(integers);
            sortedPowerSet.add(item);
        });
        return sortedPowerSet;
    }

    //From: http://stackoverflow.com/questions/17891527/optimal-way-to-obtain-get-powerset-of-a-list-recursively
    private static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    private static int comparePowerSet(SortedSet<Integer> integers, SortedSet<Integer> integers1) {
        if(integers.size() == integers1.size()) {
            Integer[] intArr = integers.toArray(new Integer[integers.size()]);
            Integer[] int1Arr = integers1.toArray(new Integer[integers.size()]);
            for(int i = 0; i < integers.size(); i++) {
                if(intArr[i].equals(int1Arr[i])) {
                    continue;
                } else {
                    return intArr[i] - int1Arr[i];
                }

            }
        }
        return integers.size() - integers1.size();
    }
}
