package util;

import data.TimeSeries;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

public class IOUtil {

    public static File[] getFileNames(String baseDir) throws NoSuchFileException {
        File folder = new File(baseDir);
        File[] fileDescriptors = folder.listFiles();
        if(fileDescriptors == null) {
            throw new NoSuchFileException("No datasets found. Please check path");
        }
        return fileDescriptors;
    }

    public static File getFirstFile(String baseDir) throws NoSuchFileException {
        return getFileNames(baseDir)[0];
    }

    public static TimeSeries getData(File fileDescriptor) throws IOException {
        double[] originalData = readFromFile(fileDescriptor);
        originalData = PaddingUtil.cutToPowerOfTwo(originalData);
        return new TimeSeries(originalData);
    }

    public static double[] readFromFile(String filePath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        return readFromFile(bufferedReader);
    }

    private static double[] readFromFile(File fileDescriptor) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileDescriptor));
        return readFromFile(bufferedReader);
    }

    private static double[] readFromFile(BufferedReader bufferedReader) throws IOException {
        List<Double> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(Double.parseDouble(line));
        }
        bufferedReader.close();
        double[] ret = new double[lines.size()];
        for(int i = 0; i < lines.size(); i++) {
            ret[i] = lines.get(i);
        }
        return ret;
    }

    public static void writeToFile (String filename, double[] data) throws IOException{
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
        for(double d : data) {
            outputWriter.write(Double.toString(d));
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }
}
