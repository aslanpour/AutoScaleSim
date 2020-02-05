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

package autoscalesim;

import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.applicationprovider.ApplicationProvider;
import autoscalesim.applicationprovider.autoscaling.Analyzer;
import autoscalesim.applicationprovider.loadmanager.LoadAdmission;
import autoscalesim.cloudprovider.DatacenterCharacteristics;
import autoscalesim.cloudprovider.CloudProvider;
import autoscalesim.enduser.EndUserEmulator;
import autoscalesim.log.DateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import autoscalesim.cloudprovider.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import autoscalesim.cloudprovider.VmAllocationPolicySimple;
import autoscalesim.cloudprovider.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet; 
import autoscalesim.applicationprovider.loadmanager.LoadBalancingRoundRobin;
import autoscalesim.applicationprovider.loadmanager.LoadBalancing;
import autoscalesim.applicationprovider.autoscaling.Monitor;
import autoscalesim.applicationprovider.autoscaling.Planner;
import autoscalesim.applicationprovider.autoscaling.PlannerRuleBased;
import autoscalesim.applicationprovider.autoscaling.Executor;
import autoscalesim.applicationprovider.autoscaling.ExecutorSimple;
import autoscalesim.applicationprovider.autoscaling.ExecutorSuperProfessional;
import autoscalesim.log.AutoScaleSimTags.DATASET;

/**
 * Experimental Setup class prepares all the three main entities of the simulation as 
 * End-User, Application Provider, and Cloud Provider. 
 * The parameters related to each entity should be set before running the simulator.
 */
public class ExperimentalSetup {

