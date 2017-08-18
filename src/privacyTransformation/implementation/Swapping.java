package privacyTransformation.implementation;

import data.HaarData;
import privacyTransformation.PrivacyTransformationHandler;
import util.DataUtil;
import util.SwitchingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Swapping implements PrivacyTransformation {


    private List<Integer> levels;
    private int mappedValue;

    @Override
    public HaarData transform(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapValueToLevels(haarData, configurationValue);

        HaarData transformedData = haarData.clone();

        for(int level = 0; level < haarData.getCoefficients().length; level++) {
            int affectedLevel = haarData.getCoefficients().length - level -1;
            boolean levelSwapping = levels.contains(affectedLevel);
            if(levelSwapping) {
                for(int indexInRow = 0; indexInRow < haarData.getCoefficients()[affectedLevel].length; indexInRow++) {
                    if(transformationHandler.getRandomHandler().getBooleanValue(affectedLevel, indexInRow)) {
                        swap(transformationHandler, transformedData, affectedLevel, indexInRow);
                    }
                }
            }
        }
        return transformedData;
    }

    @Override
    public String getDescription() {
        return "Randomly swapped on levels " + getSwappingConfigurationString();
    }

    private String getSwappingConfigurationString() {
        String configurationString = "";
        for(int level : this.levels) {
            configurationString += level + ",";
        }
        return configurationString;
    }

    private void mapValueToLevels(HaarData haarData, double configurationValue) {
        Set<Integer> levels = IntStream.range(0, haarData.getCoefficients().length).boxed().collect(Collectors.toSet());
        SortedSet<SortedSet<Integer>> powerSet = DataUtil.sortedPowerSet(levels);

        mappedValue = (int) DataUtil.mapToInterval(configurationValue, 0, powerSet.size() - 1);
        this.levels = ((SortedSet<Integer>) powerSet.toArray()[mappedValue]).stream().collect(Collectors.toList());
    }

    @Override
    public double getConfigurationValue(PrivacyTransformationHandler transformationHandler, HaarData haarData, double configurationValue) {
        mapValueToLevels(haarData, configurationValue);
        return mappedValue;
    }


    private void swap(PrivacyTransformationHandler transformationHandler, HaarData transformedData, int affectedLevel, int indexInRow) {
        transformedData.setLevel(affectedLevel, changeSignOfAffectedCoefficient(transformationHandler, affectedLevel, indexInRow, transformedData.getCoefficients()[affectedLevel]));
        adoptLevelsAbove(transformationHandler, indexInRow, transformedData, affectedLevel);
    }

    private void adoptLevelsAbove(PrivacyTransformationHandler transformationHandler, int indexInRow, HaarData transformedData, int affectedLevel) {
        int counter = 0;
        for(int currentIndex = affectedLevel + 1; currentIndex < transformedData.getCoefficients().length; currentIndex++) {
           int size = (int) Math.pow(2, counter);
           int offset = 2 * size * indexInRow;
           double[] currentCoefficients = transformedData.getCoefficients()[currentIndex];

           ArrayList<int[]> splittingPoints = new ArrayList<>();
           splittingPoints.add(new int[] {0, offset});
           splittingPoints.add(new int[] {offset+size, offset+2*size});
           splittingPoints.add(new int[] {offset, offset+size});
           splittingPoints.add(new int[] {offset+2*size, currentCoefficients.length});

           transformedData.setLevel(currentIndex, SwitchingUtil.switchArray(splittingPoints, currentCoefficients));

           transformationHandler.indicesSwitched(currentIndex, splittingPoints);
           counter++;
        }
    }

    private double[] changeSignOfAffectedCoefficient(PrivacyTransformationHandler transformationHandler, int level, int indexInRow, double[] dataRow){
        transformationHandler.setSwitched(level, indexInRow);
        dataRow[indexInRow] *= -1;
        return dataRow;
    }
}
