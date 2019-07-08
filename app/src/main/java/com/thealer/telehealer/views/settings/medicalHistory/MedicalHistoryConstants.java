package com.thealer.telehealer.views.settings.medicalHistory;

import android.content.Context;
import android.text.InputType;

import com.thealer.telehealer.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 21,January,2019
 */
public class MedicalHistoryConstants {
    public static final String MH_MEDICATION = "Medication";
    public static final String MH_PAST_MEDICAL_HISTORY = "Past Medical History";
    public static final String MH_SURGERIES = "Surgeries";
    public static final String MH_FAMILY_HISTORY = "Family History";
    public static final String MH_PERSONAL_HISTORY = "Personal History";
    public static final String MH_HEALTH_HABITS = "Health Habits";
    public static final String MH_SEXUAL_HISTORY = "Sexual History";
    public static final String MH_RECENT_IMMUNIZATION = "Recent Immunization";

    public static int getIcon(String item) {
        switch (item) {
            case MH_MEDICATION:
                return R.drawable.ic_mh_medication;
            case MH_PAST_MEDICAL_HISTORY:
                return R.drawable.ic_medical_history;
            case MH_SURGERIES:
                return R.drawable.ic_mh_surgery;
            case MH_FAMILY_HISTORY:
                return R.drawable.ic_mh_family;
            case MH_PERSONAL_HISTORY:
                return R.drawable.ic_mh_personal;
            case MH_HEALTH_HABITS:
                return R.drawable.ic_mh_habits;
            case MH_SEXUAL_HISTORY:
                return R.drawable.ic_mh_sexaul;
            case MH_RECENT_IMMUNIZATION:
                return R.drawable.ic_mh_immunization;
        }
        return 0;
    }

    public static int getTitle(String item) {
        switch (item) {
            case MH_MEDICATION:
                return R.string.info_medication;
            case MH_PAST_MEDICAL_HISTORY:
                return R.string.info_past_medical_history;
            case MH_SURGERIES:
                return R.string.info_surgeries;
            case MH_FAMILY_HISTORY:
                return R.string.info_family_history;
            case MH_PERSONAL_HISTORY:
                return R.string.info_personal_history;
            case MH_HEALTH_HABITS:
                return R.string.info_health_habits;
            case MH_SEXUAL_HISTORY:
                return R.string.info_sexual_history;
            case MH_RECENT_IMMUNIZATION:
                return R.string.info_recent_immunization;
        }
        return 0;
    }

    public static final String[] itemList = {MH_MEDICATION, MH_PAST_MEDICAL_HISTORY, MH_SURGERIES,
            MH_FAMILY_HISTORY, MH_PERSONAL_HISTORY, MH_HEALTH_HABITS, MH_SEXUAL_HISTORY, MH_RECENT_IMMUNIZATION};

    public static String getDisplayTitle(Context context, String type) {
        switch (type) {
            case MH_MEDICATION:
                return context.getString(R.string.medication);
            case MH_PAST_MEDICAL_HISTORY:
                return context.getString(R.string.past_medical_history);
            case MH_SURGERIES:
                return context.getString(R.string.surgeries);
            case MH_FAMILY_HISTORY:
                return context.getString(R.string.family_history);
            case MH_PERSONAL_HISTORY:
                return context.getString(R.string.personal_history);
            case MH_HEALTH_HABITS:
                return context.getString(R.string.health_habits);
            case MH_SEXUAL_HISTORY:
                return context.getString(R.string.sexual_history);
            case MH_RECENT_IMMUNIZATION:
                return context.getString(R.string.recent_immunization);
        }
        return "";
    }

    /**
     * For medication
     */

    public static final String[] metrics = {"mg", "mcg", "gm"};
    public static final String[] directionOne = {"Tablet", "Capsule", "Drops", "Suppository", "Ointment", "Injection", "Inhaler", "Cream", "Lozenge", "Syrup", "Powder", "Spray", "Gel", "Lotion", "Mouthwash", "Shampoo", "Solution", "Suspension", "Sachet"};
    public static final String[] directionTwo = {"Daily", "Twice a day", "Three times a day", "Four times a day", "Every <N> hours", "Before meals", "After meals", "Before bedtime", "In the morning", "In the evening", "At Noon"};

    /**
     * For Past medical history
     */
    public static final List<String> pastMedicalHistoryList = Arrays.asList("Diabetes", "High Blood pressure", "High Cholesterol", "Hypothyroidism", "Goiter", "Leukemia", "Psoriasis", "Angina", "Heart problems", "Heart murmur", "Cancer (type)", "Pneumonia", "Pulmonary embolism", "Asthma", "Emphysema", "Stroke", "Epilepsy (seizures)", "Cataracts", "Kidney disease", "Kidney stones", "Crohn's disease", "Colitis", "Anemia", "Jaundice", "Hepatitis", "Stomach or peptic ulcer", "Rheumatic fever", "Tuberculosis", "HIV/AIDS");

