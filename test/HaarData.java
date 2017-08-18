import configuration.WaveletTransformationConfiguration;
import data.TimeSeries;
import org.junit.Test;
import util.IOUtil;
import wavelet.HaarWaveletTransformation;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HaarData {

    @Test
    public void getNumberOfValues() throws IOException {

        TimeSeries originalData = IOUtil.getData(IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR)[0]);

        data.HaarData transformedData = HaarWaveletTransformation.forward(originalData);
        assertThat(transformedData.getNumberOfValues(), is(originalData.size()));
    }
}
