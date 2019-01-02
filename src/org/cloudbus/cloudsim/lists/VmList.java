/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.lists;

import static autoscalesim.applicationprovider.ApplicationProvider.getVmsDestroyedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsQuarantinedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsRequestedList;
import static autoscalesim.applicationprovider.ApplicationProvider.getVmsStartedList;
import java.util.List;

import autoscalesim.cloudprovider.Vm;
import autoscalesim.log.AutoScaleSimTags;
import java.util.ArrayList;

/**
 * VmList is a collection of operations on lists of VMs.
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmList {

	/**
	 * Return a reference to a Vm object from its ID.
	 * 
	 * @param id ID of required VM
	 * @param vmList the vm list
	 * @return Vm with the given ID, $null if not found
	 * @pre $none
	 * @post $none
	 */
	public static <T extends Vm> T getById(List<T> vmList, int id) {
		for (T vm : vmList) {
			if (vm.getId() == id) {
				return vm;
			}
		}
		return null;
	}
/////////////////////////////// MY Code   ////////////////////////////////////////////////
        public static <T extends Vm> T getbyStatus(List<T> vmList, int status){
                for (T vm : vmList){
                        if (vm.getStatus() == status){
                                return vm;
                        }
                }
                return null;
        }
        
        public static boolean getByStatus(List<Vm> vmList, int status){
            for (Vm vm : vmList){
                        if (vm.getStatus() == status){
                                return true;
                        }
                }
                return false;
        }
        
        public static <T extends Vm> T getByRequestTime(List<T> vmList, double requestTime) {
		for (T vm : vmList) {
			if (vm.getRequestTime()== requestTime) {
				return vm;
			}
		}
		return null;
	}
//////////////////////////////////////////////////////////////////
	/**
	 * Return a reference to a Vm object from its ID and user ID.
	 * 
	 * @param id ID of required VM
	 * @param userId the user ID
	 * @param vmList the vm list
	 * @return Vm with the given ID, $null if not found
	 * @pre $none
	 * @post $none
	 */
	public static <T extends Vm> T getByIdAndUserId(List<T> vmList, int id, int userId) {
		for (T vm : vmList) {
			if (vm.getId() == id && vm.getUserId() == userId) {
				return vm;
			}
		}
		return null;
	}

        public static ArrayList <Vm> getVmsList(){
            ArrayList <Vm> vmsList = new ArrayList<>();
            for(Vm vm: getVmsStartedList()){
                    vmsList.add(vm);
            }

            for(Vm vm: getVmsRequestedList()){
                    vmsList.add(vm);
            }

            for(Vm vm: getVmsQuarantinedList()){
                    vmsList.add(vm);
            }

            for(Vm vm : getVmsDestroyedList()){
                    vmsList.add(vm);
            }
            
            return vmsList;
        }
        
        /**
         * Get  
         * @return 
         */
        public static ArrayList <Vm> getVmsList(int[] status){
            ArrayList <Vm> rawVmsList = getVmsList();
            ArrayList <Vm> vmsList = new ArrayList<>();
            // Filter by Status
            for(int vmStatus : status){
                for(Vm vm: rawVmsList){
                    if(vm.getStatus() == vmStatus)
                        vmsList.add(vm);
                }
            }
            
            return vmsList;
        }
        
        public static ArrayList<Vm> getAvailableVmListToLoadBalancing(){
            // create a list of on line-vms (Vm and Ids)
            ArrayList<Vm> vmsList = new ArrayList<Vm>();
            for(int i = 0; i < getVmsStartedList().size(); i++){
                vmsList.add(getVmsStartedList().get(i));
            }
            
            return vmsList;
        }
        
        /**
         * Get  
         * @return 
         */
        public static ArrayList <Vm> getVmsList(int[] status, int[] config){
            ArrayList <Vm> rawVmsList = getVmsList(status);
            ArrayList <Vm> vmsList = new ArrayList<>();
            // Filter by Config
            for(int vmConfig : config){
                for(Vm vm: rawVmsList){
                    if(vm.getConfigurationType() == vmConfig)
                        vmsList.add(vm);
                }
            }
            return vmsList;
        }
        
        public static ArrayList <Vm> getVmsList(int[] status, int[] config, int[] purchase){
            ArrayList <Vm> rawVmsList = getVmsList(status, config);
            ArrayList <Vm> vmsList = new ArrayList<>();
            // Filter by Purchase
            for(int vmPurchase : purchase){
                for(Vm vm: rawVmsList){
                    if(vm.getPurchaseType() == vmPurchase)
                        vmsList.add(vm);
                }
            }
            return vmsList;
        }
        
        public static ArrayList <Vm> getVmsList(int[] status, int[] config, int[] purchase, int[] tier){
            ArrayList <Vm> rawVmsList = getVmsList(status, config, purchase);
            ArrayList <Vm> vmsList = new ArrayList<>();
            // Filter by Tier
            for(int tierX : tier){
                for(Vm vm: rawVmsList){
                    if(vm.getTier()== tierX)
                        vmsList.add(vm);
                }
            }
            return vmsList;
        }
        
        /**
         * Get  
         * @return 
         */
        public static ArrayList <Vm> getOnDemandVmsList(){
            int[] purchase = new int[] {AutoScaleSimTags.VM_PURCHASE_ON_DEMAND};
            ArrayList <Vm> vmsList = getVmsList(AutoScaleSimTags.VM_STATUS_LIST, AutoScaleSimTags.VM_CONFIG_LIST, purchase);
            
            return vmsList;
        }
        
        public static ArrayList <Vm> getOnDemandVmsList(int[] status){
            int[] purchase = new int[] {AutoScaleSimTags.VM_PURCHASE_ON_DEMAND};
            ArrayList <Vm> vmsList = getVmsList(status, AutoScaleSimTags.VM_CONFIG_LIST, purchase);
                
            return vmsList;
        }
        
        public static ArrayList <Vm> getOnDemandVmsList(int[] status, int[] config){
            int[] purchase = new int[] {AutoScaleSimTags.VM_PURCHASE_ON_DEMAND};
            ArrayList <Vm> vmsList = getVmsList(status, config, purchase);
                
            return vmsList;
        }
        
        public static ArrayList <Vm> getOnDemandVmsList(int[] status, int[] config, int[] tier){
            int[] purchase = new int[] {AutoScaleSimTags.VM_PURCHASE_ON_DEMAND};
            ArrayList <Vm> rawVmsList = getVmsList(status, config, purchase);
            ArrayList <Vm> vmsList = new ArrayList<>();
            for (Vm vm : rawVmsList){
                for (int tierX : tier){
                    if(vm.getTier() == tierX){
                        vmsList.add(vm);
//                        break;
                    }
                }
            }
            
            return vmsList;
        }
        
        public static ArrayList<Vm> getVmListFilterByTier(ArrayList<Vm> rawVmList, int[] tier){
            ArrayList<Vm> vmsList = new ArrayList<Vm>();
            for(Vm vm : rawVmList){
                for(int tierX: tier){
                    if (vm.getTier() == tierX){
                        vmsList.add(vm);
                        break;
                    }
                }
            }
            
            return vmsList;
        }
}
