
package dataInput;

import java.io.*;
import double_vec.*;

/**
<p>
  class tsRead

<p>
  Read a time series file.  The format of the file is
</p>
<pre>
  <i>date</i>  <i>double-value</i>
</pre>
<p>
  This class was written with daily financial time series
  in mind.  It needs to be modified to handle intra-day
  time series or any other time series that has a time
  stamp as well.
</p>
<p>
  The class is used by instantiating the constructor
  with the file name for the time series.  For example:
</p>
<pre>
  tsRead ts = new tsRead( <i>time_series</i> );
</pre>
<p>
  The tsRead class reads the time series into a double_vec
  object.
</p>
<p>
  Once data is read into the object (e.g., the object is
  constructed) the data is referenced by getting it
  as an array (double[]) via the getArray function.
</p>
<p>
  Getting the data as an array is inefficient, since the
  data must be copied into the array first.  This course
  is followed for two reasons:
</p>
  <ol>
  <li>
  <p>
  The size of the input data is not known in advance, so
  a growable data structure, like double_vec is used.
  </p>
  </li>

  <li>
  <p>
  Although the double_vec data structure works well for
  storing the input data, it is awkward when it comes to
  numeric computation, since the only way to reference
  elements is via the elementAt() function.  Compared
  to the [] operator, this tends to obscure numeric
  algorithms making the code more difficult to understand.
  This is a real drawback for the wavelet algorithms, which
  are already complex enough.
  </p>
  </li>
</ol>
<p>
  So the input data is referenced via an array, even though
  we must pay the cost of copying.  Although operator overloading
  can create its own problems, this is a case where operator
  overloading would be useful, since in C++ double_vec could
  have been used directly.
</p>
  
<h4>
   Copyright and Use
</h4>

<p>
   You may use this source code without limitation and without
   fee as long as you include:
</p>
<blockquote>
     This software was written and is copyrighted by Ian Kaplan, Bear
     Products International, www.bearcave.com, 2001.
</blockquote>
<p>
   This software is provided "as is", without any warrenty or
   claim as to its usefulness.  Anyone who uses this source code
   uses it at their own risk.  Nor is any support provided by
   Ian Kaplan and Bear Products International.
<p>
   Please send any bug fixes or suggested source changes to:
<pre>
     iank@bearcave.com
</pre>


  */
public class tsRead {
  /**
    class badData : an exception for bad
    data in the file.

   */
  private class badDataError extends Exception {
    public badDataError() { super(); }
    public badDataError( String m ) { super(m); }
  } // badData

  PushbackInputStream pushStream;
  private double_vec timeSeries;
  private boolean fileOpen = false;


  /**

    skip_spaces

    Skip white space characters.  As with all the code in this class,
    it is assumed that characters are 8-bits.  Return true if EOF is
    reached.  Otherwise return false.

    The function will read until a non-white character or end of
    file is reached.  The non-white space character is pushed back
    into the input stream.

   */
  private boolean skip_spaces()
  {
    int ch = 0;

    try {
      while (Character.isWhitespace( (char)(ch = pushStream.read()) ))
	/* nada */;

      if (ch > 0) {
	try {
	  pushStream.unread( ch );
	}
	catch (Exception e) {
	  System.out.println("skip_spaces: push back error - " +
			     e.getMessage() );
	}
      }
    }
    catch (Exception e) {
      System.out.println("skip_spaces: read error - " + e.getMessage() );
    }
    return (ch == 0);
  } // skip_spaces

  /**
   *

<p>
    read_date
<p>
    Currently dates are skipped.  The time series
    code assumes that dates are continuous and there
    are no holes in the data.
<p>
    For consistency sake there is some checking done.
    The code assumes that dates are composed of numbers
    or '/' characters.
<p>
    When this class is called it is assumed that the
    first character in the stream is a number.  The
    function reads numbers or '/' characters until 
    the end of line is reached or there is a space.
<p>
    The format for a date is
<pre>
       (number)+ '/' (number)+ / (number)+
</pre>
   */
  private boolean read_date()
       throws badDataError
  {
    int ch = 0;
    boolean foundDigit = false;
    boolean rslt = false;
    badDataError err = new badDataError( "Bad date format" );

    try {
      for (int i = 0; i < 3; i++) {
	foundDigit = false;
	while (Character.isDigit( (char)(ch = pushStream.read()) )) {
	  foundDigit = true;
	}
	
	if (ch < 0) {
	  rslt = true;  // we've reached EOF
	  break;
	}
	
	if (! foundDigit ) throw err;
	
	if (i < 2)
	  if ((char)ch != '/') throw err;
      } // for
    }
    catch (Exception e) {
      System.out.println("read_date: read error - " + e.getMessage() );
    }

    if (foundDigit) {
      // push the character after the date back into the
      // input stream.
      try {
	pushStream.unread( ch );
      }
      catch (Exception e) {
	System.out.println("read_date: push back error - " +
			   e.getMessage() );
      }
    }

    return rslt;
  } // read_date



