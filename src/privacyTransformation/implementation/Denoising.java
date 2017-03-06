package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

public class Denoising implements PrivacyTransformation {

    private double mappedThreshold;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);

        HaarData transformedData = haarData.clone();
        for(int level = 0; level < haarData.getCoefficients().length; level++) {
            transformedData.setLevel(level, adaptLevel(transformationHandler, transformedData, level));
        }
        return transformedData;
    }

    private void calculateMappedThreshold(PrivacyTransformationHandler transformationHandler, HaarData coeffs, double configurationValue) {
        double maxCoefficientForThreshold = transformationHandler.getMaxCoefficientForThresholds(coeffs);
        this.mappedThreshold = DataUtil.mapToInterval(configurationValue, 0, maxCoefficientForThreshold);
    }


    private double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level) {
        double[] dataForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRow = haarData.getCoefficients()[level];
        for(int i = 0; i < dataRow.length; i++) {
            if(Math.abs(dataForThreshold[i]) < mappedThreshold) {
                dataRow[i] = 0;
                transformationHandler.setFixedValue(level, i);
            }
        }
        return dataRow;
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);
        return mappedThreshold;
    }

    @Override
    public String getDescription() {
        return "Removed coefficients with threshold " + mappedThreshold;
    }
}
