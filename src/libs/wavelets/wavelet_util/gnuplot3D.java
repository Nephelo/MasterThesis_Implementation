
package libs.wavelets.wavelet_util;

import java.io.*;

/**
<p>
  Define the class gnuplot3D for the wavelet_util package.

<p>
  The class outputs a Haar wavelet spectrum array in a format
  that can be read by gnuplot for 3D plotting.  This function
  can be used to plot either Haar wavelet spectrums or
  Haar wavelet coefficients.

<p>
  The constructor is given an array of Haar spectrum values and
  a file name.  The Haar spectrum values will be written out
  to the file so that they can be graphed.

<p>
  The length of the array must be 2<sup>N</sup>.  The lengths
  of the spectrums are 2<sup>N-1</sup>, ... 2<sup>0</sup>.
  If the original data was
<pre>
{32.0, 10.0, 20.0, 38.0, 37.0, 28.0, 38.0, 34.0, 
 18.0, 24.0, 18.0,  9.0, 23.0, 24.0, 28.0, 34.0}
</pre>
<p>
The Haar spectrum will be:
</p>
<pre>
0.0
25.9375
29.625 22.25
25.0 34.25 17.25 27.25
21.0 29.0 32.5 36.0 21.0 13.5 23.5 31.0
</pre>
<p>
If the original data length was 2<sup>N</sup>, then the total
length of the spectrum data will be 2<sup>N</sup>-1, so the
first element is zeroed out in the case of a Haar spectrum.
In the case of the wavelet coefficients, the first value will 
be the average for the entire sample.  In either case this
value will not be output.
</p>
<p>
The plot used to display the Haar wavelet spectrums is modeled after
the plots shown in Robi Polikar's <a
href="http://engineering.rowan.edu/~polikar/WAVELETS/WTpart3.html">Wavelet
Tutorial, Part III</a>.  Here the x-axis represents the offset in the
data.  The y-axis represents the width of the Haar window (which will
be a power of two) and the z-axis represents the spectrum value.
</p>

<p>
In order for gnuplot to display a 3D surface each line must have the
same number of points.  The wavelet spectrum is graphed over the
original rage.  The first spectrum repeats two values for each
average or coefficient calculated.  The second spectrum repeats
each value four times, the third spectrum eight times, etc...
</p>

 */
public class gnuplot3D extends plot {

  String class_name() { return "gnuplot3D"; }

  /**

    Output a Haar spectrum where the x-axis is the sample value
    number, the y-axis is the log<sub>2</sub> of the window width and
    the z-axis is the value (e.g., average or average difference).

   */
  private void outputSpectrum( PrintWriter prStr, 
			       double[] values,
			       int end,
			       int windowWidth ) {
    if (end > 1) {
      if (windowWidth > 1)
	prStr.println();

      int l = binary.log2( windowWidth );

      int windowStart = 0;
      int windowEnd = windowWidth;
      int start = end >> 1;
      for (int i = start; i < end; i++) {
	for (int j = windowStart; j < windowEnd; j++) {
	  prStr.println( j + "  " +  l + "  " + values[ i ] );
	}
	windowStart = windowEnd;
	windowEnd = windowEnd + windowWidth;
      } // for
      end = start;
      windowWidth = windowWidth << 1;  // windowWidth = windowWidth * 2
      outputSpectrum( prStr, values, end, windowWidth );
    }
  } // outputSpectrum


  public gnuplot3D( double[] values, String path ) {
    PrintWriter prStr = OpenFile( path );
    
    if (prStr != null) {
      prStr.println("#");
      prStr.println("# Wavelet spectrum data formatted for gnuplot.");
      prStr.println("# To plot, use the command \"splot '<file>'\"");
      prStr.println("# were <file> is the file name.");      
      prStr.println("#");
      prStr.println("# {x, y, z} = point number, log2(windowWidth), value");
      prStr.println("#");

      int len = binary.nearestPower2( values.length );
      int windowWidth = 2;
      outputSpectrum( prStr, values, len, windowWidth );
      prStr.close();
    }
  } // gnuplot3D

} // gnuplot3D
