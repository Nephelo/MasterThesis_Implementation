package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;

public class DenoiseHard extends Denoising {

    @Override
    protected double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level, double mappedThreshold) {
        double[] dataForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRow = haarData.getCoefficients()[level];
        for(int i = 0; i < dataRow.length; i++) {
            if(Math.abs(dataForThreshold[i]) <= mappedThreshold) {
                dataRow[i] = 0;
                transformationHandler.setFixedValue(level, i);
            }
        }
        return dataRow;
    }

    @Override
    public String getDescription() {
        return "Denoisehard with threshold " + super.mappedThreshold;
    }
}
