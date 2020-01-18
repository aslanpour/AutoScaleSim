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

import static autoscalesim.applicationprovider.ApplicationProvider.getVmsQuarantinedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsRequestedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsStartedList;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.cloudprovider.CloudletSchedulerTimeShared;
import autoscalesim.cloudprovider.Vm;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.log.DateTime;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import static org.cloudbus.cloudsim.lists.VmList.getVmsList;
import static autoscalesim.applicationprovider.ApplicationProvider.getPlanner;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
import autoscalesim.ExperimentalSetup.ExecutorType;
import autoscalesim.ExperimentalSetup.SurplusVMSelectionPolicy;
/**
 * ExecutorSuperProfessional class executes the announced planner's decision.
 * To this end, this class either requests from cloud provider to add or remove resources, or sometimes
 * it tries to quarantine VMs selected as surplus to be used in the future.
 */
public class ExecutorSuperProfessional extends Executor{
    
    /**
     * 
     * @param executorType
     * @param surplusVMSelectionPolicy
     * @param COOLDOWN
     * @param onDemandVmLimit
     * @param startUpDelayType
     * @param BASE_DELAY_IN_VM_START_UP 
     */
    public ExecutorSuperProfessional(
                    final ExecutorType executorType,
                    final SurplusVMSelectionPolicy surplusVMSelectionPolicy,
                    int COOLDOWN,
                    int maxOnDemandVm,
                    int minOnDemandVm,
                    String startUpDelayType,
                    double BASE_DELAY_IN_VM_START_UP){
        
        
        super( 
                     executorType,
                     surplusVMSelectionPolicy,
                     COOLDOWN,
                     maxOnDemandVm,
                     minOnDemandVm,
                     startUpDelayType,
                     BASE_DELAY_IN_VM_START_UP);
        
    }
    
