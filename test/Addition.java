import configuration.WaveletTransformationConfiguration;
import data.HaarData;
import data.TimeSeries;
import org.junit.Test;
import util.DataUtil;
import util.IOUtil;
import wavelet.HaarWaveletTransformation;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class Addition {

    @Test
    public void addition() throws IOException, CloneNotSupportedException {
        File[] fileDescriptors = IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR);
        for(File fileDescriptor0 : fileDescriptors) {
            for(File fileDescriptor1 : fileDescriptors) {
                TimeSeries originalData0 = IOUtil.getData(fileDescriptor0);
                TimeSeries originalData1 = IOUtil.getData(fileDescriptor1);

                int min = Math.min(originalData0.size(), originalData1.size());
                originalData0.cutToNumber(min);
                originalData1.cutToNumber(min);

                HaarData transformedData0 = HaarWaveletTransformation.forward(originalData0);
                HaarData transformedData1 = HaarWaveletTransformation.forward(originalData1);

                double[][] sumCoeff = DataUtil.add2DArray(transformedData0.getCoefficients(), transformedData1.getCoefficients());
                double sumMean = transformedData0.getMean() + transformedData1.getMean();
                TimeSeries sumCoeffBack = HaarWaveletTransformation.backward(new HaarData(sumMean, sumCoeff));

                double[] sumData = DataUtil.addArrays(originalData0.getValues(), originalData1.getValues());

                for(int i = 0; i < sumCoeffBack.size(); i++) {
                    double diff = Math.abs(sumCoeffBack.getValues()[i] - sumData[i]);
                    assertThat(diff, lessThan(0.00000000001));
                }
            }
        }
    }
}
