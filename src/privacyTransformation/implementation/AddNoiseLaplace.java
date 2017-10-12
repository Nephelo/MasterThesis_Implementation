package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

import java.util.stream.IntStream;

public class AddNoiseLaplace implements PrivacyTransformation {

    private final double MAX_LAMBDA = 50;
    private double mappedLambda;
    private double mappedSigma;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapParameters(transformationHandler, haarData, configurationValue);
        double[][] coefficients = haarData.getCoefficients();
        HaarData transformedData = haarData.clone();

        for(int level = 0; level < coefficients.length; level++) {
            double[] transformedLevel = adaptLevel(transformationHandler, transformedData, level);
            transformedData.setLevel(level, transformedLevel);
        }

        return transformedData;
    }

    private void mapParameters(PrivacyTransformationHandler transformationHandler, HaarData coeffs, double configurationValue) {
        double maxCoefficientForThreshold = transformationHandler.getMaxCoefficientForThresholds(coeffs);
        mappedSigma = DataUtil.mapToInterval(configurationValue, maxCoefficientForThreshold, 0);
        mappedLambda = DataUtil.mapToInterval(configurationValue, 0.1, MAX_LAMBDA);
    }

    private double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level) {
        double[] coefficientsForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRowCopy = haarData.getCoefficients()[level];

        int weight = (int) Math.pow(2, haarData.getCoefficients().length - level);
        double magnitude = mappedLambda / weight;
        IntStream.range(0, dataRowCopy.length)
                .filter(index -> !transformationHandler.isFixedValue(level, index))
                .filter(index -> coefficientsForThreshold[index] > mappedSigma)
                .forEach(index -> {
                    // Change sign of noise if coefficient was root of switching transformation
                    int sign = 1;
                    if(transformationHandler.isSwitched(level, index)) {
                        sign *= -1;
                    }
                    double noise = sign * transformationHandler.getRandomHandler().getLastDoubleLaplaceValues(level, index, magnitude);
                    dataRowCopy[index] += noise;
                });
        return dataRowCopy;
    }

    @Override
    public String getDescription() {
        return "Added laplace noise for sigma " + mappedLambda;
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapParameters(transformationHandler, haarData, configurationValue);
        return mappedLambda;
    }
}
