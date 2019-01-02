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

import autoscalesim.ExperimentalSetup;
import autoscalesim.applicationprovider.ApplicationProvider;
import static autoscalesim.applicationprovider.ApplicationProvider.lastCloudletReceivedId;
import autoscalesim.cloudprovider.Vm;
import static autoscalesim.log.AutoScaleSimTags.dft;
import static autoscalesim.log.AutoScaleSimTags.oneTab;
import static org.cloudbus.cloudsim.lists.VmList.getOnDemandVmsList;
import static autoscalesim.log.AutoScaleSimTags.threeTabs;
import static autoscalesim.log.AutoScaleSimTags.twoTabs;
import autoscalesim.enduser.Cloudlet;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import org.cloudbus.cloudsim.Log;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import java.awt.Graphics;
import java.util.ArrayList;
import org.cloudbus.cloudsim.core.CloudSim;
import static org.cloudbus.cloudsim.lists.VmList.getVmsList;
import static autoscalesim.applicationprovider.ApplicationProvider.getMonitor;
import static autoscalesim.applicationprovider.ApplicationProvider.getAnalyzer;
import static autoscalesim.applicationprovider.ApplicationProvider.getPlanner;
import static autoscalesim.applicationprovider.ApplicationProvider.getExecutor;

/**
 * LogAutoScale class prints the auto-scaler actions every time interval that the auto-scaler is called.
 * The results are printed in the console during the simulation.
 */
public class LogAutoscaler {
    public static String blueWriting = (char)27 + "[34;1m";
    public static String blackWriting = (char)27 + "[0m" ;
    
    /**
         * 
         */
        public static void logCurrentEpoch(){
        /* Monitor */
         logMonitor();
            
        /* Analyzer */
        logAnalyzer();
            
        /* Planner */
        logPlanner();
            
        /* Executor */
        logExecutor();
        }
        
    
    
    
        private static void logMonitor(){
            // Vm Monitor
            MonitorVmHistory vmMonitor = getMonitor().latestVmHistoryRec();
            String vmMonitorStr = "VM: "
                        + "util. " + AutoScaleSimTags.dft.format(vmMonitor.getCpuUtilizationByAllTier())
                        + "% load " + AutoScaleSimTags.dft.format(vmMonitor.getCpuLoadByAllTier())
                        + "% vms " + AutoScaleSimTags.dft.format(vmMonitor.getVms()) 
                        + " (Pending " + AutoScaleSimTags.dft.format(vmMonitor.getInitialingVms())
                        + " Inservice " + AutoScaleSimTags.dft.format(vmMonitor.getRunningVms())
                        + " Quarantined " + AutoScaleSimTags.dft.format(vmMonitor.getQuarantinedVms())
                        + ") runningcloudlet " + AutoScaleSimTags.dft.format(vmMonitor.getRunningCloudlet())
                        + "  Throughput " + dft.format(vmMonitor.getThroughputFinishedCloudletsAllTiers());

            // SLA Monitor
            MonitorSLAHistory slaMonitor = getMonitor().latestSLAHistoryRec();
            String slaMonitorStr = threeTabs + "  SLA: "
                        + " RT " + AutoScaleSimTags.dft.format(slaMonitor.getAvgResponseTimePerAllTiers())
                        + " DT " + AutoScaleSimTags.dft.format(slaMonitor.getAvgDelayTimePerAllTiers()) 
                        + " SLAV(#) " + slaMonitor.getSlavNumberByAllTier()
                        + " SLAV(%) " + AutoScaleSimTags.dft.format(slaMonitor.getSlavPercent())
                        + " SLAV (seconds) " + AutoScaleSimTags.dft.format(slaMonitor.getSlavSecondByAlltier()) 
                        + " Canceled cloudlets " + slaMonitor.getCloudletsCancelled()
                        + " Failed cloudlets " + slaMonitor.getCloudletFailedCounter()
                        + " Finished cloudlets " + slaMonitor.getCloudletFinished();
            // End user Monitor
            MonitorEndUserHistory endUserMonitor = getMonitor().latestEndUserHistoryRec();
            String endUserMonitorStr = threeTabs + "  End User: "
                        + " received requests " + endUserMonitor.getRequestsPerAllTier();
            
            Log.printLine( (blueWriting + "MONITOR --- ") + (blackWriting 
                        + vmMonitorStr));
            Log.printLine(slaMonitorStr);
            Log.printLine(endUserMonitorStr);
        }
        
        private static void logAnalyzer(){
            AnalyzerHistory analyzerHistory = getAnalyzer().latestHistoryRec();
            // utilization
            String analyzerStr = "Util. " + dft.format(analyzerHistory.getCpuUtilization())
            // Delay time
                                    + "% Delay Time " + dft.format(analyzerHistory.getDelayTime());
           
            Log.printLine(blueWriting + "ANALYZE --- " + blackWriting + analyzerStr);
        }
        
        private static void logPlanner(){
            PlannerHistory plannerHistory = getPlanner().latestHistoryRec();
            int decision = plannerHistory.getDecision();
            String decisionStr = AutoScaleSimTags.getStringValueOfPlannerDecision(decision);
            Log.printLine( (blueWriting + "PLAN ---") + (blackWriting + "dec "+ decisionStr)  );
        }
        
        private static void logExecutor(){
            ExecutorHistory executorMonitor = getExecutor().latestHistoryRec();
            int action = executorMonitor.getAction();
            String actionStr = AutoScaleSimTags.getStringValueOfExecutorAction(action);
            
            Log.printLine( (blueWriting + "EXECUTE ---") + (blackWriting + "act " + actionStr 
                            + " -------- provisioned: " + executorMonitor.getProvisioning()
                            + " deProvisioned: " + executorMonitor.getDeProvisioning()));
            // executor action details
            Log.printLine(getExecutor().getActionDetails());
            
            if(ApplicationProvider.getExecutor().executorType == ExperimentalSetup.ExecutorType.SUPREX){
                Log.printLine( oneTab + getExecutor().getQuarantinedVMsUpdaterDetails());
                getExecutor().setQuarantinedVMsUpdaterDetails("Updater details: ");
            }
            
        }
    
 
       
    
}
