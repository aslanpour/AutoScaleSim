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
import autoscalesim.log.AutoScaleSimTags.DATASET;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.neuroph.core.data.DataSet;
import static autoscalesim.log.ExperimentalResult.error;
import static autoscalesim.log.ExperimentalResult.errorChecker;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * EndUserEmulator class gets dataset and continuously extract the logs and creates corresponding cloudlets.
 * Then it sends the cloudlets to application provider to be processed.
 */
public class EndUserEmulator extends SimEntity{
    DataSet datasetNASA;
    long[][] datasetWikipedia;
    public static DATASET DATASET_TYPE;
    public static int lastCreatedCloudletId;
    public static int SIMULATION_LIMIT;
    public static int aspId;
    public final int CLOUDLET_LENGTH;
    public final int PES_NUMBER;
    public static int MIN_CLOUDLET_LENGTH;
    public static int MAX_CLOUDLET_LENGTH;
    
    public static int totalRequestNumber;
    
    /**
     * 
     * @param name
     * @param SIMULATION_LIMIT
     * @param datasetType
     * @param CLOUDLET_LENGTH
     * @param PES_NUMBER
     * @throws Exception 
     */
    public EndUserEmulator(String name,
                            int SIMULATION_LIMIT,
                            DATASET DATASET_TYPE,
                            final int CLOUDLET_LENGTH,
                            final int PES_NUMBER,
                            final int minCloudletLength,
                            final int maxCloudletLength) throws Exception {
        super(name);
        
        
        
        this.SIMULATION_LIMIT = SIMULATION_LIMIT;
        setDATASET_TYPE(DATASET_TYPE);
        
        if(DATASET_TYPE == DATASET.NASA){
            /*NASA dataset regarding 28 days */
            DataSet dataSet = DataSet.load("src//others//Dataset_NASA_July.tset"); 
            setDatasetNASA(dataSet);
            setDatasetWikipedia(null);
        }else if(DATASET_TYPE == DATASET.WIKIPEDIA){
            //read wikipedia: contains timestamp, data size and response time for HTTP requests
            long [][] datasetWikipedia = readWikipediaCSVFile("src//others//", "Dataset_Wikipedia_4hour.csv", Boolean.FALSE);
            setDatasetWikipedia(datasetWikipedia);
            setDatasetNASA(null);
        }
        
        lastCreatedCloudletId = -1;
        this.CLOUDLET_LENGTH = CLOUDLET_LENGTH;
        this.PES_NUMBER = PES_NUMBER;
        this.MIN_CLOUDLET_LENGTH = minCloudletLength;
        this.MAX_CLOUDLET_LENGTH = maxCloudletLength;
        totalRequestNumber = 0;
        }
    
    
    /**
     * Generates workload and sends it to application provider continuously
     */
    protected void workloadGeneratorNASA(){
        if((CloudSim.clock() / AutoScaleSimTags.aMinute) < SIMULATION_LIMIT){
            int recordIndex = (int)CloudSim.clock() / AutoScaleSimTags.aMinute;
            // Record Values: 0=weekend 1=Day Number, 2=Hour, 3=Minute, 4=Requests count, 
            double requestCount =getDatasetNASA().getRowAt(recordIndex).getInput()[4];

            double delay;
            //Cloudlet parameters
            long length; 
            long fileSize = 300,outputSize = 300;
            Cloudlet cloudlet;
            double [] delayListInThisMinute = getDatasetNASA().getRowAt(recordIndex).getDesiredOutput();
            
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
    
    int currentdatasetIndex = 0;
    /**
     * Generates workload and sends it to application provider continuously
     */
    protected void workloadGeneratorWikipedia(){
        if((CloudSim.clock() / AutoScaleSimTags.aMinute) < SIMULATION_LIMIT){
            int thisMinute= (int)(CloudSim.clock() / AutoScaleSimTags.aMinute);
                        
            int counter=0;
            while(true){
                //if not out of range index
                if((currentdatasetIndex + counter)< getDatasetWikipedia().length){
                    long[] request = getDatasetWikipedia()[currentdatasetIndex + counter];
                    //compare this log timestamp in second with current time in second
                    int nextTimeFrame =(thisMinute * 60) + AutoScaleSimTags.aMinute; 
                    if(request[0] < nextTimeFrame){
                        counter++;
                    }else{
                        break;
                    }
                }else
                    break;
            }
            
            if (counter != 0){
                //delay and size list
                long[][] reqList = new long[counter][3];

                for (int i=0; i< counter;i++){
                    reqList[i]= getDatasetWikipedia()[currentdatasetIndex + (i)];
                }

                currentdatasetIndex+=counter;

                // create cloudlets
                double delay;
                //Cloudlet parameters
                long length; 
                long fileSize = 300,outputSize = 300;
                Cloudlet cloudlet;

                for(int i = 0 ; i < reqList.length; i++){

                    long request[] = reqList[i];

                    int tier = 0;
                    UtilizationModel utilizationModel = new UtilizationModelFull();

                    // calculate length
                    long normalizedLength = normalizedValue(request[2], MIN_CLOUDLET_LENGTH, MAX_CLOUDLET_LENGTH);

                    cloudlet = new Cloudlet(++lastCreatedCloudletId, normalizedLength, PES_NUMBER, fileSize, outputSize,
                                            utilizationModel, utilizationModel, utilizationModel,tier);

                    cloudlet.setUserId(aspId);

                    delay = request[0] - (thisMinute * 60);
                    if(delay == 0)
                        delay = 0.01;
                    send(aspId, delay, AutoScaleSimTags.LOAD_MANAGEMENT_ADMISSION, cloudlet);
                    if(normalizedLength <= 0 || delay <= 0)
                        Log.print("error - EndUser- new request");
                }

                send(getId(), AutoScaleSimTags.aMinute, AutoScaleSimTags.WORKLOAD_REQUEST);
                totalRequestNumber += reqList.length;
            }else{
                Log.printLine("There is no future cloudlet 1, all " + totalRequestNumber + " cloudlets has sent");
                sendNow(aspId, AutoScaleSimTags.WORKLOAD_FINISH);
            }
        }else if((CloudSim.clock() / AutoScaleSimTags.aMinute) == SIMULATION_LIMIT){
            Log.printLine("There is no future cloudlet 2, all " + totalRequestNumber + " cloudlets has sent");
            sendNow(aspId, AutoScaleSimTags.WORKLOAD_FINISH);
        }
    }
    
    //based on 160 bringing between 16-144 at all
    private long normalizedValue(double input, double minInput, double maxInput) {
        // bring between 0.1 and 0.9
        double initialValue = (input - minInput) / (maxInput - minInput) * 0.8 + 0.1;
        
        long finalValue = (long)(initialValue * 160);
        //make it 90% of 16-160, the rest will be network delay
//        long ninthyPercent = Math.round((finalValue * 90) / (double)100);
        

        return finalValue;
    }
    /**
     * 
     * @param ev 
     */
    @Override
    public void processEvent(SimEvent ev) {
      
        switch (ev.getTag())  {
            case AutoScaleSimTags.WORKLOAD_REQUEST:
                if(getDATASET_TYPE()== DATASET.NASA)
                    workloadGeneratorNASA();
                else if (getDATASET_TYPE()== DATASET.WIKIPEDIA)
                    workloadGeneratorWikipedia();
                
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

   
    public DATASET getDATASET_TYPE() {
        return DATASET_TYPE;
    }

    public void setDATASET_TYPE(DATASET DATASET_TYPE) {
        this.DATASET_TYPE = DATASET_TYPE;
        
        
        
    }
            
    /**
     * Gets the workload (dataset)
     * @return 
     */
    public DataSet getDatasetNASA(){
        return datasetNASA;
    }
    
    /**
     * Sets the workload (dataset)
     * @param workload 
     */
    public void setDatasetNASA(DataSet dataset){
        this.datasetNASA = dataset;
    }

    public long[][] getDatasetWikipedia() {
        return datasetWikipedia;
    }

    public void setDatasetWikipedia(long[][] datasetWikipedia) {
        this.datasetWikipedia = datasetWikipedia;
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
        
        if(getDATASET_TYPE() == DATASET.NASA)
            workloadGeneratorNASA();
        else if (getDATASET_TYPE() == DATASET.WIKIPEDIA)
            workloadGeneratorWikipedia();
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
    
    /**
     * Read a CSV file
     * @param filePath
     * @param file
     * @param labeled
     * @return 
     */
    public static long[][] readWikipediaCSVFile(String filePath,String file, boolean labeled){
        long[][] dataset;
        ArrayList dataList = new ArrayList();
        ArrayList<Long> row;
        long timer = 0;
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader(filePath + file));
            try {
                String line;
                if (labeled)
                    csvReader.readLine();
                while ( (line = csvReader.readLine()) != null ) {
                    String[] rowStr = line.split(",");
                    
                    row = new ArrayList<Long>();
                    for (int i =0; i < rowStr.length;i++){
                        row.add(Long.valueOf(rowStr[i]));
                    }
                    
                    dataList.add(row);
                } 
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace(); 
        }
        System.out.println("The file: " + file + " was read");
                
        //Move to new array
        dataset=new long[dataList.size()][3];
        
        for (int i=0; i<dataList.size(); i++){
            //change millisecond to second
            dataset[i][0] = ((ArrayList<Long>)dataList.get(i)).get(0) / 1000;
            dataset[i][1] = ((ArrayList<Long>)dataList.get(i)).get(1);
            dataset[i][2] = ((ArrayList<Long>)dataList.get(i)).get(2);
        }
        
//        //grouping
//        
//        for (int i=0; i<dataset.length; i++){
//            long[] log = dataset[i];
//            if(log[0] < (timer + 60)){
//                //grouping
//            }
//            else{
//                //new group and timer
//                timer+=60;
//            }
//        }
        
        Log.printLine("wikipedia dataset rows = " + dataset.length);
        return dataset;

    }
}
