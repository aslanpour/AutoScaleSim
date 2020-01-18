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
import static autoscalesim.applicationprovider.ApplicationProvider.cloudletSchedulerName;
import autoscalesim.cloudprovider.Vm;
import java.util.ArrayList;
import java.util.List;
import autoscalesim.cloudprovider.CloudletScheduler;
import autoscalesim.cloudprovider.CloudletSchedulerSpaceShared;
import autoscalesim.cloudprovider.CloudletSchedulerTimeShared;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.log.DateTime;
import org.cloudbus.cloudsim.core.CloudSim;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.ExperimentalSetup.ExecutorType;
import autoscalesim.ExperimentalSetup.SurplusVMSelectionPolicy;

/**
 * Executor class is the final phase of auto-scaling process, where
 * its input it the planner decision, and
 * its action is to execute the requested action
 *
 */
public abstract class Executor {

    // During execution, the executor needs to obtain these parameters
    int executorAction;
    int provisioning;
    int deProvisioning;
    private String actionDetails;
    
    private int applicationProviderId;
    
    private String quarantinedVMsUpdaterDetails;
    public ArrayList<Vm> preparedScaleUpVmList;
    public ArrayList<Vm>  preparedScaleDownVmList;
    
    private ArrayList<ExecutorHistory> historyList;
    
    public final ExecutorType executorType;
    final ExperimentalSetup.SurplusVMSelectionPolicy surplusVMSelectionPolicy;
    protected final int COOLDOWN;
    
    public int remainedCoolDownTime;
    
    private final int maxOnDemandVm;
    private final int minOnDemandVm;
    
    private final String startUpDelayType;
    private final double BASE_DELAY_IN_VM_START_UP;
    
    private int lastVmRequestedId;
    
    /**
     * 
     * @param executorType
     * @param surplusVMSelectionPolicy
     * @param COOLDOWN
     * @param onDemandVmLimit
     * @param startUpDelayType
     * @param BASE_DELAY_IN_VM_START_UP 
     */
    public Executor(
                    final ExecutorType executorType,
                    final SurplusVMSelectionPolicy surplusVMSelectionPolicy,
                    int COOLDOWN,
                    int maxOnDemandVm,
                    int minOnDemandVm,
                    String startUpDelayType,
                    double BASE_DELAY_IN_VM_START_UP){
        
        // executor needs to obtain these parameters
        executorAction = AutoScaleSimTags.ACT_NO_ACTION;
        provisioning = 0;
        deProvisioning = 0; 
        
        setActionDetails("Executor Action details: ");
        
        quarantinedVMsUpdaterDetails = "Updater details: ";
        preparedScaleUpVmList = new ArrayList<>();
        preparedScaleDownVmList = new ArrayList<>();
        
        setHistoryList(new ArrayList<ExecutorHistory>());
        
        this.executorType = executorType;
        this.surplusVMSelectionPolicy = surplusVMSelectionPolicy;
        this.COOLDOWN = COOLDOWN;
        
        remainedCoolDownTime = -1;
        
        this.maxOnDemandVm = maxOnDemandVm;
        this.minOnDemandVm = minOnDemandVm;
        this.startUpDelayType = startUpDelayType;
        this.BASE_DELAY_IN_VM_START_UP = BASE_DELAY_IN_VM_START_UP;
        
        lastVmRequestedId = -1;
    }
    
    /**
     * Executes the planner's decision
     * @return 
     */
    public abstract ArrayList execution();
    
    /**
     * Prepares a list of reserved VMs to be requested from cloud provider
     * @param vms
     * @param purchaseType
     * @param configurationType
     * @param tier
     * @return 
     */
    public List<Vm> preparationReservedVmRequest(int vms , int purchaseType, int configurationType, int tier) {
        List<Vm> scaleUpVmList = new ArrayList<>();

        //VM configuration
        double mips = AutoScaleSimTags.VM_MIPS[configurationType];
        int pesNumber = AutoScaleSimTags.VM_PES[configurationType]; 
        int ram = AutoScaleSimTags.VM_RAM[configurationType]; 
        long bw = AutoScaleSimTags.VM_BW;
        long size = AutoScaleSimTags.VM_SIZE; 


        double delayInStartUp = BASE_DELAY_IN_VM_START_UP;
        if (startUpDelayType == "Dynamic") 
            delayInStartUp = VmStartUpDelay.calculateVmStartUpDelay(BASE_DELAY_IN_VM_START_UP, 
                                                                    startUpDelayType, 
                                                                    configurationType);

        double requestTime;
        if(CloudSim.clock() < 1) // If clock is 0, It is a reserved VM!
            requestTime = CloudSim.clock() - delayInStartUp;
        else
            requestTime = CloudSim.clock();

        String vmm = "Xen"; 

        //create VMs
        Vm vm;
        addActionDetails(DateTime.timeStamp() + "Act Details -- Requests reserved VM:");
        for(int i = 0; i < vms; i++){

            lastVmRequestedId++;
            int vmId = lastVmRequestedId;
            CloudletScheduler cloudletScheduler;
            if(cloudletSchedulerName == AutoScaleSimTags.TimeShared)
                cloudletScheduler = new CloudletSchedulerTimeShared();
            else
                cloudletScheduler = new CloudletSchedulerSpaceShared();

            vm = new Vm(vmId, getApplicationProviderId() , mips, pesNumber, ram, bw, size,vmm, cloudletScheduler,
                        requestTime, configurationType, purchaseType, delayInStartUp, tier);
            //for creating a VM with a space shared scheduling policy for cloudlets:
            //vm = (vmId, ASPId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

            vm.setStatus(Vm.Requested);

            scaleUpVmList.add(vm);
            addActionDetails(" VM#" + vm.getId());
        }

        return scaleUpVmList;
    }
    
