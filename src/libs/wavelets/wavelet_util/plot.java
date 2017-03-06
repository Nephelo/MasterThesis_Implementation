
package wavelet_util;

import java.io.*;

public abstract class plot {

  abstract String class_name();

  public PrintWriter OpenFile( String path ) {
    PrintWriter prStr = null;

    try {
      FileOutputStream outStr = new FileOutputStream( path );
      prStr = new PrintWriter( outStr );
    }
    catch (Exception e) {
	System.out.println( class_name() + ": file name = " + path + ", " +
			   e.getMessage() );
    }

    return prStr;
  } // OpenFile

} // plot
