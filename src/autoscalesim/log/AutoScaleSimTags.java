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

import autoscalesim.cloudprovider.Vm;
import java.text.DecimalFormat;

/**
 * AutoScaleSimTags contains all tags used in the AutoScaleSim
 */

public class AutoScaleSimTags {
    
        /** Starting constant value for cloud-related tags **/
	protected static final int BASE = 0;
        
        /** Starting Dashboard**/
        public static final int ASP_START = BASE + 49;
        
        /** Time To Create VMs In Data centers**/
        public static final int VM_STARTING = BASE + 50;
        
        /** Finishing Dashboard **/
        public static final int ASP_FINISH = 54;
        
        /** Monitoring **/
        public static final int VMS_SYNCHRONIZATION = BASE + 57;
        public static final int AUTO_SCALING = BASE + 59;
        
        public static final int WORKLOAD_REQUEST = BASE + 60;
        public static final int WORKLOAD_START = BASE + 61;
        public static final int WORKLOAD_FINISH = BASE + 62;
        
        public static final int LOAD_MANAGEMENT_ADMISSION = BASE + 64;
//        public static final int LOAD_MANAGER_DISPATCHER_NEW_REQUESTS = BASE + 65;
                public static final int LOAD_MANAGER_NEW_REQUEST = BASE + 65;
//        public static final int LOAD_MANAGER_DISPATCHER_CANCELED_REQUESTS = BASE + 66;
        public static final int LOAD_MANAGER_CANCELED_REQUEST = BASE + 66;
        
        
        public static final int CLOUDLET_FAIL = BASE + 67;
        public static final int TIMEOUT_CHECKER = BASE + 68;

    
        public final static double VM_INITIALING_DELAY = 10 * AutoScaleSimTags.aMinute;   
        
        public final static int SpaceShared = 0;
        public final static int TimeShared = 1;

        public static DecimalFormat dft = new DecimalFormat("#.###");
        // Planer Decisions
        public final static int PLANNER_DO_NOTHING   = 0;
        public final static int PLANNER_SCALING_UP   = 1;
        public final static int PLANNER_SCALING_DOWN = -1;
        
        /**
         * Returns the string of planner's decision
         * @param decision
         * @return 
         */
        public static String getStringValueOfPlannerDecision (int decision){
            String decisionStr;
            switch(decision){
                case AutoScaleSimTags.PLANNER_SCALING_UP:
                    decisionStr = "SCALE_UP";break;
                case AutoScaleSimTags.PLANNER_SCALING_DOWN:
                    decisionStr = "SCALE_DOWN";break;
                case AutoScaleSimTags.PLANNER_DO_NOTHING:
                    decisionStr = "DO_NOTHING";break;
                default:
                    decisionStr = "ERROR";break;
            }
            
            return decisionStr;
        }
        
        /* Executor Actions */
        public final static int ACT_UP_NEW = 0;
        public final static int ACT_UP_MOVE_FROM_Q_TO_START = 1;
        public final static int ACT_UP_MOVE_FROM_Q_TO_REQ = 2;
        public final static int ACT_UP_LIMIT = 3;
        public final static int ACT_D_DESTROY = 4;
        public final static int ACT_D_DESTROY_FROM_REQ = 5;
        public final static int ACT_D_DESTROY_FROM_START = 6;
        public final static int ACT_D_DESTROY_FROM_Q = 7;
        public final static int ACT_D_MOVE_FROM_REQ_TO_Q = 8;
        public final static int ACT_D_MOVE_FROM_START_TO_Q = 9;
        public final static int ACT_D_LIMIT = 10;
        public final static int ACT_D_PARTIAL_LIMIT = 11;
        public final static int ACT_NO_ACTION = 12;
        public final static int ACT_UP_COOLDOWN = 13;
        public final static int ACT_D_COOLDOWN = 14;
        public final static int ACT_D_BILL_NOT_COMPLETED = 15;
        
