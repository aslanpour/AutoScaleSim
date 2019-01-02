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

import autoscalesim.log.AutoScaleSimTags;
import java.text.DecimalFormat;
import org.cloudbus.cloudsim.core.CloudSim;
import autoscalesim.log.DateTime;

/**
 * History class is used as a main component of Knowledge-based component of MAPE-K. 
 * Each phase of MAPE-K extends this class and save their activities in that.
 */
public class History {
    /* Key property */
    private final int tick;
    private final int weekNumber;
    private final int weekend;
    private final int dayNumber;
    private final int dayOfWeek;
    private final int hour;
    private final int minute;
    private final int second;
    private final String timeStamp;
    
    public History(){        
        tick = (int)CloudSim.clock();
        
        weekNumber = tick / AutoScaleSimTags.aWeek + 1;
        dayNumber  = (tick / AutoScaleSimTags.aDay) + 1;
        
        if ((dayNumber + DateTime.FIRST_DAY_of_week_IN_NASA_DATASET - 1) % 7 == 0) 
            dayOfWeek = 7;
        else
            dayOfWeek = (dayNumber + DateTime.FIRST_DAY_of_week_IN_NASA_DATASET - 1) % 7;
        
        hour = (tick % AutoScaleSimTags.aDay) / AutoScaleSimTags.anHour;
        minute = (tick % AutoScaleSimTags.anHour) / AutoScaleSimTags.aMinute;
        second = tick % AutoScaleSimTags.aMinute;

        if(dayOfWeek == 6 || dayOfWeek == 7)
            weekend = 1;
        else 
            weekend = 0;
        
        DecimalFormat df = new DecimalFormat("##");
        df.setMinimumIntegerDigits(2);
        timeStamp = df.format(dayNumber) + "" + df.format(hour) + "" + df.format(minute);
    }

    public int getWeekNumber(){
        return weekNumber;
    }
    
    public int getWeekend(){
        return weekend;
    }
    
    /**
     * 
     * @return 
     */
    public int getDayOfWeek(){
        return dayOfWeek;
    }
    /**
     * Day Number (starts with 1)
     * @return 
     */
    public int getDayNumber() {
        return dayNumber;
    }
    
    public int isNowWeekend(){
        int dayOfWeek = ((int)CloudSim.clock() / AutoScaleSimTags.aDay) % 7 + 1 
                                    + (DateTime.FIRST_DAY_of_week_IN_NASA_DATASET - 1);
        
        if(dayOfWeek == 6 || dayOfWeek == 7)
            return 1;
        else
            return 0;
    }
    
    /**
     *  Hour
     * @return 
     */
    public int getHour () {
        return hour;
    }
     
    /**
     *  Minute
     * @return 
     */
    public int getMinute () {
        return minute;
    }
    
    /**
     * Get seconds of current minute
     * @return 
     */
    public int getSecond(){
        return second;
    }
    
    /**
     * Get total seconds(Ticks)
     * @return 
     */
    public int getTick(){
        return tick;
    }
    
    public String getTimeStamp(){
        return timeStamp;
    }
}
