package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;

import java.util.stream.IntStream;

public class CutJumps implements PrivacyTransformation {

    private double mappedThreshold;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);

        HaarData transformedData = haarData.clone();
        for(int level = 0; level < haarData.getCoefficients().length; level++) {
            transformedData.setLevel(level, adaptRow(transformationHandler, haarData, level));
        }

        return transformedData;
    }

    private void calculateMappedThreshold(PrivacyTransformationHandler transformationHandler, HaarData coeffs, double configurationValue) {
        double maxCoefficientForThreshold = transformationHandler.getMaxCoefficientForThresholds(coeffs);
        this.mappedThreshold = DataUtil.mapToInterval(configurationValue, maxCoefficientForThreshold, 0 );
    }

    private double[] adaptRow(PrivacyTransformationHandler transformationHandler, HaarData haarData, int level) {
        double[] coefficientsForThreshold = transformationHandler.getCoefficentsForThreshold()[level];
        double[] dataRow = haarData.getCoefficients()[level];
        double[] dataRowCopy = new double[dataRow.length];
        System.arraycopy(dataRow,0, dataRowCopy, 0, dataRow.length);
        IntStream.range(0, dataRow.length)
                .forEach(index -> {
                    int sign = (int) Math.signum(coefficientsForThreshold[index]);
                    //int sign = (int) Math.signum(dataRow[index]);
                    if(sign == 0) {
                        sign = 1;
                    }
                    if(transformationHandler.isSwitched(level, index)) {
                        sign *= -1;
                    }
                    if(Math.abs(coefficientsForThreshold[index]) >= mappedThreshold) {
                        double val;
                        if (transformationHandler.isFixedValue(level, index)) {
                            // Cut jumps already executed, so use minimum of both cutJumps operations
                            // Only use in this case. In case of add noise current data value can be lower, because of rand < 0
                            val = Math.min(Math.abs(mappedThreshold), Math.abs(dataRow[index]));
                        } else {
                            val = Math.abs(mappedThreshold);
                        }
                        dataRowCopy[index] = sign * val;
                        transformationHandler.setFixedValue(level, index);
                    }
                });
        return dataRowCopy;
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        calculateMappedThreshold(transformationHandler, haarData, configurationValue);
        return mappedThreshold;
    }

    @Override
    public String getDescription() {
        return "Cut jumps with threshold " + mappedThreshold;
    }
}
