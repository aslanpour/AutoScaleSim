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
 * MonitorSLAHistory class is the knowledge-base component for monitoring phase of auto-scaling system.
 * This class is called at the end of monitoring phase by Monitor class to save the calculated parameters.
 */
public class MonitorSLAHistory extends History {
    
    /* Average Waiting time(seconds) of cloudlets in a minute */
    private double avgResponseTimePerAllTiers;
        
    private double avgDelayTimePerAllTiers;
          
    private double slavNumberByAllTier;
    /* The number of SLA Violation in a minute base on vm config*/
    private int[] slavNumbersByVmConfigs;
    private int[] slavNumbersByVmPurchases;
    /* The second of SLA Violation in a minute base on vm config*/
    
    private double slavPercent;
    
    private double slavSecondByAllTier;
    /* The second of SLA Violation in a minute base on vm config*/
    private double[] slavSecondsByVmConfigs;
    /* */
    private double[] slavSecondsByVmPurchases;
    /* */
    
    private int cloudletsCancelled;
    
    private int cloudletFailedCounter;
    
    private int cloudletFinished;
    
    public MonitorSLAHistory(double avgResponseTimePerAllTiers
                        , double avgDelayTime
                        , double slavNumberByAllTier
                        , int[] slavNumbersByVmConfigs
                        , int[] slavNumbersByVmPurchases            
                        , double slavPercent
                        , double slavSecondByAllTier
                        , double[] slavSecondsByVmConfigs
                        , double[] slavSecondsByVmPurchases
                        , int cloudletsCancelled
                        , int cloudletFailedCounter
                        , int cloudletFinished
                        ){
        // Set Date and Time
        super();
        this.avgResponseTimePerAllTiers = avgResponseTimePerAllTiers;
              
        this.avgDelayTimePerAllTiers = avgDelayTime;
        this.slavNumberByAllTier = slavNumberByAllTier;
        this.slavNumbersByVmConfigs = slavNumbersByVmConfigs;
        this.slavNumbersByVmPurchases = slavNumbersByVmPurchases;
        this.slavPercent = slavPercent;
        this.slavSecondByAllTier = slavSecondByAllTier;
        this.slavSecondsByVmConfigs = slavSecondsByVmConfigs;
        this.slavSecondsByVmPurchases = slavSecondsByVmPurchases;
        this.cloudletsCancelled = cloudletsCancelled;
        this.cloudletFailedCounter = cloudletFailedCounter;
        this.cloudletFinished = cloudletFinished;
        
    }
            
    public double getAvgResponseTimePerAllTiers(){
        return avgResponseTimePerAllTiers;
    }
    
    public void setAvgResponseTimePerAllTiers(double avgResponseTimePerAllTiers){
        this.avgResponseTimePerAllTiers = avgResponseTimePerAllTiers;
    }
    
    public double getAvgDelayTimePerAllTiers(){
        return avgDelayTimePerAllTiers;
    }
    
    public void setAvgDelayTimePerAllTiers(double avgDelayTimePerAllTiers){
        this.avgDelayTimePerAllTiers = avgDelayTimePerAllTiers;
    }
            
    public double getSlavNumberByAllTier(){
        return slavNumberByAllTier;
    }
    
    public int[] getSLAVNumbersByVmConfigs (){
        return slavNumbersByVmConfigs;
    }
    
    public void setSLAVNumbersByVmConfigs(int[] slavNumbersByVmConfigs){
        this.slavNumbersByVmConfigs = slavNumbersByVmConfigs;
    }
    
    public int[] getSLAVNumbersByVmPurchases(){
        return slavNumbersByVmPurchases;
    }
    
    public void setSLAVNumbersByVmPurchases (int[] slavNumbersByVmPurchases){
        this.slavNumbersByVmPurchases = slavNumbersByVmPurchases;
    }
    
    public double getSlavPercent(){
        return slavPercent;
    }
    
    public void setSLAVPercent(double slavPercent){
        this.slavPercent = slavPercent;
    }
    
    public double getSlavSecondByAlltier(){
        return slavSecondByAllTier;
    }
    
    
    public double[] getSLAVSecondsByVmConfigs (){
        return slavSecondsByVmConfigs;
    }
    
    public void setSLAVSecondsByVmConfigs ( double[] slavSecondsByVmConfigs){
        this.slavSecondsByVmConfigs = slavSecondsByVmConfigs;
    }
    
    public double[] getSLAVSecondsByVmPurchases(){
        return slavSecondsByVmPurchases;
    }
    
    public void setSLAVSecondsByVmPurchases (double[] slavSecondsByVmPurchases){
        this.slavSecondsByVmPurchases = slavSecondsByVmPurchases;
    }
    
    public int getCloudletsCancelled(){
        return cloudletsCancelled;
    }
    
    public void setCloudletsCancelled(int cloudletsCancelled){
        this.cloudletsCancelled = cloudletsCancelled;
    }
    
    public int getCloudletFailedCounter(){
        return cloudletFailedCounter;
    }
    
    public void setCloudletFailedCounter(int cloudletFailedCounter){
        this.cloudletFailedCounter = cloudletFailedCounter;
    }
    
    public int getCloudletFinished(){
        return cloudletFinished;
    }
    
    public void setCloudletFinished(int cloudletFinished){
        this.cloudletFinished = cloudletFinished;
    }
    
    
}