    /**
     * Prepares a list of reserved VMs to be requested from cloud provider
     * @param scaleUpVmList
     * @param vms
     * @param purchaseType
     * @param configurationType
     * @param tier
     * @return 
     */
    public List<Vm> preparationOnDemandVmRequest(List<Vm> scaleUpVmList
                                                , int vms , int purchaseType
                                                , int configurationType
                                                , int tier) {
        //VM configuration
        double mips = AutoScaleSimTags.VM_MIPS[configurationType];
        int pesNumber = AutoScaleSimTags.VM_PES[configurationType]; 
        int ram = AutoScaleSimTags.VM_RAM[configurationType]; 
        long bw = AutoScaleSimTags.VM_BW;
        long size = AutoScaleSimTags.VM_SIZE; 


        double delayInStartUp = BASE_DELAY_IN_VM_START_UP;
        if (startUpDelayType == "Dynamic") 
            delayInStartUp = VmStartUpDelay.calculateVmStartUpDelay(BASE_DELAY_IN_VM_START_UP, 
                                                                    startUpDelayType, 
                                                                    configurationType);

        double requestTime;
        if(CloudSim.clock() < 1) // If clock is 0, It is a reserved VM!
            requestTime = CloudSim.clock() - delayInStartUp;
        else
            requestTime = CloudSim.clock();

        String vmm = "Xen"; 

        //create VMs
        Vm vm;
        addActionDetails(DateTime.timeStamp() + "Requests on demand VM:");
        for(int i = 0; i < vms; i++){

                lastVmRequestedId++;
                int vmId = lastVmRequestedId;
                // Space or Time
                CloudletScheduler cloudletScheduler;
                if(cloudletSchedulerName == AutoScaleSimTags.TimeShared)
                    cloudletScheduler = new CloudletSchedulerTimeShared();
                else
                    cloudletScheduler = new CloudletSchedulerSpaceShared();

                vm = new Vm(vmId,getApplicationProviderId() , mips, pesNumber, ram, bw, size,vmm, cloudletScheduler,
                            requestTime, configurationType, purchaseType, delayInStartUp, tier);
                //for creating a VM with a space shared scheduling policy for cloudlets:
                //vm = (vmId, ASPId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

                vm.setStatus(Vm.Requested); // set status

                scaleUpVmList.add(vm);
                addActionDetails(" VM#" + vm.getId());
        }

        return scaleUpVmList;
    }
    
    /**
     * Updates the status of VMs in quarantine list
     * @return 
     */
    public List<Vm> quarantinedVMsUpdater(){
        return null;
    };
    
    
        
    /**
     * Gets the Id of application provider
     * @return 
     */
    protected int getApplicationProviderId(){
        return applicationProviderId;
    }
    
    /**
     * Sets the Id of application provider
     * @param applicationProviderId 
     */
    public void setApplicationProviderId(int applicationProviderId){
        this.applicationProviderId = applicationProviderId;
    }
    
    /**
     * Gets the details of the action
     * @return 
     */
    public String getActionDetails(){
        return actionDetails;
    }
    
    /**
     * Sets the detail of the action
     * @param actionDetails 
     */
    public void setActionDetails(String actionDetails){
        this.actionDetails = actionDetails;
    }
    
    /**
     * Adds information to action detail
     * @param actionDetails 
     */
    public void addActionDetails (String actionDetails){
        this.actionDetails += actionDetails;
    }
    
    /**
     * Gets the details of Quarantined VMs status
     * @return 
     */
    public String getQuarantinedVMsUpdaterDetails(){
        return quarantinedVMsUpdaterDetails;
    }
    
    /**
     * Sets the details of Quarantined VMs status
     * @param quarantinedVMsUpdaterDetails 
     */
    public void setQuarantinedVMsUpdaterDetails(String quarantinedVMsUpdaterDetails){
        this.quarantinedVMsUpdaterDetails = quarantinedVMsUpdaterDetails;
    }
    
    /**
     * Adds information to the Quarantined VMs updater
     * @param details 
     */
    protected void addToQuarantinedVMsUpdaterDetails(String details){
        this.quarantinedVMsUpdaterDetails += details;
    }

    public int getMaxOnDemandVm() {
        return maxOnDemandVm;
    }

    
    public int getMinOnDemandVm() {
        return minOnDemandVm;
    }
    
    
    /**
     * Gets the History of execution phase
     * @return 
     */
    public ArrayList<ExecutorHistory> getHistoryList(){
        return historyList;
    }
    
    /**
     * Sets the History of execution phase
     * @param historyList 
     */
    protected void setHistoryList(ArrayList<ExecutorHistory> historyList){
        this.historyList = historyList;
    }
    
    /**
     * Returns the size of History for execution phase
     * @return 
     */
    public int sizeHistory(){
        return getHistoryList().size();
    }
    
    /**
     * Returns the latest record of execution history
     * @return 
     */
    public ExecutorHistory latestHistoryRec(){
        return getHistoryList().get(sizeHistory()-1);
    }
}