    /**
     * Assigns specific value to each parameter related to entities.
     * @param args 
     */
    public static void main(String[] args) {
        Log.printLine("Starting AutoScaleSim Simulation...");

        try {
        // Default Setup for CloudSim
            cloudSimSetup();
             

/* (A) End-User Setting*/
            /* day(s) of simulation scenario (starts from 6th of July*/
            //for NASA----> [1-28] * 1440 min
            // for Wikipedia----> [1-4] * 60 min
            final int SIMULATION_LIMIT = 7*1440;  //NASA=7*1440, Wikipdeia=4*60
            //Dataset type
            DATASET datasetType = DATASET.NASA;
            
            /*The length (million instruction) of cloudlets */
            //Note: the ratio of cloudlet length than VM mips should not be 
            // more than cloudletTimeout times
            final int CLOUDLET_LENGTH = 5000;
            /* processing elemnts require for a cloudlet */
            final int PES_NUMBER = 2; //NASA=2, Wikipedia=1
            
            /* The time each cloudlet can be survived after being received by Application Provider*/
            int cloudletTimeout= 30; //NASA=30, Wikipedia=50
            // this is applicable only for Wikipedia workload
            int minCloudletLength = 0;
            int maxCloudletLength = 245000;//
            
//****************************************************************************************************************
/* (B) Application Provider setting*/
        
    /* Load Manager setup*/
            LoadAdmission loadAdmission = new LoadAdmission(); 
            LoadBalancing loadBalancing = new LoadBalancingRoundRobin();
            int cloudletSchedulerName = AutoScaleSimTags.TimeShared; 
            
    /* Auto-Scaling setup */
            /* The number of initial VMs to host and run the application */
           int initialVMs = 2; //NASA=2, Wikipedia=1

           /* The frequency of auto-scaling resources, in minute */
            int scalingInterval = 10; // NASA=10, Wikipedia = 2 

            /* Acceptable delay time for the execution of a cloudlet. 
            If a cloudlet delay time went beyond this value, an SLA violation has happened */
            double slaContractOnDelayTime = 1.0;
        /* Monitor */
            // Every minute, it monitors and Saves obtained information:
            Monitor monitor = new Monitor();
            
        /* Analyzer */
            // The method by which each parameter must be analyzed is set here:
            String[] analysisMethod = new String[]{
                Method.SIMPLE.name(),//[0] method for analyzing 'CPU utilization' parameter
                Method.SIMPLE.name(),//[1] method for analyzing 'Vm count' parameter
                Method.SIMPLE.name(),//[2] method for analyzing 'Throughput' parameter
                Method.SIMPLE.name(),//[3] method for analyzing 'Response Time' parameter
                Method.SIMPLE.name(),//[4] method for analyzing 'Delay time' parameter
                Method.SIMPLE.name(),//[5] method for analyzing 'SLA violation Count' parameter
                Method.SIMPLE.name(),//[6] method for analyzing 'SLA violation percent parameter'
                Method.SIMPLE.name(),//[7] method for analyzing 'SLA violation time' parameter
                Method.SIMPLE.name(),//[8] method for analyzing 'Failed Cloudlets' parameter
                Method.SIMPLE.name()};//[9] method for analyzing 'Future Workload' parameter
                    
            /*Alpha in single exponential smoothing, each index is for one analyzing parameter,
            e.g., [0] is for CPU util. */
            double sESAlpha[] = {0.2, 0, 0, 0, 0.1, 0, 0, 0, 0, 0}; //NASA=0.2, Wikipedia=0.1
           
            //How many monitored items of a parameter should be used to analyze by complex methods?
            int timeWindow = scalingInterval; //Wikipedia =5, NASA= scalingInterval 
            
            Analyzer analyzer = new Analyzer(analysisMethod, timeWindow, sESAlpha);
            
        /* Planner (resource estimation, capacity planning, or decision making) */
            /* As a Rule-Based planner containing some rules is implemented, */
            final ScalingRule rule = ScalingRule.SLA_AWARE;
            final int configurationType = AutoScaleSimTags.VM_CONFIG_T2MEDIUM; // NASA=medium, WIKIPEDIA=small
            /*Thresholds for some parameters */
            double cpuScaleUpThreshold = 70; // percentage      
            double cpuScaleDownThreshold = 40; // percentage     
            double delayTimeScaleUpThreshold = 1; // second //NASA=1, Wikipedia=0.140
            double delayTimeScaleDownThreshold = 0.2;  // second // NASA=0.2, Wikipedia=0.130              
            
            Planner planner = new PlannerRuleBased(
                                            rule,
                                            configurationType,
                                            cpuScaleUpThreshold,
                                            cpuScaleDownThreshold,
                                            delayTimeScaleUpThreshold,
                                            delayTimeScaleDownThreshold);
        /* Executor */
            final ExecutorType executorType = ExecutorType.SIMPLE;
                        
            final SurplusVMSelectionPolicy surplusVMSelectionPolicy = SurplusVMSelectionPolicy.THE_OLDEST; 
            
            /* Cool-down time (in minute) to prevent executor from contradictory actions. */
            final int COOLDOWN = 0 * AutoScaleSimTags.aMinute;
            
            /* Max. On-Demand VM which Executor is allowed to provision. */
            final int maxOnDemandVm = 40; //NASA=40, Wikipedia=10
            final int minOnDemandVm = 1;
            
          
            
            
//*****************************************************************************************************
/* (C) Cloud Provider */
        /* Create Data centers */
            createCloudProvider(cloudletTimeout);
            
        /* VM instantiation (start-up) delay */
        // "Static"
        // "Dynamic" which is related to OS, Time of the day, Server Location, the number of requested instances, etc.
            String startUpDelayType = "Static"; 
            
            /* basic value for instantiaion delay time of VMs should be at least 1 minute */
            final double BASE_DELAY_IN_VM_START_UP = 5 * AutoScaleSimTags.aMinute; //NASA=5, Wikipedia=1
            
            
            Executor executor = new ExecutorSimple(
                                            executorType,
                                            surplusVMSelectionPolicy,
                                            COOLDOWN,
                                            maxOnDemandVm,
                                            minOnDemandVm,
                                            startUpDelayType,
                                            BASE_DELAY_IN_VM_START_UP);
                
            if (executorType == ExecutorType.SUPREX){
                executor = new ExecutorSuperProfessional(
                                                executorType,
                                                surplusVMSelectionPolicy,
                                                COOLDOWN,
                                                maxOnDemandVm,
                                                minOnDemandVm,
                                                startUpDelayType,
                                                BASE_DELAY_IN_VM_START_UP);
            }
//********************************************************************************************************
            /* It writes reports collected by Aknowledge-based component to an excel file.
            We should set which reports should be written 
            To see the reports, there is an excel file with the name of SimulationResult.xls 
            in "others" package.*/
            
            //"M_VM" "M_SLA" "M_User"  which are the report of monitore VM, SLA, User requests  statuses
            // "ANALYZER"   which is the report of Analyzer results
            // "PLANNER"    which is the report of Planner results
            // "EXECUTOR"   which is the result of Executor results
            final String[] reports = new String[]{"M_VM", "M_User", "","", "", ""}; 
            

//****************************************** Experimental Setup Finished *******************************
//******************************************************************************************************  

            
            EndUserEmulator endUserEmulator = createEndUserEmulator(
                                        "EndUserEmulator",
                                        SIMULATION_LIMIT,
                                        datasetType,
                                        CLOUDLET_LENGTH,
                                        PES_NUMBER,
                                        minCloudletLength,
                                        maxCloudletLength
                                        );
            
            ApplicationProvider AP = createApplicationProvider("ApplicationProvider",
                                                        initialVMs,
                                                        datasetType,
                                                        loadAdmission,
                                                        loadBalancing,
                                                        cloudletSchedulerName,
                                                        scalingInterval,
                                                        slaContractOnDelayTime,
                                                        monitor,
                                                        analyzer,
                                                        planner,
                                                        executor,
                                                        reports);

            

            //Starts the simulation
            CloudSim.startSimulation();

            CloudSim.stopSimulation();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated at " + DateTime.timeStamp() + "due to an unexpected error");
        }
    }
    
