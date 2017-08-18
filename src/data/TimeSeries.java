package data;

import java.util.Arrays;

public class TimeSeries {

    private double[] values;

    public TimeSeries(double[] originalData) {
        this.values = originalData;
    }

    public double[] getValues() {
        return values;
    }

    public int size() {
        return values.length;
    }

    public void cutToNumber(int number) {
        this.values = Arrays.copyOfRange(this.values, 0, number);
    }

    public double[] calculateMeans() {
        double[] means = new double[values.length];
        double sum = 0;
        for(int i = 0; i < values.length; i++) {
            sum += values[i];
            means[i] = sum / (i + 1);
        }
        return means;
    }
}