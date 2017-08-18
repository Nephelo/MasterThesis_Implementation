import configuration.WaveletTransformationConfiguration;
import data.HaarData;
import data.TimeSeries;
import org.junit.BeforeClass;
import org.junit.Test;
import privacyTransformation.Configuration;
import privacyTransformation.ConflictingOperationsException;
import privacyTransformation.PrivacyTransformationHandler;
import privacyTransformation.TransformationTypes;
import util.IOUtil;
import util.PaddingUtil;
import wavelet.HaarWaveletTransformation;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class Compatibility {

    private static final boolean IS_PARALLEL = true;
    private static final boolean IS_FAST_RUN = false;

    @Test
    public void compatibility() throws IOException, InterruptedException {
        ExecutorService executorService;
        if(IS_PARALLEL) {
            int numberOfThreads = Math.max(Runtime.getRuntime().availableProcessors() - 1 , 1);
            System.out.println("Using " + numberOfThreads + " threads.");
            executorService = Executors.newFixedThreadPool(numberOfThreads);
        }

        List<TransformationTypes> transformationTypes = Arrays.asList(TransformationTypes.values());
//        List<TransformationTypes> transformationTypes = new ArrayList<>();
//        transformationTypes.add(TransformationTypes.DenoiseSoft);
//        transformationTypes.add(TransformationTypes.DenoiseNGG);
//        transformationTypes.add(TransformationTypes.DenoiseHard);
//        transformationTypes.add(TransformationTypes.Swapping);
//        transformationTypes.add(TransformationTypes.AddNoiseLaplace);
//        transformationTypes.add(TransformationTypes.AddNoiseGauss);
//        transformationTypes.add(TransformationTypes.CutJumps);
//        transformationTypes.add(TransformationTypes.IdentityTransformation);
//        transformationTypes.add(TransformationTypes.RemoveLevel);

        File[] fileDescriptors = IOUtil.getFileNames(WaveletTransformationConfiguration.DATA_BASE_DIR);
        for(File fileDescriptor : fileDescriptors) {
            TimeSeries originalDataFull = IOUtil.getData(fileDescriptor);
            final TimeSeries originalData;
            if(IS_FAST_RUN) {
                originalData = PaddingUtil.firstXData(originalDataFull, 256);
            } else {
                originalData = originalDataFull;
            }

            HaarData coeff = HaarWaveletTransformation.forward(originalData);
            if(IS_PARALLEL) {
                Runnable task = () -> {
                    System.out.println("Testing file: " + fileDescriptor.getName() + " with " + originalData.size() + " data points on thread: " + Thread.currentThread().getName());
                    runTest(transformationTypes, originalData, coeff);
                    System.out.println("Finnished testing file: " + fileDescriptor.getName());
                };
                executorService.submit(task);
            } else {
                System.out.println("Testing file: " + fileDescriptor.getName() + " with " + originalData.size() + " data points");
                runTest(transformationTypes, originalData, coeff);
                System.out.println("Finnished testing file: " + fileDescriptor.getName());
            }
        }
        if(IS_PARALLEL) {
            System.out.println("Waiting for tasks to be completed");
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.DAYS);
        }
    }

    private void runTest(List<TransformationTypes> transformationTypes, TimeSeries originalData, HaarData coeff) {
        Set<Configuration> alreadyRun = new HashSet<>();
        int successful = 0;
        int failed = 0;
        for(TransformationTypes transformationType1 : transformationTypes) {
            for(TransformationTypes transformationType2: transformationTypes) {
                System.out.println("Testing combination: " + transformationType1.name() + " and " + transformationType2.name());
                for(double param1 = 0; param1 <= 100; param1 += 1) {
                    for(double param2 = 0; param2 <= 100; param2 += 1) {
                        try {
                            PrivacyTransformationHandler oneThanTwo = new PrivacyTransformationHandler(true);
                            oneThanTwo.addTransformation(transformationType1, param1);
                            oneThanTwo.addTransformation(transformationType2, param2);
                            if(alreadyRun.contains(oneThanTwo.getConfiguration(coeff))) {
                                continue;
                            } else {
                                alreadyRun.add(oneThanTwo.getConfiguration(coeff));
                            }

                            PrivacyTransformationHandler twoThanOne = new PrivacyTransformationHandler(true);
                            twoThanOne.addTransformation(transformationType2, param2);
                            twoThanOne.addTransformation(transformationType1, param1);

                            boolean equal = checkTransformations(coeff, oneThanTwo, twoThanOne);
                            if(equal) {
                               successful++;
                               //System.out.println(oneThanTwo.getDescription() + "commutate.");
                            } else {
                                System.err.println(oneThanTwo.getDescription() + "do NOT commutate.");
                                failed++;
                            }
                        } catch (ConflictingOperationsException conflictingOperationsException) {
                            continue;
                        }
                    }
                }
            }
        }

        System.out.println("Successfull: " + successful + " failed: " + failed);
        assertEquals(0, failed);
    }

    private boolean checkTransformations(HaarData originalCoeffs, PrivacyTransformationHandler oneThanTwoTransformation, PrivacyTransformationHandler twoThanOneTransformation) {
        HaarData oneThanTwo = oneThanTwoTransformation.transform(originalCoeffs);
        HaarData twoThanOne = twoThanOneTransformation.transform(originalCoeffs);

        boolean equal = oneThanTwo.equals(twoThanOne);
        return equal;
    }
}
