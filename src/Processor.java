import util.IOUtil;
import util.PaddingUtil;

import java.io.IOException;

public class Processor {

    private final String outputDir;
    private final String baseDir;
    private final String inputDir;

    public Processor(String baseDir, String inputDir, String outputDir) {
        this.baseDir = baseDir;
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }
/*
    public void processFile(PrivacyTransformation transformation, String fileName) throws IOException, ConfigurationException {
        /*double[] originalData = prepareData(fileName);

        ArrayList<double[]> coeff = HaarWaveletTransformation.forward(originalData);
        PrintUtil.printCoefficients(coeff);
        coeff = transformation.transform(coeff);
        PrintUtil.printCoefficients(coeff);
        String transformationDescription = transformation.getDescription().replace(" ", "_");

        double[] changedData = HaarWaveletTransformation.backward(coeff);
        removeToSmallValues(changedData);
        //IOUtil.writeToFile(baseDir+outputDir+fileName+transformationDescription+".txt", changedData);

        ImageWriter.writeImage(changedData, originalData, baseDir+outputDir+fileName, transformationDescription);
        System.out.println("Done Transformation: " + transformationDescription);
    }

    private double[] prepareData(String fileName) throws IOException {
        double[] originalData = IOUtil.readFromFile(baseDir+inputDir+fileName+".txt");
        originalData = PaddingUtil.firstXData(originalData, 2056);
        originalData = PaddingUtil.cutToPowerOfTwo(originalData);
        return originalData;
    }*/
}
