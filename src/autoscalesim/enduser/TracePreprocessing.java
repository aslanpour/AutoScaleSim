/*
 * Title:        AutoScaleSim Toolkit
 * Description:  AutoScaleSim (Auto-Scaling Simulation) Toolkit for Modeling and Simulation of Auto-scaling Systems
 *		 for Cloud Applications 			
 *
 * Copyright (c) 2018 Islamic Azad University, Jahrom
 *
 * Authors: Mohammad Sadegh Aslanpour
 * 
 */
package autoscalesim.enduser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import org.cloudbus.cloudsim.Log;
/**
 *
 * @author aslanpour
 */
public class TracePreprocessing {
    public static void main(String[] args) throws IOException {
        
        //read log file (timestamp, response time and data size)
        long[][] dataset = readLog("src/others/", "mylog_4h_800_100th_fix10vm.log");
        // sort the dataset based on timestamp
//        long[][] sortedDataset = sortBubbleWikipediaTrace(dataset);
        long[][] sortedDataset = sortQuickWikipediaTrace(dataset);
        
        // write the sorted workload to a CSV file
        writeCSV(sortedDataset, "src/others/", "dataset.csv");
        
    }
    

    //read log file and write to CSV file
    public static long[][] readLog(String filePath, String fileName){
        Log.printLine("reading log . . .");
        long[][] dataset;
        ArrayList arrayList = new ArrayList();
        ArrayList<Long> row = new ArrayList<Long>();
        //read file
        long counter = 0;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath + fileName));
            try {
                String line;
                while ( (line = br.readLine()) != null ) {
                    // timestamp, type, response time, status, data size, and url
                    // each line: 1269 - GET - 97 - 404 - 271 - /mediawiki/extensions/LuceneSearch/lucenesearch.css
                    // latest timestamp is 14403699 
                    //PROCESS THE LINE
                    long timestamp = Long.valueOf(line.split("-")[0].trim());
                    long responseTime = Long.valueOf(line.split("-")[2].trim());
                    long size = Long.valueOf(line.split("-")[4].trim());
                    
                    row = new ArrayList<Long>();
                    row.add(timestamp);
                    row.add(responseTime);
                    row.add(size);
                    
                    arrayList.add(row);
                    counter++;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
           e.printStackTrace(); 
        }
        
        //Move to new array
        dataset=new long[arrayList.size()][3];
        
        for (int i=0; i<arrayList.size(); i++){
            dataset[i][0] = ((ArrayList<Long>)arrayList.get(i)).get(0);
            dataset[i][1] = ((ArrayList<Long>)arrayList.get(i)).get(1);
            dataset[i][2] = ((ArrayList<Long>)arrayList.get(i)).get(2);
        }
        System.out.println("log file counter = " + counter);
        return dataset;
    }
    
    
    
    
    public static long[][] sortQuickWikipediaTrace (long[][] dataset){
        int low = 0;
        int high = dataset.length - 1;
        return QuickSort.quickSort(dataset, low, high);
    }
//read log file and write to CSV file
    public static void ReadFile(String filePath, String fileName){
        //read file
        
        ArrayList dataList = new ArrayList<>();
        ArrayList<Double> record = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath + fileName));
            try {
                boolean firstLine = true;
                String lastTimeSeriesStr = new String();
                String line;
                while ( (line = br.readLine()) != null ) {
                    //PROCESS THE LINE
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
           e.printStackTrace(); 
        }
    }
    
    //write to CSV
    public static void writeCSV( int[] data, String filePath,String fileName) throws IOException{
        FileWriter csvWriter = new FileWriter(filePath + fileName);
                
        for (int i = 0; i < data.length; i++) {
           csvWriter.append(data[i] + "\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
    
    //write to CSV
    public static void writeCSV( double[] data, String filePath,String fileName) throws IOException{
        FileWriter csvWriter = new FileWriter(filePath + fileName);
                
        for (int i = 0; i < data.length; i++) {
           csvWriter.append(data[i] + "\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
    
    //write to CSV
    public static void writeCSV( long[][] data, String filePath,String fileName) throws IOException{
        Log.printLine("Writing to CSV . . .");
        FileWriter csvWriter = new FileWriter(filePath + fileName);
                
        for (int i = 0; i < data.length; i++) {
           csvWriter.append(data[i][0] + "," + data[i][1] + "," + data[i][2] 
                            + "\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
}
