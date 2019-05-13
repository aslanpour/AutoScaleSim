
import autoscalesim.applicationprovider.autoscaling.knowledgebase.MonitorVmHistory;
import static autoscalesim.log.AutoScaleSimTags.twoTabs;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.cloudbus.cloudsim.Log;

public class Test {
    
    enum method {
        SIMPLE,
        MA,
        WMA;
    }
    public static void main(String[] args) {
        
        Percentile pct = new Percentile();
        double arr2[] = new double[]{1.0, 19.0, 8.0, 2.5, 14.0, 14.0, 10.0};
        pct.setData(arr2);
        
        System.out.println("pc = " +pct.evaluate());
        System.out.println("pc50 = " +pct.evaluate(50));
        System.out.println("pcQ = " +pct.getQuantile());
        System.out.println("pc100 = " +pct.evaluate(100));
        pct.setQuantile(100);
        System.out.println("pcQNew = " +pct.getQuantile());
        System.out.println("pc = " +pct.evaluate(arr2, 0, 3, 50));
        String dd = "4.5";
        
        MonitorVmHistory vmHistory = new MonitorVmHistory(1, 2, 3, 4, 5, 6, 7, new int[]{1,2}, new int[]{1,2}, 10);
        vmHistory.setCpuUtilizationByAllTier(4);
        vmHistory.setCpuLoadByAllTier(5);
        vmHistory.setVms(6);
        vmHistory.setInitialingVms(7);
        vmHistory.setRunningVms(8);
        vmHistory.setQuarantinedVms(9);
        vmHistory.setRunningCloudlet(10);
        vmHistory.setThroughputFinishedCloudletsAllTiers(11);  
        
        String myMeth = method.SIMPLE.name();
        myMeth = myMeth;
          
        double[] arr = new double[]{88,1.4,45,2, 8, 3.3, 7.6, 83, 3.5, 4,
        4,1.4,3,2, 65, 3.3, 7.6, 9, 3.5, 4,4,1.4,3,2, 8, 3.3, 7.6, 9, 3.5, 87,4,1.4,3,2, 8, 3.3, 7.6, 9, 3.5, 4
        ,4,1.4,3,2, 65, 3.3, 7.6, 97, 3.5, 4,4,1.4,3,2, 8, 3.3, 7.6, 9, 3.5, 4,4,1.4,3,2, 8, 3.3, 7.6, 9, 3.5, 4
        ,15,1.4,3,59, 79, 3.3, 7.6, 90, 3.5, 4,4,1.4,3,2, 8, 3.3, 7.6, 88, 3.5, 4,4,1.4,3,2, 8, 3.3, 7.6, 9, 3.5, 4};
        //Selection Sort
//        for (int i = 0; i < arr.length - 1; i++)
//        {
//            int index = i;
//            for (int j = i + 1; j < arr.length; j++)
//                if (arr[j] < arr[index]) 
//                    index = j;
//      
//            double smallerNumber = arr[index];  
//            arr[index] = arr[i];
//            arr[i] = smallerNumber;
//        }
//        
//        for (int i = 0; i < arr.length ; i++)
//        {
//            System.out.println((i+1) + "= " + arr[i]);
//        }
        
        //Percentile Calculation
        double [] pctl = new double[]{-1, -1, -1};
        int counter = 1;
        while (counter <= 3){
            //Percentile Selection
            double p;
            if(counter == 1) 
                p = 0.9;
            else if (counter == 2)
                p = 0.95;
            else 
                p = 0.99;
//            Percentile pctl = new Percentile();
//            pctl
            //Calculate Percentile
            int index = (int)Math.ceil(p * arr.length);
            int rawIndex = (int)Math.floor(p * arr.length);
            boolean isWhole = true;
            if (index != rawIndex)
                isWhole = false;
            
            if(!isWhole)
                pctl[counter - 1] = arr[index - 1];
            else if (isWhole)
                pctl[counter - 1] = (arr[index - 1] + arr[index]) / 2;
        
            counter++;
        }
        Percentile p = new Percentile();
        p.setData(arr);
        pctl[0] = p.evaluate(90.0);
        pctl[1] = p.evaluate(95.0);
        pctl[2] = p.evaluate(99.0);
        Log.printLine(twoTabs + arr.length + "Tail Latency (percentile): " + "90th=" + pctl[0]
                        + " 95th=" + pctl[1] + " 99th=" + pctl[2]
                        + "  " + p.getQuantile());
        
    }
}