   /**
    * Executes the planner's decision
    * @return 
    */
    @Override
    public ArrayList execution(){
        
        // In the end of this phase, the Executor needs to obtain these parameters
        executorAction = AutoScaleSimTags.ACT_NO_ACTION;
        provisioning = 0;
        deProvisioning = 0;
        setActionDetails("Action details: ");
        
        // Inputs parameters
        PlannerHistory plannerHistory = getPlanner().latestHistoryRec();
        int commandPerTier = plannerHistory.getDecision();
        int requiredVms = plannerHistory.getVms();
        int purchaseType = plannerHistory.getPurchase();
        int tierType = plannerHistory.getTierType();
        int configurationType = plannerHistory.getConfiguration();
        // 
        List<Vm> scaleUpVmList =new ArrayList<Vm>();
        List<Vm> scaleDownVmList =new ArrayList<Vm>();
        
        /* If decision is Scale Up */
            if(commandPerTier == AutoScaleSimTags.PLANNER_SCALING_UP){
                // (Solution 1)choose an started or requested Vm from Quarantined VMs by minimum passed time from last hour
                int quarantinedVmID = -1;
                double minPassedTimeFromLastHour = Integer.MAX_VALUE;
                for (Vm vm : getVmsQuarantinedList()){
                    if(vm.getConfigurationType() == configurationType && vm.getPurchaseType() == purchaseType){
                        // Calculate passed time from last hour for this vm
                        double passedTimeFromLastHour = 0; // it is always between 0 and 3600
                        double availableTime = CloudSim.clock() - vm.getRequestTime();
                        
                        if(availableTime >= AutoScaleSimTags.anHour){
                            passedTimeFromLastHour = availableTime % AutoScaleSimTags.anHour;
                            // if it is equal to exactly X hour, it sets to maximum past time (means 1 hour), it is not suitable
                            if(availableTime % AutoScaleSimTags.anHour == 0)
                                passedTimeFromLastHour = AutoScaleSimTags.anHour;
                        }else if (availableTime < AutoScaleSimTags.anHour)
                            passedTimeFromLastHour = availableTime;
                        // Compare passed time of this vm by min vm
                        if(passedTimeFromLastHour < minPassedTimeFromLastHour){
                            minPassedTimeFromLastHour = passedTimeFromLastHour;
                            quarantinedVmID = vm.getId();
                        }else if (passedTimeFromLastHour == minPassedTimeFromLastHour){
                            // In equal situation, between requested and started VMSs, the started VM is selected
                            if(vm.getStartTime() != -1)
                                quarantinedVmID = vm.getId();
                        }
                    }
                }
                // is there any Quarantined vm?
                if(quarantinedVmID != -1){
                // move to special vm list (requested or started list)
                    Vm vm = VmList.getById(getVmsQuarantinedList(), quarantinedVmID);
                    if(vm.getStartTime() != -1){ // if vm is started
                        vm.setStatus(Vm.Started); // move to started vm
                        getVmsStartedList().add(vm);
                        executorAction = AutoScaleSimTags.ACT_UP_MOVE_FROM_Q_TO_START;
                        
                        addActionDetails(DateTime.timeStamp() + "Move Vm#" + vm.getId() + " To Start List");
                    }else if (vm.getStartTime() == -1){ // if vm is requested
                        vm.setStatus(Vm.Requested); // move to req vm
                        getVmsRequestedList().add(vm);
                        executorAction = AutoScaleSimTags.ACT_UP_MOVE_FROM_Q_TO_REQ;
                        
                        addActionDetails(DateTime.timeStamp() + "Move Vm#" + vm.getId() + " To Requested List");
                    }
                    // Pull out the vm from quarantined list
                    getVmsQuarantinedList().remove(VmList.getById(getVmsQuarantinedList(), quarantinedVmID));
                    
                }else{ // (Solution 2) Create new Vm or (Solution 3) don't scale up
                    if(remainedCoolDownTime < 0){//By default, if cooldown is 0 (inactive), remainedCoolDownTime is always -1
                        int onDemandVmCount = getVmsList(new int[]{Vm.Requested, Vm.Started, Vm.Quarantined}
                                                            ,new int[] {configurationType}
                                                            ,new int[]{purchaseType})
                                                        .size();
                    
                        if(onDemandVmCount >= getMaxOnDemandVm())
                            executorAction = AutoScaleSimTags.ACT_UP_LIMIT;
                        else{
                            if (onDemandVmCount + requiredVms > getMaxOnDemandVm())
                                requiredVms = getMaxOnDemandVm() - onDemandVmCount;
                        
                            scaleUpVmList = preparationOnDemandVmRequest(scaleUpVmList
                                                            ,requiredVms
                                                            , purchaseType
                                                            , configurationType
                                                            , AutoScaleSimTags.WEB_TIER);
                        
                            provisioning += requiredVms;
                            executorAction = AutoScaleSimTags.ACT_UP_NEW;
                            remainedCoolDownTime = COOLDOWN;
                        }
                        
                    }else{
                        executorAction = AutoScaleSimTags.ACT_UP_COOLDOWN;
                    }
                }
                
            /* If Scale Down */
            }else if (commandPerTier == AutoScaleSimTags.PLANNER_SCALING_DOWN){
                ArrayList<Vm> condidateVmList;
                // Choose a surplus VM from req or started list
                condidateVmList = getVmsList(new int[] {Vm.Requested, Vm.Started}, new int[]{configurationType}
                                            ,new int[]{purchaseType});
                
                boolean isCondidateVm = !condidateVmList.isEmpty();
                 //if there are VMs and they are more than the min limitation
                if(isCondidateVm == true && condidateVmList.size() > getMinOnDemandVm()){
                 // Choose condidate Vms to be destroy
                    // reducing scaling step, because of the lack of candidate vms
                    if(condidateVmList.size() < requiredVms){
                        requiredVms = condidateVmList.size();
                    }
                    
                    if(condidateVmList.size() - requiredVms < getMinOnDemandVm())
                        requiredVms = condidateVmList.size()-getMinOnDemandVm();
                    
                    ArrayList<Integer> exceptList = new ArrayList<>();
                    for(int j = 0; j < requiredVms; j++){
                        Vm surplusVM = SurplusVmSelection.policy(surplusVMSelectionPolicy, condidateVmList
                                                                                         , exceptList);
                    
                        if(surplusVM.getStatus() != -1){ 
                            exceptList.add(surplusVM.getId());
                            // remove from past location
                            // tmp
                            double availableTime = CloudSim.clock() - surplusVM.getRequestTime();
                            DecimalFormat dftTmp = new DecimalFormat("###");
                            double hourUsed = Math.floor(availableTime / AutoScaleSimTags.anHour);
                            double minuteUsed = availableTime % AutoScaleSimTags.anHour / AutoScaleSimTags.aMinute;
                            // end tmp
                            // If the surplus Vm is a just requested VM
                            if(surplusVM.getStatus() == Vm.Requested){ 
                                getVmsRequestedList().remove(surplusVM);
                                executorAction = AutoScaleSimTags.ACT_D_MOVE_FROM_REQ_TO_Q;
                                
                                addActionDetails(DateTime.timeStamp() + "Move VM #" + surplusVM.getId()
                                    + " to Quarantined List, pased time: " + hourUsed + ":" +minuteUsed + " Hour");
                            // If the surplus VM is a Started VM
                            }else if (surplusVM.getStatus() == Vm.Started){
                                getVmsStartedList().remove(surplusVM);
                                executorAction = AutoScaleSimTags.ACT_D_MOVE_FROM_START_TO_Q;

                                 addActionDetails(DateTime.timeStamp() + "Move Vm#" + surplusVM.getId() 
                                                                       + " To Q List, Its Cloudlet are:"
                                        + String.valueOf(((CloudletSchedulerTimeShared)surplusVM.getCloudletScheduler())
                                        .getCloudletExecList().size()) + " Pass Time: " + dftTmp.format(hourUsed) + ":" 
                                        + dftTmp.format(minuteUsed) + " Hour");
                                    
                                Log.printLine();
                            }
                            // Move the surplus VM to Quarantined list
                            surplusVM.setStatus(Vm.Quarantined);
                            getVmsQuarantinedList().add(surplusVM);
                        }else{
                            Log.printLine("error- Vm Selection  - did not select a vm");
                            errorChecker = true;
                            error+="error- Vm Selection  - did not select a vm";
                        }
                    }
                }else{ // there is not condidate Vm
                    executorAction = AutoScaleSimTags.ACT_D_LIMIT;
                }
            }
        
        ArrayList executionResult = new ArrayList();
        executionResult.add(scaleUpVmList.size());
        executionResult.add(scaleUpVmList);
        
        List<Vm> junkVmlist = new ArrayList<>();
        
        junkVmlist = quarantinedVMsUpdater();
        
        executionResult.add(junkVmlist.size());
        executionResult.add(junkVmlist);
        
        // Save Executor info 
        ExecutorHistory executorHistory = new ExecutorHistory(executorAction, provisioning, deProvisioning);
        getHistoryList().add(executorHistory);
        
        return executionResult;
    }
    