        /**
         * Returns the string of executor action
         * @param action
         * @return 
         */
        public static String getStringValueOfExecutorAction (int action){
            String actionStr;
            switch(action){
                case AutoScaleSimTags.ACT_UP_NEW:
                    actionStr = "ACT_UP_NEW";break;
                case AutoScaleSimTags.ACT_UP_MOVE_FROM_Q_TO_START:
                    actionStr = "ACT_UP_MOVE_FROM_Q_TO_START";break;
                case AutoScaleSimTags.ACT_UP_MOVE_FROM_Q_TO_REQ:
                    actionStr = "ACT_UP_MOVE_FROM_Q_TO_REQ"; break;
                case AutoScaleSimTags.ACT_UP_LIMIT:
                    actionStr = "ACT_UP_LIMIT";break;
                case AutoScaleSimTags.ACT_D_DESTROY:
                    actionStr = "ACT_D_DESTROY";break;
                case AutoScaleSimTags.ACT_D_DESTROY_FROM_REQ:
                    actionStr = "ACT_D_DESTROY_FROM_REQ";break;
                case AutoScaleSimTags.ACT_D_DESTROY_FROM_START:
                    actionStr = "ACT_D_DESTROY_FROM_START";break;
                case AutoScaleSimTags.ACT_D_DESTROY_FROM_Q:
                    actionStr = "ACT_D_DESTROY_FROM_Q";break;
                case AutoScaleSimTags.ACT_D_MOVE_FROM_REQ_TO_Q:
                    actionStr = "ACT_D_MOVE_FROM_REQ_TO_Q";break;
                case AutoScaleSimTags.ACT_D_MOVE_FROM_START_TO_Q:
                    actionStr = "ACT_D_MOVE_FROM_START_TO_Q";break;
                case AutoScaleSimTags.ACT_D_LIMIT:
                    actionStr = "ACT_D_LIMIT";break;
                case AutoScaleSimTags.ACT_D_PARTIAL_LIMIT:
                    actionStr = "ACT_D_PARTIAL_LIMIT";break;
                case AutoScaleSimTags.ACT_NO_ACTION:
                    actionStr = "ACT_NO_ACTION";break;
                case AutoScaleSimTags.ACT_UP_COOLDOWN:
                    actionStr = "ACT_UP_COOLDOWN";break;
                case AutoScaleSimTags.ACT_D_COOLDOWN:
                    actionStr = "ACT_D_COOLDOWN";break;
                case AutoScaleSimTags.ACT_D_BILL_NOT_COMPLETED:
                    actionStr = "ACT_D_BILL_NOT_COMPLETED";break;
                default:
                    actionStr = "ERROR";break;
            }
            
            return actionStr;
        }
         /*
	 *  Reqion : Asia Pacific(Sydney)
         *  Operation System : Amazon EC2 running SUSE Linux Enterprise Server 
         *
         ****  On-Demand Instances
         *  Instance    vCPU                ECU     Memory(GiB)     Storage(GB)     Price(per Hour)
         *  t2.micro    1(2.5 up to 3.3)    Variable    1           EBS only        $0.02
         *  t2.small    1(2.5 up to 3.3)    Variable    2           EBS only        $0.04
         *  t2.medium   2(2.5 up to 3.3)    Variable    4           EBS only        $0.08
         *  t2.large    2(2.5 up to 3.3)    Variable    8           EBS only        $0.16
         *
         ****  Reserved Instance
         *  Instance    vCPU    ECU     Memory(GiB)     Storage(GB)     Price(1-Year term)
         *  t2.micro    1       Variable    1           EBS only        $128(All Upfront)(discounts 27%)
         *  t2.small    1       Variable    2           EBS only        $257(All Upfront)(discounts 27%)
         *  t2.medium   2       Variable    2           EBS only        $515(All Upfront)(discounts 27%)
         *  t2.large    2       Variable    2           EBS only        $1030(All Upfront)(discounts 27%)
         *
         ****  Spot Instance
         *  Instance    vCPU    ECU     Memory(GiB)     Storage(GB)     Price(per Hour)
	 *  t1.micro                                                    $0.0031 
        */ 
        
        // Vm Config
        public final static int VM_CONFIG_T2MICRO   = 0;
        public final static int VM_CONFIG_T2SMALL   = 1;
        public final static int VM_CONFIG_T2MEDIUM  = 2;
        public final static int VM_CONFIG_T2LARGE   = 3;
        public final static int[] VM_CONFIG_LIST = new int[]{VM_CONFIG_T2MICRO, VM_CONFIG_T2SMALL, VM_CONFIG_T2MEDIUM
                                                            ,VM_CONFIG_T2LARGE};
        // Vm Purchase
        public final static int VM_PURCHASE_RESERVED  = 0;   // (All UPfront)(1 year term)
        public final static int VM_PURCHASE_ON_DEMAND = 1;
        public final static int VM_PURCHASE_SPOT      = 2;
        public final static int[] VM_PURCHASE_LIST = new int[]{VM_PURCHASE_RESERVED, VM_PURCHASE_ON_DEMAND, VM_PURCHASE_SPOT};
        
        
        // Vm Price (Sydney, Linux)                            Micro, Small, Medium, Large
        public final static double[] VM_PRICE_ONDEMAND       = { 0.01, 0.02, 0.04, 0.08};   // per hour
        public final static double[] VM_PRICE_RESERVED_1YEAR = { 128, 257, 515, 1030};      // 1 year term, All Upfront
        public static double[] VM_PRICE_SPOT           = { 0, 0, 0, 0};               // per hour

