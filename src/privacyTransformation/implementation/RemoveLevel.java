package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

public class RemoveLevel implements PrivacyTransformation {

    private int mappedNumberOfLevels;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedNumberOfLevels(haarData, configurationValue);

        HaarData transformedData = haarData.clone();
        for(int level = haarData.getCoefficients().length-mappedNumberOfLevels ; level < haarData.getCoefficients().length; level++) {
            transformedData.setLevel(level, new double[haarData.getCoefficients()[level].length]);
            transformationHandler.setFixedValue(level);
        }
        return transformedData;
    }

    private void calculateMappedNumberOfLevels(HaarData haarData, double configurationValue) {
        this.mappedNumberOfLevels = (int) DataUtil.mapToInterval(configurationValue, 0, haarData.getCoefficients().length);
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedNumberOfLevels(haarData, configurationValue);
        return mappedNumberOfLevels;
    }

    @Override
    public String getDescription() {
        return "Removed first "+mappedNumberOfLevels+" levels";
    }
}
