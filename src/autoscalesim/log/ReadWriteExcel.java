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
package autoscalesim.log;

import autoscalesim.applicationprovider.autoscaling.knowledgebase.ExecutorHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorEndUserHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorSLAHistory;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.AnalyzerHistory;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.PlannerHistory;

/**
 * ReadWriteExcel class is called when the ExperimentalResult class wants to print results in Excel file.
 * This class is responsible for writing the requested data to worksheets.
 */
public class ReadWriteExcel {
    
    public ReadWriteExcel(){
        
    }
    private static String FILE_PATH = "src/others/SimulationResult.xls";
    //We are making use of a single instance to prevent multiple write access to same file.
    private static final ReadWriteExcel INSTANCE = new ReadWriteExcel();
    
    public static ReadWriteExcel getInstance() {
        return INSTANCE;
    }

    /**
     * Write a dataList to a sheet(sheet name is DataList) in Default Path
     * FILE_PATH = "src/others/SimulationResult.xls"
     * @param dataList 
     */
     public static void writeDataList(ArrayList dataList){
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("DataList") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("DataList"));
                
                Sheet dataListSheet = workbook.createSheet("DataList");
                int rowIndex = 0;
                
                for(int i = 0; i < dataList.size(); i++){
                    ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
                    Row row = dataListSheet.createRow(rowIndex++);

                    for(int j = 0; j < data.size(); j++){
                        row.createCell(j).setCellValue(data.get(j));
                    }
                }

                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.println(FILE_PATH + " is successfully written");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }
    
    /**
     * write a DataList to an special sheet in a Default Path
     * FILE_PATH = "src/others/SimulationResult.xls"
     * @param dataList
     * @param sheetName 
     */
    public static void writeDataList(ArrayList dataList, String sheetName){
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet(sheetName) != null)
                    workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
                
                Sheet dataListSheet = workbook.createSheet(sheetName);
                int rowIndex = 0;
                
                for(int i = 0; i < dataList.size(); i++){
                    ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
                    Row row = dataListSheet.createRow(rowIndex++);

                    for(int j = 0; j < data.size(); j++){
                        row.createCell(j).setCellValue(data.get(j));
                    }
                }

                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.println(FILE_PATH + " is successfully written");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
                System.out.println("File is Not In Default Path");
        }
    }
     
    public static void writeVmHistoryList(ArrayList<MonitorVmHistory> vmHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("Vm") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("Vm"));
                
                Sheet vmsBehaviorSheet = workbook.createSheet("Vm");
                int rowIndex = 0;

                for(MonitorVmHistory vmHistory : vmHistoryList){
                    Row row = vmsBehaviorSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(vmHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(vmHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(vmHistory.getMinute());
                    //
                    row.createCell(cellIndex++).setCellValue(vmHistory.getCpuUtilizationByAllTier());
                    // sixth place in row is Vms number
                    row.createCell(cellIndex++).setCellValue(vmHistory.getCpuLoadByAllTier());
                    //
                    row.createCell(cellIndex++).setCellValue(vmHistory.getVms());
                    // 
                    row.createCell(cellIndex++).setCellValue(vmHistory.getInitialingVms());
                    //
                    row.createCell(cellIndex++).setCellValue(vmHistory.getRunningVms());
                    //
                    row.createCell(cellIndex++).setCellValue(vmHistory.getQuarantinedVms());
                    
                    row.createCell(cellIndex++).setCellValue(vmHistory.getRunningCloudlet());
                    //
                    row.createCell(cellIndex++).setCellValue(vmHistory.getThroughputFinishedCloudletsAllTiers());
                }
                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }

    
    
    /**
     * Write a list of Sla Behavior
     * @param slaBehaviorList 
     */
    public static void writeSLAHistoryList(ArrayList<MonitorSLAHistory> slaHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);
                
                if(workbook.getSheet("Sla") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("Sla"));
                
                Sheet slaBehaviorSheet = workbook.createSheet("Sla");
                int rowIndex = 0;

                for(MonitorSLAHistory slaHistory : slaHistoryList){
                    Row row = slaBehaviorSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(slaHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(slaHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(slaHistory.getMinute());

                    //
                    row.createCell(cellIndex++).setCellValue(slaHistory.getAvgResponseTimePerAllTiers());
                    
                    // sixth place in row is delay time
                    row.createCell(cellIndex++).setCellValue(slaHistory.getAvgDelayTimePerAllTiers());
                    
                    //
                    row.createCell(cellIndex++).setCellValue(slaHistory.getSlavNumberByAllTier());
                    
                    row.createCell(cellIndex++).setCellValue(slaHistory.getSlavPercent());
                    // 
                    row.createCell(cellIndex++).setCellValue(slaHistory.getSlavSecondByAlltier());
                    
                    //
                    row.createCell(cellIndex++).setCellValue(slaHistory.getCloudletsCancelled());
                    
                    //
                    row.createCell(cellIndex++).setCellValue(slaHistory.getCloudletFailedCounter());
                    
                    row.createCell(cellIndex++).setCellValue(slaHistory.getCloudletFinished());
                    
                }
                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }

    /**
     * Write a list of EndUsers Behavior
     * @param endUsersBehaviorList 
     */
    public static void writeEndUserHistoryList(ArrayList<MonitorEndUserHistory> endUsersHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("EndUsers") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("EndUsers"));
                
                Sheet endUsersBehaviorSheet = workbook.createSheet("EndUsers");
                int rowIndex = 0;
                
                for(MonitorEndUserHistory endUserHistory : endUsersHistoryList){
                    Row row = endUsersBehaviorSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(endUserHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(endUserHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(endUserHistory.getMinute());

                    // sixth place in row is requests number
                    row.createCell(cellIndex++).setCellValue(endUserHistory.getRequestsPerAllTier());

                    //seventh place in row is requests length
                    row.createCell(cellIndex++).setCellValue(endUserHistory.getRequestsLengthPerTier());

                }

                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @param analyzerHistoryList 
     */
    public static void writeAnalyzerHistoryList(ArrayList<AnalyzerHistory> analyzerHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("Analyzer") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("Analyzer"));
                
                Sheet analyzerSheet = workbook.createSheet("Analyzer");
                int rowIndex = 0;

                for(AnalyzerHistory analyzerHistory : analyzerHistoryList){
                    Row row = analyzerSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //first place in row is day number
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getDayNumber());
                    //first place in row is weekend
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getWeekend());
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getMinute());

                    //
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getCpuUtilization());
                    
                    //vms
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getVmsCount());
                    
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getThroughput());
                    // response time
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getResponseTime());
                    // sixth place in row is Vms number
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getDelayTime());
                    
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getSLAVCount());
                    // sla violation percentage
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getSLAVPercentage());
                    
                    // sla violation time
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getSLAVTime());
                    
                    // failed cloudlet
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getFailedCloudlet());
                    
                    // 
                    row.createCell(cellIndex++).setCellValue(analyzerHistory.getFutureUserRequest());
                    
                }
                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }
    
    
    public static void writePlannerHistoryList(ArrayList<PlannerHistory> plannerHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("Planner") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("Planner"));
                
                Sheet plannerSheet = workbook.createSheet("Planner");
                int rowIndex = 0;

                for(PlannerHistory plannerHistory : plannerHistoryList){
                    Row row = plannerSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getMinute());

                    //
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getDecision());
                    
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getVms());
                    
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getPurchase());
                    
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getTierType());
                    
                    row.createCell(cellIndex++).setCellValue(plannerHistory.getConfiguration());
                }
                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }
    
    public static void writeExecutorHistoryList(ArrayList<ExecutorHistory> executorHistoryList){
        
        try{
            FileInputStream fis = new FileInputStream(FILE_PATH);
            try{
                Workbook workbook = new HSSFWorkbook(fis);

                if(workbook.getSheet("Executor") != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("Executor"));
                
                Sheet executorSheet = workbook.createSheet("Executor");
                int rowIndex = 0;

                for(ExecutorHistory executorHistory : executorHistoryList){
                    Row row = executorSheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    //second place in row is Day Number
                    row.createCell(cellIndex++).setCellValue(executorHistory.getDayOfWeek());

                    //third place in row is Hour
                    row.createCell(cellIndex++).setCellValue(executorHistory.getHour());

                    //fourth place in row is Minute
                    row.createCell(cellIndex++).setCellValue(executorHistory.getMinute());

                    //
                    row.createCell(cellIndex++).setCellValue(executorHistory.getAction());
                    // sixth place in row is Vms number
                    row.createCell(cellIndex++).setCellValue(executorHistory.getProvisioning());
                    // 
                    row.createCell(cellIndex++).setCellValue(executorHistory.getDeProvisioning());
                }
                //write this workbook in excel file.
                try {
                        FileOutputStream fos = new FileOutputStream(FILE_PATH);
                        workbook.write(fos);
                        fos.close();

                        System.out.print(FILE_PATH + " is successfully written   ---   ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(FileNotFoundException ex){
                ex.printStackTrace();
        }
    }
    
     
   
    /**
     * Read a Sheet from Default Path
     * @param sheetName
     * @return 
     */
    public static ArrayList readASheet(String sheetName){
        ArrayList dataList =  new ArrayList();
        FileInputStream fis;
        Workbook workbook;
        try {
            fis = new FileInputStream(FILE_PATH);
            workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                
                ArrayList data = new ArrayList();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    data.add(cell.getNumericCellValue());
                }
                dataList.add(data);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    
    /**
     * Read a Sheet from an special workbook
     * @param sheetName
     * @param filePath
     * @return 
     */
    public static ArrayList readASheet(String sheetName, String filePath){
        ArrayList dataList =  new ArrayList();
        FileInputStream fis;
        Workbook workbook;
        try {
            fis = new FileInputStream(filePath);
            workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                
                ArrayList data = new ArrayList();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    data.add(cell.getNumericCellValue());
                }
                dataList.add(data);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    
    /**
     * Read a sheet, DeNormalization, write new Numbers in new sheet and new text file
     * @param sheetName 
     */
    public static void deNormalizationEndUsersBehavior(String sheetName){
        double y;
        double minInput;
        double maxInput;
        double minOutput = 0.1;
        double maxOutput = 0.8; // 0.8 + 0.1 ---> Max Output is 0.9
        double deNormalizedValue;
        
        // Read
        ArrayList dataList =  new ArrayList();
        FileInputStream fis;
        try {
             fis = new FileInputStream(FILE_PATH);
            Workbook workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext()){
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellIndex = 0;
                ArrayList<Double> data = new ArrayList<Double>();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    y = cell.getNumericCellValue();
                    switch(cellIndex){
                        case 0: // Day Number
                            minInput = 1; maxInput = 28; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                            break;
                        case 1: // Hour
                            minInput = 0; maxInput = 23; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                            break;
                        case 2: // Minute
                            minInput = 0; maxInput = 50; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                            break;
                        case 3: // Requests Count
                            minInput = 6; maxInput = 99; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                            break;
                        case 4: // Current Requests Length
                            minInput = 32500; maxInput = 795000; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                            break;
                        default: // Future Requests Length
                            minInput = 32500; maxInput = 795000; 
                            deNormalizedValue = (y - minOutput) * ( maxInput - minInput) / maxOutput + minInput;
                            data.add(deNormalizedValue);
                        break;
                    }
                    cellIndex++;
                }
                dataList.add(data);
            }
            System.out.println("Rows: " + dataList.size());
            // Write to Excel
            if(workbook.getSheet("DN_" + sheetName) != null)
                    workbook.removeSheetAt(workbook.getSheetIndex("DN_" + sheetName));
                
            Sheet normalizedSheet = workbook.createSheet("DN_" + sheetName);
            for(int i = 0; i < dataList.size() ; i++){
                Row row = normalizedSheet.createRow(i);
                int cellIndex = 0;
                ArrayList<Double> data = (ArrayList<Double>) dataList.get(i);
                //second place in row is Day Number
                row.createCell(cellIndex++).setCellValue(data.get(0));

                //third place in row is Hour
                row.createCell(cellIndex++).setCellValue(data.get(1));

                //fourth place in row is Minute
                row.createCell(cellIndex++).setCellValue(data.get(2));

                // sixth place in row is requests number
                row.createCell(cellIndex++).setCellValue(data.get(3));

                //seventh place in row is current requests length
                row.createCell(cellIndex++).setCellValue(data.get(4));

                //eighth place in row is future requests length
                row.createCell(cellIndex++).setCellValue(data.get(5));
            }
            //write this workbook in excel file.
            try {
                    FileOutputStream fos = new FileOutputStream(FILE_PATH);
                    workbook.write(fos);
                    fos.close();

                    System.out.println(FILE_PATH + " is successfully written");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //write to a txt file
            String txtfileName = "C:/AutoScaleSimFiles/" + "DN_" + sheetName + ".txt";
            File file = new File(txtfileName);
            if(file.exists())
                file.delete();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(int i = 0; i < dataList.size(); i++){
                ArrayList<Double> data = (ArrayList<Double>)dataList.get(i);
                printWriter.println(data.get(0) + "," + data.get(1) + "," + data.get(2) + ","
                                    + data.get(3) + "," + data.get(4) + "," + data.get(5));
            }
            printWriter.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * File path for saving
     * @param filePath for example: C:/AutoScaleSimFiles/example.xls
     */
    public static void setFilePath(String filePath){
        FILE_PATH = filePath;
    }
    
}
