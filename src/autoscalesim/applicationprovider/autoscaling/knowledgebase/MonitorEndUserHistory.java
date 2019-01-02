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

package autoscalesim.applicationprovider.autoscaling.knowledgebase;

/**
 * MonitorEndUserHistory class is the knowledge-base component for monitoring phase of auto-scaling system.
 * This class is called at the end of monitoring phase by Monitor class to save the calculated parameters.
 */
public class MonitorEndUserHistory extends History{
    
    /* EndUser requests Number */
    private int requestsPerAllTier;
    /* EndUser requests length */
    private long requestsLengthPerTier;
    
    public MonitorEndUserHistory(int requestsPerAllTier, long requestsLengthPerTier){
        // Set Date and Time 
        super();
        this.requestsPerAllTier = requestsPerAllTier;
        this.requestsLengthPerTier = requestsLengthPerTier;
        
    }
    
     
    public double getRequestsPerAllTier(){
        return requestsPerAllTier;
    }
    
    public long getRequestsLengthPerTier(){
        return requestsLengthPerTier;
    }
    
    public void setRequestsLengthPerTier(long requestsLengthPerTier){
        this.requestsLengthPerTier = requestsLengthPerTier;
    }
    
    

}
