package com.thealer.telehealer.views.home.monitoring.diet;

import android.content.Context;

import com.thealer.telehealer.R;

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


    public static ArrayList<String> getDietPrintOptions(Context context) {
        return new ArrayList<>(Arrays.asList(context.getString(R.string.PRINT_1_WEEK), context.getString(R.string.PRINT_2_WEEK), context.getString(R.string.PRINT_1_MONTH), context.getString(R.string.custom_range)));
    }
}
