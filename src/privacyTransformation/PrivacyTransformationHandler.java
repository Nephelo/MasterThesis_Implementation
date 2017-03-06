package privacyTransformation;

import data.HaarData;
import privacyTransformation.implementation.*;
import util.SwitchingUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PrivacyTransformationHandler {

    private final boolean isDeterministicMode;
    private boolean alreadyRun;
    private RandomHandler randomHandler;
    private HaarData transformedData;
    private List<TransformationConfiguration> transformations;
    private HaarData inputHaarData;
    private boolean[][] fixedValues;
    private boolean[][] switched;

    private class TransformationConfiguration {
        public TransformationTypes type;
        public PrivacyTransformation instance;
        public double configurationValue;

        public TransformationConfiguration(TransformationTypes type, PrivacyTransformation instance, double configurationValue) {
            this.type = type;
            this.instance = instance;
            this.configurationValue = configurationValue;
        }
    }

    public PrivacyTransformationHandler(boolean isDeterministicMode) {
        transformations = new ArrayList<>();
        alreadyRun = false;
        this.isDeterministicMode = isDeterministicMode;
        randomHandler = new RandomHandler(isDeterministicMode);
    }

    public HaarData transform(HaarData data) {
        if(alreadyRun) {
            throw new UnsupportedOperationException("Transformation already calculated");
        }

        initForTransformation(data);

        for(TransformationConfiguration transformationConfiguration : transformations) {
            PrivacyTransformation instance = transformationConfiguration.instance;
            data = instance.transform(this, data, transformationConfiguration.configurationValue);
        }
        alreadyRun = true;
        this.transformedData = data;
        return data;
    }

    private void initForTransformation(HaarData data) {
        int rows = data.getCoefficients().length;
        int columns = data.getCoefficients()[rows-1].length;
        /* clone is needed otherwise intputHaarData object is manipulated by indicesSwitched
           which will cause side effects
        */
        this.inputHaarData = data.clone();
        this.fixedValues = new boolean[rows][columns];
        this.switched = new boolean[rows][columns];
        this.randomHandler.initTransformation(rows, columns);
    }

    public void addTransformation(TransformationTypes type, double configurationValue) throws ConflictingOperationsException {
        if(isConflictingOpertation(type, configurationValue)) {
            throw new ConflictingOperationsException();
        }
        transformations.add(new TransformationConfiguration(type, geTransformationFromType(type), configurationValue));
    }

    private boolean isConflictingOpertation(TransformationTypes type, double configurationValue) {
        if(type.equals(TransformationTypes.CutJumps)) {
            for(TransformationConfiguration transformation : transformations) {
                if(transformation.type.equals(TransformationTypes.Denoising) &&
                        configurationValue <= transformation.configurationValue) {
                    return true;
                }
            }
        }

        if(type.equals(TransformationTypes.Denoising)) {
            for(TransformationConfiguration transformation : transformations) {
                if(transformation.type.equals(TransformationTypes.CutJumps) &&
                        configurationValue <= transformation.configurationValue) {
                    return true;
                }
            }
        }

        return false;
    }

    private PrivacyTransformation geTransformationFromType(TransformationTypes type) {
        switch (type) {
            case AddNoise:
                return new AddNoise();
            case CutJumps:
                return new CutJumps();
            case Denoising:
                return new Denoising();
            case IdentityTransformation:
                return new IdentityTransformation();
            case RemoveLevel:
                return new RemoveLevel();
            case Swapping:
                return new Swapping();
        }

        return new IdentityTransformation();
    }

    public double[][] getCoefficentsForThreshold() {
        return inputHaarData.getCoefficients();
    }

    public RandomHandler getRandomHandler() {
        return randomHandler;
    }

    public boolean isFixedValue(int level, int index) {
        return this.fixedValues[level][index];
    }

    public void setFixedValue(int level) {
        IntStream.range(0, fixedValues[level].length).forEach(i -> setFixedValue(level, i));
    }

    public void setFixedValue(int level, int index) {
        this.fixedValues[level][index] = true;
    }

    public boolean isSwitched(int level, int index) {
        return this.switched[level][index];
    }

    public void setSwitched(int level, int index) {
        this.switched[level][index] = true;
    }

    public double getMaxCoefficientForThresholds(HaarData defaultCoeffs) {
        Stream<double[]> stream;
        if(inputHaarData == null) {
            stream = Arrays.stream(defaultCoeffs.getCoefficients());
        } else {
            stream = Arrays.stream(getCoefficentsForThreshold());
        }
        return stream.flatMapToDouble(Arrays::stream).max().getAsDouble();
    }

    public void indicesSwitched(int row, ArrayList<int[]> splittingPoints) {
        if(isDeterministicMode) {
            randomHandler.indicesSwitched(row, splittingPoints);
        }
        fixedValues[row] = SwitchingUtil.switchArray(splittingPoints, fixedValues[row]);
        switched[row] = SwitchingUtil.switchArray(splittingPoints, switched[row]);
        inputHaarData.setLevel(row, SwitchingUtil.switchArray(splittingPoints, inputHaarData.getCoefficients()[row]));
    }

    public String getDescription() {
        String description = "";
        for(TransformationConfiguration transformation :transformations) {
            description = description + transformation.instance.getDescription() + " ";
        }
        return description;
    }

    public Configuration getConfiguration(HaarData coefficients) {
        Configuration configuration = new Configuration();
        for(TransformationConfiguration transformation : transformations) {
            TransformationTypes type = transformation.type;
            double configurationValue = transformation.instance.getConfigurationValue(this, coefficients, transformation.configurationValue);
            configuration.add(type, configurationValue);
        }
        return configuration;
    }
}
