/*
 * Title:        AutoScaleSim Toolkit
 * Description:  AutoScaleSim (Auto-Scaling Simulation) Toolkit for Modeling and Simulation
 *               of Autonomous Systems for Web Applications in Cloud
 *
 * Copyright (c) 2018, Islamic Azad University, Jahrom, Iran
 *
 * Authors: Mohammad Sadegh Aslanpour, Adel Nadjaran Toosi, Javid Taheri
 * 
 */
package autoscalesim.applicationprovider.autoscaling;

import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import autoscalesim.applicationprovider.ApplicationProvider;
import static autoscalesim.applicationprovider.ApplicationProvider.getMonitor;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.log.DateTime;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;;
import java.util.ArrayList;
import org.cloudbus.cloudsim.Log;
/**
 * Analyzer class is the second phase of auto-scaling, where the monitored parameters are analyzed.
 * Its Inputs are a collection of monitored parameters regarding resources, SLA and user's behavior
 * Its action is to use simple or complex methods to analyze all parameters.
 * Its outputs are a more accurate value for each parameter.
 */
public class Analyzer {
    
    private ArrayList<AnalyzerHistory> historyList;
    
    private String[] analysisMethod;
    private final int timeWindow;
    private final double[] sESAlpha;
    private double oldSESOutput;

    /**
     * 
     * @param analysisMethod
     * @param timeWindow
     * @param sESAlpha 
     */
    public Analyzer(String[]analysisMethod, 
                                            int timeWindow,  
                                            double sESAlpha[]
                                            ){

        setAnalysisMethod(analysisMethod);
        
        setHistoryList(new ArrayList<AnalyzerHistory>());
        
        this.timeWindow = timeWindow;
        this.sESAlpha = sESAlpha;
        this.oldSESOutput = Double.MIN_VALUE;
        
    }
    
   /**
    * Analyzing effective parameters
    */
    public void doAnalysis(){
/* initialing Analysis Parameters */
        
        // RESOURCE-AWARE
        double cpuUtilization = 0; 
        double vmCount = 0;
        double throughput = 0;
        
         // SLA-AWARE
        double responseTime = 0;
        double delayTime = 0; 
        double  slavCount = 0;
        double slavPercentage = 0;
        double slavTime = 0;
        double failedCloudlet = 0;
        
        // User-Aware
        double futureWorkload = 0;

        // Env. parameters have been set already.
        
/* calculation of analysis parameters */
        
        // RESOURCE-AWARE
        cpuUtilization = ANLZ_CPUUtil(); 
        vmCount = ANLZ_VMCount();
        throughput = ANLZ_Throughput();
        
        // SLA-AWARE
        responseTime = ANLZ_ResponseTime();
        delayTime = ANLZ_DelayTime();
        
        slavCount = ANLZ_SLAVCount();
        slavPercentage = ANLZ_SLAVPercentage();
        slavTime = ANLZ_SLAVTime();
        failedCloudlet = ANLZ_FailedCloudlet();
        
        // USER-BEHAVIOR-AWARE
        futureWorkload = ANLZ_FutureWorkload();
        
        // Environment-Aware
            // History class set these parameters itself.
        
        /* SAVE analysis results to history */
        AnalyzerHistory analyzerHistory = new AnalyzerHistory(
                                                             cpuUtilization
                                                            , vmCount
                                                            , throughput
                
                                                            , responseTime
                                                            , delayTime
                                                            , slavCount
                                                            , slavPercentage
                                                            , slavTime
                                                            , failedCloudlet
                
                                                            , futureWorkload
                                                            );
        getHistoryList().add(analyzerHistory);
    }
    
