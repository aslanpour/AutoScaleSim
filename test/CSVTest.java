/*
 * Title:        AutoScaleSim Toolkit
 * Description:  AutoScaleSim (Auto-Scaling Simulation) Toolkit for Modeling and Simulation of Auto-scaling Systems
 *		 for Cloud Applications 			
 *
 * Copyright (c) 2018 Islamic Azad University, Jahrom
 *
 * Authors: Mohammad Sadegh Aslanpour
 * 
 */
import autoscalesim.applicationprovider.autoscaling.knowledgebase.History;
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import autoscalesim.log.ReadWriteCSV;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

/**
 *
 * @author sadegh
 */
public class CSVTest {
    
    @Test 
    public void myTest(){
        assertThat(123, is(123));
    }
    
    
    @Test
    public void readWriteCSVTest(){
        ArrayList<MonitorVmHistory> vmHistoryList = new ArrayList<>();
        MonitorVmHistory vmHistory = new MonitorVmHistory(1, 2, 3, 4, 5, 6, 7, new int[]{1,2}, new int[]{1,2}, 10);
        vmHistory.setCpuUtilizationByAllTier(4);
        vmHistory.setCpuLoadByAllTier(5);
        vmHistory.setVms(6);
        vmHistory.setInitialingVms(7);
        vmHistory.setRunningVms(8);
        vmHistory.setQuarantinedVms(9);
        vmHistory.setRunningCloudlet(10);
        vmHistory.setThroughputFinishedCloudletsAllTiers(11); 
        
        vmHistoryList.add(vmHistory);
        ReadWriteCSV rw = new ReadWriteCSV();
        assertTrue(rw.writeVmHistoryList(vmHistoryList));
        
    }
    
    @Test
    public void readTest(){
        ReadWriteCSV rw2 = new ReadWriteCSV();
        rw2.readCSV("Monitor_VmHistory");

    }
}
