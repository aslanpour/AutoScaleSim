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
package autoscalesim.enduser;

import org.cloudbus.cloudsim.Log;

/**
 *
 * @author aslanpour
 */
public class BubbleSort {
    /**
     * sort array with multiple items in each row
     * @param dataset
     * @return 
     */
    public static long[][] sortBubble(long[][] dataset){
        Log.printLine("Sorting the data . . .");
        long[][] temp = dataset;
        //bubble sorting
        for (int i = 0; i < temp.length; i++){
            if (i%100000==0)
                System.out.println(i);
            
            for (int j = 1; j < (temp.length - i) ; j ++){
                long[] rowJ = temp[j];
                long keyJ = rowJ[0];
                
                long[] rowJMinusOne = temp[j-1];
                long keyJMinusOne = rowJMinusOne[0];

                if (keyJMinusOne > keyJ){
                    long[] rowTemp = rowJMinusOne;
                    temp[j-1]= rowJ;
                    temp[j]= rowTemp;
                }
            }
        }
        
        
        return temp;
    }
}
