
package libs.wavelets.experimental;

/**
<p>
  Normal curve and normal curve graphing functions

<p>
  This class supports the following public functions:
</p>
<ul>
<li>
<p>
stddev( double v[] )
</p>
<p>
Calculate the standard deviation, mean, low and high values from an
array of doubles.
</p>
</li>
<li>
<p>
integrate_curve( point curve[] )
</p>
<p>
Integrate a normal curve over a set of histogram bins.
</p>
</li>
<li>
<p>
normal_curve(...)
</p>
<p>
Calculate a normal curve, given a mean and standard deviation.
The curve is plotted over the range from low to high.
</p>
</li>
</ul>

<p>
  There are also two public nested classes:
</p>
<ul>
<li>
<p>
bell_info: mean, standard deviation, low and high value for a normal curve
</p>
</li>
<li>
<p>
point: x and y values
</p>
</li>
</ul>

<p>
This code is largely experimental and was written to understand
how a graph of sorted Haar coefficient values relates to a
normal curve with the same mean and standard deviation.
</p>

 */

public class statistics {

   /**
      Bell curve info: mean, sigma (the standard deviation),
      low (the start of the curve area) and high (the end
      of the curve area).
      
    */
   public class bell_info {
     public bell_info() {};
     public bell_info(double m, double s, double l, double h)
     {
       mean = m;
       sigma = s;
       low = l;
       high = h;
     }
     public double mean;
     public double sigma;
     public double low;
     public double high;
   } // bell_info


  /**

     Allocate a bell_info object and initialize it
     with the arguments <i>mean</i>, <i>sigma</i>,
     <i>low</i> and <i>high</i>.

   */
  private bell_info new_info(double mean, double sigma, 
			    double low, double high)
  {
    bell_info info = new bell_info(mean, sigma, low, high);
    return info;
  } // new_info


  /**
   <p>
   Calculate the mean, standard deviation.  Also
   note the low and high of the number range.
   </p>
   <p>
   The stddev function is passed an array of numbers.
   It returns the statistics for a normal curve based
   on those numbers: mean, standard deviation, low value
   and high value.
   </p>
   */
  public static bell_info stddev( double v[] )
  {
    bell_info stats = null;
    if (v != null && v.length > 0) {
      int N = v.length;
    
      double low =  v[0];
      double high = v[0];
      // calculate the mean (a.k.a average), low and high
      double sum = 0.0;
      for (int i = 0; i < N; i++) {
	sum = sum + v[i];
	if (v[i] < low)
	  low = v[i];
	if (v[i] > high)
	  high = v[i];
      }
      System.out.println("sum = " + sum );
      double mean = sum / (double)N;

      // calculate the standard deviation sum
      double stdDevSum = 0;
      double x;
      for (int i = 0; i < N; i++) {
	x = v[i] - mean;
	stdDevSum = stdDevSum + (x * x);
      }
      double sigmaSquared = stdDevSum / (N-1);
      double sigma = Math.sqrt( sigmaSquared );

      statistics s = new statistics();
      stats = s.new_info(mean, sigma, low, high);
    }
    return stats;
  } // stddev


  /**
    A point on a graph
   */ 
  public class point {
    public double x, y;
  } // point


  /**

    Allocate an array of point objects.  The
    size allocated is given by the argument
    <i>size</i>.

   */
  private point[] point_array( int size )
  {
    point a[] = new point[ size ];
    for (int i = 0; i < size; i++) {
      a[i] = new point();
    }
    return a;
  } // point array


  /**
    <p>
    Print a histogram bin to stanard output.
    </p>

   */
  private void print_bins( histo.bin histoBins[] )
  {
    if (histoBins != null) {
      for (int i = 0; i < histoBins.length; i++) {
        System.out.println( histoBins[i].start + "  " +
			    histoBins[i].percent );
      }
    }
  } // print_bins

