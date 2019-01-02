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
 * PlannerHistory class is the knowledge-base component for planning phase of auto-scaling system.
 * This class is called at the end of planning phase by Planner class to save the calculated parameters.
 */
public class PlannerHistory extends History{

    private int decision;
    private int vms;
    private int purchase;
    private int tierType;
    private int configuration;
    
    public PlannerHistory(int decision, 
                        int vms, 
                        int purchase, 
                        int tierType, 
                        int configuration){
        // set date and time
        super();
        
        this.decision = decision;
        this.vms = vms;
        this.purchase = purchase;
        this.tierType = tierType;
        this.configuration = configuration;
    }
    
    public int getDecision(){
        return decision;
    }
    
    public void setDecision(int decision){
        this.decision = decision;
    }
    
    public int getVms(){
        return vms;
    }
    
    public void setVms(int vms){
        this.vms = vms;
    }
    
    public int getPurchase(){
        return purchase;
    }
    
    public void setPurchase(int purchase){
        this.purchase = purchase;
    }
    
    public int getTierType(){
        return tierType;
    }
    
    public void setTierType(int tierType){
        this.tierType = tierType;
    }
    
    public int getConfiguration(){
        return configuration;
    }
    
    public void setConfiguration(int configuration){
        this.configuration = configuration;
    }
}
