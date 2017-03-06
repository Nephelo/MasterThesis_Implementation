

package wavelets;

import java.util.*;

import data.HaarData;
import wavelet_util.*;

/**
 *
<p>
  Class simple_haar

<p>
   This object calcalculates the "ordered fast Haar wavelet
   transform".  The algorithm used is the a simple Haar wavelet
   algorithm that does <u>not</u> calculate the wavelet transform
   in-place.  The function works on Java double values.
<p> 
   The wavelet_calc function is passed an <b>array</b> of doubles from
   which it calculates the Haar wavelet transform.  The transform is
   not calculated in place.  The result consists of a single value and
   a Vector of coefficients arrays, ordered by increasing frequency.
   The number of data points in the data used to calculate the wavelet
   must be a power of two.
<p>
   The Haar wavelet transform is based on calculating the Haar step
   function and the Haar wavelet from two adjacent values.  For
   an array of values S0, S1, S2 .. Sn, the step function and
   wavelet are calculated as follows for two adjacent points,
   S0 and S1:

<pre>
      <i>HaarStep</i> = (S0 + S1)/2  <i>// average of S0 and S1</i>
      <i>HaarWave</i> = (S0 - S1)/2  <i>// average difference of S0 and S1</i>
</pre>

<p>
   This yields two vectors: <b>a</b>, which contains the
   <i>HaarStep</i> values and <b>c</b>, which contains the
   <i>HaarWave</i> values.

<p> The result of the <tt>wavelet_calc</tt> is the single Haar value
    and a set of coefficients.  There will be ceil( log<sub>2</sub>(
    values.length() )) coefficients.

@author Ian Kaplan   

@see <i>Wavelets Made Easy by Yves Nieverglt, Birkhauser, 1999</i>

  
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

public class simple_haar extends wavelet_base {
  private double haar_value;  // the final Haar step value
  private Vector coef;        // The Haar coefficients
  private double[] data;

/**
 *      
<p>
   Calculate the Haar wavelet transform
   (the ordered fast Haar wavelet tranform).
   This calculation is <u>not</u> done in place.

<p>
  @param values
         a <tt>values</tt>: an array of double
         values on which the Haar transform is
         applied.

 */
  public void wavelet_calc( double[] values )
  {
    
    if (values != null) {
      data = values;
      coef = new Vector();
      haar_value = haar_calc(Arrays.copyOf(values, values.length));
      reverseCoef();
    }
  } // wavelet_calc


  /**

    The Haar transform coefficients are generated from the longest
    coefficient vector (highest frequency) to the shortest (lowest
    frequency).  However, the reverse Haar transform and the display
    of the values uses the coefficients from the lowest to the
    highest frequency.  This function reverses the coefficient 
    order, so they will be ordered from lowest to highest frequency.

   */
  private void reverseCoef() {
    int size = coef.size();
    Object tmp;

    for (int i = 0, j = size-1; i < j; i++, j--) {
      tmp = coef.elementAt(i);
      coef.setElementAt(coef.elementAt(j), i);
      coef.setElementAt(tmp, j);
    } // for
  } // reverseCoef


  
  /**
   * 

    Recursively calculate the Haar transform.  The result
    of the Haar transform is a single integer value
    and a Vector of coefficients.  The coefficients are
    calculated from the highest to the lowest frequency.
<p>
    The number of elements in <tt>values</tt> must be a power of two.

   */
  private double haar_calc( double[] values )
  {
    double retVal;

    double[] a = new double[ values.length/2 ];
    double[] c = new double[ values.length/2 ];

    for (int i = 0, j = 0; i < values.length; i += 2, j++) {
      a[j] = (values[i] + values[i+1])/2;
      c[j] = (values[i] - values[i+1])/2;
    }
    coef.addElement( c );
    
    if (a.length == 1)
      retVal = a[0];
    else
      retVal = haar_calc( a );

    return retVal;
  } // haar_calc


  /**
   *

<p>
     Calculate the inverse haar transform from the coefficients
     and the Haar value.
<p>
     The inverse function will overwrite the original data
     that was used to calculate the Haar transform.  Since this
     data is initialized by the caller, the caller should
     make a copy if the data should not be overwritten.
<p>
     The coefficients are stored in in a Java <tt>Vector</tt>
     container.  The length of the coefficient arrays is ordered in
     powers of two (e.g., 1, 2, 4, 8...).  The inverse Haar function
     is calculated using a butterfly pattern to write into the data
     array.  An initial step writes the Haar value into data[0].  In
     the case of the example below this would be

<pre>
     data[0] = 5.0;
</pre>
<p>
     Then a butterfly pattern is shown below.  Arrays indices start at
     0, so in this example <tt>c[1,1] is the second element of the
     second coefficient vector.

<pre>
wavelet:
{[5.0];
-3.0;
0.0, -1.0;
1.0, -2.0, 1.0, 0.0}

tmp = d[0];
d[0] = tmp + c[0, 0]
d[4] = tmp - c[0, 0]

tmp = d[0];
d[0] = tmp + c[1, 0]
d[2] = tmp - c[1, 0]
tmp = d[4];
d[4] = tmp + c[1, 1]
d[6] = tmp - c[1, 1]

tmp = d[0];
d[0] = tmp + c[2, 0]
d[1] = tmp - c[2, 0]
tmp = d[2];
d[2] = tmp + c[2, 1]
d[3] = tmp - c[2, 1]
tmp = d[4];
d[4] = tmp + c[2, 2]
d[5] = tmp - c[2, 2]
tmp = d[6];
d[6] = tmp + c[2, 3]
d[7] = tmp - c[2, 3]
</pre>

   */
  public void inverse() {
    if (data != null && coef != null && coef.size() > 0) {
      int len = data.length;

      data[0] = haar_value;

      if (len > 0) {
	// System.out.println("inverse()");
        byte log = binary.log2( len );

        len = binary.power2( log );  // calculation must be on 2 ** n values

	int vec_ix = 0;
	int last_p = 0;
	byte p_adj = 1;

        for (byte l = (byte)(log-1); l >= 0; l--) {

          int p = binary.power2( l );
	  double c[] = (double[])coef.elementAt( vec_ix );

	  int coef_ix = 0;
          for (int j = 0; j < len; j++) {
            int a_1 = p * (2 * j);
	    int a_2 = p * ((2 * j) + 1);

            if (a_2 < len) {
	      double tmp = data[a_1];
	      data[ a_1 ] = tmp + c[coef_ix];
	      data[ a_2 ] = tmp - c[coef_ix];
	      coef_ix++;
            }
            else {
              break;
            }
          } // for j
	  last_p = p;
	  p_adj++;
	  vec_ix++;
        } // for l
      }
      
    }
  } // inverse


  /**
      Print the simple Haar object (e.g, the
      final Haar step value and the coefficients.

   */
  public void pr() {
    if (coef != null) {
      System.out.print("{[" + haar_value + "]");
      int size = coef.size();
      double[] a;
      
      for (int i = 0; i < size; i++) {
	System.out.println(";");
	a = (double[])coef.elementAt(i);
	for (int j = 0; j < a.length; j++) {
	  System.out.print( a[j] );
	  if (j < a.length-1) {
	    System.out.print(", ");
	  }
	} // for j
      } // for i
      System.out.println("}");
    }
  } // pr

    public HaarData getCoefficients() {
        return new HaarData(haar_value, coef);
    }

    public void setCoefficients(HaarData haarData) {
        int size = 1;
        haar_value = haarData.getMean();

        Vector vector = new Vector();
        for(double[] c : haarData.getCoefficients()) {
            vector.add(Arrays.copyOf(c, c.length));
            size += c.length;
        }
        coef = vector;
        data = new double[size];
    }

    public double[] getData() {
        return data;
    }


  /**
   *

<p>
     Print the data values.

<p>
     The <tt>pr()</tt> method prints the coefficients in increasing
     frequency.  This function prints the data values which were
     used to generate the Haar transform.

   */
  public void pr_values() {
    if (data != null) {
      System.out.print("{");
      for (int i = 0; i < data.length; i++) {
	System.out.print( data[i] );
	if (i < data.length-1)
	  System.out.print(", ");
      }
      System.out.println("}");
    }
  } // pr_values


} // simple_haar

