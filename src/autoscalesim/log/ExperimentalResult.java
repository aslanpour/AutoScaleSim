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
package autoscalesim.log;

import autoscalesim.applicationprovider.ApplicationProvider;
import static autoscalesim.applicationprovider.ApplicationProvider.getAnalyzer;
import static autoscalesim.applicationprovider.ApplicationProvider.getExecutor;
import static autoscalesim.applicationprovider.ApplicationProvider.getMonitor;
import static autoscalesim.applicationprovider.ApplicationProvider.getPlanner;
import static autoscalesim.applicationprovider.ApplicationProvider.lastCloudletReceivedId;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import autoscalesim.cloudprovider.Vm;
import static autoscalesim.log.AutoScaleSimTags.dft;
import static autoscalesim.log.AutoScaleSimTags.oneTab;
import static autoscalesim.log.AutoScaleSimTags.twoTabs;
import java.util.ArrayList;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import static org.cloudbus.cloudsim.lists.VmList.getOnDemandVmsList;

/**
 * ExperimentalResult class is ran at the end of simulation. It calculates the metrics and prints in 
 * the console and/or an Excel file.
 */
public class ExperimentalResult {
    
    // error checker
    public static boolean errorChecker = false;
    public static String error = "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator")+
            "error" + System.getProperty("line.separator");

        public static void showResultsInConsole(){
            
            double totalCloudletReceived = (lastCloudletReceivedId + 1);
            
            Log.printLine("***********************************************************************");
            Log.printLine("Simulation is Finished At: " + DateTime.timeStamp());
            Log.printLine("Total User requests : " + totalCloudletReceived);
            Log.printLine( 
                (char)27 + "[44;1m----------------------------------------------------------------------------------"
                + (char)27 + "[0;1m");
            // Set bill for remained Vms
            setRemainedVmsBill();
            
            Log.printLine("" + (char)27 + "[31m............................. EXPERIMENTAL RESULTS .............................");
    /*Monitor */
            Log.printLine("" + (char)27 + "[34mMONITOR" + ":.............................");
        /* Vm Metric */
            MonitorVmMetrics();
            
        /* SLA Metric */
            Log.printLine(oneTab + "SLA Metrics: ...");
            MonitorSLAMetrics();
        /* End user Metrics*/
            Log.print(oneTab + "End User Metrics: ...");
            monitorEndUserMetrics();
            
    /* Analyzer */
            Log.printLine();
            Log.printLine("" + (char)27 + "[34mANALYZE" + ":.............................");
            analyzerMetrics();
    /* PlannerRuleBased */
            Log.printLine("" + (char)27 + "[34mPLANNING" + ":.............................");
            plannerMetrics();
    /* Executor */
           Log.printLine("" + (char)27 + "[34mEXECUTOR" + ":.............................");
            executorMetrics();
    /* Cost */
            Log.printLine("" + (char)27 + "[31mCOST" + ":.............................");
            costMetrics();
            Log.printLine("**************************************************************************");
            
            if(errorChecker == true)
                Log.print(error);
            
        }
        
        /**
         * 
         * @param ExperimentalReportItemList 
         */
        public static void printReports(String[] ITEMS_TO_REPORT_LIST){
            // Write to CSV file
            for (String reportWhat : ITEMS_TO_REPORT_LIST) {
                switch(reportWhat){
                case "null":break;
                case "M_VM":    //ReadWriteExcel.writeVmHistoryList(getMonitor().getVmHistoryList());
                                ReadWriteCSV.writeVmHistoryList(getMonitor().getVmHistoryList());break;
                    
                case "M_SLA":   //ReadWriteExcel.writeSLAHistoryList(getMonitor().getSLAHistoryList());
                                ReadWriteCSV.writeSLAHistoryList(getMonitor().getSLAHistoryList());break;
                    
                case "M_User":  //ReadWriteExcel.writeEndUserHistoryList(getMonitor().getEndUserHistoryList());
                                ReadWriteCSV.writeEndUserHistoryList(getMonitor().getEndUserHistoryList());break;
                    
                case "ANALYZER"://ReadWriteExcel.writeAnalyzerHistoryList(getAnalyzer().getHistoryList());
                                ReadWriteCSV.writeAnalyzerHistoryList(getAnalyzer().getHistoryList());break;
                    
                case "PLANNER": //ReadWriteExcel.writePlannerHistoryList(getPlanner().getHistoryList());
                                ReadWriteCSV.writePlannerHistoryList(getPlanner().getHistoryList());break;
                    
                case "EXECUTOR"://ReadWriteExcel.writeExecutorHistoryList(getExecutor().getHistoryList());
                                ReadWriteCSV.writeExecutorHistoryList(getExecutor().getHistoryList());break;
                default:
                    break;
                }
            }
        }
               
        /**
         * 
         */
        private static void MonitorVmMetrics(){
            Log.printLine(oneTab + "VM Metrics:");
            double totalUtilizationTmpAllTiers = 0;
            double totalCpuLoadTmpAllTiers = 0;
            double avgLifeTime = 0;
            int maxVm = 0;
            int maxOnDemandVm = Integer.MIN_VALUE;
            int maxInitialingVm = 0; 
            int maxRunningVm = 0; 
            int maxQuarantinedVm = 0;
            double throughput = 0;
            
            int sizeHistory = getMonitor().getVmHistoryList().size();
            for(int i = 0; i<sizeHistory ; i++){
                MonitorVmHistory vmHistory = getMonitor().getVmHistoryList().get(i);
                // cpu util
                totalUtilizationTmpAllTiers += vmHistory.getCpuUtilizationByAllTier();
                totalCpuLoadTmpAllTiers += vmHistory.getCpuLoadByAllTier();
                // max vm
                if(vmHistory.getVms() > maxVm) maxVm = vmHistory.getVms();
                // max on demand
                if (vmHistory.getVmsPurchase()[AutoScaleSimTags.VM_PURCHASE_ON_DEMAND] > maxOnDemandVm) 
                    maxOnDemandVm = vmHistory.getVmsPurchase()[AutoScaleSimTags.VM_PURCHASE_ON_DEMAND];
                // max initialing
                if(vmHistory.getInitialingVms() > maxInitialingVm) 
                    maxInitialingVm = vmHistory.getInitialingVms();
                // max running
                if(vmHistory.getRunningVms() > maxRunningVm)
                    maxRunningVm = vmHistory.getRunningVms();
                // max quarantined
                if(vmHistory.getQuarantinedVms() > maxQuarantinedVm)
                    maxQuarantinedVm = vmHistory.getQuarantinedVms();
            }
            double avgUtilizationTmpAllTier = 0;
            double avgCpuLoadTmpAllTier = 0;
            avgUtilizationTmpAllTier = totalUtilizationTmpAllTiers / sizeHistory;
            avgCpuLoadTmpAllTier = totalCpuLoadTmpAllTiers / sizeHistory;
            

            //SD
            double skewCpuUtil = 0, 
            squareSkewCpuUtil = 0, 
            sumSquareSkewCpuUtil = 0, 
            cpuUtilizationSD = 0;
            
            double skewCpuLoad = 0, 
            squareSkewCpuLoad = 0, 
            sumSquareSkewCpuLoad = 0, 
            cpuLoadSD = 0;
            for(int i =0; i < sizeHistory;i++){
                MonitorVmHistory vmHistory = getMonitor().getVmHistoryList().get(i);
                //Util SD
                skewCpuUtil = vmHistory.getCpuUtilizationByAllTier() - avgUtilizationTmpAllTier;
                
                squareSkewCpuUtil = Math.pow(skewCpuUtil, 2);
                
                sumSquareSkewCpuUtil += squareSkewCpuUtil;
                //Load SD
                skewCpuLoad = vmHistory.getCpuLoadByAllTier() - avgCpuLoadTmpAllTier;
                squareSkewCpuLoad = Math.pow(skewCpuLoad, 2);
                sumSquareSkewCpuLoad += squareSkewCpuLoad;
            }
            //util
            sumSquareSkewCpuUtil = sumSquareSkewCpuUtil / sizeHistory;
            cpuUtilizationSD = Math.sqrt(sumSquareSkewCpuUtil);
            //load
            sumSquareSkewCpuLoad = sumSquareSkewCpuLoad / sizeHistory;
            cpuLoadSD = Math.sqrt(sumSquareSkewCpuLoad);
            
            Log.printLine(twoTabs + "CPU Utilization (%):" + dft.format(avgUtilizationTmpAllTier) + " Avg.   "
                                                            + dft.format(cpuUtilizationSD) + " SD");
            Log.printLine(twoTabs + "CPU Load (%): " + dft.format(avgCpuLoadTmpAllTier) + " Avg.   "
                                                            + dft.format(cpuLoadSD) + " SD");
            //Throughput
            double throughputAllTier = 0;
            
            for(int i = 0; i < sizeHistory; i++){
                throughputAllTier += getMonitor().getVmHistoryList().get(i).getThroughputFinishedCloudletsAllTiers();
            }
            throughputAllTier /= (double)sizeHistory;
            Log.printLine(oneTab + " Throughput: " + dft.format(throughputAllTier) + " %");
            
            // lifeTime
            for(Vm vm : getOnDemandVmsList()){
                avgLifeTime += vm.getLifeTime();
            }
            
            if(avgLifeTime!=0){// to avoid divided by zero
                //in second
                avgLifeTime /= getOnDemandVmsList().size();
            }
            
            // in minute
            double availableMinutes = 0;
            if(avgLifeTime !=0){// to avoid divided by zero
                availableMinutes = (int)(avgLifeTime / (double) AutoScaleSimTags.aMinute);
                availableMinutes  += (avgLifeTime % 60) / 100;
            }
            
            Log.printLine(twoTabs + "VMs Life Time (min.): " + dft.format(availableMinutes));
            
            Log.printLine(twoTabs + "Max used Vms: " + maxVm);
            
            Log.print(twoTabs + "Max used On-Demand Vms: " + maxOnDemandVm);
            
            Log.print(oneTab + "Max Initialing Vms: " + maxInitialingVm);
           
            Log.print(oneTab + "Max Running Vms: " + maxRunningVm);
           
            Log.printLine(oneTab + "Max Quarantined Vms: " + maxQuarantinedVm);
            
            Log.printLine();
        }
        
        public static void MonitorSLAMetrics(){
            
            reportOfSLAResponseTime();
            reportSLADelayTime();
            reportSLATailLatency();
            reportSLACancelCloudlet();
            reportSLAFailureCloudlet();
            reportSLASLAViolationPercent();
            reportSLASLAviolationSecond();
     }
        
    private static void reportOfSLAResponseTime(){
        int sizeHistory = getMonitor().getSLAHistoryList().size();
        /* Response Time average */
            // sum respone times
            double sumResponseTimeAllTiers = 0;
            for(int i = 0; i< sizeHistory; i++){
                sumResponseTimeAllTiers += getMonitor().getSLAHistoryList().get(i).getAvgResponseTimePerAllTiers();
            }
            // avg Response time
            double avgResponseTimeAllTiers = 0;
            avgResponseTimeAllTiers = sumResponseTimeAllTiers / sizeHistory;
            
            
            // SD Response time
            double skewAllTiers = 0;
            double squareSkewAllTiers = 0;
            double sumSquareSkewAllTiers = 0;
            double responseTimeAllTiersSD = 0;
            for(int i =0; i < sizeHistory;i++){
                MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);
                skewAllTiers = slaHistory.getAvgResponseTimePerAllTiers() - avgResponseTimeAllTiers;
                
                squareSkewAllTiers = Math.pow(skewAllTiers, 2);
                
                sumSquareSkewAllTiers += squareSkewAllTiers;
            }
            // variance
            sumSquareSkewAllTiers = sumSquareSkewAllTiers / sizeHistory;
            // SD
            responseTimeAllTiersSD = Math.sqrt(sumSquareSkewAllTiers);
            
            Log.printLine(twoTabs + "Response Time (Sec.): " + dft.format(avgResponseTimeAllTiers) + " Avg.   "
                                                            + dft.format(responseTimeAllTiersSD) + " SD");
    }
     
    
        
    private static void reportSLADelayTime(){
        int sizeHistory = getMonitor().getSLAHistoryList().size();
        /* Delay Time average */
            double sumDelayTimeAllTiers = 0;
            for(int i = 0; i< sizeHistory; i++){
                sumDelayTimeAllTiers += getMonitor().getSLAHistoryList().get(i).getAvgDelayTimePerAllTiers();
            }
            // avg Delay time
            double avgDelayTimeAllTiers = 0;
            
            avgDelayTimeAllTiers = sumDelayTimeAllTiers / sizeHistory;
            
            
            // SD Delay time
            double skewAllTiers = 0;
            double squareSkewAllTiers = 0;
            double sumSquareSkewAllTiers = 0;
            double delayTimeAllTiersSD = 0;
            for(int i =0; i < sizeHistory;i++){
                MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);
                skewAllTiers = slaHistory.getAvgDelayTimePerAllTiers() - avgDelayTimeAllTiers;
                
                squareSkewAllTiers = Math.pow(skewAllTiers, 2);
                
                sumSquareSkewAllTiers += squareSkewAllTiers;
            }
            // variance
            sumSquareSkewAllTiers = sumSquareSkewAllTiers / sizeHistory;
            // SD
            delayTimeAllTiersSD = Math.sqrt(sumSquareSkewAllTiers);
            
            Log.printLine(twoTabs + "Delay Time (Sec.): " + dft.format(avgDelayTimeAllTiers) + " Avg.   "
                                                            + dft.format(delayTimeAllTiersSD) + " SD");
    }
    
    private static void reportSLATailLatency(){
        // Generate a list of latencies
        double[] arr = new double[getMonitor().sizeSLAHistory()];
        for(int i = 0; i <arr.length; i++){
            arr[i] = getMonitor().getSLAHistoryList().get(i).getAvgDelayTimePerAllTiers();
        }
        
        //Percentile Calculation
        Percentile pctl = new Percentile();
        pctl.setData(arr);
        
        Log.printLine(twoTabs + "Tail Latency (percentile): " 
                + "  50th=" + dft.format(pctl.evaluate(50.0))
                + "  75th = " + dft.format(pctl.evaluate(75))
                + "  90th=" + dft.format(pctl.evaluate(90.0))
                + "  95th = " + dft.format(pctl.evaluate(95.0))
                + "  99th = " + dft.format(pctl.evaluate(99.0))
                + "  99.9th = " + dft.format(pctl.evaluate(99.9))
                + "  99.99th = " + dft.format(pctl.evaluate(99.99)));
    }
    
    private static void reportSLACancelCloudlet(){
        int sizeHistory = getMonitor().getSLAHistoryList().size();
        int totalCloudletCancelled = 0;
        
        for(int i =0; i < sizeHistory;i++){
            MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);

            totalCloudletCancelled += slaHistory.getCloudletsCancelled();
        }
        
        Log.printLine(twoTabs + "Cloudlet Canceled: " + totalCloudletCancelled);
    }
    
    private static void reportSLAFailureCloudlet(){
        int sizeHistory = getMonitor().getSLAHistoryList().size();
        
        int totalCloudletFailured = 0;
        
        for(int i =0; i < sizeHistory ;i++){
            MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);

            totalCloudletFailured += slaHistory.getCloudletFailedCounter();
        }
        
        Log.printLine(twoTabs + "Cloudlet Failured: "  + totalCloudletFailured);
    }
    
    private static void reportSLASLAViolationPercent(){
        int sizeHistory = getMonitor().getSLAHistoryList().size();
        // sla violation number
        int totalSlavNumberAllTiers = 0;

        for(int i =0; i <sizeHistory ;i++){
            MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);

            totalSlavNumberAllTiers += slaHistory.getSlavNumberByAllTier();
        }

        int totalReqAlltiers = 0;
        for(int i = 0; i< sizeHistory; i++){
            MonitorEndUserHistory endUserHistory = getMonitor().getEndUserHistoryList().get(i);

            totalReqAlltiers += endUserHistory.getRequestsPerAllTier();
        }

        Log.printLine(twoTabs + "SLA Violation: " 
                    + dft.format((totalSlavNumberAllTiers / (double)totalReqAlltiers) * 100) + "%");
    }
    
    private static void reportSLASLAviolationSecond(){
        // SLA Violation Second Per tier
            int sizeHistory = getMonitor().getSLAHistoryList().size();
            
            double totalSlavSecondAllTiers = 0;
            for(int i =0; i < sizeHistory;i++){
                MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);
                    totalSlavSecondAllTiers += slaHistory.getSlavSecondByAlltier();
            }
            
            double totalSlavHourAllTier = Math.ceil(totalSlavSecondAllTiers / (double)(AutoScaleSimTags.anHour));
            
            Log.printLine(twoTabs + "SLA Violation: "  
                    + " (hour:" + totalSlavHourAllTier + " second: " + dft.format(totalSlavSecondAllTiers));
    }
    
    public static void monitorEndUserMetrics(){
        int sizeHistory = getMonitor().getEndUserHistoryList().size();
        
        int totalReqAlltiers = 0;
        for(int i = 0; i< sizeHistory; i++){
            MonitorEndUserHistory endUserHistory = getMonitor().getEndUserHistoryList().get(i);
            totalReqAlltiers += endUserHistory.getRequestsPerAllTier();
        }

        Log.printLine( "Total User reguests: " + totalReqAlltiers);
        double totalCloudletReceived = (lastCloudletReceivedId + 1);
        if(totalReqAlltiers != totalCloudletReceived){
            error += "reporter - total received request and logged are not match";
            errorChecker = true;
            Log.printLine("reporter - total received request and logged are not match");
        }
    }
    
    public static void analyzerMetrics(){
            double cpuUtilizationPerTier = 0;
            double delayTimePerTier = 0;
            
            for(int i = 0; i < getAnalyzer().sizeHistory(); i++){
                AnalyzerHistory analyzerhistory = getAnalyzer().getHistoryList().get(i);
                
                cpuUtilizationPerTier += analyzerhistory.getCpuUtilization();
                delayTimePerTier += analyzerhistory.getDelayTime();
            }
            
            // avg
            cpuUtilizationPerTier = cpuUtilizationPerTier / (double)getAnalyzer().sizeHistory();
            delayTimePerTier = delayTimePerTier / (double)getAnalyzer().sizeHistory();
            
            Log.printLine(oneTab + "Analyzed Cpu Util. (Avg.): " + dft.format(cpuUtilizationPerTier) + "%  " 
                    + "and Analyzed Delay time (Avg.): " + dft.format(delayTimePerTier) + "s " );
    }
    
    public static void plannerMetrics(){
            int scaleUpDec = 0; 
            int scaleDownDec = 0;
            int contradictoryDecisions = 0;
            
            for(int i = 0; i < getPlanner().sizeHistory(); i++){
                PlannerHistory plannerHistory = getPlanner().getHistoryList().get(i);
                
                if(plannerHistory.getDecision() == AutoScaleSimTags.PLANNER_SCALING_UP){

                    scaleUpDec ++;

                    if(i > 0){
                        PlannerHistory plannerHistoryPast = getPlanner().getHistoryList().get(i-1);
                        if(plannerHistoryPast.getDecision() == AutoScaleSimTags.PLANNER_SCALING_DOWN)
                            contradictoryDecisions++;
                    }

                }else if (plannerHistory.getDecision() == AutoScaleSimTags.PLANNER_SCALING_DOWN){
                    scaleDownDec ++;

                    if(i > 0){
                        PlannerHistory plannerHistoryPast = getPlanner().getHistoryList().get(i-1);
                        if(plannerHistoryPast.getDecision() == AutoScaleSimTags.PLANNER_SCALING_UP)
                            contradictoryDecisions++;
                    }
                }
            }
            
            //Calculate Time to Adaptation
            double timeToAdaptation = 0;
            // for every decision of planner
            for (int i = 0; i < getPlanner().sizeHistory(); i++){
                int decision = getPlanner().getHistoryList().get(i).getDecision();
                // if the decision is scale up, calculate time to adaptation after the decision
                if(decision == AutoScaleSimTags.PLANNER_SCALING_UP){
                    // find corresponding index of delay time history in monitor
                    int indexOfMonitorItem = (i + 1) * ApplicationProvider.scalingInterval;
                    // calculate from current delay time
                    for(int j = indexOfMonitorItem; j < getMonitor().getSLAHistoryList().size(); j++){
                        double delayTime = getMonitor().getSLAHistoryList().get(j).getAvgDelayTimePerAllTiers();
                        if(delayTime > ApplicationProvider.slaContractOnDelayTime){
                            timeToAdaptation++;
                        }else{
                            timeToAdaptation++;
                            break;
                        }
                            
                    }
                }
            }

            
            // calculate the average of time to adaptation
            double avgTimeToAdaptationMin = timeToAdaptation / scaleUpDec;
            
            // convert it from minute to second
            timeToAdaptation *= 60;
            double avgTimeToAdaptationSEC = timeToAdaptation / scaleUpDec;
            
            avgTimeToAdaptationMin = Math.floor(avgTimeToAdaptationSEC / AutoScaleSimTags.aMinute);
            double avg = (avgTimeToAdaptationSEC % AutoScaleSimTags.aMinute);
            avgTimeToAdaptationMin += ( avg/ (100));
        
            Log.printLine(oneTab + "Scale Up Decisions: " + scaleUpDec);
            Log.printLine(oneTab + "Scale Down Decisions: "+scaleDownDec);
            Log.printLine(oneTab + "Contradictory Scaling Decisions: " + contradictoryDecisions);
            Log.printLine(oneTab + "Time To Adaptation : " + dft.format(avgTimeToAdaptationSEC) + " sec."
                                                            + "    " + dft.format(avgTimeToAdaptationMin) + " min.");
    }
    
    public static void executorMetrics(){
        int provisionedVm = 0; 
        int deProvisionedVm = 0;
        int contradictoryAction = 0;
        
        for(int i = 0; i < getExecutor().sizeHistory(); i++){
            ExecutorHistory executorHistory = getExecutor().getHistoryList().get(i);
            //  provisioning per tier
            provisionedVm += executorHistory.getProvisioning();
            // deprovisioning per tier
            deProvisionedVm += executorHistory.getDeProvisioning();
            // contradictory actions
            if(i> 0){
                ExecutorHistory previousExecHistory = getExecutor().getHistoryList().get(i-1);
                // if current action is scale up
                if(executorHistory.getAction() == AutoScaleSimTags.ACT_UP_NEW){
                    // if previous action is scale down
                    if(previousExecHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY
                            || previousExecHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_REQ
                            || previousExecHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_START
                            || previousExecHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_Q)
                        
                        contradictoryAction++;
                // if current action is scale down
                }else if (executorHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY
                            || executorHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_REQ
                            || executorHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_START
                            || executorHistory.getAction() == AutoScaleSimTags.ACT_D_DESTROY_FROM_Q){
                    // if previous action is scale up
                    if(previousExecHistory.getAction() == AutoScaleSimTags.ACT_UP_NEW)
                        contradictoryAction++;
                }
            }
            
        }
        Log.printLine(oneTab + "Provisioned On-Demand Vm:" + provisionedVm);
        Log.printLine(oneTab + "De-Provisioned On-Demand Vm:" + deProvisionedVm);
        Log.printLine(oneTab + "Contradictory Scaling Actions: " + contradictoryAction);
        
        
    }
        
    public static void costMetrics(){
        // Set Renting Costs of On demand Vms
        double rentingCostPerAllTier = 0;
        // Get Destroyed, initialing, running, and quarantined
        ArrayList<Vm> vmList = getOnDemandVmsList();
        for(Vm vm : vmList){
            // per tier
            rentingCostPerAllTier += vm.getBill();
        }
        
        // set Pnalty cost
        int sizeHistory = getMonitor().getSLAHistoryList().size();
            
        //SLA Violation by config
        double[] totalSLAVSecondByConfigs = new double[]{0, 0, 0, 0};

        for(int i =0; i < sizeHistory;i++){
            MonitorSLAHistory slaHistory = getMonitor().getSLAHistoryList().get(i);

            // by config
            totalSLAVSecondByConfigs[0] += slaHistory.getSLAVSecondsByVmConfigs()[AutoScaleSimTags.VM_CONFIG_T2MICRO];
            totalSLAVSecondByConfigs[1] += slaHistory.getSLAVSecondsByVmConfigs()[AutoScaleSimTags.VM_CONFIG_T2SMALL];
            totalSLAVSecondByConfigs[2] += slaHistory.getSLAVSecondsByVmConfigs()[AutoScaleSimTags.VM_CONFIG_T2MEDIUM];
            totalSLAVSecondByConfigs[3] += slaHistory.getSLAVSecondsByVmConfigs()[AutoScaleSimTags.VM_CONFIG_T2LARGE];
        }

        // SLA Violation, change to hour format
        // by config
        double[] totalSLAVHourByConfigs = new double[]{0, 0, 0, 0};
        totalSLAVHourByConfigs[0] = Math.ceil(totalSLAVSecondByConfigs[0] / (double)(AutoScaleSimTags.anHour));
        totalSLAVHourByConfigs[1] = Math.ceil(totalSLAVSecondByConfigs[1] / (double)(AutoScaleSimTags.anHour));
        totalSLAVHourByConfigs[2] = Math.ceil(totalSLAVSecondByConfigs[2] / (double)(AutoScaleSimTags.anHour));
        totalSLAVHourByConfigs[3] = Math.ceil(totalSLAVSecondByConfigs[3] / (double)(AutoScaleSimTags.anHour));

        

        // Set Penalty Costs by config
        double[] totalSLAPByConfigs = new double[]{0, 0, 0, 0};
           
        totalSLAPByConfigs[0] = (totalSLAVHourByConfigs[0] * AutoScaleSimTags.VM_PRICE_ONDEMAND[AutoScaleSimTags.VM_CONFIG_T2MICRO]);
        totalSLAPByConfigs[1] = (totalSLAVHourByConfigs[1] * AutoScaleSimTags.VM_PRICE_ONDEMAND[AutoScaleSimTags.VM_CONFIG_T2SMALL]);
        totalSLAPByConfigs[2] = (totalSLAVHourByConfigs[2] * AutoScaleSimTags.VM_PRICE_ONDEMAND[AutoScaleSimTags.VM_CONFIG_T2MEDIUM]);
        totalSLAPByConfigs[3] = (totalSLAVHourByConfigs[3] * AutoScaleSimTags.VM_PRICE_ONDEMAND[AutoScaleSimTags.VM_CONFIG_T2LARGE]);
        // sla penalty cost
        double totalPenaltyCost = 0;
        totalPenaltyCost = totalSLAPByConfigs[0] 
                            + totalSLAPByConfigs[1] 
                            + totalSLAPByConfigs[2] 
                            + totalSLAPByConfigs[3];

        Log.printLine(oneTab + "Renting Cost: $ "
                + dft.format(rentingCostPerAllTier));
        Log.printLine(oneTab + "SLA Penalty: $ " 
                    + dft.format(totalPenaltyCost));

        Log.printLine(oneTab + "Total Cost : $ "
                + (char)27 + "[31m......   " 
                                            + dft.format(rentingCostPerAllTier + totalPenaltyCost) + "    .......");
    }
    
    private static void setRemainedVmsBill(){
            for(Vm vm :getOnDemandVmsList(new int[]{Vm.Requested, Vm.Started, Vm.Quarantined})){
                /* set destroy time for remined vms */
                vm.setDestroyTime(CloudSim.clock());

                /* set bill for remined vms */
                    // destroyed vm has been calculated before
                    // A requested (initialing) vm has 1 hour bill, in default
                    // The bill for started (running) and q (quarantined) VMs should be calculated now
                double availableHoursRoundedUp = 
                                Math.ceil((vm.getDestroyTime() - vm.getRequestTime()) / (double)AutoScaleSimTags.anHour);
                double bill =  availableHoursRoundedUp * vm.getPrice();
                if(bill != 0) // If this vm has not been started right now
                    vm.setBill(bill);

                /* set life time for remined vms */ 
                double availableSecond = vm.getDestroyTime() - vm.getRequestTime();
                //If the VM was requested and destroyed at the latest time of simulation
                if (availableSecond == 0 && vm.getRequestTime()>0 && vm.getDestroyTime()>0)
                    availableSecond=1;
                
                vm.setLifeTime(availableSecond);
            }
            
            for(Vm vm : getOnDemandVmsList()){
                if(vm.getLifeTime() == 0){
                errorChecker = true;
                error+="error-setRemainedVmsBill";
                Log.printLine("error-setRemainedVmsBill");
                }
            }
        }
}
