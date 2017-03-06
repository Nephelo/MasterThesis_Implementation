package data;

import java.util.Arrays;
import java.util.Vector;
import java.util.stream.IntStream;

public class HaarData {
    private double mean;
    private double[][] coefficients;

    public HaarData(int levels, int maxItemsPerLevel) {
        this.coefficients = new double[levels][maxItemsPerLevel];
    }

    public HaarData(double haar_value, Vector<double[]> coef) {
        this.coefficients = new double[coef.size()][coef.get(0).length];
        this.mean = haar_value;
        for(int i = 0; i < coef.size(); i++) {
            this.coefficients[i] = coef.get(i);
        }
    }

    public HaarData(double mean, double[][] coefficients) {
        this.mean = mean;
        this.coefficients = coefficients;
    }

    public double getMean() {
        return mean;
    }

    public double[][] getCoefficients() {
        return coefficients;
    }

    public void setLevel(int level, double[] transformedLevel) {
        if(this.coefficients.length < level) {
            throw new IllegalArgumentException("Number of levels is to small");
        } else {
            this.coefficients[level] = transformedLevel;
        }
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public HaarData clone() {
        HaarData copiedObject = new HaarData(coefficients.length, coefficients[0].length);
        copiedObject.setMean(mean);
        IntStream.range(0, coefficients.length).forEach(index -> {
            double[] copiedCoefficients = new double[coefficients[index].length];
            System.arraycopy(coefficients[index], 0, copiedCoefficients, 0, coefficients[index].length);
            copiedObject.setLevel(index, copiedCoefficients);
        });
        return copiedObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HaarData haarData = (HaarData) o;

        if (Double.compare(haarData.mean, mean) != 0) {
            return false;
        }
        if(coefficients.length != haarData.coefficients.length) {
            return false;
        }
        for(int i = 0; i < coefficients.length; i++) {
            if(coefficients[i].length != haarData.coefficients[i].length) {
                return false;
            }
            for(int j = 0; j < coefficients[i].length; j++) {
                if(Math.abs(coefficients[i][j] - haarData.coefficients[i][j]) > 0.00001) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.deepHashCode(coefficients);
        return result;
    }
}
