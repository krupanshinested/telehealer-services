package com.thealer.telehealer.views.home.monitoring.diet;

import android.content.Context;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 25,March,2019
 */
class DietPdfGenerator {
    private final String pdfHtml = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "    <head>\n" +
            "        <meta charset=\"utf-8\" />\n" +
            "        <title>Chat Details</title>\n" +
            "    </head>\n" +
            "    <body style=\"margin:40px;overflow: visible;\">\n" +
            "        <style>\n" +
            "            div {\n" +
            "                text-align: center;\n" +
            "                position: relative;\n" +
            "                overflow: visible;\n" +
            "            }\n" +
            "        span {\n" +
            "            display: inline-block;\n" +
            "            overflow: visible;\n" +
            "        }\n" +
            "        span:before,\n" +
            "        span:after {\n" +
            "            border-top: 1px solid black;\n" +
            "            display: block;\n" +
            "            height: 1px;\n" +
            "            content: \" \";\n" +
            "            width: 40%;\n" +
            "            position: absolute;\n" +
            "            left: 0;\n" +
            "            top: 0.5em;\n" +
            "        }\n" +
            "        span:after {\n" +
            "            right: 0;\n" +
            "            left: auto;\n" +
            "        }\n" +
            "        \n" +
            "        h2 {\n" +
            "            display: block;\n" +
            "            margin-top: 0.33em;\n" +
            "            margin-bottom: 0.33em;\n" +
            "            margin-left: 0;\n" +
            "            margin-right: 0;\n" +
            "            font-weight: bold;\n" +
            "        }\n" +
            "        \n" +
            "        h4 {\n" +
            "            display: block;\n" +
            "            margin-top: 0.33em;\n" +
            "            margin-bottom: 0.33em;\n" +
            "            margin-left: 0;\n" +
            "            margin-right: 0;\n" +
            "            font-weight: bold;\n" +
            "        }\n" +
            "        \n" +
            "        </style>\n" +
            "        <table width=\"100%\" height=\"20\">\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"60%\">\n" +
            "                <h2><b><font face=\"Helvetica Neue\">#TITLE#</font><b></h2>\n" +
            "                <h4><font face=\"Helvetica Neue\" color=\"gray\">#DATE#</font></h4>\n" +
            "                </td>\n" +
            "                <td width=\"40%\" align = \"right\">\n" +
            "                \n" +
            "                <table><tr><td><img src=\"#ICON_DATA#\" alt=\"LOGO\" height=\"60\" width=\"60\"></td><td> <h2><b><font face=\"Helvetica Neue\">Telehealer</font><b></h2></td></tr></table>\n" +
            "                </td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        <hr color = \"#BACKGROUND_COLOR#\">\n" +
            "        <br>\n" +
            "        <table width=\"100%\" height=\"20\">\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_NAME_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_NAME#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_DOB_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_DOB#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_GENDER_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\"size=\"4\" >#PATIENT_GENDER#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\" size=\"4\">#PERIOD_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\" size=\"4\">#PERIOD#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        </br>\n" +
            "        <table width = \"100%\" height = \"40\">\n" +
            "            <tr style=\"background-color:#EEEEEE; padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"15%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\" >#CREATED_AT_LABEL#</font></td>\n" +
            "                <td width=\"45%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#VALUE_LABEL#</font></td>\n" +
            "                <td width=\"40%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#NUTRIENTS_LABEL#</font></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        <table width = \"100%\" height = \"50\" border=1 frame=void rules=rows cellpadding=\"10\">\n" +
            "            #BODY#\n" +
            "        </table>\n" +
            "    </body>\n" +
            "</html>";
    private final String pdfHtmlBody = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
            "    <td width=\"15%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#CREATED_AT#</font></td>\n" +
            "    <td width=\"45%\" align = \"left\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">\n" +
            "        <table width = \"100%\" height = \"50\">\n" +
            "        #VALUE#\n" +
            "        </table>\n" +
            "    </font></td>\n" +
            "    <td width=\"40%\" align = \"left\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">\n" +
            "        <table width = \"100%\" height = \"50\">\n" +
            "        #NUTRITION#\n" +
            "        </table>\n" +
            "    </font></td>\n" +
            "</tr>\n";
    private final String pdfHtmlBodyContent = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
            "    <td width=\"50%\" valign=\"top\" align = \"left\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#SUB_TITLE#</font></td>\n" +
            "    <td width=\"50%\" align = \"left\"><b><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#SUB_VALUE#</br></font></b></td>\n" +
            "</tr>";

