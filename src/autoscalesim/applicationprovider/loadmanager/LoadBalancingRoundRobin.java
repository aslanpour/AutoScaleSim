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

package autoscalesim.applicationprovider.loadmanager;

import static autoscalesim.applicationprovider.ApplicationProvider.getCloudletCancelledList;
import static autoscalesim.applicationprovider.ApplicationProvider.getCloudletReceivedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsRequestedList;
import autoscalesim.cloudprovider.Vm;
import autoscalesim.enduser.Cloudlet;
import java.util.ArrayList;
import org.cloudbus.cloudsim.Log;
import java.util.List;
import org.cloudbus.cloudsim.lists.CloudletList;

/**
 * LoadBalancingRoundRobin class dispatches the cloudlets by Round Robin technique.
 */
public class LoadBalancingRoundRobin extends LoadBalancing{

    
    public static int loadDispatcherVmId = -1;

    /**
     * Dispatches arrived users' requests among available VMs in a Round Robin manner.
     * @param availableVmList 
     */
    @Override
    public void dispatchingNewRequests(ArrayList<Vm> availableVmList){
        int previousVmId = loadDispatcherVmId;
            int suitableVmId = -1;
            // create a list of on line-vms (Ids)
            ArrayList<Integer> vmIdList = new ArrayList<Integer>();
            for(Vm vm: availableVmList){
                vmIdList.add(vm.getId());
            }
            
            // sort Id
            for (int i = 0; i < vmIdList.size() - 1; i ++) {
                 int idI = vmIdList.get(i);

                 for (int j = i + 1; j < vmIdList.size(); j++) {
                     int idJ = vmIdList.get(j);
                     if(idJ < idI){
                         vmIdList.set(i, idJ);
                         vmIdList.set(j, idI);
                     }
                 }
            }
            
            // choosing a Vm for each cloudlet received
            for(int i = 0; i< getCloudletReceivedList().size(); i++){
                /* Select Next Vm */
                // If there is a bigger Id
                boolean isThereABiggerID = false;
                for(int id: vmIdList){
                    if(id > previousVmId){
                        suitableVmId = id; // next vm
                        isThereABiggerID = true;
                        break;
                    }
                }
                // if there is not a bigger Id
                if(isThereABiggerID == false){
                    if(availableVmList.size() != 0)
                        suitableVmId = vmIdList.get(0); // first vm
                    else
                        if (getVmsRequestedList().size() > 0)
                            Log.printLine("There is not any On Line Vm, some requested vms has not been ran yet");
                        else
                            Log.printLine("There is not any Vms");
                }
                getCloudletReceivedList().get(i).setVmId(suitableVmId);
                previousVmId = suitableVmId;
            }
            loadDispatcherVmId = suitableVmId;
    }
    
    /**
     * Dispatches cloudlets that have been canceled due to scale down execution by auto-scaler.
     * This method dispatches each canceled cloudlet from getCloudletCancelledList to a VM of availableVmList
     * in a Round Robin manner.
     * @param availableVmList 
     */
    @Override
    public void dispatchingCanceledRequests(ArrayList<Vm> availableVmList){
        int previousVmId = loadDispatcherVmId;
            int suitableVmId = -1;
            // create a list of on line-vms (Ids)
            ArrayList<Integer> vmIdList = new ArrayList<Integer>();
            for(Vm vm: availableVmList){
                vmIdList.add(vm.getId());
            }
            
            // sort Id
            for (int i = 0; i < vmIdList.size() - 1; i ++) {
                 int idI = vmIdList.get(i);

                 for (int j = i + 1; j < vmIdList.size(); j++) {
                     int idJ = vmIdList.get(j);
                     if(idJ < idI){
                         vmIdList.set(i, idJ);
                         vmIdList.set(j, idI);
                     }
                 }
            }
            
            // choosing a Vm for each cloudlet cancelled
            for(int i = 0; i< getCloudletCancelledList().size(); i++){
                /* Select Next Vm */
                // If there is a bigger Id
                boolean isThereABiggerID = false;
                for(int id: vmIdList){
                    if(id > previousVmId){
                        suitableVmId = id; // next vm
                        isThereABiggerID = true;
                        break;
                    }
                }
                // if there is not a bigger Id
                if(isThereABiggerID == false){
                    if(availableVmList.size() != 0)
                        suitableVmId = vmIdList.get(0); // first vm
                    else
                        if (getVmsRequestedList().size() > 0)
                            Log.printLine("There is not any On Line Vm, some requested vms has not been ran yet");
                        else
                            Log.printLine("There is not any Vms");
                }
                getCloudletCancelledList().get(i).setVmId(suitableVmId);
                previousVmId = suitableVmId;
            }
            loadDispatcherVmId = suitableVmId;
    }
    
}
