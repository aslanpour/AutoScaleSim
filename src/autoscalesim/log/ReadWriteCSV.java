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
package autoscalesim.log;

import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author sadegh
 */
public class ReadWriteCSV {
    
    private static String FILE_PATH = "src/others/";
    
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    /**
     * 
     * @param vmHistoryList
     * @return 
     */
    public static boolean writeVmHistoryList(ArrayList<MonitorVmHistory> vmHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Monitor_VmHistory.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "DayOfWeek,Hour,Minute,CPU Util,CPU Load,VMs No,Initialing VMs"
                                        + ",Running VMs,Quarantined VMs,RunningCloudlets,Throughput";
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new Vm history object list to the CSV file
            for(MonitorVmHistory vmHistory : vmHistoryList){
                fileWriter.append(String.valueOf(vmHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getCpuUtilizationByAllTier()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getCpuLoadByAllTier()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getVms()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getInitialingVms()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getRunningVms()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getQuarantinedVms()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getRunningCloudlet()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(vmHistory.getThroughputFinishedCloudletsAllTiers()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param slaHistoryList
     * @return 
     */
    public static boolean writeSLAHistoryList(ArrayList<MonitorSLAHistory> slaHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Monitor_SLAHistory.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "DayOfWeek,Hour,Minute,Response Time,Delay TIme,SLA Violation Number"
                                        + ",SLA Violation Percent, SLA Violation Second, Cancel Cloudlet"
                                        + ", Failed Cloudlet, Finished Cloudlet";
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new SLA history object list to the CSV file
            for(MonitorSLAHistory slaHistory : slaHistoryList){
                fileWriter.append(String.valueOf(slaHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getAvgResponseTimePerAllTiers()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getAvgDelayTimePerAllTiers()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getSlavNumberByAllTier()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getSlavPercent()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getSlavSecondByAlltier()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getCloudletsCancelled()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getCloudletFailedCounter()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(slaHistory.getCloudletFinished()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param endUserHistoryList
     * @return 
     */
    public static boolean writeEndUserHistoryList(ArrayList<MonitorEndUserHistory> endUserHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Monitor_EndUserHistory.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "DayOfWeek,Hour,Minute,Requests, Requests Length";
                                        
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new End USer history object list to the CSV file
            for(MonitorEndUserHistory endUserHistory : endUserHistoryList){
                fileWriter.append(String.valueOf(endUserHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(endUserHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(endUserHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(endUserHistory.getRequestsPerAllTier()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(endUserHistory.getRequestsLengthPerTier()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param analyzerHistoryList
     * @return 
     */
    public static boolean writeAnalyzerHistoryList(ArrayList<AnalyzerHistory> analyzerHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Analyzer_History.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "Day Number, Weekend, DayOfWeek,Hour,Minute, CPU Util., VMs count"
                                        + ", Throughput, Response Time, Delay Time, SLA Violation count"
                                        + ", SLA Violation Percent, SLA Violation Time"
                                        + ", Failed Cloudlet, Future Requests";
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new Analyzer history object list to the CSV file
            for(AnalyzerHistory analyzerHistory : analyzerHistoryList){
                
                fileWriter.append(String.valueOf(analyzerHistory.getDayNumber()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getWeekend()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getCpuUtilization()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getVmsCount()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getThroughput()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getResponseTime()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getDelayTime()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getSLAVCount()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getSLAVPercentage()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getSLAVTime()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getFailedCloudlet()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(analyzerHistory.getFutureUserRequest()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param plannerHistoryList
     * @return 
     */
    public static boolean writePlannerHistoryList(ArrayList<PlannerHistory> plannerHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Planner_History.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "DayOfWeek,Hour,Minute, Decisions, VMs, Purchase, Tier Type, Config Type";
            
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new Planner history object list to the CSV file
            for(PlannerHistory plannerHistory : plannerHistoryList){
                fileWriter.append(String.valueOf(plannerHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getDecision()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getVms()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getPurchase()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getTierType()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(plannerHistory.getConfiguration()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param executorHistoryList
     * @return 
     */
    public static boolean writeExecutorHistoryList(ArrayList<ExecutorHistory> executorHistoryList) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(FILE_PATH + "Executor_History.csv");

            //Write the CSV file header
            //CSV file header
            final String FILE_HEADER = "DayOfWeek,Hour,Minute, Action, Provisioning, Deprovisioning";
            
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new Executor history object list to the CSV file
            for(ExecutorHistory executorHistory : executorHistoryList){
                fileWriter.append(String.valueOf(executorHistory.getDayOfWeek()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(executorHistory.getHour()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(executorHistory.getMinute()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(executorHistory.getAction()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(executorHistory.getProvisioning()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(executorHistory.getDeProvisioning()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param fileName
     * @return 
     */
    public static ArrayList readCSV (final String fileName)	{
        
        /* Monitor VM History attributes index
          DAYOFWEEK
          HOUR
          MINUTE
          CPU_UTILIZATION
          CPU_LOAD
          VMS
          INITIALING_VMS
          RUNNING_VMS
          QUARANTINED_VM
          RUNNING_CLOUDLET
          THROUGHPUT */

        //Create a new list of Monitor VM History to be filled by CSV file data 
        ArrayList dataList = new ArrayList<>();
        
        BufferedReader fileReader = null;

        try {

            String line = "";

            //Create the file reader
            fileReader = new BufferedReader(new FileReader(FILE_PATH + fileName + ".csv"));

            //Read the CSV file header to skip it
            fileReader.readLine();

            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                ArrayList list = new ArrayList();                
                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                    
                    for (String token : tokens) {
                        if(token.contains("."))
                            list.add(Double.valueOf(token));
                        else
                            list.add(Integer.valueOf(token));
                    }
                }
                
                dataList.add(list);
            }
            
        } 
        catch (Exception e) {
                System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
        return dataList;
    }
    
}
