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
 * MonitorVmHistory class is the knowledge-base component for monitoring phase of auto-scaling system.
 * This class is called at the end of monitoring phase by Monitor class to save the calculated parameters.
 */
public class MonitorVmHistory extends History{
    /* The Cpu utilization of vms */
    private double cpuUtilizationByAllTier;
    private double cpuLoadByAllTier;
    /* vms */
    private int vms;
    private int initialingVms;
    private int runningVms;
    private int quarantinedVms;
    private int runningCloudlet;
    private int[] vmsConfig;
    private int[] vmsPurchase;
    private double throughputFinishedCloudletsAllTiers;
    public MonitorVmHistory(
                       double cpuUtilizationByAllTier, 
                       double cpuLoadByAllTier,
                       int vms, 
                       int initialingVms, 
                       int runningVms, 
                       int quarantinedVms,
                       int runningCloudlet, 
                       int[] vmsConfig, 
                       int[] vmsPurchase,
                       double throughputFinishedCloudletsAllTiers
                       ){
        // Set Day and Time 
        super();
        
        this.cpuUtilizationByAllTier = cpuUtilizationByAllTier;
        this.cpuLoadByAllTier = cpuLoadByAllTier;
        this.vms = vms;
        
        this.initialingVms = initialingVms;
        this.runningVms = runningVms;
        this.quarantinedVms = quarantinedVms;
        this.runningCloudlet = runningCloudlet;
        this.vmsConfig = vmsConfig;
        this.vmsPurchase = vmsPurchase;
        this.throughputFinishedCloudletsAllTiers = throughputFinishedCloudletsAllTiers;
    }
    
        
    public double getCpuUtilizationByAllTier(){
        return cpuUtilizationByAllTier;
    }
    
    public void setCpuUtilizationByAllTier (double cpuUtilizationByAllTier){
        this.cpuUtilizationByAllTier = cpuUtilizationByAllTier;
    }
    
    public void setCpuLoadByAllTier(double cpuLoadByAllTier){
        this.cpuLoadByAllTier = cpuLoadByAllTier;
    }
    
    public double getCpuLoadByAllTier(){
        return cpuLoadByAllTier;
    }
    
    public int getVms(){
        return vms;
    }
    
    public void setVms(int onDemandVms){
        this.vms = onDemandVms;
    }
    
    public int getInitialingVms(){
        return initialingVms;
    }
    
    public void setInitialingVms (int initialingVms){
        this.initialingVms = initialingVms;
    }
    
    public int getRunningVms (){
        return runningVms;
    }
    
    public void setRunningVms(int runningVms){
        this.runningVms = runningVms;
    }
    
    public int getQuarantinedVms (){
        return quarantinedVms;
    }
    
    public void setQuarantinedVms (int quarantinedVms){
        this.quarantinedVms = quarantinedVms;
    }
    public int getRunningCloudlet (){
        return runningCloudlet;
    }
    
    public void setRunningCloudlet (int runningCloudlet){
        this.runningCloudlet = runningCloudlet;
    }
    
    public int[] getVmsConfig (){
        return vmsConfig;
    }
    
    public void setVmsConfig (int[] vmsConfig){
        this.vmsConfig = vmsConfig;
    }
    
    public int[] getVmsPurchase(){
        return vmsPurchase;
    }
    
    public void setVmsPurchase(int[] vmsPurchase){
        this.vmsPurchase = vmsPurchase;
    }
    
       
    public double getThroughputFinishedCloudletsAllTiers(){
        return throughputFinishedCloudletsAllTiers;
    }
    
    public void setThroughputFinishedCloudletsAllTiers(double throughputFinishedCloudletsAllTiers){
        this.throughputFinishedCloudletsAllTiers = throughputFinishedCloudletsAllTiers;
    }
}
