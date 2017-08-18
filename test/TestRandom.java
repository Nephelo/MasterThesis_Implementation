import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class TestRandom {

    @Test
    public void randomTest() {
        long seed = 123456789;
        Random rand = new Random(seed);
        double firstRandom = rand.nextGaussian();
        assertThat(firstRandom, not(rand.nextGaussian()));

        rand.setSeed(seed);
        assertThat(rand.nextGaussian(), is(firstRandom));

    }
}