    private Context context;

    private final String PDF_TITLE = "#TITLE#";
    private final String PDF_DATE = "#DATE#";
    private final String PDF_ICON = "#ICON_DATA#";
    private final String PDF_BACKGROUND_COLOR = "#BACKGROUND_COLOR#";
    private final String PATIENT_NAME_LABLE = "#PATIENT_NAME_LABEL#";
    private final String PATIENT_NAME = "#PATIENT_NAME#";
    private final String PATIENT_DOB_LABLE = "#PATIENT_DOB_LABEL#";
    private final String PATIENT_DOB = "#PATIENT_DOB#";
    private final String PATIENT_GENDER_LABLE = "#PATIENT_GENDER_LABEL#";
    private final String PATIENT_GENDER = "#PATIENT_GENDER#";
    private final String PERIOD_LABLE = "#PERIOD_LABEL#";
    private final String PERIOD = "#PERIOD#";
    private final String CREATED_AT_LABLE = "#CREATED_AT_LABEL#";
    private final String VALUE_LABLE = "#VALUE_LABEL#";
    private final String NUTRIENTS_LABLE = "#NUTRIENTS_LABEL#";
    private final String PDF_BODY = "#BODY#";

    private final String CREATED_AT = "#CREATED_AT#";
    private final String VALUE = "#VALUE#";
    private final String NUTRITION = "#NUTRITION#";

    private final String CONTENT_TITLE = "#SUB_TITLE#";
    private final String CONTENT_VALUE = "#SUB_VALUE#";


    public DietPdfGenerator(Context context) {
        this.context = context;
    }

    public String getPdfHtmlContent(Map<String, ArrayList<DietApiResponseModel>> pdfList, UserBean userBean) {
        String body = getBody(pdfList);
        String pdfHtml = getHtml(pdfList, userBean);
        return pdfHtml.replace(PDF_BODY, body);
    }

    private String getHtml(Map<String, ArrayList<DietApiResponseModel>> pdfList, UserBean userBean) {
        String nameLable = context.getString(R.string.pdf_label_name);
        String dobLable = context.getString(R.string.pdf_label_dob);
        String genderLable = context.getString(R.string.pdf_label_gender);
        String periodLable = context.getString(R.string.pdf_label_period);

        String title = context.getString(R.string.diet_report);
        String date = Utils.getCurrentFomatedDate();
        String icon;
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            icon = "app_icon_patient.png";
        } else {
            icon = "app_icon_doctor.png";
        }
        String backgroundColor = context.getString(R.string.app_gradient_start);
        String created_at_label = context.getString(R.string.date).toUpperCase();
        String value_label = context.getString(R.string.food).toUpperCase();
        String nutrients_label = context.getString(R.string.nutrition_facts).toUpperCase();

        String name, dob, gender;
        String period = calculatePeriod(pdfList.keySet());
        if (userBean != null) {
            name = userBean.getName();
            dob = userBean.getDob().replace("DoB : ", "");
            gender = userBean.getGender();
        } else {
            name = UserDetailPreferenceManager.getUserDisplayName();
            dob = UserDetailPreferenceManager.getDob().replace("DoB : ", "");
            gender = UserDetailPreferenceManager.getGender();
        }

