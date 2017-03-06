package data;

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