    /**
     * Checks the billing status for quarantined VMs
     * If the renting time for a VM is 4.00h, for instance, the VM is selected to be released.
     * @return 
     */
    @Override
    public List<Vm> quarantinedVMsUpdater(){
        ArrayList<Vm> junkVmsList = new ArrayList<Vm>();
        for(Vm vm : getVmsQuarantinedList()){
            double remainedTimeFromLastHour;
            double availabletime = CloudSim.clock() - vm.getRequestTime();
            if(availabletime >= AutoScaleSimTags.anHour)
                remainedTimeFromLastHour = availabletime % AutoScaleSimTags.anHour;
            else{ // if availableTime < AutoScaleSimTags.anHour
                remainedTimeFromLastHour = availabletime;
                if(availabletime == 0) // it is a requested vm (right now)
                    remainedTimeFromLastHour = AutoScaleSimTags.anHour;
            }
            //Add for destroy
            if(remainedTimeFromLastHour == 0){
                junkVmsList.add(vm);
                deProvisioning ++;
//                autoscalesim.applicationprovider.ApplicationProvider.deProvisionedOnDemandVm++;
                
                addToQuarantinedVMsUpdaterDetails(" " + DateTime.timeStamp() 
                                            + " realased VM# " + vm.getId() + " at " + DateTime.timeStamp());
            }
        }

        return junkVmsList;
    }
        
       
}
