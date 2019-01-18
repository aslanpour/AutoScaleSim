/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import autoscalesim.log.ReadWriteExcel;
import java.util.ArrayList;
import org.cloudbus.cloudsim.Log;
import org.neuroph.core.data.DataSet;

/**
 *
 * @author Aslanpour
 */
public class DataSetCreator {
    public static final String FILE_PATH = "src/others/";
    
    public static void main(String[] args) {
        
        int maxRequest = 200;
        String sheetName = "4-17_Aug_inc20%nW_4vg60_3ou";
        //D_15-27_Jul_STD200nW4d_avg60_1h
//        DS_15-27_July95_STD200_avg60_1h
//        DS_4-17_Aug95_inc20%_1h
        ArrayList dataList;
        dataList = ReadWriteExcel.readASheet(sheetName, "src/others/SimulationResult.xls");
        
//         avg 
//        ArrayList newDataList = new ArrayList<Double>();
//        
//        for(int i=0; i<dataList.size();i++){
//            if(i >= 0){ // ؟؟؟
//                ArrayList<Double> row = (ArrayList < Double >)dataList.get(i);
//                
//                double sum = 0;
//                
////                double sumWeight = 0;
////                double weight = 10; // ؟؟؟
//                
//                for (int j= i ; j > i - 10; j--){ // ؟؟؟
//                    ArrayList<Double> rowTmp = (ArrayList <Double>)dataList.get(j);
//                    
//                    sum += rowTmp.get(4); // req
////                    sum += rowTmp.get(3) * weight; // req
////                    sumWeight += weight;
////                    weight--;
//                }
//                
//                double avg = sum / 10; // ???
////                double avg = sum / sumWeight;
//                
////                row.set(4, avg);
////                avg = (row.get(3) + row.get(4)) / 2; // ???????
//                row.add(avg);
////                row.remove(0);
//                
//                newDataList.add(row);
//            }
//        }
//        
//        ReadWriteExcel.writeDataList(newDataList, "avg60");
//        avg end
        
        Log.printLine("excel file has read");
        int inputCount = 4; //????  0-weekend, 1-day of week, 2-hour of day, 3-minute of hour, and 4-user's request
        int outputCount = 1; // request 10, 20, 30, 40, 50, and "60" minutes future
        double[] inputMinValues = new double[]{1 , 0, 0,0}; // for 5 inputs --> {0, 1 ,0, 0, 0};
//        double[] inputMinValues = new double[]{0 ,1 , 0, 0, 0,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0}; //13
        
        double[] inputMaxValues = new double[]{7, 23, 59,maxRequest}; //for 5 inputs-->{1, 1, 7, 23, 59, maxRequest}
//        double[] inputMaxValues = new double[]{1, 7, 23, 999, 20, 30, 30, 1, 1, 30, 200, 200, 200};
        
        double[] outputMinValues = new double[] {0};
//        double[] outputMinValues = new double[] {-1};
        
        double[] outputMaxValues = new double[]{maxRequest};
//        double[] outputMaxValues = new double[]{1};
        
        
        // destination of output file
        String fileName = sheetName;
        String filePath  = "C:/AutoScaleSimFiles/";
        
        createSupervizedDataSetNormalized(dataList
                                        , inputCount, outputCount
                                        , inputMinValues, inputMaxValues
                                        , outputMinValues, outputMaxValues
                                        , filePath, fileName, true);
    }
    
    
    /**
     * Unsupervised DataSet
     * @param dataList
     * @param inputs
     * @param filePath
     * @param fileName
     * @param isNeedTextfile 
     */
    public static void writeUnSupervizedDataSet(ArrayList dataList, 
                                                int inputs, 
                                                String filePath, 
                                                String fileName, 
                                                boolean isNeedTextfile
                                                ){
        DataSet dataSet = new DataSet(inputs);

        for(int i = 0; i < dataList.size(); i++){
            ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
            double[] row = new double[data.size()];
            for(int j = 0; j < data.size(); j++){
                row[j] = data.get(j);
            }
            
            dataSet.addRow(row);
        }
        dataSet.save(filePath + fileName + ".tset");
        if(isNeedTextfile)
            dataSet.saveAsTxt(filePath + fileName + ".txt", ",");
    }
    
    /**
     * 
     * @param dataList
     * @param inputCount
     * @param outputCount
     * @param filePath
     * @param fileName
     * @param isNeedTextfile 
     */
    public static void writeSupervizedDataSet(ArrayList dataList, int inputCount, int outputCount
                                                        , String filePath, String fileName, boolean isNeedTextfile){
        DataSet dataSet = new DataSet(inputCount, outputCount);

        // Before creation of DataSet, the Excel sheet should be contains inputs and outputs in each row.
        for(int i = 0; i < dataList.size(); i++){
            ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
            
            if(data.size() != (inputCount + outputCount))
                Log.printLine("error - in excel record size");
            
            double[] input = new double[inputCount];
            double[] output = new double[outputCount];
            
            for(int j = 0; j < data.size(); j++){
                if (j < inputCount)
                    input[j] = data.get(j);
                else
                    output[j - outputCount] = data.get(j);
            }
            
            dataSet.addRow(input, output);
        }
        dataSet.save(filePath + fileName + ".tset");
        if(isNeedTextfile)
            dataSet.saveAsTxt(filePath + fileName + ".txt", ",");
    }
    
    public static void createSupervizedDataSetNormalized(ArrayList dataList, int inputCount, int outputCount
                                                        , double[] inputMinValues, double[] inputMaxValues
                                                        , double[] outputMinValues, double[] outputMaxValues
                                                        , String filePath, String fileName, boolean isNeedTextfile){
        
        DataSet dataSet = new DataSet(inputCount, outputCount);

        
        for(int i = 0; i < dataList.size(); i++){
            ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
            
            if(data.size() != (inputCount + outputCount))
                Log.printLine("error - in excel record size");
            
            double[] input = new double[inputCount];
            double[] output = new double[outputCount];
            
            for(int j = 0; j < data.size(); j++){
                if (j < inputCount)
                    input[j] = normalizedValue(data.get(j), inputMinValues[j], inputMaxValues[j]);
                else
                    output[j - inputCount] = normalizedValue(data.get(j), outputMinValues[j-inputCount]
                                                                             , outputMaxValues[j-inputCount]);
            }
            
            dataSet.addRow(input, output);
        }
        
        dataSet.save(filePath + fileName + ".tset");
        Log.printLine("data set has been created in " + filePath + fileName);
        if(isNeedTextfile){
            dataSet.saveAsTxt(filePath + fileName + ".txt", ",");
            Log.printLine("data set (txt) has been created in " + filePath + fileName);
        }
    }
    
    /**
     * 
     * @param input
     * @param minInput
     * @param maxInput
     * @return 
     */
    public static double normalizedValue(double input, double minInput, double maxInput) {
            return (input - minInput) / (maxInput - minInput) * 0.8 + 0.1;
    }

    /**
     * 
     * @param input
     * @param minInput
     * @param maxInput
     * @return 
     */
    public static double deNormalizedValue(double input, double minInput, double maxInput) {
            return (input - 0.1) * ( maxInput - minInput) / 0.8 + minInput;
    }
}
