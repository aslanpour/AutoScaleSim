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

import autoscalesim.ExperimentalSetup;
import autoscalesim.applicationprovider.ApplicationProvider;
import static autoscalesim.applicationprovider.ApplicationProvider.sumCloudletFailured;
import static autoscalesim.applicationprovider.ApplicationProvider.sumCloudletReturned;
import static autoscalesim.applicationprovider.ApplicationProvider.getCloudletFailedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsQuarantinedList;
import static autoscalesim.applicationprovider.ApplicationProvider.sumRequestsPerAllTier;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import autoscalesim.cloudprovider.Vm;
import autoscalesim.log.DateTime;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import autoscalesim.enduser.Cloudlet;
import java.util.ArrayList;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import static org.cloudbus.cloudsim.lists.VmList.getVmListFilterByTier;
import static org.cloudbus.cloudsim.lists.VmList.getVmsList;

/**
 * Monitor class continuously collects all effective parameters from the whole system, ranging from resources to 
 * SLA and end users' behavior.
 */
public class Monitor {
    
    // Monitor Knowledge base containers
    private ArrayList<MonitorVmHistory> vmHistoryList;
    private ArrayList<MonitorSLAHistory> slaHistoryList;
    private ArrayList<MonitorEndUserHistory> endUserHistoryList;
    
    /**
     * 
     */
    public Monitor(){
        
        setVmHistoryList(new ArrayList<MonitorVmHistory>() );
        setSLAHistoryList(new ArrayList<MonitorSLAHistory>());
        setEndUserHistoryList(new ArrayList<MonitorEndUserHistory>());
    }
    
    /**
     * Monitors the parameters collected by sensors
     */
    public void doMonitoring(){
        /* Low Level Parameters */
        vmStatus();
        /* High Level Parameters */
        slaStatus();
        endUserStatus();
        //Environment parameters will be calculated by default
        
        reSetMonitorSensors();
    }
        
    /**
     * Collects the monitored parameters related to VMs
     */
    private void vmStatus(){
        // Initialing parameters
        double cpuUtilizationByAllTier = 0;
        double cpuLoadByAllTier = 0;
        int vms = 0;
        int initialingVms = 0;
        int runningVms = 0;
        int quarantinedVms = 0;
        int runningCloudlet = 0;
        int[] vmsConfig = new int[]{0, 0, 0, 0};
        int[] vmsPurchase = new int[]{0, 0, 0};
        double throughputFinishedCloudletsAllTiers = 0;
        
        /* Set CPU Utilization and Load Utilization*/
            ArrayList<Vm> vmList = new ArrayList<>();
            //Prepare the VM list
            if(ApplicationProvider.getExecutor().executorType == ExperimentalSetup.ExecutorType.SIMPLE){ 
                // we should consider running vms and initialing vms
                vmList = getVmsList( new int[]{Vm.Requested, Vm.Started});
            }else if (ApplicationProvider.getExecutor().executorType == ExperimentalSetup.ExecutorType.SUPREX){ 
                // We should consider running, initialing and quarantined vms
                vmList = getVmsList( new int[]{Vm.Requested, Vm.Started, Vm.Quarantined});
            }
            
            // Calculate Utilization
            for(Vm vm : vmList){
                double usedPEsVM = vm.getTotalUtilizationOfCpu(CloudSim.clock());
                double totalPeVM = vm.getNumberOfPes();
                            
                //vm load
                double vmLoad = (usedPEsVM / totalPeVM) * 100;
                
                // Cpu Util.
                cpuLoadByAllTier += vmLoad;
                if(vmLoad <= 100) cpuUtilizationByAllTier += vmLoad;else cpuUtilizationByAllTier += 100;
                
            }

            // Average Load and CPU utilization
            cpuLoadByAllTier = cpuLoadByAllTier / vmList.size();
            cpuUtilizationByAllTier = cpuUtilizationByAllTier / vmList.size();

        /* other parameters */
        for(Vm vm: getVmsList(new int[]{Vm.Requested, Vm.Started, Vm.Quarantined})){

            /* VMs Count */
            vms ++;

            /* VMs Status */
            switch(vm.getStatus()){
                case Vm.Requested:
                    initialingVms++; break;
                case Vm.Started:
                    runningVms++; break;
                case Vm.Quarantined:
                    quarantinedVms++; break;
            }
            /* Running Cloudlets */
            runningCloudlet+= vm.getCloudletScheduler().runningCloudlets();

            /* Configs */
            vmsConfig[vm.getConfigurationType()]++;

            /* Purchase */
            vmsPurchase[vm.getPurchaseType()]++;
        }
        
        if((double)ApplicationProvider.sumRequestsPerAllTier != 0)
            throughputFinishedCloudletsAllTiers = (sumCloudletReturned + sumCloudletFailured)
                                                / (double)ApplicationProvider.sumRequestsPerAllTier * 100;
            
        MonitorVmHistory monitorVmHistory = new MonitorVmHistory(
                                            cpuUtilizationByAllTier
                                            , cpuLoadByAllTier
                                            , vms
                                            , initialingVms
                                            , runningVms
                                            , quarantinedVms
                                            , runningCloudlet
                                            , vmsConfig
                                            , vmsPurchase
                                            , throughputFinishedCloudletsAllTiers);
        
        getVmHistoryList().add(monitorVmHistory);
    }

