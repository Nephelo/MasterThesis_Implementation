package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;

public interface PrivacyTransformation {

    HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue);

    double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue);

    String getDescription();
}
