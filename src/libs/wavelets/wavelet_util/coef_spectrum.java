

package wavelet_util;

import java.io.*;


/**
  <p>
  After the wavelet transform is calculated, regenerate the time
  series with a given spectrum set to zero, or with all but a given
  spectrum set to zero.  The plots are generated from the highest
  frequency spectrum to the lower frequency spectrums.  The
  highest frequency spectrum is left out of later plots since
  this spectrum contains most of the noise.
  </p>

  <p>
  Wavelets allow a time series to be examined by
  filtering the component spectrum.  For example, the
  Haar wavelet transform can be calculated and the
  highest frequency spectrum of coefficients can
  be set to zero.  When the reverse Haar transform
  is calculated, the time series will be regenerated
  without this spectrum.
  </p>

  <p>
  Wavelets can also be used to look at a single
  spectrum in isolation.  This can be done by
  setting all but the one spectrum to zero and
  then regenerating the time series.  This will
  result in a time series showning the contribution
  of that spectrum.
  </p>

 */
public class coef_spectrum extends plot {

   final private int min_spectrum = 64;

   String class_name() { return "coef_spectrum"; }

  /**
    Regenerate the time series from the coefficient
    array and output the time series to a file.
   */
  private void output_time_series( String file_name, double coef[] )
  {
    PrintWriter prStr = OpenFile( file_name );
    if (prStr != null) {
      wavelets.inplace_haar haar = new wavelets.inplace_haar();
      haar.setWavefx( coef );
      haar.setIsOrdered();
      haar.inverse();      // calculate the inverse Haar transform

      // the time series has been regenerated in the coef array
      for (int i = 0; i < coef.length; i++) {
	prStr.println( i + "  " + coef[i] );
      }
      prStr.close();
    }
  } // output_time_series


  /**
    Make a copy of the coefficient array.  If limit
    is greater than zero copy up to limit, otherwise
    copy the entire array.
   */
  private double[] copy_coef( double coef[], int limit )
  {
    double new_array[] = null;

    if (coef != null) {
      new_array = new double[ coef.length ];
      int end = coef.length;
      if (limit > 0)
	end = limit;
	
      for (int i = 0; i < end; i++) {
	new_array[i] = coef[i];
      }
    }
    return new_array;
  } // copy_coef


  /**
    <p>
    Moving from the high frequency coefficient spectrum
    to the lower frequency spectrum, set each spectrum
    to zero and output the regenerated time series to
    a file.
    </p>

    <p>
    The highest frequency spectrum contains most of the
    noise so when subsequent spectrum are set to zero,
    the highest frequency spectrum is not included.
    </p>

   */
  public void filter_one_spectrum( double coef[] )
  {
    int end = coef.length;
    int start = end >> 1;
    int noise_start = start;
    int limit = 0;
    while (start >= min_spectrum) {
      double new_array[] = copy_coef( coef, limit );
      // set the spectrum to zero
      for (int i = start; i < end; i++) {
	new_array[i] = 0;
      }
      String file_name = "all_but_" + start;
      output_time_series( file_name, new_array );
      end = start;
      start = end >> 1;
      limit = noise_start;
    }
  } // filter_one_spectrum


  /**
    <p>
    Moving from high frequency to lower frequency, regenerate
    the time series from only one spectrum.
    </p>
    <p>
    Note that coef[0] contains the time series average and
    must exist for all coefficient arrays in order to 
    regenerate the time series.
    </p>
   */
  public void only_one_spectrum( double coef[] )
  {
    int end = coef.length;
    int start = end >> 1;
    while (start >= min_spectrum) {
      double new_array[] = new double[ coef.length ];
      for (int i = 0; i < min_spectrum; i++) {
	new_array[i] = coef[i];
      }
      // Copy spectrum 
      for (int i = start; i < end; i++) {
	new_array[i] = coef[i];
      }
      String file_name = "only_" + start;
      output_time_series( file_name, new_array );
      end = start;
      start = end >> 1;
    }    
  } // only_one_spectrum

} // coef_spectrum
