import org.junit.Test;
import util.DataUtil;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DataUtilTest {

    @Test
    public void powerSetTest() {
        Set<Integer> inputList = new HashSet<>();
        inputList.add(1);
        inputList.add(2);
        inputList.add(3);

        SortedSet<SortedSet<Integer>> powerSet = DataUtil.sortedPowerSet(inputList);


        Set<Integer> testSet = new HashSet<>();
        assertThat(powerSet.toArray()[0], is(testSet));

        testSet.clear();
        testSet.add(1);
        assertThat(powerSet.toArray()[1], is(testSet));

        testSet.clear();
        testSet.add(2);
        assertThat(powerSet.toArray()[2], is(testSet));

        testSet.clear();
        testSet.add(3);
        assertThat(powerSet.toArray()[3], is(testSet));

        testSet.clear();
        testSet.add(1);
        testSet.add(2);
        assertThat(powerSet.toArray()[4], is(testSet));

        testSet.clear();
        testSet.add(1);
        testSet.add(3);
        assertThat(powerSet.toArray()[5], is(testSet));

        testSet.clear();
        testSet.add(2);
        testSet.add(3);
        assertThat(powerSet.toArray()[6], is(testSet));

        testSet.clear();
        testSet.add(1);
        testSet.add(2);
        testSet.add(3);
        assertThat(powerSet.toArray()[7], is(testSet));
    }

}
