package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AddNoise implements PrivacyTransformation {

    private final double MAX_SIGMA = 25;
    private double mappedSigma;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        setMappedSigma(configurationValue);
        double[][] coefficients = haarData.getCoefficients();
        HaarData transformedData = haarData.clone();
        double noiseDensity = calculateNoiseDensity(transformationHandler);

        for(int level = 0; level < coefficients.length; level++) {
            double[] transformedLevel = adaptLevel(transformationHandler, transformedData, noiseDensity, level);
            transformedData.setLevel(level, transformedLevel);
        }

        return transformedData;
    }

    private void setMappedSigma(double configurationValue) {
        mappedSigma = DataUtil.mapToInterval(configurationValue, 0, MAX_SIGMA);
    }

    private double calculateNoiseDensity(PrivacyTransformationHandler transformationHandler) {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicInteger countBig = new AtomicInteger(0);
        Arrays.stream(transformationHandler.getCoefficentsForThreshold())
                .flatMapToDouble(Arrays::stream)
                .forEach(value -> {
                    if(Math.abs(value) > mappedSigma) {
                        countBig.incrementAndGet();
                    }
                    count.incrementAndGet();
                });
        if(countBig.intValue() == 0) {
            return 0;
        } else {
            return (double) count.intValue()/ (double) countBig.intValue();
        }
    }

    private double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, double noiseDensity, int level) {
        double[] coefficientsForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRowCopy = haarData.getCoefficients()[level];
        IntStream.range(0, dataRowCopy.length)
                .filter(index -> !transformationHandler.isFixedValue(level, index))
                .forEach(index -> {
                    // Use sign of original values
                    // Otherwise (using sign of current row) sign change when near 0
                    int sign = (int) Math.signum(coefficientsForThreshold[index]);
                    if(transformationHandler.isSwitched(level, index)) {
                        sign *= -1;
                    }
                    //int sign = (int) Math.signum(dataRowCopy[index]);
                    double noise = sign * transformationHandler.getRandomHandler().getLastDoubleRandomValues(level, index) * mappedSigma * Math.sqrt(noiseDensity);
                    dataRowCopy[index] += noise;
                });
        return dataRowCopy;
    }

    @Override
    public String getDescription() {
        return "Added noise for sigma " + mappedSigma;
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        setMappedSigma(configurationValue);
        return mappedSigma;
    }
}
