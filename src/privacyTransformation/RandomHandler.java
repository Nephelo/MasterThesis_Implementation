package privacyTransformation;

import configuration.WaveletTransformationConfiguration;
import util.SwitchingUtil;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomHandler {

    private static final long SEED = WaveletTransformationConfiguration.SEED;
    private final boolean isDeterministicMode;
    private double[][] doubleGeneratedRandom = null;
    private Random random;
    private boolean[][] booleanGeneratedRandom;

    public RandomHandler(boolean isDeterministicMode) {
        this.isDeterministicMode = isDeterministicMode;
        random = new Random();

        if(isDeterministicMode) {
            random.setSeed(SEED);
        }
    }


    public double getLastDoubleRandomValues(int row, int index) {
        if(isDeterministicMode) {
            return doubleGeneratedRandom[row][index];
        } else {
            return random.nextGaussian();
        }
    }


    public boolean getBooleanValue(int row, int column) {
        if(isDeterministicMode) {
            return booleanGeneratedRandom[row][column];
        } else {
            return random.nextBoolean();
        }
    }

    private double[][] generateDoubleRandomValues(int rows, int columns) {
        double[][] generatedDouble = new double[rows][columns];
        IntStream.range(0, rows).forEach(row -> {
            IntStream.range(0, columns).forEach(column  -> {
                generatedDouble[row][column] = random.nextGaussian();
            });
        });
        return generatedDouble;
    }

    private boolean[][] generateBooleanRandomValues(int rows, int columns) {
        boolean[][] generatedBooleans = new boolean[rows][columns];
        IntStream.range(0, rows).forEach(row -> {
            IntStream.range(0, columns).forEach(column  -> {
                generatedBooleans[row][column] = random.nextBoolean();
            });
        });
        return generatedBooleans;
    }

    public void initTransformation(int rows, int columns) {
        if(doubleGeneratedRandom == null) {
            doubleGeneratedRandom = generateDoubleRandomValues(rows, columns);
        }
        if(booleanGeneratedRandom == null) {
            booleanGeneratedRandom = generateBooleanRandomValues(rows, columns);
        }
    }

    public void indicesSwitched(int row, ArrayList<int[]> splittingPoints) {
        doubleGeneratedRandom[row] = SwitchingUtil.switchArray(splittingPoints, doubleGeneratedRandom[row]);
        booleanGeneratedRandom[row] = SwitchingUtil.switchArray(splittingPoints, booleanGeneratedRandom[row]);
    }
}
