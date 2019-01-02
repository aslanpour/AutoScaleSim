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

import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.log.DateTime;

/**
 * VmStartUpDelay class indicates how much it takes to run a requested VM. Precisely, the time from 
 * requesting a VM from cloud provider to the time the VM is ready and can serves the incoming load.
 * 
 */
public class VmStartUpDelay {

    /**
     * Calculates the delay time for instantiation of a VM
     * @param BASE_DELAY_IN_VM_START_UP
     * @param startUpDelayType
     * @param config
     * @return 
     */
    public static double calculateVmStartUpDelay(double BASE_DELAY_IN_VM_START_UP
                                                , String startUpDelayType
                                                , int config){
        double vmStartUpDelay = 0;
        vmStartUpDelay = BASE_DELAY_IN_VM_START_UP ;
                
        if (startUpDelayType == "Dynamic"){ 
            // time of the day
            double delayCausedByTimeofTheDay = 0;
            double hour = DateTime.hourOfDay();
            if(hour >=0 && hour <=5) delayCausedByTimeofTheDay = 0;
            else if (hour >=6 && hour <=12) delayCausedByTimeofTheDay = 1;
            else if (hour >= 13 && hour <=18) delayCausedByTimeofTheDay = 2;
            else if (hour >= 19 && hour <= 23) delayCausedByTimeofTheDay = 1;
            
            delayCausedByTimeofTheDay = delayCausedByTimeofTheDay * AutoScaleSimTags.aMinute;
            
            vmStartUpDelay += delayCausedByTimeofTheDay;
            
            // vm config
            double delayCausedByVmConfig = 0;
            if(config == AutoScaleSimTags.VM_CONFIG_T2MICRO)
                delayCausedByVmConfig = 0;
            else if (config == AutoScaleSimTags.VM_CONFIG_T2SMALL)
                delayCausedByVmConfig = 1;
            else if (config == AutoScaleSimTags.VM_CONFIG_T2MEDIUM)
                delayCausedByVmConfig = 2;
            else if (config == AutoScaleSimTags.VM_CONFIG_T2LARGE)
                delayCausedByVmConfig = 3;
            
            delayCausedByVmConfig = delayCausedByVmConfig * AutoScaleSimTags.aMinute;
            
            vmStartUpDelay += delayCausedByVmConfig;
        }
        
        return vmStartUpDelay;
    }
    
}
