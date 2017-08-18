
package libs.wavelets.double_vec;

/**
 *
<p>
   class double_vec
</p>
<p>
   The double_vec class is based on a C++ template (sadly Java does
   not have generics yet).  This class (and the template it is based
   on) is similar in design to the C++ Standard Template Library
   <vector> template.  The double_vec class supports a growable array
   to which elements can be continuously added.
</p>
<p>
   The double_vec class is designed for dense arrays where the all
   elements of the array are used (e.g., there are no empty elements).
</p>
   Usage:
<ul>
<li>
<p>
       Elements are added to the end of the array via the append()
       function.
</p>
</li>
<li>
<p>
       Elements in the array can be accessed via the elementAt()
       function (Java has no operator overloading so the [] operator
       cannot be used).
</p>
</li>
</ul>

<p>
  A doubling algorithm is used when the data size is expanded because
  it minimizes the amount of copying that must be done.  The array
  will quickly grow to a the size needed to accomodate the data set
  and no more copying will be necessary.  For large arrays this has the
  drawback that more memory may be allocated than is needed, since
  the amount of memory used grows exponentially.
</p>

<p>
  The template on which the double_vec class was based was
  used to implement a String container class.  As a result,
  this class may be overkill as a double container.
</p>

<h4>
Usage
</h4>

<p>
  Using the <tt>elementAt()</tt> and <tt>setElementAt()</tt> functions
  can obscure the clarity of numeric code.  One way around this is to
  use the <tt>getData()</tt> function to return the array and the
  <tt>length()</tt> function to return the amount of data in the
  array.  (Note that the array will usually be larger than the number
  of data elements).
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


public class double_vec {
  private final static int StartArraySize = 128;
  private int num_elem;     // number of data elements currently in the array
  private int array_size;   // array size (e.g., data capacity)
  double[] array;

  /**
   *
<p>
     Double the amount of memory allocated for the array.
     Return true if memory allocation succeeded, false
     otherwise.

   */
  private void twice()
  {

    double[] old_array = array;
    int new_size = array_size * 2;
    
    // if "new" fails it will throw an OutOfMemoryError exception
    array = new double[ new_size ];
    for (int i = 0; i < array_size; i++) {
      array[i] = old_array[i];
    }
    
    array_size = new_size;
  } // twice


  /**
   *

     Allocate an array whose initial size is StartArraySize

   */
  public double_vec()
  {
    array = new double[ StartArraySize ];
    num_elem = 0;
    array_size = StartArraySize;
  } // double_vec constructor


  /**
   *
     Return the number of elements currently in the array
   */
  public int length() { return num_elem; }

  /**
   *
      Assign array element <tt>ix</tt> the value <tt>val</tt>.
   */
  public void setElementAt( double val, int ix ) 
       throws IndexOutOfBoundsException
  {
    if (ix >= num_elem) {
      String msg = "double_vec: setElementAt(" + ix + ") out of bounds";
      throw new IndexOutOfBoundsException(msg);
    }

    array[ix] = val;
  }


  /**
   *
     Return the array element at index <tt>ix</tt>.  If
     <tt>ix</tt> is out of range an 
     IndexOutOfBoundsException will be thrown.

   */
  public double elementAt( int ix ) 
       throws IndexOutOfBoundsException
  {
    if (ix >= num_elem) {
      String msg = "double_vec: elementAt(" + ix + ") out of bounds";
      throw new IndexOutOfBoundsException(msg);
    }

    return array[ix];
  }

  /**
   *
      Return a reference to the double_vec array.
   */
  public double[] getData() { return array; }


  /**
   *
      Append an item to the end of the array
   */
  public void append( double val )
  {

    if (num_elem == array_size) {
      twice();
    }

    array[ num_elem ] = val;
    num_elem++;
  } // append


  /**
   *
<p>

   Expand the number of array data slots by "amount" elements.  Note
   that "array_size" is the total amount of storage available for data
   slots.  "num_elem" is the number of data slots occupied by data.
   The bounds over which the array can be indexed is governed by
   num_elem.  Note that after expand() is called the new data elements
   can be read, but their value is undefined until they are
   initialized.

   */
  public void expand( int amount )
  {
    while (num_elem + amount >= array_size) {
      twice();
    }
    num_elem += amount;
  } // expand


  /**
   *
     Remove one item from the end of the array.
   */
  public void remove()
  {
    if (num_elem > 0)
      num_elem--;
  } // remove

  /**
   *
<p>
   Set the number of data elements in the array to
   a new value (note that this will usually be
   smaller than the array size, unless a power of
   two is chosen for "new_size").
   */
  public void set_size( int new_size )
  {
    if (new_size <= array_size) {
      num_elem = new_size;
    }
    else { // new_size > array_size
      int num_new_elem = new_size - num_elem;
      expand( num_new_elem );
    }
  } // set_size

} // double_vec

