
package libs.wavelets.experimental;

import java.io.*;

class plot {

  PrintWriter OpenFile( String path ) {
    PrintWriter prStr = null;

    try {
      FileOutputStream outStr = new FileOutputStream( path );
      prStr = new PrintWriter( outStr );
    }
    catch (Exception e) {
	System.out.println("gnuplot3D: file name = " + path + ", " +
			   e.getMessage() );
    }

    return prStr;
  } // OpenFile

} // plot
