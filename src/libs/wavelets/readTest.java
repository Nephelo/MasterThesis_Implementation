
import dataInput.*;

/**
  Test the time series read code.
 */
class readTest {
  public static void main( String[] args ) {
    tsRead ts = new tsRead("amat_close");
    double[] vals = ts.getArray();

    if (vals != null) {
      for (int i = 0; i < vals.length; i++) {
	System.out.println(i + "  " + vals[i] );
      }
    }
    else
      System.out.println("readTest: time series is null");
  } // main
}
