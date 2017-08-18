import configuration.WaveletTransformationConfiguration;
import data.HaarData;
import data.TimeSeries;
import org.junit.Ignore;
import org.junit.Test;
import util.IOUtil;
import util.PaddingUtil;
import wavelet.HaarWaveletTransformation;
import util.DataUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

public class Multiplication {

    private static final int FACTOR = 10;

    @Test
    public void multiplicationFactor() throws IOException {
        File[] fileDescriptors = IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR);
        for (File fileDescriptor : fileDescriptors) {
            TimeSeries originalData = IOUtil.getData(fileDescriptor);

            HaarData transformedData = HaarWaveletTransformation.forward(originalData);

            double[][] mulitCoeff = DataUtil.multiply2DArray(transformedData.getCoefficients(), FACTOR);
            double multMean = transformedData.getMean() * FACTOR;
            TimeSeries multCoeffBack = HaarWaveletTransformation.backward(new HaarData(multMean, mulitCoeff));
            double[] multiplyData = DataUtil.multiplyArrays(originalData.getValues(), FACTOR);

            for (int i = 0; i < multCoeffBack.size(); i++) {
                double diff = Math.abs(multCoeffBack.getValues()[i] - multiplyData[i]);
                assertThat(diff, lessThan(0.00000000001));
            }
        }
    }

    @Test
    @Ignore
    public void multiplicationCoeff() throws IOException {
        File[] fileDescriptors = IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR);
        for (File fileDescriptor0 : fileDescriptors) {
            for(File fileDescriptor1 : fileDescriptors) {
                TimeSeries originalData0 = IOUtil.getData(fileDescriptor0);
                TimeSeries originalData1 = IOUtil.getData(fileDescriptor1);

                int min = Math.min(originalData0.size(), originalData1.size());
                originalData0.cutToNumber(min);
                originalData1.cutToNumber(min);

                HaarData transformedData0 = HaarWaveletTransformation.forward(originalData0);
                HaarData transformedData1 = HaarWaveletTransformation.forward(originalData1);

                double[][] multiCoeff = DataUtil.multiply2DArray(transformedData0.getCoefficients(), transformedData1.getCoefficients());
                double multiMean = transformedData0.getMean() * transformedData1.getMean();
                TimeSeries multiplyBack = HaarWaveletTransformation.backward(new HaarData(multiMean, multiCoeff));
                double[] multiplyData = DataUtil.multiplyArrays(originalData0.getValues(), originalData1.getValues());

                for(int i = 0; i < multiplyBack.size(); i++) {
                    double diff = Math.abs(multiplyBack.getValues()[i] - multiplyData[i]);
                    System.out.println(diff);
                    assertThat(diff, lessThan(0.1));
                }
            }
        }
    }
}