    /**
     * Analyzing CPU utilization
     * @return 
     */
    private double ANLZ_CPUUtil(){
        double analyzedCPUUtilization = -1;
        // Get VM monitor history
        ArrayList<MonitorVmHistory> tmpVmHistoryList = getMonitor().getVmHistoryList();
        int sizeVmHistory = tmpVmHistoryList.size();
        // Set the latest monitored Cpu utilization item
        double parameter = tmpVmHistoryList.get(sizeVmHistory - 1).getCpuUtilizationByAllTier();
        // Set a list of monitored Cpu utilization
        int window = timeWindow;
        if (sizeVmHistory < window)
            window = sizeVmHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpVmHistoryList.get(sizeVmHistory - 1 - i).getCpuUtilizationByAllTier();
        }
        
                     
        //Index 0 of AnalysisMethod variable indicates the analyzing method for analyzing 'CPU utilization'    
        switch(getAnalysisMethod()[0]){
            // Simple
            case "SIMPLE": 
                analyzedCPUUtilization = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedCPUUtilization = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedCPUUtilization = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedCPUUtilization = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedCPUUtilization = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[0]);
                oldSESOutput = analyzedCPUUtilization;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - cpu utilization analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - cpu utilization analysis method not found");
        }
        
        return analyzedCPUUtilization;
    }
    
    /**
     * Analyzing VMs count
     * @return 
     */
    private double ANLZ_VMCount(){
        double analyzedVmCount = -1;
        // Get VM monitor history
        ArrayList<MonitorVmHistory> tmpVmHistoryList = getMonitor().getVmHistoryList();
        int sizeVmHistory = tmpVmHistoryList.size();
        // Set the latest monitored VM count item
        double parameter = tmpVmHistoryList.get(sizeVmHistory - 1).getVms();
        // Set a list of monitored VM count
        int window = timeWindow;
        if (sizeVmHistory < window)
            window = sizeVmHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpVmHistoryList.get(sizeVmHistory - 1 - i).getVms();
        }
        
                     
        //Index 1 of AnalysisMethod variable indicates the analyzing method for analyzing 'VM count'    
        switch(getAnalysisMethod()[1]){
            // Simple
            case "SIMPLE": 
                analyzedVmCount = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedVmCount = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedVmCount = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedVmCount = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedVmCount = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[1]);
                oldSESOutput = analyzedVmCount;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - VM count analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - VM count analysis method not found");
        }
        
        return analyzedVmCount;
    }
    
    /**
     * Analyzing Throughput
     * @return 
     */
    private double ANLZ_Throughput(){
        double analyzedThroughput = -1;
        // Get VM monitor history
        ArrayList<MonitorVmHistory> tmpVmHistoryList = getMonitor().getVmHistoryList();
        int sizeVmHistory = tmpVmHistoryList.size();
        // Set the latest monitored Throughout item
        double parameter = tmpVmHistoryList.get(sizeVmHistory - 1).getThroughputFinishedCloudletsAllTiers();
        // Set a list of monitored Throughout
        int window = timeWindow;
        if (sizeVmHistory < window)
            window = sizeVmHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpVmHistoryList.get(sizeVmHistory - 1 - i).getThroughputFinishedCloudletsAllTiers();
        }
        
                     
        //Index 2 of AnalysisMethod variable indicates the analyzing method for analyzing 'Throughout'    
        switch(getAnalysisMethod()[2]){
            // Simple
            case "SIMPLE": 
                analyzedThroughput = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedThroughput = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedThroughput = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedThroughput = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedThroughput = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[2]);
                oldSESOutput = analyzedThroughput;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - Throughout analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - Throughout analysis method not found");
        }
        return analyzedThroughput;
    }
    
    /**
     * Analyzing Response Time
     * @return 
     */
    private double ANLZ_ResponseTime(){
        double analyzedResponseTime = -1;
        
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored Response Time item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getAvgResponseTimePerAllTiers();
        // Set a list of monitored Response Time
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getAvgResponseTimePerAllTiers();
        }
        
                     
        //Index 3 of AnalysisMethod variable indicates the analyzing method for analyzing 'Response Time'    
        switch(getAnalysisMethod()[3]){
            // Simple
            case "SIMPLE": 
                analyzedResponseTime = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedResponseTime = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedResponseTime = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedResponseTime = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedResponseTime = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[3]);
                oldSESOutput = analyzedResponseTime;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - Response Time analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - Response Time analysis method not found");
        }
        return analyzedResponseTime;
    }
    
    /**
     * Analyzing Delay Time
     * @return 
     */
    private double ANLZ_DelayTime(){
        double analyzedDelayTime = -1;
        
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored Delay Time item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getAvgDelayTimePerAllTiers();
        // Set a list of monitored Delay Time
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getAvgDelayTimePerAllTiers();
        }
        
                     
        //Index 4 of AnalysisMethod variable indicates the analyzing method for analyzing 'Delay Time'    
        switch(getAnalysisMethod()[4]){
            // Simple
            case "SIMPLE": 
                analyzedDelayTime = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedDelayTime = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedDelayTime = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedDelayTime = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedDelayTime = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[4]);
                oldSESOutput = analyzedDelayTime;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - Delay Time analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - Delay Time analysis method not found");
        }
        return analyzedDelayTime;
    }
    
    /**
     * Analyzing SLA violation count
     * @return 
     */
    private double ANLZ_SLAVCount(){
        double analyzedSLAVCount = -1;
        
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored SLA Violation count item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getSlavNumberByAllTier();
        // Set a list of monitored SLA Violation Count
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getSlavNumberByAllTier();
        }
        
                     
        //Index 5 of AnalysisMethod variable indicates the analyzing method for analyzing 'SLA Violation Count'    
        switch(getAnalysisMethod()[5]){
            // Simple
            case "SIMPLE": 
                analyzedSLAVCount = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedSLAVCount = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedSLAVCount = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedSLAVCount = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedSLAVCount = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[5]);
                oldSESOutput = analyzedSLAVCount;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - SLA Violation Count analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - SLA Violation Count analysis method not found");
        }
        
        return analyzedSLAVCount;
    }
    
    /**
     * Analyzing SLA violation percentage
     * @return 
     */
    private double ANLZ_SLAVPercentage(){
        double analyzedSLAVPercentage = -1;
                      
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored SLA Violation Percent item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getSlavPercent();
        // Set a list of monitored SLA Violation Percent
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getSlavPercent();
        }
        
                     
        //Index 6 of AnalysisMethod variable indicates the analyzing method for analyzing 'SLA Violation Percent'    
        switch(getAnalysisMethod()[6]){
            // Simple
            case "SIMPLE": 
                analyzedSLAVPercentage = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedSLAVPercentage = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedSLAVPercentage = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedSLAVPercentage = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedSLAVPercentage = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[6]);
                oldSESOutput = analyzedSLAVPercentage;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - SLA Violation Percent analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - SLA Violation Percent analysis method not found");
        }
        return analyzedSLAVPercentage;
    }
    
    /**
     * Analyzing SLA violation time (second)
     * @return 
     */
    private double ANLZ_SLAVTime(){
        double analyzedSLAVTime = -1;
        
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored SLA Violation Time item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getSlavSecondByAlltier();
        // Set a list of monitored SLA Violation Time
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getSlavSecondByAlltier();
        }
        
                     
        //Index 7 of AnalysisMethod variable indicates the analyzing method for analyzing 'SLA Violation Time'    
        switch(getAnalysisMethod()[7]){
            // Simple
            case "SIMPLE": 
                analyzedSLAVTime = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedSLAVTime = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedSLAVTime = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedSLAVTime = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedSLAVTime = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[7]);
                oldSESOutput = analyzedSLAVTime;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - SLA Violation Time analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - SLA Violation Time analysis method not found");
        }
        
        return analyzedSLAVTime;
    }
    
    /**
     * Analyzing Failed cloudlets
     * @return 
     */
    private double ANLZ_FailedCloudlet(){
        double analyzedFailedCloudlet = -1;
        
        // Get SLA monitor history
        ArrayList<MonitorSLAHistory> tmpSLAHistoryList = getMonitor().getSLAHistoryList();
        int sizeSLAHistory = tmpSLAHistoryList.size();
        // Set the latest monitored Failed Cloudlet item
        double parameter = tmpSLAHistoryList.get(sizeSLAHistory - 1).getCloudletFailedCounter();
        // Set a list of monitored Failed Cloudlet
        int window = timeWindow;
        if (sizeSLAHistory < window)
            window = sizeSLAHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpSLAHistoryList.get(sizeSLAHistory - 1 - i).getCloudletFailedCounter();
        }
        
                     
        //Index 8 of AnalysisMethod variable indicates the analyzing method for analyzing 'Failed Cloudlet'    
        switch(getAnalysisMethod()[8]){
            // Simple
            case "SIMPLE": 
                analyzedFailedCloudlet = parameter;
                break;
                
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedFailedCloudlet = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedFailedCloudlet = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedFailedCloudlet = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedFailedCloudlet = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[8]);
                oldSESOutput = analyzedFailedCloudlet;
                break;
            
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - Failed Cloudlet analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - Failed Cloudlet analysis method not found");
        }
        
        return analyzedFailedCloudlet;
    }
    
        
    /**
     * Analyzing Workload
     * @return 
     */
    private double ANLZ_FutureWorkload(){
        double analyzedFutureWorkload = -1;
        
        // Get EndUser monitor history
        ArrayList<MonitorEndUserHistory> tmpEndUserHistoryList = getMonitor().getEndUserHistoryList();
        int sizeEndUserHistory = tmpEndUserHistoryList.size();
        // Set the latest monitored Workload item
        double parameter = tmpEndUserHistoryList.get(sizeEndUserHistory - 1).getRequestsPerAllTier();
        // Set a list of monitored Workload
        int window = timeWindow;
        if (sizeEndUserHistory < window)
            window = sizeEndUserHistory;
        
        double parameterList[] = new double[window];
        for(int i = 0; i< window;i++){
            parameterList[i] = tmpEndUserHistoryList.get(sizeEndUserHistory - 1 - i).getRequestsPerAllTier();
        }
        
                     
        //Index 9 of AnalysisMethod variable indicates the analyzing method for analyzing 'Future Workload'    
        switch(getAnalysisMethod()[9]){
            //Simple
            case "SIMPLE": 
                analyzedFutureWorkload = parameter;
            // Moving Average
            case "COMPLEX_MA": 
                    analyzedFutureWorkload = calculateMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average
            case "COMPLEX_WMA": 
                    analyzedFutureWorkload = calculateWeightedMovingAverage(parameterList);
                break;
                
            // Weighted Moving Average (weighting by Fibonacci numbers)
            case "COMPLEX_WMAfibo": 
                analyzedFutureWorkload = calculateWeightedMovingAverageFibonacci(parameterList);
                break;
                
            // Single exponential smoothing
            case "COMPLEX_SES":
                analyzedFutureWorkload = calculateSingleExponentialSmoothing(parameter, oldSESOutput, sESAlpha[9]);
                oldSESOutput = analyzedFutureWorkload;
                break;
                
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - Future Workload analysis method not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - Future Workload analysis method not found");
        }
        return analyzedFutureWorkload;
    }
    
    /**
     * Analyzes the indicated parameter by Moving Average method
     * @param parameterList
     * @return 
     */
    private double calculateMovingAverage(double[] parameterList){
        double sumMovingAverage = 0;
        for(int i = 0; i < parameterList.length; i++){
            sumMovingAverage += parameterList[i];
        }
        return sumMovingAverage / parameterList.length;
    }
    
    /**
     * Analyzes the indicated parameter by Weighted Moving average method
     * @param parameterList
     * @return 
     */
    private double calculateWeightedMovingAverage(double[] parameterList){
        double sumWeightedItem = 0;
        double sumWeight = 0;
        int weight = 1;

        for(int i = 0; i < parameterList.length; i++){
            sumWeightedItem += parameterList[i] * weight;  

            sumWeight += weight;
            weight++;
        }
        return sumWeightedItem / sumWeight;
    }
    
    /**
     * Analyzes the indicated parameter by Weighted Moving average method.
     * This method uses Fibonacci technique.
     * @param parameterList
     * @return 
     */
    private double calculateWeightedMovingAverageFibonacci(double[] parameterList){
        double sumWeightedItems = 0;
        double sumWeight = 0;
        int weight;
        int fibo1 = 0; 
        int fibo2 = 1;
        for(int i = 0; i < parameterList.length; i++){
            weight = fibo1 + fibo2;
            sumWeightedItems += parameterList[i] * weight;
            sumWeight += weight;
            fibo1 = fibo2; fibo2 = weight;                
        }
        return sumWeightedItems / sumWeight;
    }
    
    /**
     * Analyzes the indicated parameter by Single Exponential Parameter method
     * @param parameter
     * @param oldSESOutput
     * @param alpha
     * @return 
     */
    private double calculateSingleExponentialSmoothing(double parameter, double oldSESOutput, double alpha){
        if (oldSESOutput == Double.MIN_VALUE) 
            oldSESOutput = parameter;
        
        return (alpha * parameter) + ((1 - alpha) * oldSESOutput);
    }
    
    /**
     * Gets analyzing method
     * @return 
     */
    private String[] getAnalysisMethod(){
        return analysisMethod;
    }
    
    /**
     * Sets Analyzing method
     * @param analysisMethod 
     */
    private void setAnalysisMethod(String[] analysisMethod){
        this.analysisMethod = analysisMethod;
    }
    
    /**
     * Gets the History of analyzing phase
     * @return 
     */
    public ArrayList<AnalyzerHistory> getHistoryList(){
        return historyList;
    }
    
    /**
     * Sets the History of analyzing phase
     * @param historyList 
     */
    private void setHistoryList(ArrayList<AnalyzerHistory> historyList){
        this.historyList = historyList;
    }
    
    /**
     * Returns the size of History for analyzing phase
     * @return 
     */
    public int sizeHistory(){
        return getHistoryList().size();
    }
    
    /**
     * Returns the latest record of analyzing history
     * @return 
     */
    public AnalyzerHistory latestHistoryRec(){
        return getHistoryList().get(sizeHistory()-1);
    }

}