  /**

    <p>
    The amount of Gaussian noise in the Haar wavelet coefficients can
    be seen by graphing a histogram of the coefficients along with a
    Gaussian (normal) curve.  For this graph to be meaningful the two
    histograms must be expressed in the same scale.
    </p>

    <p>
    The statistics.normal_curve function generates a normal
    bell curve around the median where the x-axis is the 
    number range over which the curve is plotted and the 
    y-axis the the percentage probability for a point to
    appear at the associated place on the x-axis.
    </p>

    <p>
    In contrast the y-axis for the histogram calculated for the Haar
    coefficients reflects the percentage of the total points in that
    particular histogram bin range.
    </p>

    <p>
    The histogram of the Haar coefficients is plotted by calculating
    the percentage of the total for each histogram bin.  This
    means that all the Haar histogram bins sum to one.  To plot
    the normal curve in the same units as the histogram plot of the
    Haar coefficients, the normal curve is integrated over each
    histogram bin.  The area under the normal curve is one also.
    The output is written to the standard output.
    </p>

    <p>
    Summary:
    </p>
    <p>
    Input argument <i>curve</i>:
    </p>
    <pre>
    x-axis: number range
    y-axis: probability fraction
    </pre>

   */
  public void integrate_curve( point curve[] )
  {
    if (curve != null) {
      final int num_bins = 24;
      int len = curve.length;
      double low = curve[0].x;
      double high = curve[len-1].x;
      double range = high - low;
      double step = range / num_bins;
      double start = low;
      int i;

      histo.bin histoBins[] = new histo.bin[ num_bins ];
      for (i = 0; i < num_bins; i++) {
	histo t = new histo();
	histoBins[i] = t.alloc_bin();
	histoBins[i].start = start;
	histoBins[i].percent = 0;
	start = start + step;
      }
      start = low;
      double end = low + step;
      i = 0;
      int ix = 0;
      double delta;
      double area = 0.0;
      double sum = 0.0;
      while (i < curve.length-1) {
	if (curve[i].x >= start && curve[i].x < end) {
	  delta = curve[i+1].x - curve[i].x;
	  area = curve[i].y * delta;
	  histoBins[ix].percent = histoBins[ix].percent + area;
	  sum = sum + area;
	  i++;
	}
	else {
	  start = end;
	  end = end + step;
	  ix++;
	}
      } // while
      System.out.println("integrate_curve: area under curve = " + sum);
      print_bins( histoBins );
    }
  } // integrate_curve



  /**
     Calculate the information for a graph (composed of point
     objects) based on the bell_info argument.  The graph
     will have a number of point objects equal to num_points.
     The equation to calculate a normal curve is used:
     <pre>
       f(y) = (1/(s * sqrt(2 * pi)) e<sup>-(1/(2 * s<sup>2</sup>)(y-u)<sup>2</sup></sup>
     </pre>
     <p>
     Where <i>u</i> is the mean and <i>s</i> is the standard deviation.
     </p>
   */
  public static point[] normal_curve( bell_info info, int num_points )
  {
    point[] graph = null;

    if (info != null) {
      statistics stat = new statistics();
      graph = stat.point_array( num_points );

      double s = info.sigma;
      // calculate 1/(s * sqrt(2 * pi)), where <i>s</i> is the stddev
      double sigmaSqrt = 1.0 / (s * (Math.sqrt(2 * Math.PI)));
      double oneOverTwoSigmaSqrd = 1.0 / (2 * s * s);

      double range = info.high - info.low;
      double step = range / num_points;
      double y = info.low;
      double f_of_y;
      double t;
      for (int i = 0; i < num_points; i++) {
	graph[i].x = y;
	t = y - info.mean;
	f_of_y = sigmaSqrt * Math.exp( -(oneOverTwoSigmaSqrd * t * t) );
	graph[i].y = f_of_y;
	y = y + step;
      }
    }
    return graph;
  } // normal_curve
  
} // statistics
