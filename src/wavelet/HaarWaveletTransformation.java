package wavelet;

import data.HaarData;
import data.TimeSeries;
import libs.wavelets.wavelets.simple_haar;

public class HaarWaveletTransformation {

    public static HaarData forward(TimeSeries data) {
        simple_haar transformation = new simple_haar();
        transformation.wavelet_calc(data.getValues());
        return transformation.getCoefficients();
    }

    public static TimeSeries backward(HaarData coefficients) {
        simple_haar transformation = new simple_haar();
        transformation.setCoefficients(coefficients);
        transformation.inverse();
        return new TimeSeries(transformation.getData());
    }
}
