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

package autoscalesim.applicationprovider.autoscaling.knowledgebase;

/**
 * AnalyzerHistory class is the knowledge-base component for analyzing phase of auto-scaling system.
 * This class is called at the end of analyzing phase by Analyzer class to save the calculated parameters.
 */
public class AnalyzerHistory extends History{
    // the environment parameters are set by itslef
    
    private double cpuUtilization;
    private double vmsCount;
    private double throughput;
    
    private double responseTime;
    private double delayTime;
    private double slavCount;
    private double slavPercentage;
    private double slavTime;
    private double failedCloudlet;
    
    private double futureUserRequest;
    
    
    
    /**
     * 
     * @param cpuUtilization
     * @param vmsCount
     * @param throughput
     * @param responseTime
     * @param delayTime
     * @param slavCount 
     * @param slavPercentage
     * @param slavTime
     * @param failedCloudlet
     * @param futureUserRequest 
     */
    public AnalyzerHistory(
            double cpuUtilization, 
            double vmsCount, 
            double  throughput,
            double responseTime, 
            double delayTime, 
            double slavCount, 
            double slavPercentage, 
            double slavTime, 
            double failedCloudlet,
            double futureUserRequest){
        // set date and time 
        super();
        
        this.cpuUtilization = cpuUtilization;
        this.vmsCount = vmsCount;
        this.throughput = throughput;
        
        this.responseTime = responseTime;
        this.delayTime = delayTime;
        this.slavCount = slavCount;
        this.slavPercentage = slavPercentage;
        this.slavTime = slavTime;
        this.failedCloudlet = failedCloudlet;
        
        this.futureUserRequest = futureUserRequest;
    }
    
    
    public double getCpuUtilization(){
        return cpuUtilization;
    }
    
    public void setCpuUtilization (double cpuUtilization){
        this.cpuUtilization = cpuUtilization;
    }
    
    public double  getVmsCount(){
        return vmsCount;
    }
    
    public void setVmsCount(double  vmsCount){
        this.vmsCount = vmsCount;
    }
    
    public double getThroughput(){
        return throughput;
    }
    
    public void setThroughput(double throughput){
        this.throughput = throughput;
    }
    
    public double getResponseTime(){
        return responseTime;
    }
    
    public void setResponseTime(double responseTime){
        this.responseTime = responseTime;
    }
    
    public double getDelayTime(){
        return delayTime;
    }
    
    public void setDelayTime(double delayTime){
        this.delayTime = delayTime;
    }
    
    
    public double getSLAVCount(){
        return slavCount;
    }
    
    public void setSLAVCount(double slavCount){
        this.slavCount = slavCount;
    }
    
    public double getSLAVPercentage(){
        return slavPercentage;
    }
    
    public void setSLAVPercentage(double slavPercentage){
        this.slavPercentage = slavPercentage;
    }
    
    public double getSLAVTime(){
        return slavTime;
    }
    
    public void setSLAVTime(double slavTime){
        this.slavTime = slavTime;
    }
    
    public double getFailedCloudlet(){
        return failedCloudlet;
    }
    
    public void setFailedCloudlet(double failedCloudlet){
        this.failedCloudlet = failedCloudlet;
    }
    
    public double getFutureUserRequest(){
        return futureUserRequest;
    }
    
    public void setFutureUserrequest(double futureUserRequest){
        this.futureUserRequest = futureUserRequest;
    }
    
}