    /**
     * Monitors parameters related to SLA
     */
    private void slaStatus(){
        /* initialing parameters */
        double avgResponseTimePerAllTiers = 0;
        double avgDelayTimePerAllTiers = 0;
        
        double slavNumberByAllTier;
        int[] slavNumbersByVmConfigs;
        int[] slavNumbersByVmPurchases;
        double slavPercent = 0;
        double slavSecondByAllTier;
        double[] slavSecondsByVmConfigs;
        double[] slavSecondsByVmPurchases;
        int cloudletsCancelled; 
        int cloudletFailedCounter = 0;
        int cloudletFinished = 0;

        /* Average Response and Delay Time */ 
        
        if(sumCloudletReturned + sumCloudletFailured > 0){
            avgResponseTimePerAllTiers = (ApplicationProvider.sumResponseTime
                                        / (sumCloudletReturned + sumCloudletFailured));
            
            avgDelayTimePerAllTiers = (ApplicationProvider.sumDelayTime
                                        / (sumCloudletReturned + sumCloudletFailured));
        }

        // SLA Violation
        slavNumberByAllTier = ApplicationProvider.sumSLAVNumbersByTier;
        slavNumbersByVmConfigs = ApplicationProvider.sumSLAVNumbersByVmConfigs;
        slavNumbersByVmPurchases = ApplicationProvider.sumSLAVNumbersByVmPurchases;
        
        if (sumCloudletReturned + sumCloudletFailured > 0)
            slavPercent = slavNumberByAllTier / (sumCloudletReturned + sumCloudletFailured);
        
        slavSecondByAllTier = ApplicationProvider.sumSLAVSecondsByTier;
        slavSecondsByVmConfigs = ApplicationProvider.sumSLAVSecondsByVmConfigs;
        slavSecondsByVmPurchases = ApplicationProvider.sumSLAVSecondsByVmPurchases;
        
        /* Cloudlet cancelled */
        cloudletsCancelled = ApplicationProvider.sumCloudletsCancelled;
        // Cloudlet Failure
        cloudletFailedCounter = sumCloudletFailured;
            
        cloudletFinished = ApplicationProvider.sumCloudletReturned;
            
        MonitorSLAHistory monitorSLAHistory = new MonitorSLAHistory(
                                         avgResponseTimePerAllTiers
                                        , avgDelayTimePerAllTiers
                                        , slavNumberByAllTier
                                        , slavNumbersByVmConfigs
                                        , slavNumbersByVmPurchases
                                        , slavPercent
                                        , slavSecondByAllTier
                                        , slavSecondsByVmConfigs
                                        , slavSecondsByVmPurchases
                                        , cloudletsCancelled
                                        , cloudletFailedCounter
                                        , cloudletFinished
                                        );
            getSLAHistoryList().add(monitorSLAHistory);
    }
        
