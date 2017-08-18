package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

public abstract class Denoising implements PrivacyTransformation {

    double mappedThreshold;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);

        HaarData transformedData = haarData.clone();
        for(int level = 0; level < haarData.getCoefficients().length; level++) {
            transformedData.setLevel(level, adaptLevel(transformationHandler, transformedData, level, mappedThreshold));
        }
        return transformedData;
    }

    private void calculateMappedThreshold(PrivacyTransformationHandler transformationHandler, HaarData coeffs, double configurationValue) {
        double maxCoefficientForThreshold = transformationHandler.getMaxCoefficientForThresholds(coeffs);
        this.mappedThreshold = DataUtil.mapToInterval(configurationValue, 0, maxCoefficientForThreshold);
    }

    protected abstract double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level, double mappedThreshold);

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);
        return mappedThreshold;
    }

    protected int getSwitchingSign(PrivacyTransformationHandler transformationHandler, int level, int i) {
        int sign = 1;
        if(transformationHandler.isSwitched(level, i)) {
            sign = -1;
        }
        return sign;
    }

    public abstract String getDescription();

}
