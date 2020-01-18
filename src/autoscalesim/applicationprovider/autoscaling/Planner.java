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

import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import autoscalesim.log.AutoScaleSimTags;
import java.util.ArrayList;

/**
 * Planner class is responsible for resource estimation. The scale up or scale down decision will
 * be taken here.
 */


public abstract class Planner {
    
    private int plannerDecision;
    private int plannerStepSize;
    private int purchaseType;
    private int tierType;
    public int configurationType;
    
    private ArrayList<PlannerHistory> historyList;
    
    /**
     * 
     */
    public Planner(int configurationType){
        // PlannerRuleBased's output - initialing output parameters related to final decision
        
        plannerDecision = AutoScaleSimTags.PLANNER_DO_NOTHING;
        plannerStepSize = 0;
        tierType = -1;
        this.configurationType = configurationType;
        
        setHistoryList(new ArrayList<PlannerHistory>());
    }
    
    /**
     * Reaches a scaling decision
     */
    public abstract void doPlanning();
    
    /**
     * Gets the planner decision
     * @return 
     */
    public int getPlannerDecision(){
        return plannerDecision;
    }
    
    /**
     * Sets the planner decision
     * @param plannerDecision 
     */
    protected void setPlannerDecision(int plannerDecision){
        this.plannerDecision = plannerDecision;
    }
    
    /**
     * Gets the planner step size
     * @return 
     */
    public int getPlannerstepSize(){
        return plannerStepSize;
    }
    
    /**
     * Sets the planner step size
     * @param plannerStepSize 
     */
    protected void setPlannerStepsize(int plannerStepSize){
        this.plannerStepSize  = plannerStepSize;
    }
    
    /**
     * Gets Purchase type
     * @return 
     */
    public int getPurchaseType(){
        return purchaseType;
    }
    
    /**
     * Sets purchase type
     * @param purchaseType 
     */
    protected void setPurchaseType(int purchaseType){
        this.purchaseType = purchaseType;
    }
    
    /**
     * Gets tier type
     * @return 
     */
    public int getTierType(){
        return tierType;
    }
    
    /**
     * Sets tier type
     * @param tierType 
     */
    protected void setTierType(int tierType){
        this.tierType = tierType;
    }
    
    /**
     * Gets configuration type
     * @return 
     */
    public int getConfigurationType(){
        return configurationType;
    }
    
    /**
     * Set configuration type
     * @param configurationType 
     */
    protected void setConfigurationType(int configurationType){
        this.configurationType = configurationType;
    }
    
   /**
    * Gets the History of Planning phase
    * @return 
    */
    public  ArrayList<PlannerHistory> getHistoryList(){
        return historyList;
    }
    
    /**
     * Sets the History of Planning phase
     * @param historyList 
     */
    protected void setHistoryList(ArrayList<PlannerHistory> historyList){
        this.historyList = historyList;
    }
    
    /**
     * Returns the size of History for Planning phase
     * @return 
     */
    public int sizeHistory(){
        return getHistoryList().size();
    }
    
    /**
     * Returns the latest record of Planning history
     * @return 
     */
    public PlannerHistory latestHistoryRec(){
        return getHistoryList().get(sizeHistory()-1);
    }
}
