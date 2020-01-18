/*
 * Title:        AutoScaleSim Toolkit
 * Description:  AutoScaleSim (Auto-Scaling Simulation) Toolkit for Modeling and Simulation of Auto-scaling Systems
 *		 for Cloud Applications 			
 *
 * Copyright (c) 2018 Islamic Azad University, Jahrom
 * https://www.programcreek.com/2012/11/quicksort-array-in-java/

 * Authors: Mohammad Sadegh Aslanpour
 * 
 */
package autoscalesim.enduser;

import java.util.Arrays;

/**
 *
 * @author aslanpour
 */
public class QuickSort {
    public static void main(String[] args) {
        long length = normalizedValue(245000, 245000, 245000);
        double initialValue = (245000 - 0) / (245000 - 0) * 0.8 + 0.1;
        //bring between 10 and 100
        long finalValue = (long)(initialValue * 160);
        long fff = Math.round((finalValue * 80) / (double)100);
        long ddd = Math.round((finalValue * 20) /(double)100);
        long asafaf= fff + ddd;
//        finalValue = (long)finalValue + (long)(initialValue * 20);
        
        double a=1.5;
        long ex=Math.round(a);
        int[] x = { 9, 2, 4, 7, 3, 7, 10 };
        System.out.println(Arrays.toString(x));

        int low = 0;
        int high = x.length - 1;

        quickSort(x, low, high);
        System.out.println(Arrays.toString(x));
    }

    private static long normalizedValue(double input, double minInput, double maxInput) {
        double initialValue = (input - minInput) / (maxInput - minInput) * 0.8 + 0.1;
        long finalValue = (long)(initialValue * 10);
        return finalValue;
    }
    /**
     * 
     * @param arr the array to be sorted
     * @param low the lowest index, equals 0
     * @param high the highest index in the array, length - 1
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (arr == null || arr.length == 0)
            return;
        
        if (low >= high)
            return;

        // pick the pivot
        int middle = low + (high - low) / 2;
        // calculate pivot number, I am taking pivot as middle index number
        int pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which 
             * is greater then the pivot value, and also we will identify a number 
             * from right side which is less then the pivot value. Once the search 
             * is done, then we exchange both numbers.
             */
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                //move index to next position on both sides
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);
    }
    
    /**
     * 
     * @param arr the array to be sorted
     * @param low the lowest index, equals 0
     * @param high the highest index in the array, length - 1
     */
    public static long[] quickSort(long[] arr, int low, int high) {
        if (arr == null || arr.length == 0)
            return null;
        
        if (low >= high)
            return null;

        // pick the pivot
        int middle = low + (high - low) / 2;
        // calculate pivot number, I am taking pivot as middle index number
        long pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which 
             * is greater then the pivot value, and also we will identify a number 
             * from right side which is less then the pivot value. Once the search 
             * is done, then we exchange both numbers.
             */
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                long temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                //move index to next position on both sides
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);
        
        return arr;
    }
    
    /**
     * 
     * @param arr the array to be sorted
     * @param low the lowest index, equals 0
     * @param high the highest index in the array, length - 1
     */
    public static long[][] quickSort(long[][] arr, int low, int high) {
        if (arr == null || arr.length == 0)
            return null;
        
        if (low >= high)
            return null;

        // pick the pivot
        int middle = low + (high - low) / 2;
        // calculate pivot number, I am taking pivot as middle index number
        long[] pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which 
             * is greater then the pivot value, and also we will identify a number 
             * from right side which is less then the pivot value. Once the search 
             * is done, then we exchange both numbers.
             */
            while (arr[i][0] < pivot[0]) {
                i++;
            }

            while (arr[j][0] > pivot[0]) {
                j--;
            }

            if (i <= j) {
                long[] temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                //move index to next position on both sides
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);
        
        return arr;
    }
    
}