  /**
     read_double
<p>
     Read the character string for a floating point
     value and convert it into a double.  The
     format for a double is
<pre>
       [+|-] (digit)+ '.' (digit)+
</pre>
<p>
     The function is passed a reference to an array of
     doubles.  It returns dv[0] with the value (Java has
     not pass by reference except via objects).
     
     If the function reaches EOF, it return false, otherwise
     true.

   */
  private boolean read_double( double[] dv )
       throws badDataError
  {
    final int DBL_SIGN  = 1; // + | - 
    final int DBL_INT   = 2; // the part that is > 1 => (digit)+
    final int DBL_FRAQ  = 3; // fraction after '.' => (digit)+
    final int DBL_DONE  = 4; // finished parsing double
    int state = DBL_SIGN;

    String s = new String();
    boolean rslt = false;
    char ch = 0;
    
    dv[0] = 0.0;

    try {
      while (state != DBL_DONE) {
	ch = (char)(pushStream.read());

	if (ch > 0xff) { // EOF, since we're not supporting wide chars
	  rslt = true;
	  state = DBL_DONE;
	}

	switch (state) {

        case DBL_SIGN:
	  if (ch == '+' || ch == '-' || ch == '.' || Character.isDigit( ch )) {
	    s = s + ch;
	    if (ch == '.')
	      state = DBL_FRAQ;
	    else
	      state = DBL_INT;
	  }
	  else {
	    badDataError err = new badDataError();
	    throw err;
	  }
	  break;

        case DBL_INT:
	  if (Character.isDigit( ch ) || ch == '.') {
	    s = s + ch;

	    if (ch == '.')
	      state = DBL_FRAQ;
	  }
	  else {
	    state = DBL_DONE;
	  }
	  break;

        case DBL_FRAQ:
	  if (Character.isDigit( ch )) {
	    s = s + ch;
	  }
	  else {
	    state = DBL_DONE;
	  }
	  break;
	  
	} // switch
	
      } // while
    } // try
    catch (Exception e) {
      System.out.println("read_double: error reading file - " +
			 e.getMessage() );
    }

    // push the last character back into the input stream
    try {
      pushStream.unread( ch );
    }
    catch (Exception e) {
      System.out.println("read_double: push back error - " +
			 e.getMessage() );
    }

    if (s.length() > 0) {  // convert string to double
      Double dblObj = new Double( s );
      dv[0] = dblObj.doubleValue();
    }

    return rslt;
  }  // read_double



  /**
    <p>
    time_series_read

    <p>
    Read a time series file into a double_vec object

   */
  private void time_series_read()
  {
    timeSeries = new double_vec();
    boolean end_of_file = false;
    double dv[] = new double[1];

    try {
      while (! end_of_file ) {
	end_of_file = skip_spaces();
	if (! end_of_file) {

	  end_of_file = read_date();
	  if (! end_of_file) {

	    end_of_file = skip_spaces();
	    if (! end_of_file) {

	      end_of_file = read_double( dv );
	      if (! end_of_file) {
		timeSeries.append( dv[0] );
	      } // read_double
	    } // skip_spaces
	  } // read_date
	} // skip_spaces
      } // while
    }
    catch (Exception e) {
      if (e instanceof badDataError) {
	System.out.println("bad data: " + e.getMessage() );
      }
    }
  } // time_series_read


  private boolean openStream( String name )
  {
    boolean rslt = false;

    try {
      FileInputStream fStream;

      fStream = new FileInputStream( name );
      // Allocating the FileInputStream opens the file.
      // If the open fails, either because access is not
      // allowed or because it does not exist, 
      // an exception will be thrown.  If this happens we
      // will not proceed to the next line.
      pushStream = new PushbackInputStream( fStream );
      rslt = true;
    }
    catch (Exception e ) {
      if (e instanceof FileNotFoundException) {
        System.out.println("could not open file " + name );
      }
      else if (e instanceof SecurityException) {
        System.out.println("not allowed to open file " + name );
      }
      else {
        System.out.println(e.toString() + "unexpected exception" );
      }
    }
    return rslt;
  } // openStream


  /**
    setSize
<p>
    Set the size of the double_vec.  This
    function is useful if the size needs to
    be set to the nearest power of two less
    than or equal to the data size before getting
    the data as an array.  The wavelet algorithms
    do no work on arrays whose length is not 
    a power of two.
    
   */
  public void setSize( int size )
  {
    if (timeSeries != null) {
      timeSeries.set_size( size );
    }
  }  // setSize


  /**
    getSize

    Return the size of the double_vec.
   */
  public int getSize() {
    int size = 0;

    if (timeSeries != null) {
      size = timeSeries.length();
    }
    return size;
  } // getSize


  /**
    getArray
<p>
    Return the data in a double[] object.
   */
  public double[] getArray() {
    double[] dblArray = null;

    if (timeSeries != null) {
      int len = timeSeries.length();
      
      if (len > 0) {
	dblArray = new double[len];
	for (int i = 0; i < len; i++) {
	  dblArray[i] = timeSeries.elementAt( i );
	} // for
      }
    }

    return dblArray;
  } // getArray


  /**
   <p>
    <tt>tsRead</tt> constructor

   <p>
    The <tt>tsRead</tt> constructor is passed a file
    (or file name path) name.  If the open succeeds
    the time series will be read into the object.
    The time series is read into a double_vec object
    which is returned by the getDoubleVec() function.

   */
  public tsRead( String file_name )
  {
    if (openStream( file_name )) {
      time_series_read();
    }
  } // tsRead

} // tsRead