    /**
     * For Surgeries
     */
    public static final List<String> surgeriesList = Arrays.asList("Cataract Removal", "Tonsil Removal", "Appendix Removal", "Gallbladder Removal", "Wisdom teeth Removal", "Dental implants", "Hip replacement", "Knee replacement", "Shoulder surgery", "Fracture repair", "Breast surgery", "C - Section", "Kidney stone removal", "Stomach surgeries(bypass)", "Heart bypass / stents", "Pacemaker", "Spine surgeries", "Vasectomies", "Circumcision", "Bilateral tubal ligation", "Thyroid surgery", "Hysterectomy", "Hernia repair");

    /**
     * for family history
     */
    public static final List<String> familyHistoryList = Arrays.asList("Alcoholism or Drug Use", "Diabetes", "Heart Disease", "High Blood Pressure", "High Cholesterol", "Osteoporosis", "Mental Illness", "Stroke", "Thyroid Disease", "Cancer");

    public static final List<String> relationList = Arrays.asList("Mother", "Father", "Sister", "Brother", "GrandMother (mother's Side)", "GrandFather (mother's Side)", "GrandMother (father's Side)", "GrandFather (father's Side)", "Child");

    /**
     * for recent immunization
     */

    public static final List<String> recentImmunizationList = Arrays.asList("Tetanus", "Pneumonia", "Influenza", "Hepatitis B");

    /**
     * for sexual history
     */

    public static final String SEXUALLY_ACTIVE = "Are you sexually active?";
    public static final String EVER_BEEN_PREGNANT = "Have you ever been pregnant?(Woman Only)";
    public static final String MENSURAL_PERIODS = "Do you have menstrual periods?";

    public static final List<String> sexualHistoryList = Arrays.asList(SEXUALLY_ACTIVE, EVER_BEEN_PREGNANT, MENSURAL_PERIODS);

    public static final String HOW_MANY_TIMES = "How many times?";
    public static final String HOW_MANY_MISSCARRIAGES = "How many miscarriages?";
    public static final String HOW_MANY_ABORTIONS = "How many abortions?";
    public static final String HOW_MANY_CHILDRES = "How many children do you have living?";
    public static final String PERIODS_STOPPED_AT = "If no, at what age did they stop?";
    public static final String IS_PERIOD_REGULAR = "If yes, are your periods regular?";

    public static List<String> pregnant_questions = Arrays.asList(HOW_MANY_TIMES, HOW_MANY_MISSCARRIAGES, HOW_MANY_ABORTIONS, HOW_MANY_CHILDRES);
    public static List<String> mensural_questions = Arrays.asList(PERIODS_STOPPED_AT, IS_PERIOD_REGULAR);


    /**
     * For health habits
     */

    public static final String DO_YOU_SMOKE = "Do you smoke or use any tobacco products?";
    public static final String DO_YOU_DRINK = "Do you drink alcohol?";
    public static final String USED_OTHER_DRUGS = "Have you regularly used other drugs?";

    public static final List<String> healthHabitList = Arrays.asList(DO_YOU_SMOKE, DO_YOU_DRINK, USED_OTHER_DRUGS);

    public static final String[] yesNoQuit = {"Yes", "No", "Quit"};

    public static final String NO_OF_CIGRATTES = "Number of cigarettes each day?";
    public static final String HOW_MANY_YEARS = "For how many years?";
    public static final String OTHER_FORMS_OF_TOBACCO = "Other forms of tobacco used?";
    public static final String HOW_MUCH = "How much? ( in litres )";
    public static final String HOW_OFTEN = "How often? ( in weeks )";
    public static final String CUT_DOWN = "Have you ever felt that you should cut down on your drinking?";
    public static final String STILL_USING = "If yes, are you still using them?";

    public static List<String> smoke_questions = Arrays.asList(NO_OF_CIGRATTES, HOW_MANY_YEARS, OTHER_FORMS_OF_TOBACCO);
    public static List<String> drink_questions = Arrays.asList(HOW_MUCH, HOW_OFTEN, CUT_DOWN);
    public static List<String> drugs_questions = Collections.singletonList(STILL_USING);

    /**
     * For Personal history
     */

    public static final String PROBLEM_WITH_YOUR_BIRTH = "Problems with your birth?";
    public static final String BORN_AND_RAISE = "Where were you born & raised?";
    public static final String LEVEL_OF_EDUCATION = "Level of education?";
    public static final String MARITAL_STATUS = "Marital Status";
    public static final String CURRENT_OR_PAST_OCCUPATION = "What is your current or past occupation?";
    public static final String WORKING_HOURS = "How many hours do you work per week?";
    public static final String SPECIFIC_REASON_FOR_NOT_WORKING = "If you are not working specify the reason";
    public static final String DISABILITY_OR_SSI = "Did you receive disability or SSI?";
    public static final String EVER_HAD_LEGAL_PROBLEMS = "Have you ever had legal problems?";
    public static final String RELIGION = "Religion";

