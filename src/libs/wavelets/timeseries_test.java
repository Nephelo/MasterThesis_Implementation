package libs.wavelets;

import libs.wavelets.wavelet_util.*;
import libs.wavelets.dataInput.*;

/**
<p>
  Test the Inplace Haar wavelet algorithm with a financial
  time series, in this case, the daily close price for
  Applied Materials (symbol: AMAT).
<p>
  The code below reads the time series and generates outputs
  the coefficients and the wavelet spectrum for graphing.

 */
class timeseries_test {

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
	  gnuplot3D pts;

		libs.wavelets.wavelets.inplace_haar haar = new libs.wavelets.wavelets.inplace_haar();
	  haar.wavelet_calc( vals );
	  haar.order();
	  pts = new gnuplot3D(vals, "coef" );
	  haar.inverse();
	  haar.wavelet_spectrum( vals );
	  pts = new gnuplot3D(vals, "spectrum");
	}
      }
  } // main

}
