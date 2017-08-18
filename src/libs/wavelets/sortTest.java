package libs.wavelets;

import libs.wavelets.sort.generic_sort;

import java.util.Random;

/**
<p>
  Test for generic sort
</p>
<p>
  Classes to sort a specific type are derived from
  the abstract <i>generic_sort</i> class.  This
  code tests the sort code by creating two specific
  sort classes: one that sorts arrays of testElem
  objects by index and one that sorts by the
  <i>val</i> field.
</p>

 */
class sortTest {

  
  /**
    <p>
    Test data structure: index is the array index and
    val is the data element.
    </p>
    <p>
    An array of <i>testElem</i> objects can be sorted
    by value and than rearranged back into the original
    order by sorting by index.
    </p>
   */
  private class testElem {
    testElem( int i ) { index = i; }
    public int index;
    public double val;
  }

  
  /**
    Sort by index
   */
  private class sort_testElem_index extends generic_sort {

    /**

      if (a.index == b.index) return 0
      if (a.index < b.index) return -1
      if (a.index > b.index) return 1;

     */
    protected int compare( Object a, Object b )
    {
      int rslt = 0;
      testElem t_a = (testElem)a;
      testElem t_b = (testElem)b;

      if (t_a.index < t_b.index)
	rslt = -1;
      else if (t_a.index > t_b.index)
	rslt = 1;

      return rslt;
    } // compare

  } // sort_testElem_index


  /**
    Sort by value
   */
  private class sort_testElem_val extends generic_sort {

    /**

      if (a.val == b.val) return 0
      if (a.val < b.val) return -1
      if (a.val > b.val) return 1;

     */
    protected int compare( Object a, Object b )
    {
      int rslt = 0;
      testElem t_a = (testElem)a;
      testElem t_b = (testElem)b;

      if (t_a.val < t_b.val)
	rslt = -1;
      else if (t_a.val > t_b.val)
	rslt = 1;

      return rslt;
    } // compare

  } // sort_testElem_val

  
  public sort_testElem_val alloc_sort_testElem_val()
  {
    return new sort_testElem_val();
  }


  public sort_testElem_index alloc_sort_testElem_index()
  {
    return new sort_testElem_index();
  }

  
  public testElem[] alloc_array( int size )
  {
    testElem a[] = new testElem[ size ];
    for (int i = 0; i < size; i++) {
      a[i] = new testElem( i );
    }
    return a;
  }


  void printArray( testElem a[] )
  {
    for (int i = 0; i < a.length; i++) {
      System.out.println(i + " " + a[i].index + " " + a[i].val );
    }
  }


  public static void main( String[] args ) {
    sortTest s = new sortTest();

    final int size = 20;
    testElem a[] = s.alloc_array( size );
    Random generator = new Random();
    
    for (int i = 0; i < a.length; i++) {
      a[i].val = generator.nextDouble();
    }

    sort_testElem_index sortByIndex = s.alloc_sort_testElem_index();
    sort_testElem_val   sortByVal   = s.alloc_sort_testElem_val();

    System.out.println("before sort by val");
    s.printArray( a );

    sortByVal.sort( a );

    System.out.println("after sort by val");
    s.printArray( a );

    sortByIndex.sort( a );

    System.out.println("after sort by index");
    s.printArray( a );
  }

}  // sortTest