    public static final List<String> personalHistoryList = Arrays.asList(PROBLEM_WITH_YOUR_BIRTH, BORN_AND_RAISE, LEVEL_OF_EDUCATION, MARITAL_STATUS, CURRENT_OR_PAST_OCCUPATION, WORKING_HOURS, SPECIFIC_REASON_FOR_NOT_WORKING, DISABILITY_OR_SSI, EVER_HAD_LEGAL_PROBLEMS, RELIGION);

    public static final List<String> optionsRequired = Arrays.asList(SPECIFIC_REASON_FOR_NOT_WORKING, LEVEL_OF_EDUCATION, MARITAL_STATUS);

    public static final String[] notWorkingReasonList = {"Retired", "Disabled", "Sick Leave", "Student", "None"};
    public static final String[] educationLevelsList = {"School 1", "School 2", "School 3", "School 4", "School 5", "School 6", "School 7", "School 8", "School 9", "School 10", "School 11", "School 12", "College 1", "College 2", "College 3", "College 4"};
    public static final String[] maritalStatusList = {"Single", "Maried", "Divorced", "Separated", "Widowed", "Partnered/Significant Other"};

    public static String getPersonalHistoryInputHint(String title) {
        switch (title) {
            case BORN_AND_RAISE:
            case MARITAL_STATUS:
            case CURRENT_OR_PAST_OCCUPATION:
            case WORKING_HOURS:
            case RELIGION:
            case SPECIFIC_REASON_FOR_NOT_WORKING:
                return " ";
            case LEVEL_OF_EDUCATION:
                return "highest level of degree..";
            case DISABILITY_OR_SSI:
                return "if yes,what disability & how long";


        }
        return null;
    }


    /**
     * For common
     */
    public static final int LIST_YES_OR_NO = 1;
    public static final int LIST_SEXUALLY_ACTIVE_WITH = 2;
    public static final int LIST_YES_NO_QUIT = 3;
    public static final int LIST_NOT_WORKING_REASON = 4;
    public static final int LIST_EDUCATION_LEVEL = 5;
    public static final int LIST_MARITAL_STATUS = 6;

    public static final List<String> userInputRequiredList = Arrays.asList("Cancer (type)", "Cancer", DISABILITY_OR_SSI, EVER_HAD_LEGAL_PROBLEMS, WORKING_HOURS,
            LEVEL_OF_EDUCATION, PROBLEM_WITH_YOUR_BIRTH, RELIGION, CURRENT_OR_PAST_OCCUPATION);
    public static final String[] yesOrNoList = {"Yes", "No"};
    public static final String[] sexWithList = {"Men", "Women", "Both"};

    private static int inputTypeNumber = InputType.TYPE_CLASS_NUMBER;

    public static final Map<String, Integer> inputTypeMap = new HashMap<String, Integer>();

    public static Map<String, Integer> getInputTypeMap() {
        inputTypeMap.clear();
        inputTypeMap.put(HOW_MANY_TIMES, inputTypeNumber);
        inputTypeMap.put(HOW_MANY_MISSCARRIAGES, inputTypeNumber);
        inputTypeMap.put(HOW_MANY_ABORTIONS, inputTypeNumber);
        inputTypeMap.put(HOW_MANY_CHILDRES, inputTypeNumber);
        inputTypeMap.put(PERIODS_STOPPED_AT, inputTypeNumber);
        inputTypeMap.put(NO_OF_CIGRATTES, inputTypeNumber);
        inputTypeMap.put(HOW_MANY_YEARS, inputTypeNumber);
        inputTypeMap.put(HOW_MUCH, inputTypeNumber);
        inputTypeMap.put(HOW_OFTEN, inputTypeNumber);
        return inputTypeMap;
    }

    public static int getListType(String title) {
        switch (title) {
            case SPECIFIC_REASON_FOR_NOT_WORKING:
                return LIST_NOT_WORKING_REASON;
            case LEVEL_OF_EDUCATION:
                return LIST_EDUCATION_LEVEL;
            case MARITAL_STATUS:
                return LIST_MARITAL_STATUS;
        }
        return 0;
    }
}

//switch (item) {
//        case MH_MEDICATION:
//        case MH_PAST_MEDICAL_HISTORY:
//        case MH_SURGERIES:
//        case MH_FAMILY_HISTORY:
//        case MH_PERSONAL_HISTORY:
//        case MH_HEALTH_HABITS:
//        case MH_SEXUAL_HISTORY:
//        case MH_RECENT_IMMUNIZATION:
//        }