    /**
     * Sets basic parameters for simulation
     */
    private static void cloudSimSetup(){
        // First step: Initialize the CloudSim package. It should be called before creating any entities.
            int num_ASP = 1;   // The number of Application Service Providers
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_ASP, calendar, trace_flag);
    }
    
    /**
     * Creates an entity called EndUser to emulate web's requests
     * @param name
     * @param SIMULATION_LIMIT
     * @param dataSetAndDelayList
     * @param CLOUDLET_LENGTH
     * @param PES_NUMBER
     * @return
     * @throws Exception 
     */
    private static EndUserEmulator createEndUserEmulator(String name,
                            final int SIMULATION_LIMIT,
                            final DATASET dataset,
                            final int CLOUDLET_LENGTH,
                            final int PES_NUMBER,
                            final int minCloudletLength,
                            final int maxCloudletLength) throws Exception {
        
        EndUserEmulator endUserEmulator = null;
        
        try {
            endUserEmulator = new EndUserEmulator("EndUserEmulator",
                                        SIMULATION_LIMIT,
                                        dataset,
                                        CLOUDLET_LENGTH,
                                        PES_NUMBER,
                                        minCloudletLength,
                                        maxCloudletLength);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return endUserEmulator;
    }
    
    /**
     * Creates an Application Provider entity
     * @param name
     * @param initialVMs
     * @param loadAdmission
     * @param loadBalancing
     * @param cloudletSchedulerName
     * @param scalingInterval
     * @param slaContractOnDelayTime
     * @param monitor
     * @param analyzer
     * @param planner
     * @param executor
     * @param reports
     * @return 
     */ 
    private static ApplicationProvider createApplicationProvider(String name,
                                                        int initialVMs,
                                                        DATASET datasetType,
                                                        LoadAdmission loadAdmission,
                                                        LoadBalancing loadBalancing,
                                                        int cloudletSchedulerName,
                                                        int scalingInterval,
                                                        double slaContractOnDelayTime,
                                                        Monitor monitor,
                                                        Analyzer analyzer,
                                                        Planner planner,
                                                        Executor executor,
                                                        final String[] reports){

            ApplicationProvider ASP = null;
            try {
                ASP = new ApplicationProvider("ApplicationProvider",
                                                    initialVMs,
                                                    datasetType,
                                                    loadAdmission,
                                                    loadBalancing,
                                                    cloudletSchedulerName,
                                                    scalingInterval,
                                                    slaContractOnDelayTime,
                                                    monitor,
                                                    analyzer,
                                                    planner,
                                                    executor,
                                                    reports);
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
            return ASP;
    }
     
    /**
     * Creates a Cloud Provider entity
     * @param cloudletTimeout 
     */
    private static void createCloudProvider(int cloudletTimeout){
        
        // Second step: Create Datacenters
        
            //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            // each Dc can allocate 10 vms(with 2 pes)
            // 5 Dcs can allocate 50 vms (with 2 pes)
            int hostCount = 10; // Hosts

            int[] pesList = new int[hostCount]; // Pes List of each Host
            int[] ram = new int[hostCount]; // Ram capacity of each Host
            int[] bw = new int[hostCount]; // BW capacity of each Host
            long[] storage = new long[hostCount] ; // storage capacity of each Host
            for(int i = 0; i < hostCount; i++){
                pesList[i] = 2;
                ram[i] = 100000;
                bw[i] = 2000000;
                storage[i] = 10000000;
            }
            
            int mips = 25000;
            
            //each data center can allocate 10 vms
            CloudProvider datacenter0 = createDatacenter("Datacenter_0", hostCount, pesList, mips,
                                                        ram,bw, storage, cloudletTimeout);
            CloudProvider datacenter1 = createDatacenter("Datacenter_1", hostCount, pesList, mips,
                                                        ram,bw, storage, cloudletTimeout);
            CloudProvider datacenter2 = createDatacenter("Datacenter_2", hostCount, pesList, mips,
                                                        ram,bw, storage, cloudletTimeout);
            CloudProvider datacenter3 = createDatacenter("Datacenter_3", hostCount, pesList, mips,
                                                        ram,bw, storage, cloudletTimeout);
            CloudProvider datacenter4 = createDatacenter("Datacenter_4", hostCount, pesList, mips,
                                                        ram,bw, storage, cloudletTimeout);
    }
    
    /**
     * Creates a Data Center class
     * @param name
     * @param hostcount
     * @param pescount
     * @param mipslenght
     * @param ram
     * @param bw
     * @param storage
     * @param cloudletTimeout
     * @return 
     */
    private static CloudProvider createDatacenter(String name, int hostcount, int[] pescount , 
                                            int mipslenght, int[] ram, int[] bw, long[] storage, int cloudletTimeout){

            List<Host> hostList = new ArrayList<Host>();

            for (int i=0; i< hostcount;i++){

                    List<Pe> peListTmp = new ArrayList<Pe>();
                    for(int j= 0; j < pescount[i]; j++)
                            peListTmp.add(new Pe(j, new PeProvisionerSimple(mipslenght)));

                    hostList.add(
                            new Host(
                                    i,
                                    new RamProvisionerSimple(ram[i]),
                                    new BwProvisionerSimple(bw[i]),
                                    storage[i],
                                    peListTmp,
                                    new VmSchedulerTimeShared(peListTmp)
                            )
                    ); 
            }

            String arch = "x86";      // system architecture
            String os = "Linux";          // operating system
            String vmm = "Xen";
            double time_zone = 10.0;         // time zone this resource located
            double cost = 3.0;              // the cost of using processing in this resource
            double costPerMem = 0.05;		// the cost of using memory in this resource
            double costPerStorage = 0.1;	// the cost of using storage in this resource
            double costPerBw = 0.1;			// the cost of using bw in this resource
            LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now
            
            DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


            // 6. Finally, we need to create a PowerDatacenter object.
            CloudProvider datacenter = null;
            try {
                    datacenter = new CloudProvider(name, characteristics,
                                                new VmAllocationPolicySimple(hostList), storageList, 0, cloudletTimeout);
            } catch (Exception e) {
                    e.printStackTrace();
            }

            return datacenter;
    }
    
    public enum Method {
        SIMPLE, //returns just the current observed paramteres
        COMPLEX_MA, //is Moving Average 
        COMPLEX_WMA, //is Weighted Moving Average 
        COMPLEX_WMAfibo, //is Fibonacci Weighted Moving Average, i.e., weighting by Fibonacci numbers
        COMPLEX_SES; //is Single Exponential Smoothing 
        
    }
    public enum ScalingRule {
        /* please cite: https://ieeexplore.ieee.org/abstract/document/7498443/ */
        RESOURCE_AWARE, // Resource aware, - Decision-making by Resource utilization rule (Amazon default policy)
        SLA_AWARE, //      SLA aware - Decision making by delay time rule
        HYBRID, //        Resource-and-SLA aware - Decision making by both resource utilization and delay time rules

        /* please cite: https://www.sciencedirect.com/science/article/pii/S1389128612003763 */
        UT_1Al, //    'Util.-based One Alaram'- Decision-making by resources utilization
        UT_2Al, //    'Util.-based Two alarms' - Decision-making by resources utilization
        LAT_1Al, //   'Latency Two alarms' - Decision-making by latency (i.e., delay)
        LAT_2Al; //   'Latency Two alarms' - Decision-making by latency (i.e., delay) */
    }
    
    public enum ExecutorType {
        // Implemented Executors are as follows:
        SIMPLE, // Executes like Amazon EC2 executor
        SUPREX; // refer to https://www.sciencedirect.com/science/article/pii/S1084804517302448
    }
    public enum SurplusVMSelectionPolicy{
        /* when performing scale down decisions, it is neccessary to choose a policy for selecting surplus VM
            Implemented policies are:*/
        RANDOM, // selects the VM randomly
        THE_OLDEST, // selects the oldest VM (like Amazon default policy)
        THE_YOUNGEST, // selects the youngest VM 
        CLOUDLET_AWARE, // selects the VM which has the lowest running cloudlet
        LOAD_AWARE, // selects the VM which has the lowest load
        COST_AWARE_SIMPLE, // selects the the most cost-efficient VM 
        COST_AWARE_PROFESSIONAL; // selects the most cost-and-load-efficient VM
    }
}
