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

import static autoscalesim.applicationprovider.ApplicationProvider.getCloudletReceivedList;
import static autoscalesim.applicationprovider.ApplicationProvider.lastCloudletReceivedId;
import static autoscalesim.applicationprovider.ApplicationProvider.sumRequestsLengthPerTier;
import static autoscalesim.applicationprovider.ApplicationProvider.sumRequestsPerAllTier;
import autoscalesim.enduser.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * LoadAdmission class is where the incoming load is handled. This class admits the cloudlets.
 */
public class LoadAdmission {
    
    public void admission(Cloudlet cloudletReceived){

        cloudletReceived.setCloudletId(++lastCloudletReceivedId);
        cloudletReceived.setFirstSubmissionTime(CloudSim.clock());

        getCloudletReceivedList().add(cloudletReceived);
        // sensors
        sumRequestsPerAllTier++;
        sumRequestsLengthPerTier += cloudletReceived.getCloudletLength();
        
    }
}
