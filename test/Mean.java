import configuration.WaveletTransformationConfiguration;
import data.HaarData;
import data.TimeSeries;
import org.junit.Test;
import privacyTransformation.ConflictingOperationsException;
import privacyTransformation.PrivacyTransformationHandler;
import privacyTransformation.TransformationTypes;
import util.DataUtil;
import util.IOUtil;
import wavelet.HaarWaveletTransformation;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class Mean {

    @Test
    public void isTheSame() throws IOException, CloneNotSupportedException, ConflictingOperationsException {
        File[] fileDescriptors = IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR);
        for(File fileDescriptor : fileDescriptors) {
            TimeSeries originalData = IOUtil.getData(fileDescriptor);
            HaarData haarData = HaarWaveletTransformation.forward(originalData);
            System.out.println("Testing file: " + fileDescriptor.getName() + " with " + originalData.size() + " data points");
            for(TransformationTypes transformation : TransformationTypes.values()) {
                System.out.println("Testing transformation: " + transformation.name());
                for(int configurationValue = 0; configurationValue <= 100; configurationValue++) {
                    PrivacyTransformationHandler privacyTransformationHandler = new PrivacyTransformationHandler(true);
                    privacyTransformationHandler.addTransformation(transformation, configurationValue);
                    HaarData transformedData = privacyTransformationHandler.transform(haarData);
                    assertThat(transformedData.getMean(), is(haarData.getMean()));
                }

            }
        }
    }
}