        return pdfHtml
                .replace(PDF_TITLE, title)
                .replace(PDF_DATE, date)
                .replace(PDF_ICON, icon)
                .replace(PDF_BACKGROUND_COLOR, backgroundColor)
                .replace(PATIENT_NAME_LABLE, nameLable)
                .replace(PATIENT_DOB_LABLE, dobLable)
                .replace(PATIENT_GENDER_LABLE, genderLable)
                .replace(PERIOD_LABLE, periodLable)
                .replace(PATIENT_NAME, name)
                .replace(PATIENT_DOB, dob)
                .replace(PATIENT_GENDER, gender)
                .replace(PERIOD, period)
                .replace(CREATED_AT_LABLE, created_at_label)
                .replace(VALUE_LABLE, value_label)
                .replace(NUTRIENTS_LABLE, nutrients_label);
    }

    private String calculatePeriod(Set<String> keySet) {
        String endingDate = Utils.getCurrentFomatedDate();

        List<String> sortedDate = new ArrayList<>(keySet);
        Collections.sort(sortedDate, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Utils.getDateFromString(o1).compareTo(Utils.getDateFromString(o2));
            }
        });

        return Utils.getDayMonthYear(sortedDate.get(0)) + " - " + endingDate;
    }

    private String getBody(Map<String, ArrayList<DietApiResponseModel>> pdfList) {
        StringBuilder bodyBuilder = new StringBuilder();

        Set<String> dates = pdfList.keySet();
        List<String> sortedDate = new ArrayList<>(dates);
        Collections.sort(sortedDate, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Utils.getDateFromString(o2).compareTo(Utils.getDateFromString(o1));
            }
        });

        for (String date : sortedDate) {

            ArrayList<DietApiResponseModel> dietApiResponseModels = pdfList.get(date);

            String body = getData(date, dietApiResponseModels);

            bodyBuilder = bodyBuilder.append(body);
        }

        return bodyBuilder.toString();
    }

    private String getData(String date, ArrayList<DietApiResponseModel> dietApiResponseModels) {

        int calories = 0, protein = 0, fat = 0, carbs = 0;

        List<String> breakfastList = new ArrayList<>();
        List<String> lunchList = new ArrayList<>();
        List<String> dinnerList = new ArrayList<>();
        List<String> snacksList = new ArrayList<>();

        for (int i = 0; i < dietApiResponseModels.size(); i++) {
            if (dietApiResponseModels.get(i).getFood() != null) {
                String mealType = dietApiResponseModels.get(i).getMeal_type();
                if (mealType.equalsIgnoreCase(DietConstant.TYPE_BREAKFAST)) {
                    breakfastList.add(dietApiResponseModels.get(i).getFood().getName());
                } else if (mealType.equalsIgnoreCase(DietConstant.TYPE_LUNCH)) {
                    lunchList.add(dietApiResponseModels.get(i).getFood().getName());
                } else if (mealType.equalsIgnoreCase(DietConstant.TYPE_DINNER)) {
                    dinnerList.add(dietApiResponseModels.get(i).getFood().getName());
                } else if (mealType.equalsIgnoreCase(DietConstant.TYPE_SNACKS)) {
                    snacksList.add(dietApiResponseModels.get(i).getFood().getName());
                }

                if (dietApiResponseModels.get(i).getFood().getTotalNutrients() != null) {
                    if (dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY) != null)
                        calories = (int) (calories + dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY).getQuantity());

                    if (dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS) != null)
                        carbs = (int) (carbs + dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS).getQuantity());

                    if (dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT) != null)
                        fat = (int) (fat + dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT).getQuantity());

                    if (dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN) != null)
                        protein = (int) (protein + dietApiResponseModels.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN).getQuantity());
                }
            }
        }

        String nutrientList = getDietValueHtml(context.getString(R.string.energy), String.valueOf(calories)) +
                getDietValueHtml(context.getString(R.string.protein), String.valueOf(protein)) +
                getDietValueHtml(context.getString(R.string.fat), String.valueOf(fat)) +
                getDietValueHtml(context.getString(R.string.carbohydrates), String.valueOf(carbs));

        String dietValueList = getDietValueHtml(DietConstant.TYPE_BREAKFAST, breakfastList) +
                getDietValueHtml(DietConstant.TYPE_LUNCH, lunchList) +
                getDietValueHtml(DietConstant.TYPE_DINNER, dinnerList) +
                getDietValueHtml(DietConstant.TYPE_SNACKS, snacksList);

        return pdfHtmlBody.replace(CREATED_AT, Utils.getDayMonthYear(date))
                .replace(VALUE, dietValueList)
                .replace(NUTRITION, nutrientList);
    }

    private String getDietValueHtml(String title, List<String> value) {
        String contentValue;
        if (value.isEmpty()) {
            contentValue = "-";
        } else {
            contentValue = value.toString().substring(1, value.toString().length() - 1);
        }
        return pdfHtmlBodyContent.replace(CONTENT_TITLE, title).replace(CONTENT_VALUE, contentValue);
    }

    private String getDietValueHtml(String title, String value) {
        String unit = FoodConstant.UNIT_GRAM;

        if (title.equals(context.getString(R.string.energy))) {
            unit = FoodConstant.UNIT_CAL;
        }
        return pdfHtmlBodyContent.replace(CONTENT_TITLE, title).replace(CONTENT_VALUE, value + " " + unit);
    }
}