        /**
         * Monitors parameters related to End-users (or workload)
         */
        private void endUserStatus(){
            
            int requestsPerAllTier = ApplicationProvider.sumRequestsPerAllTier;
            long requestsLengthPerTier = ApplicationProvider.sumRequestsLengthPerTier;
            
            MonitorEndUserHistory monitorEndUserHistory = new MonitorEndUserHistory(requestsPerAllTier, requestsLengthPerTier);
            
            getEndUserHistoryList().add(monitorEndUserHistory);
        }

        
        /**
         * Resets the sensors
         */
        private void reSetMonitorSensors(){
         // Reset SLA sensors
            ApplicationProvider.sumResponseTime = 0;
            ApplicationProvider.sumDelayTime = 0;
            ApplicationProvider.sumSLAVNumbersByTier = 0;
            ApplicationProvider.sumSLAVNumbersByVmConfigs = new int[]{0, 0, 0, 0};
            ApplicationProvider.sumSLAVNumbersByVmPurchases = new int[]{0, 0, 0};
            ApplicationProvider.sumSLAVSecondsByTier = 0;
            ApplicationProvider.sumSLAVSecondsByVmConfigs = new double[]{0, 0, 0, 0};
            ApplicationProvider.sumSLAVSecondsByVmPurchases = new double[]{0, 0, 0};
            ApplicationProvider.sumCloudletsCancelled = 0;
            ApplicationProvider.sumCloudletReturned = 0;   
            ApplicationProvider.sumCloudletFailured = 0;
            // Reset End-user sensors
            ApplicationProvider.sumRequestsPerAllTier = 0;
            ApplicationProvider.sumRequestsLengthPerTier = 0;
            
        }
        
        /**
         * Gets the History of Monitoring phase for VMs
         * @return 
         */
        public ArrayList<MonitorVmHistory> getVmHistoryList(){
            return vmHistoryList;
        }
        
        /**
         * Sets the History of Monitoring phase for VMs
         * @param vmHistoryList 
         */
        private void setVmHistoryList(ArrayList<MonitorVmHistory> vmHistoryList){
            this.vmHistoryList = vmHistoryList;
        }
        
        /**
         * Gets the History of Monitoring phase for SLA
         * @return 
         */
        public ArrayList<MonitorSLAHistory> getSLAHistoryList(){
            return slaHistoryList;
        }
        
        /**
         * Sets the History of Monitoring phase for SLA
         * @param slaHistoryList 
         */
        private void setSLAHistoryList(ArrayList<MonitorSLAHistory> slaHistoryList){
            this.slaHistoryList = slaHistoryList;
        }
        
        /**
         * Gets the History of Monitoring phase for End-users
         * @return 
         */
        public ArrayList<MonitorEndUserHistory> getEndUserHistoryList(){
            return endUserHistoryList;
        }
        
        /**
         * Sets the History of Monitoring phase for End-users
         * @param endUserHistoryList 
         */
        private void setEndUserHistoryList(ArrayList<MonitorEndUserHistory> endUserHistoryList){
            this.endUserHistoryList = endUserHistoryList;
        }
        
        /**
         * Returns the size of History for Monitoring phase, VM part
         * @return 
         */
        public int sizeVmHistory(){
            return getVmHistoryList().size();
        }
        
        /**
         * Returns the size of History for Monitoring phase, SLA part
         * @return 
         */
        public int sizeSLAHistory(){
            return getSLAHistoryList().size();
        }
        
        /**
         * Returns the size of History for Monitoring phase, End-user part
         * @return 
         */
        public int sizeEnduserHistory(){
            return getEndUserHistoryList().size();
        }
        
        /**
         * Returns the latest record of Monitoring history, VM part
         * @return 
         */
        public MonitorVmHistory latestVmHistoryRec(){
            return getVmHistoryList().get(sizeVmHistory()- 1);
        }
        
        /**
         * Returns the latest record of Monitoring history, SLA part
         * @return 
         */
        public MonitorSLAHistory latestSLAHistoryRec(){
            return getSLAHistoryList().get(sizeSLAHistory()- 1);
        }
        
        /**
         * Returns the latest record of Monitoring history, End-user part
         * @return 
         */
        public MonitorEndUserHistory latestEndUserHistoryRec(){
            return getEndUserHistoryList().get(sizeEnduserHistory()- 1);
        }
}
