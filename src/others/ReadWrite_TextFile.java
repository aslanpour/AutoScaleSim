/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autoscalesim.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Mohammad Sadegh Aslanpour 
 */
public class ReadWrite_TextFile {

    final String filePath = "C:/";
    final String fileName = "datasetFile";
    // call read method
    // call write method

/**
     * 
     * 
     * @param fullFilePath
     * @param filePath
     * @param fileName
     * @return
     * @throws IOException 
     */
    public static ArrayList getTimeStampFromDatasetFileClarkNetHttp_And_NASA(String filePath, String fileName){
        //read file
        
        ArrayList dataList = new ArrayList<>();
        ArrayList<Double> record = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath + fileName + ".txt"));
            try {
                boolean firstLine = true;
                String lastTimeSeriesStr = new String();
                String line;
                while ( (line = br.readLine()) != null ) {
                    // printing out each line in the file
//                    System.out.println(line);
                    if(!line.contains("[") && !line.contains("]")){
                        System.out.println("error - Line without time");
                        continue;
                    }
                    int firstIndex = line.indexOf('[');
                    int lastIndex = line.indexOf(']');
                    String time = line.substring(firstIndex + 1, lastIndex);
                        // default timestamp in Clarknet [28/Aug/1995:00:00:34 -0400]
                        // default timestamp in NASA [01/Jul/1995:00:00:01 -0400]
                        // default timestamp in EPA [29:23:53:25]
                    String timeStr;
                    record = new ArrayList<Double>();
                    String field = new String();
                    
                    field = time.substring(0, 2);
                    timeStr = field;
                    record.add(Double.valueOf(field)); // Day
                    
                    time = time.substring(12, 20);
                    field = new String();
                    for(char ch:time.toCharArray()){
                        if (ch != ':')
                            field += ch;
                        else{
                            record.add(Double.valueOf(field)); // Hour, Minute
                            timeStr += field;
                            field = new String();
                        }
                    }
                    record.add(Double.valueOf(field)); // Second
                    timeStr += field;
                    // if this record order is not true, it should not save
                    if(firstLine){
                        lastTimeSeriesStr = timeStr;
                        firstLine = false;
                    }else{
                        if(Double.valueOf(timeStr) < Double.valueOf(lastTimeSeriesStr)){
                            System.out.println("error in time order");
                            System.out.println("Last time:" + lastTimeSeriesStr);
                            System.out.println("this time: " + timeStr);
                            continue;
                        }
                    }
                    lastTimeSeriesStr = timeStr;
                    
                    dataList.add(record);
                } 
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Time Stamp List is successfully gotten from Text File");
        return dataList;
    }
    
    
}
    