
import java.io.*;
import wavelets.*;
import wavelet_util.*;
import dataInput.*;

/**
<p>
  Generate histograms for the Haar coefficients created by
  applying the Haar transform to the time series for the
  Applied Materials (symbol: AMAT) daily close price.  Plot
  the histograms along with a normal curve with a mean
  and standard deviation calculated from the coefficients.
<p>
  There are 512 data points in the AMAT daily close price time
  series.  This program generates a histogram for the 
  first three high frequency sets of coefficients (e.g., 256, 
  128, and 64 coefficients).
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
/*class curve_plot_test {

  /**

    <p>
    Write out the log of the close price.  Daily return
    is usually expressed as the log of today's close
    minus the log of yesterday's close.  This function
    allows a plot of the log close price to be compared
    to the close price.
    </p>
   */
  /*public class plot_log extends plot {

    String class_name() { return "plot_log"; }
    
    public plot_log( double v[] ) {
      String file_name = "log_coef";
      PrintWriter prStr = OpenFile( file_name );
      if (prStr != null) {
	for (int i = 0; i < v.length; i++) {
	  prStr.println(i + " " + v[i] );
	}
	prStr.close();
      }
    }

  } // class plot_log

  
  public void plot_log( double v[] )
  {
    plot_log t = new plot_log( v );
  }


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

	  wavelets.inplace_haar haar = new wavelets.inplace_haar();
	  haar.wavelet_calc( vals );
	  haar.order();

	  bell_curves curves = new bell_curves();
	  curves.plot_curves( vals );
	}
      }
  } // main

}*/
