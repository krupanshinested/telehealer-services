package com.thealer.telehealer.views.home.monitoring.diet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Aswin on 21,February,2019
 */
public class DietConstant {
    public static final int TYEP_HEADER = 0;
    public static final int TYEP_DATA = 1;
    public static final int TYEP_ADD_NEW = 2;

    public static final String TYPE_BREAKFAST = "Breakfast";
    public static final String TYPE_LUNCH = "Lunch";
    public static final String TYPE_DINNER = "Dinner";
    public static final String TYPE_SNACKS = "Snacks";

    public static final String PRINT_1_WEEK = "Last 1 weeks";
    public static final String PRINT_2_WEEK = "Last 2 weeks";
    public static final String PRINT_1_MONTH = "Last 1 Month";
    public static final String DIET_PRINT_ALL = "All Diet History";
    public static final ArrayList<String> dietPrintOptions = new ArrayList<>(Arrays.asList(PRINT_1_WEEK, PRINT_2_WEEK, PRINT_1_MONTH, DIET_PRINT_ALL));

}
