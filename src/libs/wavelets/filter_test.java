
import wavelet_util.*;
import dataInput.*;

/**
  Apply the gaussian filter to a time series (in this case
  the time series for Applied Materials (symbol: AMAT).
  Two files will be generated: <i>filtered_data</i> which
  contains the close price time series that has been 
  filtered using Haar wavelets and <i>filtered_data_noise</i>
  which contains the noise spectrum.
 */

class filter_test {

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
	  noise_filter filter = new noise_filter();
	  filter.filter_time_series( "filtered_data", vals );
	}
      }
  }

} // filter_test

