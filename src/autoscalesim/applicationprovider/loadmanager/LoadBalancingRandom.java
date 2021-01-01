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
import java.util.Random;
import org.cloudbus.cloudsim.lists.CloudletList;

/**
 * LoadBalancingRandom class dispatches the cloudlets by randomly.
 */
public class LoadBalancingRandom extends LoadBalancing{

    
    public static int loadDispatcherVmId = -1;

    /**
     * Dispatches arrived users' requests among available VMs randomly.
     * @param availableVmList 
     */
    @Override
    public void dispatchingNewRequests(ArrayList<Vm> availableVmList){
        // create a list of on line-vms (Ids)
        ArrayList<Integer> vmIdList = new ArrayList<Integer>();
        for(Vm vm: availableVmList){
            vmIdList.add(vm.getId());
        }

        

        // choosing a Vm for each cloudlet received
        for(int i = 0; i< getCloudletReceivedList().size(); i++){
            Random randomId = new Random();
            int rndId = randomId.nextInt(vmIdList.size());
            getCloudletReceivedList().get(i).setVmId(vmIdList.get(rndId));
            
        }
        
    }
    
    /**
     * Dispatches cloudlets that have been canceled due to scale down execution by auto-scaler.
     * This method dispatches each canceled cloudlet from getCloudletCancelledList to a VM of availableVmList
     * in a Round Robin manner.
     * @param availableVmList 
     */
    @Override
    public void dispatchingCanceledRequests(ArrayList<Vm> availableVmList){
        // create a list of on line-vms (Ids)
        ArrayList<Integer> vmIdList = new ArrayList<Integer>();
        for(Vm vm: availableVmList){
            vmIdList.add(vm.getId());
        }

        

        // choosing a Vm for each cloudlet received
        for(int i = 0; i< getCloudletCancelledList().size(); i++){
            Random randomId = new Random();
            int rndId = randomId.nextInt(vmIdList.size());
            getCloudletCancelledList().get(i).setVmId(vmIdList.get(rndId));
            
        }
            
       
    }
    
}
