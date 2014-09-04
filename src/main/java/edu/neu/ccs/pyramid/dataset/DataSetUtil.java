package edu.neu.ccs.pyramid.dataset;

import org.apache.mahout.math.Vector;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by chengli on 8/7/14.
 */
public class DataSetUtil {

    /**
     * only keep the first numFeatures features
     * @param clfDataSet
     * @param numFeatures
     * @return
     */
    public static ClfDataSet trim(ClfDataSet clfDataSet, int numFeatures){
        if (numFeatures> clfDataSet.getNumFeatures()){
            throw new IllegalArgumentException("numFeatures> clfDataSet.getNumFeatures()");
        }
        ClfDataSet trimmed ;
        // keep density
        if (clfDataSet.isDense()) {
            trimmed = new DenseClfDataSet(clfDataSet.getNumDataPoints(), numFeatures);
        } else{
            trimmed = new SparseClfDataSet(clfDataSet.getNumDataPoints(),numFeatures);
        }
        for (int i=0;i<trimmed.getNumDataPoints();i++){
            FeatureRow featureRow = clfDataSet.getFeatureRow(i);
            //only copy non-zero elements
            Vector vector = featureRow.getVector();
            for (Vector.Element element: vector.nonZeroes()){
                int featureIndex = element.index();
                double value = element.get();
                if (featureIndex<numFeatures){
                    trimmed.setFeatureValue(i,featureIndex,value);
                }
            }
        }
        //copy labels
        int[] labels = clfDataSet.getLabels();
        for (int i=0;i<trimmed.getNumDataPoints();i++){
            trimmed.setLabel(i,labels[i]);
        }
        //just copy settings
        for (int i=0;i<trimmed.getNumDataPoints();i++){
            trimmed.putDataSetting(i,clfDataSet.getFeatureRow(i).getSetting());
        }
        for (int j=0;j<numFeatures;j++){
            trimmed.putFeatureSetting(j,clfDataSet.getFeatureColumn(j).getSetting());
        }
        return trimmed;
    }

    /**
     *
     * @param inputFile
     * @param outputFile
     * @param start inclusive, first column is 0
     * @param end inclusive
     */
    public static void extractColumns(String inputFile, String outputFile,
                                      int start, int end, String delimiter) throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)))
        ){
            String line;
            while((line = br.readLine())!=null){
                String[] split = line.split(Pattern.quote(delimiter));
                System.out.println(Arrays.toString(split));
                System.out.println(split.length);
                for (int i=start;i<=end;i++){
                    System.out.println(i);
                    bw.write(split[i]);
                    if (i<end){
                        bw.write(delimiter);
                    }
                }
                bw.newLine();
            }
        }
    }

    public static void extractColumns(String inputFile, String outputFile,
                                      int start, int end, Pattern pattern) throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)))
        ){
            String line;
            while((line = br.readLine())!=null){
                //remove leading spaces
                String[] split = line.trim().split(pattern.pattern());
                for (int i=start;i<=end;i++){
                    bw.write(split[i]);
                    if (i<end){
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
        }
    }




}
