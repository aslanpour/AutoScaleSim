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

import autoscalesim.cloudprovider.Vm;
import java.util.ArrayList;

/**
 * LoadBalancing class dispatch cloudlets between VMs.
 * 
 */
public abstract class LoadBalancing {
    
    /**
     * getCloudletReceivedList
     */
    public abstract void dispatchingNewRequests(ArrayList<Vm> availableVmList);
    
    /**
     * getCloudletCancelledList
     */
    public abstract void dispatchingCanceledRequests(ArrayList<Vm> availableVmList);
    
    
    
}
