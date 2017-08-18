package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;

public class DenoiseNGG extends Denoising {

    @Override
    protected double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level, double mappedThreshold) {
        double[] dataForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRow = haarData.getCoefficients()[level];
        for(int i = 0; i < dataRow.length; i++) {
            int sign = getSwitchingSign(transformationHandler, level, i);
            if(Math.abs(dataForThreshold[i]) <= mappedThreshold) {
                dataRow[i] = 0;
                transformationHandler.setFixedValue(level, i);
            } else if (!transformationHandler.isFixedValue(level, i)) {
                // sign is -1 when value is swapped
                dataRow[i] = dataRow[i] - sign * (Math.pow(mappedThreshold, 2) / dataForThreshold[i]);
            }
        }
        return dataRow;
    }

    @Override
    public String getDescription() {
        return "DenoiseNGG with threshold " + mappedThreshold;
    }
}
