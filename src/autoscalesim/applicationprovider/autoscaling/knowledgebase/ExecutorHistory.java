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
 * ExecutorHistory class is the knowledge-base component for execution phase of auto-scaling system.
 * This class is called at the end of execution phase by Executor class to save the calculated parameters.
 */
public class ExecutorHistory extends History{
    // per tier
    private int action;
    private int provisioning;
    private int deProvisioning;
    
    public ExecutorHistory(int action, 
                            int provisioning, 
                            int deProvisioning){
        // set date and time
        super();
        
        this.action = action;
        this.provisioning = provisioning;
        this.deProvisioning = deProvisioning;
    }
    
    public int getAction(){
        return action;
    }
    
    public void setAction(int action){
        this.action = action;
    }
    
    public int getProvisioning(){
        return provisioning;
    }
    
    public void setProvisioning(int provisioning){
        this.provisioning = provisioning;
    }
    
    public int getDeProvisioning(){
        return deProvisioning;
    }
    
    public void setDeProvisioning(int deProvisioning){
        this.deProvisioning = deProvisioning;
    }
}
