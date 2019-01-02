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

import java.text.DecimalFormat;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.Log;

/**
 * DateTime class calculates the information regarding date and time.
 */
public class DateTime {
    
    public static final int FIRST_DAY_of_week_IN_NASA_DATASET = 6;
    /**
     * 
     * @return 
     */
    public static String timeStamp(){
        DecimalFormat df = new DecimalFormat();
        df.setMinimumIntegerDigits(2);
        String timeStamp = dayOfWeekStr() + ":" + df.format(hourOfDay()) + ":" + df.format(minuteOfhour()) + ":" + df.format(second()) + " ";
        return timeStamp;
    }
    
       
    public static int weekNumber(){
        return dayNumber() / 7 + 1;
    }
    
  /**
     * Is Weekend Current Day?(Saturday or Sunday)
     * @return 
     */
    public static boolean weekend(){
        if(dayOfWeek() == AutoScaleSimTags.SATURDAY || dayOfWeek() == AutoScaleSimTags.SUNDAY)
            return true;
        else
            return false;
    }
    
    /**
     * Today Name
     * @return 
     */
    public static int dayOfWeek() {
         if ((dayNumber() + FIRST_DAY_of_week_IN_NASA_DATASET - 1) % 7 == 0) 
            return 7;
        else
            return (dayNumber() + FIRST_DAY_of_week_IN_NASA_DATASET - 1) % 7;
    }
   
    public static String dayOfWeekStr() {
        String dayOfWeekStr;
         switch(dayOfWeek()){
             case 1: dayOfWeekStr = "MONDAY";break;
             case 2: dayOfWeekStr = "THUESDAY";break;
             case 3: dayOfWeekStr = "WEDNESDAY";break;
             case 4: dayOfWeekStr = "THURSDAY";break;
             case 5: dayOfWeekStr = "FRIDAY";break;
             case 6: dayOfWeekStr = "SATURDAY";break;
             case 7: dayOfWeekStr = "SUNDAY";break;
             default:dayOfWeekStr = "";
         }
         return dayOfWeekStr;
    }
    /**
     * Day Number (starts with 1)
     * @return 
     */
    public static int dayNumber() {
        return (tick() / AutoScaleSimTags.aDay) + 1;
    }
    
    /**
     * Current Day Name of Week
     * @return 
     */
    public static String dayName() {
         int dayOfWeek = dayOfWeek();
         String dayName;
         
         switch (dayOfWeek){
             case 0:
                 dayName = "Monday";
                 break;
             case 1:
                 dayName = "Thuseday";
                 break;
             case 2:
                 dayName = "Wednesday";
                 break;
             case 3:
                 dayName = "Thursday";
                 break;
             case 4:
                 dayName = "Friday";
                 break;
             case 5:
                 dayName = "Saturday";
                 break;
             default:
                 dayName = "Sunday";
                 break;
         }

         return dayName;
    }
    
    /**
     * Current Hour
     * @return 
     */
    public  static int hourOfDay () {
        return (tick() % AutoScaleSimTags.aDay) / AutoScaleSimTags.anHour;
    }
     
    /**
     * Current Minute
     * @return 
     */
    public static int minuteOfhour () {
        return (tick() % AutoScaleSimTags.anHour) / AutoScaleSimTags.aMinute;
    }
    
    
    
    
    
    
    
    /**
     * Get seconds of current minute
     * @return 
     */
    public static double second(){
        return tick() % AutoScaleSimTags.aMinute;
    }
    
    /**
     * Get total seconds(Ticks)
     * @return 
     */
    public static int tick(){
        return (int)CloudSim.clock();
    }
    
    /**
     * Stage of the Day (DayBreak, Morning Before 7, Morning After 7, Noon and AfterNoon, Evening, Night)
     * @return Stage
     */
    public static int dayPart(){
         int hour = hourOfDay();
        int dayPart;
        
        if (hour >= 0 && hour < 5)          // 0, 1, 2, 3, 4      
            dayPart = AutoScaleSimTags.DayBreak;
        else if (hour >= 5 && hour < 7)     // 5, 6
            dayPart = AutoScaleSimTags.MorningBefor7;
        else if (hour >= 7 && hour < 12)    // 7, 8, 9, 10, 11
            dayPart = AutoScaleSimTags.MorningAfter7;
        else if (hour >=12 && hour < 18)    // 12, 13, 14, 15, 16, 17
            dayPart = AutoScaleSimTags.Noon_AfterNoon;
        else if (hour >= 18 && hour < 21)   // 18, 19, 20
            dayPart = AutoScaleSimTags.Evening;
        else if (hour >=21 && hour <24)     // 21, 22, 23
            dayPart = AutoScaleSimTags.Night;
        else {
            dayPart = 0;
            Log.printLine("Error");
        }
        
        return dayPart;
    }
    
    /**
     * Stage of the Day (DayBreak, Morning Before 7, Morning After 7, Noon and AfterNoon, Evening, Night)
     * @param hour
     * @return Stage
     */
    public static int stageOfTheDay (double hour) {
        int stageOfTheDay;
        
        if (hour >= 0 && hour < 5)          // 0, 1, 2, 3, 4      
            stageOfTheDay = AutoScaleSimTags.DayBreak;
        else if (hour >= 5 && hour < 7)     // 5, 6
            stageOfTheDay = AutoScaleSimTags.MorningBefor7;
        else if (hour >= 7 && hour < 12)    // 7, 8, 9, 10, 11
            stageOfTheDay = AutoScaleSimTags.MorningAfter7;
        else if (hour >=12 && hour < 18)    // 12, 13, 14, 15, 16, 17
            stageOfTheDay = AutoScaleSimTags.Noon_AfterNoon;
        else if (hour >= 18 && hour < 21)   // 18, 19, 20
            stageOfTheDay = AutoScaleSimTags.Evening;
        else if (hour >=21 && hour <24)     // 21, 22, 23
            stageOfTheDay = AutoScaleSimTags.Night;
        else {
            stageOfTheDay = 0;
            Log.printLine("Error");
        }
        
        return stageOfTheDay;
    }
    
    private static int monthNumber(){
        int days = dayNumber();
        
        if(days <=31) return 1;
        if(days <=60) return 2;
        if(days <= 91) return 3;
        if(days <= 121) return 4;
        if(days <= 152) return 5;
        if(days <= 182) return 6;
        if(days <= 213) return 7;
        if(days <= 244) return 8;
        if(days<= 274) return 9;
        if(days <= 305) return 10;
        if(days <= 335) return 11;
        
        return 12;

    }
    
    private static String monthName(){
        String monthName;
        switch(monthNumber()){
            case 1:
                monthName =  "January";
                break;
            case 2:
                monthName = "February";
                break;
            case 3:
                monthName = "March";
                break;
            case 4:
                monthName = "April";
                break;
            case 5: 
                monthName = "May";
                break;
            case 6:
                monthName = "June";
                break;
            case 7:
                monthName = "July";
                break;
            case 9:
                monthName = "August";
                break;
            case 10:
                monthName = "October";
                break;
            case 11:
                monthName = "November";
                break;
            default:
                monthName = "December";
                break;
        }
        return monthName;
    }
}
