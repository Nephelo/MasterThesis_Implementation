package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;

public class DenoiseSoft extends Denoising {

    @Override
    protected double[] adaptLevel(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level, double mappedThreshold) {
        double[] dataForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRow = haarData.getCoefficients()[level];
        for(int i = 0; i < dataRow.length; i++) {
            // sign is -1 when coefficient was switched
            int sign = getSwitchingSign(transformationHandler, level, i);
            if(dataForThreshold[i] < 0) {
                sign *= -1;
            }
            /*  sign is -1 when original value was < 0 and not switched
                sign is -1 when original value was > 0 and switched
                sign is 1 when original value was > 0 and not switched
                sign is 1 when original value was < 0 and switched
                This means the variable sign conatains the sign of the current coefficient,
                independet of the current value. This means the sign hasn't changed,
                when coefficient is near 0.
            */
            if(Math.abs(dataForThreshold[i]) <= mappedThreshold) {
                dataRow[i] = 0;
                transformationHandler.setFixedValue(level, i);
            }
            // only change values with distance to 0 is > mappedThreshold
            else if(!transformationHandler.isFixedValue(level, i)) {
                /* intended effect: move all data values closer to 0
                 if datavalue > 0 -> datavalue - mappedThreshold
                 if datavalue < 0 -> datavalue + mappedThreshold
                */
                if (sign > 0) {
                    dataRow[i] = dataRow[i] - mappedThreshold;
                }
                else {
                    dataRow[i] = dataRow[i] + mappedThreshold;
                }
            }
        }
        return dataRow;
    }

    @Override
    public String getDescription() {
        return "DenoiseSoft with threshold " + mappedThreshold;
    }
}
