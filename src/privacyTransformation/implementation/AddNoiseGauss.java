package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AddNoiseGauss implements PrivacyTransformation {

    private final double MAX_PHI = 5;
    private double mappedSigma;
    private double mappedPhi;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapParameters(transformationHandler, haarData, configurationValue);
        double[][] coefficients = haarData.getCoefficients();
        HaarData transformedData = haarData.clone();
        double noiseDensity = calculateNoiseDensity(transformationHandler);

        for(int level = 0; level < coefficients.length; level++) {
            double[] transformedLevel = adaptLevel(transformationHandler, transformedData, noiseDensity, level);
            transformedData.setLevel(level, transformedLevel);
        }

        return transformedData;
    }

    private void mapParameters(PrivacyTransformationHandler transformationHandler, HaarData coeffs, double configurationValue) {
        double maxCoefficientForThreshold = transformationHandler.getMaxCoefficientForThresholds(coeffs);
        mappedSigma = DataUtil.mapToInterval(configurationValue, 0, maxCoefficientForThreshold);
        mappedPhi = DataUtil.mapToInterval(configurationValue, 0, MAX_PHI);
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
                .filter(index -> coefficientsForThreshold[index] > mappedSigma)
                .forEach((int index) -> {
                    // Change sign of noise if coefficient was root of switching transformation
                    int sign = 1;
                    if(transformationHandler.isSwitched(level, index)) {
                        sign *= -1;
                    }
                    double noise = sign * transformationHandler.getRandomHandler().getLastDoubleGaussValues(level, index) * Math.sqrt(mappedPhi * Math.sqrt(noiseDensity));
                    dataRowCopy[index] += noise;
                });
        return dataRowCopy;
    }

    @Override
    public String getDescription() {
        return "Added gaussian noise for sigma " + mappedSigma;
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapParameters(transformationHandler, haarData, configurationValue);
        return mappedSigma;
    }
}
