package privacyTransformation;

import configuration.WaveletTransformationConfiguration;
import org.apache.commons.math3.distribution.LaplaceDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.Pair;
import util.SwitchingUtil;

import java.util.*;
import java.util.stream.IntStream;

public class RandomHandler {

    private static final long SEED = WaveletTransformationConfiguration.SEED;
    private final boolean isDeterministicMode;
    private final RandomGenerator randomGenerator;
    private final LaplaceDistribution laplaceDistribution;
    private double[][] doubleGeneratedGaussRandom = null;
    private Random random;
    private boolean[][] booleanGeneratedRandom;
    private double[][] doubleGeneratedLaplaceRandom = null;

    public RandomHandler(boolean isDeterministicMode) {
        this.isDeterministicMode = isDeterministicMode;
        random = new Random();
        randomGenerator = new JDKRandomGenerator();
        if(isDeterministicMode) {
            random.setSeed(SEED);
            randomGenerator.setSeed(SEED);
        }
        this.laplaceDistribution = new LaplaceDistribution(randomGenerator, 0, 1);
    }


    public double getLastDoubleGaussValues(int row, int index) {
        if(isDeterministicMode) {
            return doubleGeneratedGaussRandom[row][index];
        } else {
            return random.nextGaussian();
        }
    }

    public double getLastDoubleLaplaceValues(int row, int index, double magnitude) {
        /*  Calculation of magnitude see: http://massmatics.de/merkzettel/#!935:Laplace-Verteilung/
            If X ~ Lap(mu, sigma) and Y = aX+b, then Y ~ Lap(a mu + b, a sigma)
        */
        if(isDeterministicMode) {
            return magnitude * doubleGeneratedLaplaceRandom[row][index];
        } else {
            return magnitude * laplaceDistribution.sample();
        }
    }

    public boolean getBooleanValue(int row, int column) {
        if(isDeterministicMode) {
            return booleanGeneratedRandom[row][column];
        } else {
            return random.nextBoolean();
        }
    }

    private double[][] generateDoubleGaussValues(int rows, int columns) {
        double[][] generatedDouble = new double[rows][columns];
        IntStream.range(0, rows).forEach(row -> IntStream.range(0, columns).forEach(column  -> generatedDouble[row][column] = random.nextGaussian()));
        return generatedDouble;
    }

    private double[][] generateDoubleLaplaceValues(int rows, int columns) {
        double[][] generatedDouble = new double[rows][columns];
        IntStream.range(0, rows).forEach(row -> generatedDouble[row] = laplaceDistribution.sample(columns));
        return generatedDouble;
    }

    private boolean[][] generateBooleanRandomValues(int rows, int columns) {
        boolean[][] generatedBooleans = new boolean[rows][columns];
        IntStream.range(0, rows).forEach(row -> IntStream.range(0, columns).forEach(column  -> generatedBooleans[row][column] = random.nextBoolean()));
        return generatedBooleans;
    }

    public void initTransformation(int rows, int columns) {
        if(doubleGeneratedGaussRandom == null) {
            doubleGeneratedGaussRandom = generateDoubleGaussValues(rows, columns);
        }
        if(doubleGeneratedLaplaceRandom == null) {
            doubleGeneratedLaplaceRandom = generateDoubleLaplaceValues(rows, columns);
        }
        if(booleanGeneratedRandom == null) {
            booleanGeneratedRandom = generateBooleanRandomValues(rows, columns);
        }
    }

    public void indicesSwitched(int row, ArrayList<int[]> splittingPoints) {
        Pair<Integer, ArrayList<int[]>> splitting = new Pair<>(row, splittingPoints);
        executeIndicesSwitched(row, splittingPoints);
    }

    private void executeIndicesSwitched(int row, ArrayList<int[]> splittingPoints) {
        doubleGeneratedGaussRandom[row] = SwitchingUtil.switchArray(splittingPoints, doubleGeneratedGaussRandom[row]);
        doubleGeneratedLaplaceRandom[row] = SwitchingUtil.switchArray(splittingPoints, doubleGeneratedLaplaceRandom[row]);
        booleanGeneratedRandom[row] = SwitchingUtil.switchArray(splittingPoints, booleanGeneratedRandom[row]);
    }
}
