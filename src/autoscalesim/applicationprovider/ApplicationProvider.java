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

package autoscalesim.applicationprovider;

import autoscalesim.ExperimentalSetup;
import autoscalesim.log.LogAutoscaler;
import org.cloudbus.cloudsim.CloudSimTags;
import autoscalesim.cloudprovider.DatacenterCharacteristics;
import autoscalesim.cloudprovider.CloudProvider;
import autoscalesim.enduser.Cloudlet;
import autoscalesim.applicationprovider.autoscaling.Analyzer;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.cloudprovider.Vm;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.CloudletList;
import org.cloudbus.cloudsim.lists.VmList;
import autoscalesim.log.DateTime;
import autoscalesim.applicationprovider.autoscaling.ExecutorSimple;
import autoscalesim.applicationprovider.autoscaling.ExecutorSuperProfessional;
import org.cloudbus.cloudsim.Log;
import org.neuroph.core.NeuralNetwork;
import autoscalesim.applicationprovider.autoscaling.Monitor;
import autoscalesim.applicationprovider.autoscaling.PlannerRuleBased;
import autoscalesim.applicationprovider.loadmanager.LoadAdmission;
import autoscalesim.applicationprovider.autoscaling.Executor;
import autoscalesim.applicationprovider.autoscaling.Planner;
import autoscalesim.applicationprovider.loadmanager.LoadBalancing;
import autoscalesim.enduser.EndUserEmulator;
import static autoscalesim.enduser.EndUserEmulator.MAX_CLOUDLET_LENGTH;
import static autoscalesim.enduser.EndUserEmulator.MIN_CLOUDLET_LENGTH;
import autoscalesim.log.AutoScaleSimTags.DATASET;
import static autoscalesim.log.AutoScaleSimTags.oneTab;
import static autoscalesim.log.AutoScaleSimTags.twoTabs;
import autoscalesim.log.ExperimentalResult;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.cloudbus.cloudsim.lists.VmList.getAvailableVmListToLoadBalancing;
import static org.cloudbus.cloudsim.lists.VmList.getVmsList;
/**
 * Application Provider(AP) class represents a broker acting on behalf of a user. AP rents VMs from 
 * Cloud Provider, hosts its Web application on the VMs and then lets users send their requests to
 * the application. Meanwhile, AP runs two main components: Load Manager to process the incoming load, and
 * Auto-scaling to dynamically add and remove resources to avoid maladaptation of the application.
 * 
 * 
 * @author Mohammad Sadegh Aslanpour
 */
public class ApplicationProvider extends SimEntity {
        
        public static int applicationProviderId;
    /* Load Manager parameters */
        LoadAdmission loadAdmission; 
        LoadBalancing loadBalancing;
        public static int cloudletCancelledCounterAck = 0;
    /* Auto-Scaling parameters */
        public static int vmsCountInStart;
        public static int scalingInterval;
    /* Monitor phase */
        public static Monitor monitor;
        // SLA Metrics
        public static double sumResponseTime;
        public static double sumDelayTime;
        public static int sumSLAVNumbersByTier;
        public static int[] sumSLAVNumbersByVmConfigs;
        public static int[] sumSLAVNumbersByVmPurchases;
        public static double sumSLAVSecondsByTier;
        public static double[] sumSLAVSecondsByVmConfigs;
        public static double[] sumSLAVSecondsByVmPurchases;
        public static int sumCloudletsCancelled;
        public static int sumCloudletFailured;
        public static int sumCloudletReturned;
        
        
//        public static int slaViolation; // mybe temporary
        
        // End-user metrics
        public static int sumRequestsPerAllTier;
        public static long sumRequestsLengthPerTier;
        
        /* Analyze phase */
        public static Analyzer analyzer;
        
        protected double[] sESAlpha;

        /* PlannerRuleBased phase */
        public static Planner planner;
        
        public static double slaContractOnDelayTime; // to calculate SLAV
        
        /* Executor phase */
        public static Executor executor;
        
        /* Cloud Provider information */
	protected List<Integer> datacenterIdsList;
	protected List<Integer> datacenterRequestedIdsList;
        private List<Integer> datacenterUsedIdList; 
	protected Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList;
	protected Map<Integer, Integer> vmsToDatacentersMap;
        
        // Vm
        public static int cloudletSchedulerName;
        public static int lastVmRequestedId;
        protected int vmsAcks;
        public int vmsRequested;
        public static String startUpDelayType;
        public static double BASE_DELAY_IN_VM_START_UP;
            // Vm lists
        protected static List<? extends Vm> vmsRequestedList;
        protected static List<? extends Vm> vmsStartedList;
        protected static List<? extends Vm> vmsQuarantinedList;
        protected static List<? extends Vm> vmsDestroyedList;

        // End user information
        public static boolean IsFutureCloudlet;
        public static int lastCloudletReceivedId;
        public static DATASET datasetType;
        
        // Cloudlet lists
        public static List<? extends Cloudlet> cloudletReceivedList;
        public List<? extends Cloudlet> cloudletSentList;
        public List<? extends Cloudlet> cloudletCompletedList;
        public static List<? extends Cloudlet> cloudletCancelledList;
        public static List<? extends Cloudlet> cloudletFailedList;
        
        // Initialing Settings
        private final String[] ITEMS_TO_REPORT_LIST;

