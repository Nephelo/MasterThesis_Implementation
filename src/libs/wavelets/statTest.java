package libs.wavelets;

import libs.wavelets.dataInput.*;
import libs.wavelets.wavelets.*;
import libs.wavelets.wavelet_util.binary;
import libs.wavelets.experimental.*;

/**
  Test the experimental code to generate a normal curve
  with the mean and standard deviation derived from
  a coefficient spectrum (in this case the highest
  frequency spectrum).

 */
class statTest {

  public static void print_curve( libs.wavelets.experimental.statistics.bell_info info,
				  statistics.point curve[] )
  {
    System.out.println("#");
    System.out.println("# mean = " + info.mean );
    System.out.println("# stddev = " + info.sigma );
    System.out.println("#");
    for (int i = 0; i < curve.length; i++) {
      System.out.println( curve[i].x + "  " + curve[i].y );
    }
    System.out.println();
  }


  public static void main( String[] args )
  {
    tsRead ts = new tsRead("amat_close");
    int len = ts.getSize();
    if (len > 0) {
      len = binary.nearestPower2(len);
      ts.setSize( len );
      double vals[] = ts.getArray();

      libs.wavelets.wavelets.inplace_haar haar = new inplace_haar();
      haar.wavelet_calc( vals );
      haar.order();
      int end = vals.length;
      int start = end >> 1;
      double coef[] = new double[ start ];
      int ix = 0;
      for (int i = start; i < end; i++) {
	coef[ix] = vals[i];
	ix++;
      }

      statistics.bell_info info = statistics.stddev( coef );
      if (info != null) {
	statistics.point curve[] = statistics.normal_curve(info, start);
	// print_curve( info, curve );
	statistics stat = new statistics();
        stat.integrate_curve( curve );
      }
    } 
  } // main

} // statTest
