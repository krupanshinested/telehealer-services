package com.thealer.telehealer.views.home.monitoring.diet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aswin on 14,March,2019
 */
public class FoodConstant {
    public static final String FOOD_ENERGY = "ENERC_KCAL";
    public static final String FOOD_CARBS = "CHOCDF";
    public static final String FOOD_FAT = "FAT";
    public static final String FOOD_PROTEIN = "PROCNT";

    public static final Set<String> FAT_SET = new HashSet<>(Arrays.asList("FASAT", "FAMS", "FAPU", "FATRN"));

    public static final Set<String> CARBS_SET = new HashSet<>(Arrays.asList("FIBTG", "SUGAR", "SUGAR_added"));
    public static final String UNIT_GRAM = "g";
    public static final String UNIT_CAL = "cal";
}
