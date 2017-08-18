import configuration.WaveletTransformationConfiguration;
import data.HaarData;
import org.junit.Test;
import privacyTransformation.ConflictingOperationsException;
import privacyTransformation.PrivacyTransformationHandler;
import privacyTransformation.TransformationTypes;
import util.IOUtil;
import wavelet.HaarWaveletTransformation;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class Transformation {

    @Test
    public void transformationDoesntChangeInputObject() throws IOException, ConflictingOperationsException {
        TransformationTypes[] transformationTypes = TransformationTypes.values();
        HaarData inputHaar = HaarWaveletTransformation.forward(IOUtil.getData(IOUtil.getFirstFile(WaveletTransformationConfiguration.DATA_BASE_DIR)));
        for(TransformationTypes transformationType : transformationTypes) {
            for(int configurationValue = 0; configurationValue < 100; configurationValue++) {
                PrivacyTransformationHandler privacyTransformationHandler = new PrivacyTransformationHandler(true);
                privacyTransformationHandler.addTransformation(transformationType, configurationValue);

                privacyTransformationHandler.transform(inputHaar);
                HaarData originalData = HaarWaveletTransformation.forward(IOUtil.getData(IOUtil.getFirstFile(WaveletTransformationConfiguration.DATA_BASE_DIR)));
                assertThat(inputHaar, is(originalData));
            }
        }

    }
}
