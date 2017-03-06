package util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

public class ImageWriter {

    public static void writeImage(double[] data, double[] originalData, String filePath, String chartTitle) throws IOException {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        addDataToDataSet(originalData, dataSet, "Original Data");
        addDataToDataSet(data, dataSet, "Transformed Data");

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "Time", "Energy usage", dataSet, PlotOrientation.VERTICAL, false, false, false);

        int width = 1920; /* Width of the image */
        int height = 1080; /* Height of the image */
        File lineChart = new File(filePath+"_"+chartTitle+".jpg");
        ChartUtilities.saveChartAsJPEG(lineChart , chart, width ,height);
    }

    private static XYDataset addDataToDataSet(double[] data, XYSeriesCollection dataSet, String key) {
        XYSeries series1 = new XYSeries(key);
        int counter = 0;
        for(double d: data) {
           series1.add(counter/60.0, d);
           counter++;
        }
        dataSet.addSeries(series1);
        return dataSet;
    }
}
