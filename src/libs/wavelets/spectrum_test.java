package libs.wavelets;

import libs.wavelets.wavelet_util.*;
import libs.wavelets.dataInput.*;
import libs.wavelets.wavelets.inplace_haar;

/**
  Generate gnuplot files for the time series with
  various spectrum removed.

 */

class spectrum_test {

  public static void main( String[] args ) {
      String timeSeriesFile = "amat_close";  // Applied Materials Close prices
      tsRead data = new tsRead( timeSeriesFile );
      //
      // The wavelet algorithms work on arrays whose length is a power
      // of two.  Set the length to the nearest power of two that is
      // less than or equal to the data length.
      //
      int len = data.getSize();
      if (len > 0) {
	int newSize = binary.nearestPower2( len );
	data.setSize( newSize );
	double[] vals = data.getArray();

	if (vals != null) {

	  libs.wavelets.wavelets.inplace_haar haar = new inplace_haar();
	  haar.wavelet_calc( vals );
	  haar.order();

	  libs.wavelets.wavelet_util.coef_spectrum spectrum = new coef_spectrum();
	  spectrum.filter_one_spectrum( vals );
	  spectrum.only_one_spectrum( vals );
	}
      }
  }

} // spectrum_test

