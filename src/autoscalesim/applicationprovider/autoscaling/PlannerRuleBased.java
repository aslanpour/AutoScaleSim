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

import autoscalesim.ExperimentalSetup;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import autoscalesim.log.AutoScaleSimTags;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;
import static autoscalesim.applicationprovider.ApplicationProvider.getAnalyzer;
import org.cloudbus.cloudsim.Log;
import java.util.ArrayList;
import org.cloudbus.cloudsim.core.CloudSim;
import autoscalesim.ExperimentalSetup.ScalingRule;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
/**
 * PlannerRuleBased class takes scaling decisions based on some rule-based methods. Such methods uses
 * thresholds for effective parameters and if the thresholds are violated, then the planner reaches its
 * decision. 
 */
public class PlannerRuleBased extends Planner{
    
    private final ScalingRule rule;
    private final double cpuScaleUpThreshold;
    private final double cpuScaleDownThreshold;
    private final double delayTimeMax;
    private final double delayTimeMin;
    
    /**
     * 
     * @param rule
     * @param configurationType
     * @param cpuScaleUpThreshold
     * @param cpuScaleDownThreshold
     * @param delayTimeMax
     * @param delayTimeMin
     */
    public PlannerRuleBased(
                            final ScalingRule rule,
                            int configurationType,
                            double cpuScaleUpThreshold,
                            double cpuScaleDownThreshold,
                            double delayTimeMax,
                            double delayTimeMin){
        super(configurationType);
        
        this.rule = rule;
        this.cpuScaleUpThreshold = cpuScaleUpThreshold;
        this.cpuScaleDownThreshold = cpuScaleDownThreshold;
        this.delayTimeMax = delayTimeMax;
        this.delayTimeMin = delayTimeMin;
    }
    
    
    @Override
    public void doPlanning(){

        /* Planner's outputs - initialing output parameters */
        setPlannerDecision(AutoScaleSimTags.PLANNER_DO_NOTHING);
        setPlannerStepsize(1);
        setPurchaseType(AutoScaleSimTags.VM_PURCHASE_ON_DEMAND);
        setTierType(AutoScaleSimTags.WEB_TIER);
//        setConfigurationType(AutoScaleSimTags.VM_CONFIG_T2MEDIUM);
        
        /* Planner's Inputs - parameters ready to contribute to decision making */
        AnalyzerHistory analyzerHistory = getAnalyzer().latestHistoryRec();

        double analyzedCpuUtilization = analyzerHistory.getCpuUtilization();
        double analyzedVmCount = analyzerHistory.getVmsCount();
        double analyzedThroughput = analyzerHistory.getThroughput();
        
        double analyzedResponseTime = analyzerHistory.getResponseTime();
        double analyzedDelayTime = analyzerHistory.getDelayTime();
        double analyzedSLAVcount = analyzerHistory.getSLAVCount();
        double analyzedSLAVPercentage = analyzerHistory.getSLAVPercentage();
        double analyzedSLAVTime = analyzerHistory.getSLAVTime();
        double analyzedFailedCloudlet = analyzerHistory.getFailedCloudlet();

        double analayzedFutureUserRequest = analyzerHistory.getFutureUserRequest();

        /* Select the rule */
        switch(rule){
            case RESOURCE_AWARE  : rule_ResourceAware(analyzedCpuUtilization); break;
            case SLA_AWARE  : rule_SLAAware(analyzedDelayTime); break;
            case HYBRID : rule_HYBRID(analyzedCpuUtilization, analyzedDelayTime); break;
            case UT_1Al : rule_UT_1Al(analyzedCpuUtilization); break;
            case UT_2Al : rule_UT_2Al(analyzedCpuUtilization); break;
            case LAT_1Al : rule_LAT_1Al(analyzedCpuUtilization, analyzedDelayTime); break;
            case LAT_2Al : rule_LAT_2Al(analyzedCpuUtilization, analyzedDelayTime); break;
            default:
                errorChecker = true;
                error += "Planner class, scaling Rule not found";
                Log.printLine("Planner class, scaling Rule not found");
        }

        // Saving PlannerRuleBased results in its History
        PlannerHistory plannerHistory = new PlannerHistory(getPlannerDecision(), 
                                                            getPlannerstepSize(), 
                                                            getPurchaseType(), 
                                                            getTierType(), 
                                                            getConfigurationType());
        getHistoryList().add(plannerHistory);
    }
    
    /**
     * Resource-aware rule
     * @param cpuUtil 
     */
    private void rule_ResourceAware(double cpuUtil){
        if(cpuUtil > cpuScaleUpThreshold)
            setPlannerDecision( AutoScaleSimTags.PLANNER_SCALING_UP);
        else if (cpuUtil < cpuScaleDownThreshold)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
    }
    
    /**
     * SLA-aware rule
     * @param delayTime 
     */
    private void rule_SLAAware(double delayTime){
        if(delayTime > delayTimeMax)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        else if (delayTime < delayTimeMin)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
    }
    
    /**
     * Hybrid rule, both Resource-and-SLA-aware
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_HYBRID(double cpuUtil, double delayTime){
        if(cpuUtil > cpuScaleUpThreshold && delayTime > delayTimeMax)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        else if (cpuUtil < cpuScaleDownThreshold && delayTime < delayTimeMin)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810. 
     * @param cpuUtil 
     */
    private void rule_UT_1Al(double cpuUtil){
        if(cpuUtil > 62)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        else if (cpuUtil < 50)
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_LAT_1Al(double cpuUtil, double delayTime){
        if(delayTime > 0.2) 
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        else if (cpuUtil < 50) 
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.   
     * @param cpuUtil 
     */
    private void rule_UT_2Al(double cpuUtil){
        if(cpuUtil > 70){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
            setPlannerStepsize(2);
        }else if (cpuUtil <= 70 && cpuUtil > 62){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        // down
        }else if (cpuUtil < 50 && cpuUtil >= 25){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
        }else if (cpuUtil < 25){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
            setPlannerStepsize(2); 
        }
    }
    
    /**
     * Casalicchio, E. and Silvestri, L., 2013. 
     * Mechanisms for SLA provisioning in cloud-based service providers. 
     * Computer Networks, 57(3), pp.795-810.   
     * @param cpuUtil
     * @param delayTime 
     */
    private void rule_LAT_2Al(double cpuUtil, double delayTime){
        if(delayTime > 0.5){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
            setPlannerStepsize(2);
        }else if (delayTime > 0.2){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_UP);
        // down
        }else if (cpuUtil < 50 && cpuUtil >= 25){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
        }else if (cpuUtil <= 25){
            setPlannerDecision(AutoScaleSimTags.PLANNER_SCALING_DOWN);
            setPlannerStepsize(2);
        }
    }
    
        
    
}
