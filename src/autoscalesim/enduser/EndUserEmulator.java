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

package autoscalesim.enduser;

import org.cloudbus.cloudsim.CloudSimTags;
import autoscalesim.enduser.Cloudlet;
import autoscalesim.log.AutoScaleSimTags;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.neuroph.core.data.DataSet;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;

/**
 * EndUserEmulator classs gets dataset and continuously extract the logs and creates corresponding cloudlets.
 * Then it sends the cloudlets to application provider to be processed.
 */
public class EndUserEmulator extends SimEntity{
    DataSet workload;
    public static int lastCreatedCloudletId;
    public static int SIMULATION_LIMIT;
    public static int aspId;
    public final int CLOUDLET_LENGTH;
    public final int PES_NUMBER;
    public static int totalRequestNumber;
    
    /**
     * 
     * @param name
     * @param SIMULATION_LIMIT
     * @param dataSetAndDelayList
     * @param CLOUDLET_LENGTH
     * @param PES_NUMBER
     * @throws Exception 
     */
    public EndUserEmulator(String name,
                            int SIMULATION_LIMIT,
                            final DataSet dataSetAndDelayList,
                            final int CLOUDLET_LENGTH,
                            final int PES_NUMBER) throws Exception {
        super(name);
        
        setWorkload(dataSetAndDelayList);
        this.SIMULATION_LIMIT = SIMULATION_LIMIT;
        lastCreatedCloudletId = -1;
        this.CLOUDLET_LENGTH = CLOUDLET_LENGTH;
        this.PES_NUMBER = PES_NUMBER;
        totalRequestNumber = 0;
        }
    
    /**
     * Generates workload and sends it to application provider continuously
     */
    protected void workloadGenerator(){
        if((CloudSim.clock() / AutoScaleSimTags.aMinute) < SIMULATION_LIMIT){
            int recordIndex = (int)CloudSim.clock() / AutoScaleSimTags.aMinute;
            // Record Values: 0=weekend 1=Day Number, 2=Hour, 3=Minute, 4=Requests count, 
            double requestCount =getWorkload().getRowAt(recordIndex).getInput()[4];

            double delay;
            //Cloudlet parameters
            long length; 
            long fileSize = 300,outputSize = 300;
            Cloudlet cloudlet;
            double [] delayListInThisMinute = getWorkload().getRowAt(recordIndex).getDesiredOutput();
            
            for(int i = 0 ; i < requestCount; i++){

                length = CLOUDLET_LENGTH;
                        
                int tier = 0;
                
                UtilizationModel utilizationModel = new UtilizationModelFull();
                
                cloudlet = new Cloudlet(++lastCreatedCloudletId, length, PES_NUMBER, fileSize, outputSize,
                                        utilizationModel, utilizationModel, utilizationModel,tier);

                cloudlet.setUserId(aspId);

                delay = delayListInThisMinute[i];
                if(delay == 0)
                    delay = 0.01;
                send(aspId, delay, AutoScaleSimTags.LOAD_MANAGEMENT_ADMISSION, cloudlet);
                if(length <= 0 || delay <= 0)
                    Log.print("error - EndUser- new request");
            }


            send(getId(), AutoScaleSimTags.aMinute, AutoScaleSimTags.WORKLOAD_REQUEST);

            totalRequestNumber += requestCount;
        }else if((CloudSim.clock() / AutoScaleSimTags.aMinute) == SIMULATION_LIMIT){
            Log.printLine("There is no future cloudlet, all " + totalRequestNumber + " cloudlets has sent");
            sendNow(aspId, AutoScaleSimTags.WORKLOAD_FINISH);
        }
    }
    
    
    /**
     * 
     * @param ev 
     */
    @Override
    public void processEvent(SimEvent ev) {
      
        switch (ev.getTag())  {
            case AutoScaleSimTags.WORKLOAD_REQUEST:
                workloadGenerator();
                break;
            case AutoScaleSimTags.WORKLOAD_START:
                processClientStart(ev);
                break;
            default:
                processOtherEvent(ev);
                break;
        }
    }

    /**
     * 
     */
    @Override
    public void startEntity() {
        Log.printLine(getName() + " is starting...");
        CloudSim.setClientId(this.getId());
    }
    
    @Override
    public void shutdownEntity(){
        // do nothing
    }
    
    /**
     * Gets the workload (dataset)
     * @return 
     */
    public DataSet getWorkload(){
        return workload;
    }
    
    /**
     * Sets the workload (dataset)
     * @param workload 
     */
    public void setWorkload(DataSet workload){
        this.workload = workload;
    }
    
    /**
     * Know ASP entity Id and send Requests
     * @param ev 
     */
    protected void processClientStart(SimEvent ev){
        int aspId = (int)ev.getData();
        if (CloudSim.getEntity(aspId) != null){
            this.aspId = aspId;
        }else 
            Log.printLine("Error in identify ASP");
        
        workloadGenerator();
    }
   /**
    * 
    * @param ev 
    */
    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            Log.printLine(getName() + ".processOtherEvent(): Error - an event is null.");
        }
    }
    
}
