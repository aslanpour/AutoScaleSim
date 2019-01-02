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

import static autoscalesim.applicationprovider.ApplicationProvider.getPlanner;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsDestroyedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsRequestedList;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import autoscalesim.cloudprovider.Vm;
import autoscalesim.log.DateTime;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import static org.cloudbus.cloudsim.lists.VmList.getOnDemandVmsList;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
import autoscalesim.ExperimentalSetup.ExecutorType;
import autoscalesim.ExperimentalSetup.SurplusVMSelectionPolicy;
/**
 * ExecutorSimple class executes planner decision directly without any change, meaning that it
 * receives the planner decision and requests from cloud provider to add or remove resources.
 */
public class ExecutorSimple extends Executor{
    
    /**
     * 
     * @param executorType
     * @param surplusVMSelectionPolicy
     * @param COOLDOWN
     * @param onDemandVmLimit
     * @param startUpDelayType
     * @param BASE_DELAY_IN_VM_START_UP 
     */
    public  ExecutorSimple(
                    final ExecutorType executorType,
                    final SurplusVMSelectionPolicy surplusVMSelectionPolicy,
                    int COOLDOWN,
                    int onDemandVmLimit,
                    String startUpDelayType,
                    double BASE_DELAY_IN_VM_START_UP){
        
        
        super(
                     executorType,
                     surplusVMSelectionPolicy,
                     COOLDOWN,
                     onDemandVmLimit,
                     startUpDelayType,
                     BASE_DELAY_IN_VM_START_UP);
    }
    
    /**
     * Executes the planner's decision
     * @return 
     */
    @Override
    public ArrayList execution(){
        
        // executor needs to obtain these parameters
        executorAction = AutoScaleSimTags.ACT_NO_ACTION;
        provisioning = 0;
        deProvisioning = 0; 
        setActionDetails("Action details: ");
        
        // Inputs
        PlannerHistory plannerHistory = getPlanner().latestHistoryRec();
        int commandPerTier = plannerHistory.getDecision();
        int requiredVms = plannerHistory.getVms();
        int purchaseType = plannerHistory.getPurchase();
        int tierType = plannerHistory.getTierType();
        int configurationType = plannerHistory.getConfiguration();
        //
        List<Vm> scaleUpVmList =new ArrayList<Vm>();
        List<Vm> scaleDownVmList =new ArrayList<Vm>();
        
            // Scale Up
            if(commandPerTier == AutoScaleSimTags.PLANNER_SCALING_UP){
                if(remainedCoolDownTime < 0){
                    int onDemandVmCount = getOnDemandVmsList(new int[]{Vm.Requested, Vm.Started}
                            ,new int[] {AutoScaleSimTags.VM_CONFIG_T2MICRO, AutoScaleSimTags.VM_CONFIG_T2SMALL,
                                        AutoScaleSimTags.VM_CONFIG_T2MEDIUM, AutoScaleSimTags.VM_CONFIG_T2LARGE}).size();
                
                    if(onDemandVmCount >= onDemandVmLimit)
                        executorAction = AutoScaleSimTags.ACT_UP_LIMIT;
                    else{
                        if (onDemandVmCount + requiredVms > onDemandVmLimit)
                            requiredVms = onDemandVmLimit - onDemandVmCount;
                        
                        scaleUpVmList = preparationOnDemandVmRequest(scaleUpVmList
                                                            ,requiredVms
                                                            , purchaseType
                                                            , configurationType
                                                            , AutoScaleSimTags.WEB_TIER);
                        
                        provisioning += requiredVms;
                        executorAction = AutoScaleSimTags.ACT_UP_NEW;
                        remainedCoolDownTime = COOLDOWN * AutoScaleSimTags.aMinute;
                        
                    }
                    
                }else{
                    executorAction = AutoScaleSimTags.ACT_UP_COOLDOWN;
                }
                
            // Scale Down
            }else if (commandPerTier == AutoScaleSimTags.PLANNER_SCALING_DOWN){
                // prepare condidate vms
                ArrayList<Vm> condidateVmList = getOnDemandVmsList(new int[] {Vm.Requested, Vm.Started}
                                                            , new int[] {configurationType});   
                
                boolean isCondidateVm = !condidateVmList.isEmpty();
                if(isCondidateVm == true){
                 
                    // reducing scaling step, because of lack of candidate vms
                    if(condidateVmList.size() < requiredVms){
                        requiredVms = condidateVmList.size();
                    }
                    
                    // Choose Surplus Vms to be destroyed
                    ArrayList<Integer> exceptList = new ArrayList<>();
                    for(int j = 0; j < requiredVms; j++){
                        Vm surplusVM = SurplusVmSelection.policy(surplusVMSelectionPolicy, condidateVmList
                                                                                         , exceptList);
                        //has been selected any candidate vm?
                        if(surplusVM.getStatus() != -1){
                            scaleDownVmList.add(surplusVM);
                            
                            //Executor logs
                            deProvisioning++;
                            
                            if(surplusVM.getStatus() == Vm.Requested){
                                executorAction = AutoScaleSimTags.ACT_D_DESTROY_FROM_REQ;
                                addActionDetails(DateTime.timeStamp() 
                                                    + " VM#" + surplusVM.getId() + " destroyed from requested list");
                            }else if (surplusVM.getStatus() == Vm.Started){
                                executorAction = AutoScaleSimTags.ACT_D_DESTROY_FROM_START;
                                addActionDetails(DateTime.timeStamp()
                                                        + " VM#" + surplusVM.getId() + " destroyed from started list");
                            }else{
                                errorChecker = true;
                                error = "error- Vm Selection  - selected Vm to be down is not requested or started";
                            }
                                
                            
                            exceptList.add(surplusVM.getId());
                        }else{
                            Log.printLine("error- Vm Selection  - did not select a vm");
                            errorChecker = true;
                            error = "error- Vm Selection  - did not select a vm";
                        }
                    }
                      
                }else{ // there is not any condidate Vm
                    executorAction = AutoScaleSimTags.ACT_D_LIMIT;
                }
            }
        
        //
        ArrayList executionResult = new ArrayList();
        executionResult.add(scaleUpVmList.size());
        executionResult.add(scaleUpVmList);
        executionResult.add(scaleDownVmList.size());
        executionResult.add(scaleDownVmList);
        
        // Save Exxecuter info 
        ExecutorHistory executorHistory = new ExecutorHistory(executorAction, provisioning, deProvisioning);
        getHistoryList().add(executorHistory);
        
//        return remainedSurplusVmList;
        return executionResult;
    }
    
    
}
