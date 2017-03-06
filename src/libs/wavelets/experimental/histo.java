
package experimental;

import java.io.*;

import sort.*;

/**
  
<p>
  Support for generating histograms for the Haar wavelet
  coefficients.
<p>
  

 */

public class histo extends plot {

  public class mean_median {
    public double mean;    // mean (a.k.a. average
    public double median;  // middle of the sorted number sequence
    public double low;     // low value
    public double high;    // high value
  }

  public class bin {
    public bin() {};
    public double start; // x-axis
    public double percent;    // y-axis 
  } // bin

  public bin alloc_bin()
  {
    bin b = new bin();
    return b;
  }


  /**
    Generate a histogram array from the input vector.  The
    data in the input vector is modified.
<p>
    The values to be histogrammed are sorted to obtain a low and a
    high value.
</p>
<p>
    The histogram is calculated by dividing the number range into a
    set of bins.  Each bin has a start value.  The end value is
    the start of the next bin.  The sorted input array is traversed
    and the number in each bin range are counted.  Each bin is set
    to the associated count.  The histogram is graphed with the
    bin range on the x-axis and the count on the y-axis.
</p>
<p>
    The histogram range has zero elements at the top and bottom to
    improve the appearance of the histogram when plotted with
    gnuplot.
</p>
<p>
    The function also returns the mean (a.k.a average) and the
    median in the argument <i>m</i>.  The median is the middle
    of a sorted list of numbers.  In the case of the wavelet
    algorithm, all arrays are a power of two.  So the middle
    value is calculated from the two values on either side
    of the middle.  For example, if the vector has 32 numbers,
    the median is calculated from the average of v[15] and
    v[16].
</p>
   */
  private bin[] calcHisto( double v[], mean_median m ) 
  {
    bin histoBins[] = null;
    m.mean = 0.0;
    m.median = 0.0;

    if (v != null && v.length > 0) {
      final int numSteps = 21;

      qsort.sort( v );

      double step = (v[v.length-1] - v[0]) / numSteps;

      double start = v[0] - step;
      histoBins = new bin[ numSteps + 3 ];
      for (int i = 0; i < histoBins.length; i++) {
	bin b = new bin();
	b.percent = 0.0;
	b.start = start;
	start = start + step;
	histoBins[i] = b; 
      }

      int binIx = 0;
      int i = 0;
      start = v[0] - step;
      double end = v[0];
      histoBins[binIx].start = start;
      histoBins[binIx].percent = 0.0;
      double sum = 0.0;
      int count = 0;
      double total = 0;
      while (i < v.length && binIx < histoBins.length) {
	if (v[i] >= start && v[i] < end) {
	  sum = sum + v[i];
	  count++;
	  i++;
	}
	else {
	  histoBins[binIx].percent = (double)count / v.length;
	  total = total + histoBins[binIx].percent;
	  count = 0;
	  binIx++;
	  if (binIx < histoBins.length) {
	    start = end;
	    end = start + step;
	  }
	}
      } // while
      System.out.println("Total = " + total );
      System.out.println("sum = " + sum );
      if (m != null) {
	int half = v.length >> 1;  // half = length / 2;
	m.mean = sum / v.length;
	m.median = (v[half-1] + v[half])/2;
	m.low = v[0];
	m.high = v[v.length-1];
      }
    } // if
    return histoBins;
  } // calcHisto


  /**
   <p>
   histo class constructor
   </p>
   <p>
   The histo class constructor is initialized with an array of doubles
   and a String that contains the path for a file name.  The
   constructor writes out a file that contains histogram plot data
   formatted for gnuplot.  The input array <i>vals</i> is sorted.
   </p>

   <p>
   The function returns the mean (a.k.a. average) and the 
   median in a two element double array.
   </p>
   */
  public mean_median histogram( double vals[], String path ) {
    mean_median m = null;
    if (vals != null && vals.length > 0) {
      PrintWriter prStr = OpenFile( path );
      if (prStr != null) {
	m = new mean_median();
	bin histoBins[] = calcHisto( vals, m );
	prStr.println("#");
	prStr.println("# mean = " + m.mean);
	prStr.println("# median = " + m.median );
	prStr.println("#");
	for (int i = 0; i < histoBins.length; i++) {
	  prStr.println(" " + histoBins[i].start + 
			" " + histoBins[i].percent );
	}
	prStr.close();
      }
    }
    return m;
  } // histogram

} // histo
