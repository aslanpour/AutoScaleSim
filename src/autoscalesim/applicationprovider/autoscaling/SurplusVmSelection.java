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

import autoscalesim.enduser.Cloudlet;
import autoscalesim.cloudprovider.CloudletSchedulerTimeShared;
import autoscalesim.log.AutoScaleSimTags;
import java.util.List;
import autoscalesim.log.DateTime;
import autoscalesim.cloudprovider.Vm;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
import java.util.ArrayList;
import java.util.Random;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import autoscalesim.ExperimentalSetup.SurplusVMSelectionPolicy;
/**
 * SurplusVmSelectionPolioy class is called if the executor wants to execute an scale-down decision.
 * In this situation, this class selects a VM as surplus. There are some policies here.
 */
public class SurplusVmSelection {
       
    /**
     * Selects and runs the indicated policy for selecting a surplus VM
     * @param policy
     * @param condidateVmList
     * @param exceptList
     * @return 
     */
    
    protected static Vm policy(final SurplusVMSelectionPolicy policy, ArrayList<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedVm = new Vm();
        
        switch(policy){
            
            case RANDOM: selectedVm = random(condidateVmList, exceptList);
            break;
            case THE_OLDEST: selectedVm = theOldest(condidateVmList, exceptList);
            break;
            case THE_YOUNGEST: selectedVm = theYoungest(condidateVmList, exceptList);
            break;
            case CLOUDLET_AWARE: selectedVm = cloudletsAware(condidateVmList, exceptList);
            break;
            case LOAD_AWARE: selectedVm = loadAware(condidateVmList, exceptList);
            break;
            case COST_AWARE_SIMPLE: selectedVm = costAwareSimple(condidateVmList, exceptList);
            break;
            case COST_AWARE_PROFESSIONAL: selectedVm = costAwareProfessional(condidateVmList, exceptList);
            break;
            default:
                errorChecker = true;
                error += "Error in Analyzer class- resource aware analayzer - surplus Vm selection policy was not found";
                Log.printLine("Error in Analyzer class- resource aware analayzer - surplus Vm selection policy was not found");
                break;
        }
        
        return selectedVm;
    }
    
    
        
    /**
     * Selects a surplus VM in a random way
     * @param condidateVmList
     * @param exceptList
     * @return 
     */
    private static Vm random(List<Vm> condidateVmList, ArrayList<Integer> exceptList){

        Vm selectedVm = condidateVmList.get(0);
        do{
            Random random = new Random();
        
            int randomIndex = random.nextInt(condidateVmList.size());
            selectedVm = condidateVmList.get(randomIndex);
        }while(exceptList.contains(selectedVm.getId()));
        
        
        return selectedVm;
    }
    
       /**
     * Selects the oldest VM
     * @param condidateVmList
     * @param exceptList
     * @return 
     */
    private static Vm theOldest(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedvm = condidateVmList.get(0);
        double oldest = Double.MAX_VALUE;

        for(Vm vm: condidateVmList){
            if(vm.getRequestTime() < oldest && !exceptList.contains(vm.getId())){
                oldest = vm.getRequestTime();
                selectedvm = vm;
            }
        }
        return selectedvm;
    }
    
    /**
     * Select Vms By latest Request Time among On-Demand Vms
     * Priority by Selected Config.
     * Last Up First Down
     * The new Vm, should be down
     * @param vmList on demand list
     * @param vmsCount
     * @return 
     */
    private static Vm theYoungest(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedvm = condidateVmList.get(0);
        double youngest = Double.MIN_VALUE;

        for(Vm vm: condidateVmList){
                if(vm.getRequestTime() > youngest && !exceptList.contains(vm.getId())){
                    youngest = vm.getRequestTime();
                    selectedvm = vm;
                }
        }
        
        return selectedvm;
    }
    
    
    