        /**
         * 
         * @param name
         * @param reservedVMs
         * @param loadAdmission
         * @param loadBalancing
         * @param cloudletSchedulerName
         * @param scalingInterval
         * @param slaContractOnDelayTime
         * @param monitor
         * @param analyzer
         * @param planner
         * @param executor
         * @param ITEMS_TO_REPORT_LIST
         * @throws Exception 
         */
        public ApplicationProvider(String name,
                                        int reservedVMs,
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
                                        final String[] ITEMS_TO_REPORT_LIST) throws Exception {
            super(name);
            
            
        /* Load Manager */
            setLoadAdmission(loadAdmission);
            setLoadBalancing(loadBalancing);
        /* Auto Scaling */
            vmsCountInStart = reservedVMs;
            this.scalingInterval = scalingInterval;
            this.slaContractOnDelayTime = slaContractOnDelayTime;
            /*Monitor phase*/
            setMonitor(monitor);
            // SLA Metrice
            sumResponseTime = 0;
            sumDelayTime = 0;
            sumSLAVNumbersByTier = 0;
            sumSLAVNumbersByVmConfigs = new int[]{0, 0, 0, 0};
            sumSLAVNumbersByVmPurchases = new int[]{0, 0, 0};
            sumSLAVSecondsByTier = 0;
            sumSLAVSecondsByVmConfigs = new double[]{0, 0, 0, 0};
            sumSLAVSecondsByVmPurchases = new double[]{0, 0, 0};
            sumCloudletsCancelled = 0;
            sumCloudletReturned = 0;
            sumCloudletFailured = 0;

            // End-user History
            sumRequestsPerAllTier = 0;
            sumRequestsLengthPerTier = 0;
            this.datasetType = datasetType;
            /* Analyze phase */
            setAnalyzer(analyzer);
            
            
            /* PlannerRuleBased phase */
            setPlanner(planner);
            
            /* Executor phase */
            
            setExecutor(executor);
            getExecutor().setApplicationProviderId(getId());
            
            // AP
            this.ITEMS_TO_REPORT_LIST = ITEMS_TO_REPORT_LIST;
            
            // Vm
            setCloudletSchedulerName(cloudletSchedulerName);
            setVmsRequestedList(new ArrayList<Vm>());
            setVmsStartedList(new ArrayList<Vm>());
            setVmsQuarantinedList(new ArrayList<Vm>());
            setVmsDestroyedList(new ArrayList<Vm>());
            setVmsRequested(0);
            setVmsAcks(0);

            // Cloudlet
            setCloudletReceivedList(new ArrayList<Cloudlet>());
            setCloudletSentList(new ArrayList<Cloudlet>());
            setCloudletCancelledList(new ArrayList<Cloudlet>());
            setCloudletFinishedList(new ArrayList<Cloudlet>());
            setCloudletFailedList(new ArrayList<Cloudlet>());
            IsFutureCloudlet = true;
            lastCloudletReceivedId = -1;

            // Data Center
            setDatacenterIdsList(new LinkedList<Integer>());
            setDatacenterRequestedIdsList(new ArrayList<Integer>());
            setVmsToDatacentersMap(new HashMap<Integer, Integer>());
            setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());
            setDatacenterUsedIdList(new ArrayList<Integer>());
	}

        /**
	 * Processes events available for this ASP.
	 * Manages events
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	@Override
	public void processEvent(SimEvent ev) {
            switch (ev.getTag()) {
            // Resource characteristics request
                case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
                        processResourceCharacteristicsRequest(ev);
                        break;
                // Resource characteristics answer
                case CloudSimTags.RESOURCE_CHARACTERISTICS:
                        processResourceCharacteristics(ev);
                        break;
                // VM Creation answer
                case CloudSimTags.VM_CREATE_ACK:
                        processVmCreate(ev);
                        break;
                case CloudSimTags.VM_DESTROY_ACK:
                        processVmDestroy(ev);
                        break;
                // A finished cloudlet returned
                case CloudSimTags.CLOUDLET_COMPLETE:
                        processCloudletComplete(ev);
                        break;
                case CloudSimTags.CLOUDLET_CANCEL:
                        processCloudletCancel(ev);
                        break;
                case AutoScaleSimTags.CLOUDLET_FAIL:
                        processCloudletFail(ev);
                        break;
                case CloudSimTags.END_OF_SIMULATION:
                        shutdownEntity();
                        break;
                case AutoScaleSimTags.ASP_START:
                        ASPStart();
                        break;
                case AutoScaleSimTags.VM_STARTING:
                        vmsStarting();
                        break;
                case AutoScaleSimTags.AUTO_SCALING:
                        autoScaling(ev);
                        break;
                case AutoScaleSimTags.LOAD_MANAGEMENT_ADMISSION:
                        loadManager(ev);
                        break;
                case AutoScaleSimTags.LOAD_MANAGER_NEW_REQUEST:
                        loadManager(ev);
                        break;
                case AutoScaleSimTags.LOAD_MANAGER_CANCELED_REQUEST:
                        loadManager(ev);
                        break;
                case AutoScaleSimTags.VMS_SYNCHRONIZATION:
                        vmsSynchronization(ev); 
                        break;
                case AutoScaleSimTags.WORKLOAD_FINISH:
                        IsFutureCloudlet = false;
                        break;
                case AutoScaleSimTags.ASP_FINISH:
                        ExperimentalResult.showResultsInConsole();
                        ExperimentalResult.printReports(ITEMS_TO_REPORT_LIST);
                        clearDatacenters();         
                        finishSimulation();
                        break;
                // other unknown tags are processed by this method
                default:
                        processOtherEvent(ev);
                        break;
            }
	}
        
        /**
         * Starts ASP With X reserved VMs ( X = vmsCountInStart)
         */
        protected void ASPStart(){
            int vms = vmsCountInStart;
            int purchaseType = AutoScaleSimTags.VM_PURCHASE_ON_DEMAND;
            int configurationType = planner.getConfigurationType();
            int tier = AutoScaleSimTags.WEB_TIER;
            List<Vm> vmList;
            vmList = getExecutor().preparationReservedVmRequest(
                                                                vms, 
                                                                purchaseType, 
                                                                configurationType, 
                                                                tier);  
            effectorScaleUp(vmList);
//            
            Log.printLine(DateTime.timeStamp()+ oneTab + "ASP Starts with (web "+ vms + " app - db -" 
                                                        + ") Reserved Instance(s) ");
            
            sendNow(getId(), AutoScaleSimTags.VM_STARTING);
        }
        
        /**
         * Load Manager Component
         * It performs Admission and Dispatching of user requests to the application
         * Also re-dispatches canceled user request
         * @param ev 
         */
        public void loadManager(SimEvent ev){
            int receiver = ev.getTag();
            switch(receiver){
                case AutoScaleSimTags.LOAD_MANAGEMENT_ADMISSION:
                    Cloudlet newCloudlet = (Cloudlet)ev.getData();
                    getLoadAdmission().admission(newCloudlet);
                    sendNow(getId(), AutoScaleSimTags.LOAD_MANAGER_NEW_REQUEST, newCloudlet);
                    break;
                    
                case AutoScaleSimTags.LOAD_MANAGER_NEW_REQUEST:
                    // if this cloudlet is the end one in the received list, perform dispatching, 
                    // because of batch load dispatching i.e., if we have some cloudlets together in a second, 
                    // it admits all of them and then starts.
                    if(getCloudletReceivedList().get(getCloudletReceivedList().size() - 1)
                                                    .getCloudletId() == ((Cloudlet)ev.getData()).getCloudletId()){
                        
                        // assigning a vm id for each cloudlet
                        getLoadBalancing().dispatchingNewRequests(VmList.getAvailableVmListToLoadBalancing());
                        // Submit each Cloudlet to a specified Vm
                        submitCloudletToVMs();
                    }
                    
                    break;
                // dispatching canceled cloudlets
                case AutoScaleSimTags.LOAD_MANAGER_CANCELED_REQUEST:
                    getLoadBalancing().dispatchingCanceledRequests(getAvailableVmListToLoadBalancing());
                    // submits cancelled cloudlets to their vms
                    submitCloudletToVMs(); 
                    break;
                default:
                    errorChecker = true; 
                    error+= "Load Manager received en event with unknown Data";
                    Log.printLine("Load Manager received en event with unknown Data");
                    break;
            }
        }
        
        /**
         * Auto Scaling Component
         * It dynamically adjusts the number of rented VMs to incoming user requests
         * It does this by MAPE strategy. 
         * In MAPE there are 4 main phases to do: Monitor, Analyze, Planner, Executor
         */
        public void autoScaling(SimEvent ev) {
            String receiver = (String)ev.getData();
            switch(receiver){
                
                // Before running the suto-scaling system: Gets the latest status of all rented VMs
                case "VmsSynchronization":
                    vmsSynchronization(ev);
                    break;
                // First: Monitors the effective parameters in 3 categories: VM, SLA, EndUser
                case "Monitor" :
                    getMonitor().doMonitoring();
                    
                    getExecutor().remainedCoolDownTime -= AutoScaleSimTags.aMinute;
                    // Is time to run full aut-scaling?
                    if((CloudSim.clock() % (AutoScaleSimTags.aMinute * scalingInterval) == 0)){
                        Log.printLine();
                        String s = (char)27 + "[31;1m ********************* Auto-Scaling Logger *********************";
                        Log.printLine(DateTime.timeStamp()+ s);
                        sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Analyzer");
                    }else if(getExecutor().executorType == ExperimentalSetup.ExecutorType.SUPREX)
                        sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "QuarantinedVMsUpdater");
                    else
                        sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Coordinator");
                    
                    break;
                // Second: Analyzes parameters such as CPU Utiization, Response Time, User Requests, and Throughput
                case "Analyzer":
                    getAnalyzer().doAnalysis();
                    
                    sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Planner");
                    break;
                // Third: Makes decision
                case "Planner":
                    getPlanner().doPlanning();
                    
                    sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Executor");
                    break;
                // Forth: Executes the decisons
                case "Executor":
                    ArrayList executorResult = getExecutor().execution();
                    
                    int scaleUpCount = (int)executorResult.get(0);
                    List<Vm> scaleUpVms = (List<Vm>)executorResult.get(1);
                    int scaleDownCount = (int)executorResult.get(2);
                    List<Vm> scaleDownVms = (List<Vm>)executorResult.get(3);
                    
                    if(scaleUpCount > 0)
                        effectorScaleUp(scaleUpVms);
                    
                    boolean waitingForVmDestroyAck = false;
                    if(scaleDownCount > 0)
                        waitingForVmDestroyAck = effectorScaleDown(scaleDownVms);
                                        
                    if(waitingForVmDestroyAck == false)
                        sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Coordinator");
                    
                    break;
                // In using Super Professional Executor, All Quarantined VMs' deadline should be checked
                case "QuarantinedVMsUpdater":
                    List<Vm> junkVmList = getExecutor().quarantinedVMsUpdater();
                    if(junkVmList.isEmpty() == false)
                        effectorScaleDown(junkVmList);
                    else
                        sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Coordinator");
                    break;
                // Coordinator recalls auto scaler for next minute to be ran again
                case "Coordinator":
                    coordinator();
                    break;
            }
        }
        
        /**
         * Synchronize VMs status every Minute
         * It gets the final status of all VMs in the cloud providers
         * @param ev 
         */
        public void vmsSynchronization (SimEvent ev) {
            // Requested (by coordinator or vms starting)
            if(ev.getSource() == getId()) { // sender is ASP (request)
                // do refreshing
                if (CloudSim.clock() > 0.111) {
                   // Identity all used DC
                    ArrayList<Vm> vmList = VmList.getVmsList(new int[]{Vm.Started, Vm.Quarantined});
                    for (Vm vm : vmList) {
                        if(vm.getStartTime() != -1){ // maybe a requested vm has been sent to quarantined list
                            int dcId = vm.getHost().getDatacenter().getId();
                            if (!getDatacenterUsedIdsList().contains(dcId))
                                getDatacenterUsedIdsList().add(dcId);
                        }
                    }
                    // Synchronize all used DC (Send request)
                    for (int datacenterId : getDatacenterUsedIdsList()){
                        sendNow(datacenterId, AutoScaleSimTags.VMS_SYNCHRONIZATION);
                    }
                } else { // In Start of Simulation
                    // turn on timeout checker
                    for (int datacenterId : getDatacenterIdsList()){
                        sendNow(datacenterId, AutoScaleSimTags.TIMEOUT_CHECKER);
                    }
                    
                    coordinator();
                }
            } else { // Answer from a Data center
                int datacenterId = ev.getSource();
                int index = getDatacenterUsedIdsList().indexOf(datacenterId);
                getDatacenterUsedIdsList().remove(index);

                // Is the latest answer from Datacenters?
                if (getDatacenterUsedIdsList().size() == 0){
//                        reportOfVms();
                    sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Monitor");
                }
            }
        }
        
        /**
         * Directly  requests VMs to be allocated from Cloud Provider
         * @param scaleUpVmList 
         */
        public void effectorScaleUp(List<Vm> scaleUpVmList){
            submitVmsRequestedList(scaleUpVmList);
        }
        
        /**
         * Directly requests VMs to be de-allocated from Cloud Provider
         * @param scaleDownVmList
         * @return 
         */
        public boolean effectorScaleDown(List<Vm> scaleDownVmList) {
            boolean waitingForVmDestroyAck = false;
            int requestedVmsNeedingToWaitForDestroying = 0;
            
            for(Vm surplusVm : scaleDownVmList){
                
        /* For Requested Vm */
                if(surplusVm.getStatus() == Vm.Requested){

                    getVmsRequestedList().remove(VmList.getById(getVmsRequestedList(), surplusVm.getId()));
                    surplusVm.setStatus(Vm.Destroyed);
                    surplusVm.setDestroyTime(CloudSim.clock());
                    // set Life Time 
                    double availableSecond = surplusVm.getDestroyTime() - surplusVm.getRequestTime();
                    surplusVm.setLifeTime(availableSecond);
                
                    getVmsDestroyedList().add(surplusVm);

                    // its bill doesn't need to be set

                /* An started Vm */
                }else if (surplusVm.getStatus() == Vm.Started || surplusVm.getStatus() == Vm.Quarantined){
                    
                    waitingForVmDestroyAck = true;
                    requestedVmsNeedingToWaitForDestroying++;
                    
                    // Cancel all running cloudlet in this Vm
                    for (Cloudlet cloudlet : getCloudletSentList()) {
                        if (cloudlet.getVmId() == surplusVm.getId()){
                            sendNow(getVmsToDatacentersMap().get(surplusVm.getId()), CloudSimTags.CLOUDLET_CANCEL, cloudlet);
                            cloudletCancelledCounterAck++;
                        }
                    }

                    // Set bill (just for On-Demand Instance)
                    if(surplusVm.getPurchaseType() == AutoScaleSimTags.VM_PURCHASE_ON_DEMAND){
                        double availableHours = Math.ceil((CloudSim.clock() - surplusVm.getRequestTime()) / AutoScaleSimTags.anHour);
                        double bill =  availableHours * surplusVm.getPrice();
                        if(bill != 0) 
                            surplusVm.setBill(bill);
                    }

                    sendNow(getVmsToDatacentersMap().get(surplusVm.getId()), CloudSimTags.VM_DESTROY_ACK, surplusVm);

                    // remove from quarantined list
                    if(getExecutor().executorType == ExperimentalSetup.ExecutorType.SUPREX){ 
                        // in Suprex executpr, a Vm would destroy just from q list
                        getVmsQuarantinedList().remove(VmList.getById(getVmsQuarantinedList(), surplusVm.getId()));
                    }else{
                    // remove from started list
                        getVmsStartedList().remove(VmList.getById(getVmsStartedList(), surplusVm.getId()));
                    }

                    if(surplusVm.getStartTime() == -1){
                        Log.printLine("error-scaling down - selected vm is not in  started status");
                        errorChecker = true;
                        error ="error-scaling down - selected vm is not in  started status";
                    }
                    surplusVm.setStatus(Vm.Destroyed);
                    surplusVm.setDestroyTime(CloudSim.clock());
                    // set Life Time
                    double availableSecond = surplusVm.getDestroyTime() - surplusVm.getRequestTime();
                    surplusVm.setLifeTime(availableSecond);

                    getVmsDestroyedList().add(surplusVm);

                }
                
                setVmsRequested(requestedVmsNeedingToWaitForDestroying);
                setVmsAcks(0);

            }

            return waitingForVmDestroyAck;
        }
                
        /**
         * Manages internal events every 1 minute
         */
        public void coordinator() {
            // LogAutoscaler after a full running of auto-scaling
            if((CloudSim.clock() % (AutoScaleSimTags.aMinute * scalingInterval) == 0 && CloudSim.clock() > 0.99)){
                LogAutoscaler.logCurrentEpoch();
            }
            // Are there any Future, Sent Or Cancelled cloudlet?
            if (IsFutureCloudlet 
                || (getCloudletFinishedList().size() + getCloudletFailedList().size() ) < lastCloudletReceivedId + 1) {
               
                // Vms that haven't started yet, from req list or q list
                ArrayList<Vm> unInitialedVmList = getVmsList(new int[]{Vm.Requested, Vm.Quarantined});
                boolean thereIsVmStartUpInNextMinute = false;
                for(Vm vm: unInitialedVmList){
                    if(vm.getStartTime() != -1) 
                        continue;
                    double timeForStarting = vm.getRequestTime() + vm.getDelayInStartUp();
                    if((CloudSim.clock() + AutoScaleSimTags.aMinute) == timeForStarting){
                        thereIsVmStartUpInNextMinute = true;
                        break;
                    }
                }
                // Deciding about the next min wheather is time to vm starting or just continuing of mape loop
                if (thereIsVmStartUpInNextMinute)
                    send(getId(), AutoScaleSimTags.aMinute, AutoScaleSimTags.VM_STARTING);
                else 
                    send(getId(), AutoScaleSimTags.aMinute, AutoScaleSimTags.AUTO_SCALING, "VmsSynchronization");

            } else {    // There isn't any new cloudlet
                
                sendNow(getId(), AutoScaleSimTags.ASP_FINISH);
            }
        }
       
        /**
         * Starts vm(s) - The process of scaling up
         */
        public void vmsStarting () {
            Log.printLine(DateTime.timeStamp() + "Scaling UP:  Running a New Vm");
            setDatacenterRequestedIdsList(new ArrayList<Integer>());

            createVmsInDatacenter(getDatacenterIdsList().get(0));
        }
        
        /**
         * Initials requested VMs
         * @param datacenterId 
         */
        protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		String datacenterName = CloudSim.getEntityName(datacenterId);
                // prepare requested Vms
                    // requested list
                ArrayList<Vm> vmList;
                vmList = getVmsList( new int[]{Vm.Requested});
                    /* requested vms in q list */
                for(Vm vm : getVmsQuarantinedList()){
                    if(vm.getStartTime() == -1)
                        vmList.add(vm);
                }
                /* end */
                
                Iterator<Vm> iterator = vmList.iterator();
                while (iterator.hasNext()){
                    Vm vm = iterator.next(); 
                    
                    double timeForStarting = vm.getRequestTime() + vm.getDelayInStartUp();
                    if(CloudSim.clock() == timeForStarting){
                        sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                        requestedVms++;
                    }
                }
                
		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}
        
        /**
	 * Process the ack received due to a request for VM Initialing.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processVmCreate(SimEvent ev) {
            int[] data = (int[]) ev.getData();
            int datacenterId = data[0];
            int vmId = data[1];
            int result = data[2];

            if (result == CloudSimTags.TRUE) {
                // Vm started
                Vm vm;
                vm = VmList.getById(getVmsRequestedList(), vmId);
                if (vm == null)
                    vm = VmList.getById(getVmsQuarantinedList(), vmId);

                // Change Vm Status
                vm.setStatus(Vm.Started);
                vm.setStartTime(CloudSim.clock());
                Log.printLine(DateTime.timeStamp()+ twoTabs + "VM #" + vmId + " Ran");

                getVmsToDatacentersMap().put(vmId, datacenterId);
                // Exchange vm list
                if(VmList.getById(getVmsRequestedList(), vmId) != null){ // if it is in requested list
                    getVmsStartedList().add(vm);
                    getVmsRequestedList().remove(VmList.getById(getVmsRequestedList(), vmId));//Remove by index
                }else if(VmList.getById(getVmsQuarantinedList(), vmId) != null){ // if it is in quarantined list
                    vm.setStatus(Vm.Quarantined);
                    getVmsQuarantinedList().remove(VmList.getById(getVmsQuarantinedList(), vmId));//Remove by index
                    getVmsQuarantinedList().add(vm);// 
                }

            } else {
                Log.printLine(DateTime.timeStamp()+ twoTabs + "Process Vm Create: Starting of VM #" + vmId
                                    + " failed in Datacenter #" + datacenterId);
            }

            incrementVmsAcks();

            // Whether all the requested VMs (in req list and q list) have been started?
            ArrayList<Vm> unInitialedVmList = getVmsList( new int[]{Vm.Requested, Vm.Quarantined});

            boolean thereIsVmStartUpInThisMinute = false;
            for(Vm vm: unInitialedVmList){
                if(vm.getStartTime() != -1) //If the Vm is in Quarantine and its Start Time is not -1, it is not in requested status 
                    continue;
                double timeForStarting = vm.getRequestTime() + vm.getDelayInStartUp();
                if(CloudSim.clock() == timeForStarting){ //  Is it time to launch this vm?
                    thereIsVmStartUpInThisMinute = true;
                    break;
                }
            }
            // is there any vm to start?
            if (thereIsVmStartUpInThisMinute == false){
                if(CloudSim.clock() == 0){
                Log.printLine("Application Provider has hosted its application in the cloud (by "
                        + vmsCountInStart + " reserved VMs).");
                Log.printLine(" Now, end users are able to access to the application and send"
                        + " their requests to it");
                Log.printLine("On the other hand, The Auto-Scaling mechanism avoids over-provisioning and under-"
                        + "provisioning of resources every " + scalingInterval + " minute(s)");
                Log.printLine();
                Log.printLine();
                }
                sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "VmsSynchronization");
            }else{
                // all the acks received, but some VMs were not started
                if (getVmsRequested() == getVmsAcks()) {
                    // find id of the next datacenter that has not been tried
                    for (int nextDatacenterId : getDatacenterIdsList()) {
                        if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                            createVmsInDatacenter(nextDatacenterId);
                            return;
                        }
                    }

                    // all datacenters already queried
                    Log.printLine(DateTime.timeStamp()+ "Process Vm Create: Error: Some Vms Didn't Start");
                    errorChecker= true;
                    error+= "Process Vm Create: Error: Some Vms Didn't Start";
                }
            }
	}
        
        /**
         * The Answer of vm destroy from Cloud Provider
         * @param ev 
         */
        public void processVmDestroy(SimEvent ev) {
            Log.printLine(DateTime.timeStamp()+ oneTab + "Process vm Destroy");
            Vm vm = (Vm) ev.getData();
            Log.printLine(DateTime.timeStamp()+ oneTab + "Vm# " + vm.getId() + "has been destroyed");

            incrementVmsAcks();
            if(getVmsRequested() == getVmsAcks()){ // All Scale Downs have been done
                sendNow(getId(), AutoScaleSimTags.AUTO_SCALING, "Coordinator");
            }
        }
        

        /**
	 * Submit cloudlet to the VM.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void submitCloudletToVMs() {
                List<Cloudlet> cloudletSubmittedList = new ArrayList<Cloudlet>();
                int i = 0;
                // First: Submit cancelled cloudlets to vms
                for (Cloudlet cloudlet : getCloudletCancelledList()) {
                    sendNow(getVmsToDatacentersMap().get(cloudlet.getVmId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);

                    cloudletSubmittedList.add(cloudlet);
                }
                
                // Exchange Sent Cloudlets
                for (Cloudlet cloudlet : cloudletSubmittedList) {
                        getCloudletSentList().add(cloudlet);
                        getCloudletCancelledList().remove // remove by ID
                                (CloudletList.getById(getCloudletCancelledList(), cloudlet.getCloudletId()));
                }
                    
                cloudletSubmittedList = new ArrayList<>();

                // Second: Submit received cloudlets to vms
		for (Cloudlet cloudlet : getCloudletReceivedList()) {

                        sendNow(getVmsToDatacentersMap().get(cloudlet.getVmId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
                        cloudletSubmittedList.add(cloudlet);
		}
                
                // Exchange Sent Cloudlets
                for (Cloudlet cloudlet : cloudletSubmittedList) {
                        getCloudletSentList().add(cloudlet);
                        getCloudletReceivedList().remove
                                (CloudletList.getById(getCloudletReceivedList(), cloudlet.getCloudletId()));
                }
	}
        
        /**
	 * Process a cloudlet return event.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletComplete(SimEvent ev) {
            
            Cloudlet cloudlet = (Cloudlet) ev.getData();
            // Get its Vm
            ArrayList<Vm> vmList;
            vmList = getVmsList( new int[]{Vm.Started, Vm.Quarantined});
            
            if (VmList.getById(vmList, cloudlet.getVmId()) == null){
                Log.print("error- its vm has been destroyed before");
                errorChecker=true;
                Log.printLine("error- its vm has been destroyed before");
                return;
            }
            
            Vm vm = VmList.getById(vmList, cloudlet.getVmId());
            // Compare Desire Response Time and Delay
            double vmMips = vm.getMips();
            int vmPEs = vm.getNumberOfPes();
            if(cloudlet.getNumberOfPes() > vmPEs){
                errorChecker = true;
                error += "--cloudletReturn - cloudlet pes was bigger than vm's";
                Log.printLine("--cloudletReturn - cloudlet pes was bigger than vm's");
            }
            // Set Finish Time
                // Has already been set. 
            
            // Set Response Time
            cloudlet.setResponseTime(CloudSim.clock() 
                    - cloudlet.getFirstSubmissionTime()
                    + networkDelay(cloudlet.getCloudletLength()));
            sumResponseTime+= cloudlet.getResponseTime();
            
            // Set Delay Time
            double delayTime = cloudlet.getTotalWaitingTime(cloudletSchedulerName, vmMips, vmPEs)
                        + networkDelay(cloudlet.getCloudletLength());
            if (delayTime == -0.0)
                delayTime = 0.0;
            cloudlet.setDelayInFinishing(delayTime);
            sumDelayTime += delayTime;

            /* Does SLA has been violated for this cloudlet? */
            if(delayTime > slaContractOnDelayTime){
//                slaViolation++;
                sumSLAVNumbersByTier ++;
                sumSLAVNumbersByVmConfigs[vm.getConfigurationType()]++;
                sumSLAVNumbersByVmPurchases[vm.getPurchaseType()] ++;
                sumSLAVSecondsByTier += delayTime - slaContractOnDelayTime;
                sumSLAVSecondsByVmConfigs[vm.getConfigurationType()] += delayTime - slaContractOnDelayTime;
                sumSLAVSecondsByVmPurchases[vm.getPurchaseType()] += delayTime - slaContractOnDelayTime;
            }

            sumCloudletReturned++;
            // Exchange Cloudlet
            getCloudletFinishedList().add(cloudlet);
            //Remove old version of cloudlet
            getCloudletSentList().remove(CloudletList.getById(getCloudletSentList(), cloudlet.getCloudletId()));
        }
        
        /**
         * Cloudlet Cancel 
         * @param ev  data is cloudlet
         */
        public void processCloudletCancel (SimEvent ev) { 
            
            cloudletCancelledCounterAck --;
            Log.printLine(DateTime.timeStamp() + oneTab + "process cloudlet cancel");
            Cloudlet cloudletCanceled = (Cloudlet)ev.getData();
            // Get its Vm
            ArrayList<Vm> vmList;
            vmList = getVmsList( new int[]{Vm.Destroyed});

            if (VmList.getById(vmList, cloudletCanceled.getVmId()) == null){
                Log.print("error- its vm has been destroyed before");
                errorChecker=true;
                error += "ap- process cloudlet cancel- ";
            }
            
            Vm vm = VmList.getById(vmList, cloudletCanceled.getVmId());
            
            sumCloudletsCancelled ++;
            cloudletCanceled.setVmId(-1);
            cloudletCanceled.setExecStartTime(0);
            cloudletCanceled.setFailTime(CloudSim.clock()); 
            // Exchange cloudlet list
            getCloudletCancelledList().add(cloudletCanceled);
            // Remove by Id of cloudlet's old version
            getCloudletSentList().remove(CloudletList.getById(getCloudletSentList(), cloudletCanceled.getCloudletId()));
            
            if(cloudletCancelledCounterAck == 0){
                sendNow(getId(), AutoScaleSimTags.LOAD_MANAGER_CANCELED_REQUEST);
            }
        }

        /**
         * Process a Cloudlet Failure event
         * @param ev 
         */
        protected void processCloudletFail(SimEvent ev) {
            Cloudlet failedCloudlet = (Cloudlet)ev.getData();
            
            // Set Failure time, by default it is -1
            failedCloudlet.setFailTime(CloudSim.clock());
            
            // Set Finish Time
                failedCloudlet.setFinishTime(CloudSim.clock());
                
            try {
                // Set Status (Cloudlet Cancel method sets it as Cancel status)
                failedCloudlet.setCloudletStatus(Cloudlet.FAILED);
            } catch (Exception ex) {
                Logger.getLogger(ApplicationProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Get its Vm
            ArrayList<Vm> vmList;
            vmList = getVmsList( new int[]{Vm.Started, Vm.Quarantined});

            if (VmList.getById(vmList, failedCloudlet.getVmId()) == null){
                Log.print("error- its vm has been destroyed before");
                errorChecker=true;
                error+="error- its vm has been destroyed before";
                return;
            }
            
            Vm vm = VmList.getById(vmList, failedCloudlet.getVmId());
        
        // Compare Desire Response Time and Delay
            double vmMips = vm.getMips();
            int vmPEs = vm.getNumberOfPes();
            if(failedCloudlet.getNumberOfPes() > vmPEs){
                errorChecker = true;
                error += "--cloudletReturn - cloudlet pes was bigger than vm pes";
                Log.printLine("--cloudletReturn - cloudlet pes was bigger than vm pes");
            }
            // Set Response Time
            failedCloudlet.setResponseTime(CloudSim.clock() 
                    - failedCloudlet.getFirstSubmissionTime()
                    + networkDelay(failedCloudlet.getCloudletLength()));
            sumResponseTime+= failedCloudlet.getResponseTime();
            
            // Set Delay Time
            double delayTime = failedCloudlet.getTotalWaitingTime(cloudletSchedulerName, vmMips, vmPEs)
                    + networkDelay(failedCloudlet.getCloudletLength());
            failedCloudlet.setDelayInFinishing(delayTime);
            sumDelayTime += delayTime;

            /* Does SLA has been violated by this cloudlet? */
            if(delayTime > slaContractOnDelayTime){
//                slaViolation++;
                sumSLAVNumbersByTier ++;
                sumSLAVNumbersByVmConfigs[vm.getConfigurationType()]++;
                sumSLAVNumbersByVmPurchases[vm.getPurchaseType()] ++;
                sumSLAVSecondsByTier += delayTime - slaContractOnDelayTime;
                sumSLAVSecondsByVmConfigs[vm.getConfigurationType()] += delayTime - slaContractOnDelayTime;
                sumSLAVSecondsByVmPurchases[vm.getPurchaseType()] += delayTime - slaContractOnDelayTime;
            }

            sumCloudletFailured++;

            // Exchange Cloudlet
            getCloudletFailedList().add(failedCloudlet);
            getCloudletFinishedList().add(failedCloudlet);
            //Remove old version of cloudlet
            getCloudletSentList().remove(CloudletList.getById(getCloudletSentList(), failedCloudlet.getCloudletId()));
        }
        
        
        /**
         * This delay is calculated after receiving the cloudlet because: 
         * a) this is a part of AutoScaleSim and one might miss it if we place it
         * in the VM or cloud provider
         * b) if this is in Vm side, the VM will be occupied for this cloudlet
         * while this is completed.
         * @param cloudletLength
         * @return 
         */
        private double networkDelay(long cloudletLength){
            double delay = 0;
            if (datasetType.equals(DATASET.NASA)){
                delay=0;
            }else if (datasetType.equals(DATASET.WIKIPEDIA)){
//                // bring between 0.1 and 0.9
//                delay = (cloudletLength - MIN_CLOUDLET_LENGTH) / (MAX_CLOUDLET_LENGTH - MIN_CLOUDLET_LENGTH) * 0.8 + 0.1;
//                //bring between 0.01 to 0.09
//                delay = delay / (double)10;
                // assuming that the bw is 1000kbps and the to millisecond
                delay = cloudletLength / (double)1000 / (double)1000;
            }
            
            return delay;
        }
        /**
	 * Process the return of a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);
                
                // All data center characteristics have been received
		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()){
                    // It allows application provider to be started and to rent reserved instances
                    send(getId(), 0, AutoScaleSimTags.ASP_START);
                    // It allows end users to send their request to the application
                    send(CloudSim.getClientId(), 0, AutoScaleSimTags.WORKLOAD_START, getId());
                }
	}

	/**
	 * Process a request for the characteristics of a PowerDatacenter.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processResourceCharacteristicsRequest(SimEvent ev) {
		setDatacenterIdsList(CloudSim.getCloudResourceList());
		setDatacenterCharacteristicsList(new HashMap<Integer, DatacenterCharacteristics>());

		Log.printLine(DateTime.timeStamp()+ "Cloud Resource List received with "
				+ getDatacenterIdsList().size() + " resource(s)");

		for (Integer datacenterId : getDatacenterIdsList()) {
			sendNow(datacenterId, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	/**
	 * Overrides this method when making a new and different type of ASP. This method is called
	 * by {@link #body()} for incoming unknown tags.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != null
	 * @post $none
	 */
	protected void processOtherEvent(SimEvent ev) {
		if (ev == null) {
			Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
                        errorChecker=true;
                        error+=getName() + ".processOtherEvent(): " + "Error - an event is null.";
			return;
		}

		Log.printLine(getName() + ".processOtherEvent(): "
				+ "Error - event unknown by this ASP.");
                errorChecker=true;
                error+=getName() + ".processOtherEvent(): Error - event unknown by this ASP.";
	}

	
        
        /**
	 * Send an internal event communicating the end of the simulation.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void finishSimulation() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {

	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {
		Log.printLine(getName() + " is starting...");
		schedule(getId(), 0, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
	}

         /**
	 * Destroy the virtual machines running in data centers.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void clearDatacenters() {
            ArrayList<Vm> vmList;
            vmList = getVmsList( new int[]{Vm.Started, Vm.Quarantined});
            
            for (Vm vm : vmList) {
//        	Log.printLine(DateTime.timeStamp() + "Destroying VM #" + vm.getId());
                if(vm.getStartTime() !=-1)
                    sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.VM_DESTROY, vm);
                    
            }

            getVmsStartedList().clear();
	} 

        /**
	 * Gets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet received list
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Cloudlet> List<T> getCloudletReceivedList() {
		return (List<T>) cloudletReceivedList;
	}

	/**
	 * Sets the cloudlet received list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletList the new cloudlet received list
	 */
	protected <T extends Cloudlet> void setCloudletReceivedList(List<T> cloudletReceivedList) {
		this.cloudletReceivedList = cloudletReceivedList;
	}
        
          /**
	 * Submit cloudlets "list" to cloudlet received
	 * 
	 * @param list the list
	 * @pre list !=null
	 * @post $none
	 */
        public void submitCloudletReceivedList(List<? extends Cloudlet> list) {
		getCloudletReceivedList().addAll(list);
	}
        
        /**
         * Submit a cloudlet to cloudlet received
         * @param cloudlet 
         */
        public void submitCloudletReceived(Cloudlet cloudlet){
            getCloudletReceivedList().add(cloudlet);
        }
	/**
	 * Gets the cloudlet sent list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet sent list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletSentList() {
		return (List<T>) cloudletSentList;
	}

	/**
	 * Sets the cloudlet sent list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletSentList the new cloudlet sent list
	 */
	protected <T extends Cloudlet> void setCloudletSentList(List<T> cloudletSentList) {
		this.cloudletSentList = cloudletSentList;
	}
        
        /**
         * Gets the cloudlet cancelled list.
         * @param <T>
         * @return 
         */
	public static <T extends Cloudlet> List<T> getCloudletCancelledList(){
            return (List<T>) cloudletCancelledList;
        }
        
        /**
         * Sets the cloudlet cancelled list.
         * @param <T>
         * @param cloudleCancelledList 
         */
        protected <T extends Cloudlet> void setCloudletCancelledList(List<T> cloudleCancelledList) {
            this.cloudletCancelledList = cloudleCancelledList;
        }
        
        
        protected <T extends Cloudlet> void setCloudletFailedList(List<T> cloudleFailedList) {
            this.cloudletFailedList = cloudleFailedList;
        }
        
        public static <T extends Cloudlet> List<T> getCloudletFailedList(){
            return (List<T>) cloudletFailedList;
        }
        /**
	 * Gets the cloudlet finished list.
	 * 
	 * @param <T> the generic type
	 * @return the cloudlet finished list
	 */
	@SuppressWarnings("unchecked")
	public <T extends Cloudlet> List<T> getCloudletFinishedList() {
		return (List<T>) cloudletCompletedList;
	}

       	/**
	 * Sets the cloudlet finished list.
	 * 
	 * @param <T> the generic type
	 * @param cloudletReceivedList the new cloudlet finished list
	 */
	protected <T extends Cloudlet> void setCloudletFinishedList(List<T> cloudletFinishedList) {
		this.cloudletCompletedList = cloudletFinishedList;
	}
//////////////////////////////////////////
       
        
      
        /**
         * Sets Requested Vm List
         */
        public static <T extends Vm> List<T> getVmsRequestedList() {
		return (List<T>) vmsRequestedList;
	}
        
        /**
         * Gets Requested vm list
         * @param <T>
         * @param vmsRequestedList 
         */
        protected <T extends Vm> void setVmsRequestedList(List<T> vmsRequestedList) {
		this.vmsRequestedList = vmsRequestedList;
	}
        
         /**
         * 
         * @param list 
         */
        public static void submitVmsRequestedList(List<? extends Vm> list) {
		getVmsRequestedList().addAll(list);
        }
        
        public static <T extends Vm> List<T> getVmsQuarantinedList(){
            return (List<T>)vmsQuarantinedList;
        }
        
        protected <T extends Vm> void setVmsQuarantinedList(List<T> vmsQuarantinedList){
            this.vmsQuarantinedList = vmsQuarantinedList;
        }
        
          /**
         * 
         * @param <T>
         * @return 
         */
	public static <T extends Vm> List<T> getVmsStartedList() {
		return (List<T>) vmsStartedList;
	}

	/**
	 * Sets the vms Started list.
	 * 
	 * @param <T> the generic type
	 * @param vmsCreatedList the vms created list
	 */
	protected <T extends Vm> void setVmsStartedList(List<T> vmsStartedList) {
		this.vmsStartedList = vmsStartedList;
	}
        
        /**
         * 
         * @param <T>
         * @return 
         */
        public static <T extends Vm> List<T> getVmsDestroyedList(){
            return (List<T>) vmsDestroyedList;
        }
        
        /**
         * 
         * @param <T>
         * @param vmDestroyedList 
         */
        protected <T extends Vm> void setVmsDestroyedList (List<T> vmDestroyedList) {
            this.vmsDestroyedList = vmDestroyedList;
        }
        
        /**
         * 
         * @param list 
         */
	public void submitVmsDestroyedList(List<? extends Vm> list) {
		getVmsDestroyedList().addAll(list);
        }
        
      

	/**
	 * Gets the vms requested.
	 * 
	 * @return the vms requested
	 */
	protected int getVmsRequested() {
		return vmsRequested;
	}
        
	/**
	 * Sets the vms requested.
	 * 
	 * @param vmsRequested the new vms requested
	 */
	protected void setVmsRequested(int vmsRequested) {
		this.vmsRequested = vmsRequested;
	}
        
       
	/**
	 * Gets the vms acks.
	 * 
	 * @return the vms acks
	 */
	protected int getVmsAcks() {
		return vmsAcks;
	}

	/**
	 * Sets the vms acks.
	 * 
	 * @param vmsAcks the new vms acks
	 */
	protected void setVmsAcks(int vmsAcks) {
		this.vmsAcks = vmsAcks;
	}

	/**
	 * Increment vms acks.
	 */
	protected void incrementVmsAcks() {
		vmsAcks++;
	}

	/**
	 * Gets the datacenter ids list.
	 * 
	 * @return the datacenter ids list
	 */
	protected List<Integer> getDatacenterIdsList() {
		return datacenterIdsList;
	}

	/**
	 * Sets the datacenter ids list.
	 * 
	 * @param datacenterIdsList the new datacenter ids list
	 */
	protected void setDatacenterIdsList(List<Integer> datacenterIdsList) {
		this.datacenterIdsList = datacenterIdsList;
	}

	/**
	 * Gets the vms to datacenters map.
	 * 
	 * @return the vms to datacenters map
	 */
	protected Map<Integer, Integer> getVmsToDatacentersMap() {
		return vmsToDatacentersMap;
	}

	/**
	 * Sets the vms to datacenters map.
	 * 
	 * @param vmsToDatacentersMap the vms to datacenters map
	 */
	protected void setVmsToDatacentersMap(Map<Integer, Integer> vmsToDatacentersMap) {
		this.vmsToDatacentersMap = vmsToDatacentersMap;
	}

	/**
	 * Gets the datacenter characteristics list.
	 * 
	 * @return the datacenter characteristics list
	 */
	protected Map<Integer, DatacenterCharacteristics> getDatacenterCharacteristicsList() {
		return datacenterCharacteristicsList;
	}

	/**
	 * Sets the datacenter characteristics list.
	 * 
	 * @param datacenterCharacteristicsList the datacenter characteristics list
	 */
	protected void setDatacenterCharacteristicsList(
			Map<Integer, DatacenterCharacteristics> datacenterCharacteristicsList) {
		this.datacenterCharacteristicsList = datacenterCharacteristicsList;
	}

	/**
	 * Gets the datacenter requested ids list.
	 * 
	 * @return the datacenter requested ids list
	 */
	protected List<Integer> getDatacenterRequestedIdsList() {
		return datacenterRequestedIdsList;
	}

	/**
	 * Sets the datacenter requested ids list.
	 * 
	 * @param datacenterRequestedIdsList the new datacenter requested ids list
	 */
	protected void setDatacenterRequestedIdsList(List<Integer> datacenterRequestedIdsList) {
		this.datacenterRequestedIdsList = datacenterRequestedIdsList;
	}

        /**
         * Gets data center used list
         * @return 
         */
        protected List<Integer> getDatacenterUsedIdsList() {
		return datacenterUsedIdList;
	}
        
        /**
         * Sets data center used list
         * @param datacenterUsedIdList 
         */
        protected void setDatacenterUsedIdList(List<Integer> datacenterUsedIdList) {
            this.datacenterUsedIdList = datacenterUsedIdList;
        }
        
        /**
         * Gets the name of Cloudlet Scheduler
         * @return 
         */
       public int getCloudletSchedulerName(){
           return cloudletSchedulerName;
       }
       
       /**
        * Sets the name of Cloudlet Scheduler
        * @param cloudletSchedulerName 
        */
       public void setCloudletSchedulerName(int cloudletSchedulerName){
           this.cloudletSchedulerName = cloudletSchedulerName;
       }
       
       /**
        * Gets Load Admission
        * @return 
        */
        public LoadAdmission getLoadAdmission(){
            return loadAdmission;
        }
       
        /**
         * Sets Load Admission
         * @param loadAdmission 
         */
        protected void setLoadAdmission(LoadAdmission loadAdmission){
            this.loadAdmission = loadAdmission;
        }
        
        /**
         * Gets Load Balancing method
         * @return 
         */
        public LoadBalancing getLoadBalancing(){
            return loadBalancing;
        }
        
        /**
         * Sets Load Balancing method
         * @param loadBalancing 
         */
        protected void setLoadBalancing(LoadBalancing loadBalancing){
            this.loadBalancing = loadBalancing;
        }
        
        /**
         * Gets Monitor class
         * @return 
         */
        public static Monitor getMonitor(){
            return monitor;
        }
        
        /**
         * Sets Monitor class
         * @param monitor 
         */
        protected void setMonitor(Monitor monitor){
            this.monitor = monitor;
        }
        
        /**
         * Gets Analyzer class
         * @return 
         */
        public static Analyzer getAnalyzer(){
            return analyzer;
        }
        
        /**
         * Sets Analyzer class
         * @param analyzer 
         */
        protected void setAnalyzer(Analyzer analyzer){
            this.analyzer = analyzer;
        }
        
        /**
         * Gets Planner class
         * @return 
         */
        public static Planner getPlanner(){
            return planner;
        }
        
        /**
         * Sets Planner class
         * @param planner 
         */
        protected void setPlanner(Planner planner){
            this.planner = planner;
        }
        
        /**
         * Gets Executor class
         * @return 
         */
        public static Executor getExecutor(){
            return executor;
        }
        
        /**
         * Sets Executor class
         * @param executor 
         */
        protected void setExecutor(Executor executor){
            this.executor = executor;
        }
}