        // Vm details                             Micro, Small, Medium, Large
        public final static double [] VM_MIPS	= { 2500, 2500, 2500, 2500 };
        public final static int[] VM_PES	= { 1, 1, 2, 4 }; // Number of CPUs
        public final static int[] VM_RAM	= { 1024, 2048, 4096, 8192 }; //Vm memory (MB)
        public final static long VM_BW		= 100000; // 100 Mbit/s 
        public final static long VM_SIZE	= 2500; // 2.5 GB, image size (MB)
        
        // Status
        public final static int[] VM_STATUS_LIST = new int[]{Vm.Requested, Vm.Started, Vm.Quarantined, Vm.Destroyed};

        // Tier
        public final static int WEB_TIER = 0;
        public final static int APPLICATION_TIER = 1;
        public final static int DATABAE_TIER = 2;
       
        //Dataset
        public enum DATASET {
            NASA,
            WIKIPEDIA
        }
        // Week Days
        public static final int MONDAY    = 1;                 // first day of week
        public static final int TUESDAY   = 2;
        public static final int WEDNESDAY = 3;
        public static final int THURSDAY  = 4;
        public static final int FRIDAY    = 5;
        public static final int SATURDAY  = 6;                  // Weekend
        public static final int SUNDAY    = 7;                  // Weekend

        // Stage of the day
        public static final int DayBreak       = 0;             // 0 - 4
        public static final int MorningBefor7  = 1;             // 5 - 6
        public static final int MorningAfter7  = 2;             // 7 - 11
        public static final int Noon_AfterNoon = 3;             // 12 - 17
        public static final int Evening        = 4;             // 18 - 20
        public static final int Night          = 5;             // 21 - 23

        // Time (base on Second)
        public static final int aYear = 365 * 24 * 60 * 60; // Base on a non- Leap year
        public static final int aYear_Leap = 366 * 24 * 60 * 60; // Base on a Leap year
        
        public static final int aMonth_28 = 28 * 24 * 60 * 60; // Base on 28 days
        public static final int aMonth_29 = 29 * 24 * 60 * 60; // Base on 29 days
        public static final int aMonth_30 = 30 * 24 * 60 * 60; // Base on 30 days
        public static final int aMonth_31 = 31 * 24 * 60 * 60; // Base on 31 days
        
        public static final int aWeek   = 60 * 60 * 24 * 7;
        public static final int aDay    = 60 * 60 * 24;
        public static final int anHour  = 60 * 60;
        public static final int aMinute = 60;
        
        public final static String oneTab = "   ";
        public final static String twoTabs = "       ";
        public final static String threeTabs = "          ";
        
        /**
         * A sample of how the delay time of instantiation of a VM is calculated
         * @return 
         */
        public static int[][] delayInVmStartUpMatrix(){
            int[][] delayMatrix = new int[][]{
                //          0-5    6-12    13-18   19-23
                /*micro */  {5,     6,      7,      6},
                /* small */ {6,     7,      8,      7},
                /* medium*/ {7,     8,      9,       8},
                /* large */ {8,     9,      10,      9}
            };
        
            return delayMatrix;
        }
        
        /* Color Writing */ 
        //30 black
        //31 red
//        32 green
//        33 yellow
//        34 blue
//        35 magenta
//        36 cyan
//        37 white
//        40 black background
//        41 red background
//        42 green background
//        43 yellow background
//        44 blue background
//        45 magenta background
//        46 cyan background
//        47 white background
//        1 make bright (usually just bold)
//        21 stop bright (normalizes boldness)
//        4 underline
//        24 stop underline
//        0 clear all formatting
//        
//    System.out.println((char)27 + "[34;43mBlue text with yellow background");
}