    /**
     * Chooses Vm by minimum running cloudlet, to reduce cloudlet cancellation criteria
     * @param vmList
     * @param vmsCount
     * @return 
     */
    private static Vm cloudletsAware(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedVm = condidateVmList.get(0);
        double minRunningCloudlets = Integer.MAX_VALUE;

        for(int i = 0; i < condidateVmList.size(); i++){
            Vm vm = condidateVmList.get(i);
            double runningCloudlets = vm.getCloudletScheduler().runningCloudlets();

            if(runningCloudlets < minRunningCloudlets && !exceptList.contains(vm.getId())){
                minRunningCloudlets = runningCloudlets;
                selectedVm = vm;
            }
        }
        return selectedVm;
    }
      
   
    /**
     * Chooses a vm with minimum remained load
     * @param vmList
     * @return 
     */
    private static Vm loadAware(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedVm = condidateVmList.get(0);
        double minLoad = Integer.MAX_VALUE;

        for(int i = 0; i < condidateVmList.size(); i++){
            Vm vm = condidateVmList.get(i);

                double vmRemainedLoad = 0;
                for(ResCloudlet resCloudlet :((CloudletSchedulerTimeShared)vm.getCloudletScheduler()).getCloudletExecList()){
                    Cloudlet cloudlet = resCloudlet.getCloudlet();
                    double length = cloudlet.getCloudletLength() * cloudlet.getNumberOfPes();
                    double ranTime = (CloudSim.clock() - cloudlet.getSubmissionTime());
                    double cloudletRemainedLength;
                    cloudletRemainedLength = length - (ranTime * (vm.getMips() * vm.getNumberOfPes()));
                    /* A Vm by 2 core can not execute a cloudlet by 1 core, sooner than running by 1 core */
                    //If a cloudlet by 1 core is running in a vm by 2 core, so ran time calculates just by cloudlet cores
                    if(vm.getNumberOfPes() > cloudlet.getNumberOfPes()){
                        cloudletRemainedLength = length - (ranTime * (vm.getMips() * cloudlet.getNumberOfPes()));
                    }
                    
                    vmRemainedLoad += cloudletRemainedLength;
                }
                
                if(vmRemainedLoad < minLoad && !exceptList.contains(vm.getId())){
                    minLoad = vmRemainedLoad;
                    selectedVm = vm;
                }
        }
        return selectedVm;
    }
        
    
    
    /**
     * Selects the surplus VM in a cost-saving way
     * @param condidateVmList
     * @param exceptList
     * @return 
     */    
    private static Vm costAwareSimple(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        // har kodam ke az akharin saatash bishtarin estefade shode entekhab mishavad
        Vm selectedVm = condidateVmList.get(0);
        double maxPassedTimeFromLastHour = Integer.MIN_VALUE;
        for (Vm vm : condidateVmList) {
            if(exceptList.contains(vm.getId()))
                continue;
            
            double passedTimeFromLastHour;
            double availabletime = CloudSim.clock() - vm.getRequestTime();
            
            passedTimeFromLastHour = availabletime % AutoScaleSimTags.anHour;
            if(passedTimeFromLastHour == 0)
                passedTimeFromLastHour = AutoScaleSimTags.anHour;
            
            // agar vm req dar 0 bood, in vm hamin hala request shode va yek saat az zamanash baghi mande
            if(availabletime == 0)
                passedTimeFromLastHour = 0;
            
             if (passedTimeFromLastHour > maxPassedTimeFromLastHour){
                 maxPassedTimeFromLastHour = passedTimeFromLastHour;
                 selectedVm = vm;
             }
             // agar halate barabar bod va in vm req bood in rntekhab shavad, chon dar halate takhir va cloudlet ham nadrad
//             }else if (passedTimeFromLastHour == maxPassedTimeFromLastHour)
//                 if(vm.getStartTime() == -1)
//                     selectedVm = vm;
                 
        }
        return selectedVm;
    }
    
    /**
     * Selects the VM in a cost-saving and load-aware way
     * @param condidateVmList
     * @param exceptList
     * @return 
     */
    private static Vm costAwareProfessional(List<Vm> condidateVmList, ArrayList<Integer> exceptList){
        Vm selectedVm = condidateVmList.get(0);
        double maxTime = Integer.MIN_VALUE;
        double cpuUtilization = Integer.MAX_VALUE;
        for (Vm vm : condidateVmList) {
            if(exceptList.contains(vm.getId()))
                continue;
            
            double availableTime = DateTime.tick()- vm.getRequestTime();
            double passedSecondsFromLastHour = availableTime % AutoScaleSimTags.anHour;

            if(passedSecondsFromLastHour == 0){// Excaxtly X hour(s)
                passedSecondsFromLastHour = AutoScaleSimTags.anHour; 
            }

            if(passedSecondsFromLastHour > maxTime){
                cpuUtilization = (vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) /
                        (vm.getMips() * vm.getNumberOfPes()))
                        * 100;
                maxTime = passedSecondsFromLastHour;
                selectedVm = vm;
            }else if (passedSecondsFromLastHour == maxTime){
                double vmCPUUtilization = (vm.getTotalUtilizationOfCpuMips(CloudSim.clock()) /
                        (vm.getMips() * vm.getNumberOfPes()))
                        * 100;
                if(vmCPUUtilization < cpuUtilization){
                    cpuUtilization = vmCPUUtilization;
                    selectedVm = vm;
                }
            }
        }
        
        return selectedVm;
    }
    
    
        
}
