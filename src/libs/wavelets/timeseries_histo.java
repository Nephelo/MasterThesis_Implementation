package libs.wavelets;

import libs.wavelets.wavelet_util.*;
import libs.wavelets.experimental.*;
import libs.wavelets.dataInput.*;

/**
<p>
  Generate histograms for the Haar coefficients created by
  applying the Haar transform to the time series for the
  Applied Materials (symbol: AMAT) daily close price.
<p>
  There are 512 data points in the AMAT daily close price time
  series.  This program generates a histogram for the 
  first four high frequency sets of coefficients (e.g., 256, 
  128, 64, and 32 coefficients).
</p>
<p>
  Financial theory states that the average daily return
  (e.g, the difference between today'ss close prices and
  yesterday's close price) is normally distributed.  So
  the histogram of the highest frequency coefficients, which
  reflect the difference between two close prices, should
  be bell curve shaped, centered around zero.
</p>
<p>
  The close price in the AMAT time series rises sharply about half way
  through.  So as the coefficient frequency decreases, the histogram
  will be shifted farther and farter away from zero.
</p>
<p>
Note that an inplace Haar transform is used that replaces
the values with the coefficients.  The order function
orders the coefficients from the butterfly pattern generated
by the inplace algorithm into increasing frequencies, where
the lowest frequency is at the beginning of the array.  Each
frequency is a power of two: 2, 4, 8, 16, 32, 64, 128, 256.
</p>

 */
class timeseries_histo {

  /**
   <p>
   Graph the coefficients.
   </p>
   */
  private void graph_coef( double[] vals )
  {
    histo graph = new histo();
    String file_name_base = "coef_histo";
    histo.mean_median m;

    int start;
    int end = vals.length;
    do {
      start = end >> 1;
      double coef[] = new double[ start ];
      int ix = 0;
      double sum = 0;
      for (int i = start; i < end; i++, ix++) {
	coef[ix] = vals[i];
	sum = sum + vals[i];
      }
      System.out.println("graph_coef: sum = " + sum );
      String file_name = file_name_base + start;
      m = graph.histogram( coef, file_name );
      if (m != null) {
	System.out.println("coef " + start + " mean = " + m.mean +
			   " median = " + m.median );
      }
      end = start;
    } while (start > 32); // while
    
  } // graph_coef


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

	  timeseries_histo tsHisto = new timeseries_histo();
	  tsHisto.graph_coef( vals );
	}
      }
  } // main

}
