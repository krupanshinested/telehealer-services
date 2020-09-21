package com.thealer.telehealer.views.home.vitals;

import android.content.Context;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;

import java.util.List;

/**
 * Created by Aswin on 13,December,2018
 */
public class VitalPdfGenerator {

    private final String htmlHeaderDetail = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "    <head>\n" +
            "        <meta charset=\"utf-8\" />\n" +
            "        <title>Chat Details</title>\n" +
            "    </head>\n" +
            "    <body style=\"margin:40px\">\n" +
            "        <style>\n" +
            "            div {\n" +
            "                text-align: center;\n" +
            "                position: relative;\n" +
            "            }\n" +
            "        span {\n" +
            "            display: inline-block;\n" +
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
            "        </style>\n" +
            "        <table width=\"100%\" height=\"20\">\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"60%\">\n" +
            "                <h2><b><font face=\"Helvetica Neue\">#VITAL_TITLE#</font><b></h2>\n" +
            "                <h4><font face=\"Helvetica Neue\" color=\"gray\">#VITAL_DATE#</font></h4>\n" +
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
            "                <td width=\"20%\"><font face=\"Helvetica Neue\">#PATIENT_NAME_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\">#PATIENT_NAME#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\">#PATIENT_DOB_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\">#PATIENT_DOB#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\">#PATIENT_GENDER_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\">#PATIENT_GENDER#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"20%\"><font face=\"Helvetica Neue\">#PERIOD_LABEL#</font></td>\n" +
            "                <td width=\"80%\"><h4><b><font face=\"Helvetica Neue\">#PERIOD#</font></b></h4></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        <br>\n" +
            "        <table width = \"100%\" height = \"40\">\n" +
            "            <tr style=\"background-color:#EEEEEE; padding-left: 4px; padding-right: 4px;\">\n" +
            "                <td width=\"30%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#CREATED_AT_LABEL#</font></td>\n" +
            "                <td width=\"20%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#VALUE_LABEL#</font></td>\n" +
            "                <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#CAPTURED_BY_LABEL#</font></td>\n" +
            "                <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#MODE_LABEL#</font></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        <table width = \"100%\" height = \"50\">\n" +
            "        #MESSAGE_INFO#\n" +
            "        </table>\n" +
            "    </body>\n" +
            "</html>\n" +
            "\n" +
            "\n";
    private final String htmlList = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
            "    <td width=\"30%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#CREATED_AT#</font></td>\n" +
            "    <td width=\"20%\" align = \"center\"><b><font face=\"Helvetica Neue\" color = \"black\">#VALUE#</font></b></td>\n" +
            "    <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#CREATED_BY#</font></td>\n" +
            "    <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\">#MODE#</font></td>\n" +
            "</tr>\n";


    private final String PDF_TITLE = "#VITAL_TITLE#";
    private final String PDF_DATE = "#VITAL_DATE#";
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
    private final String CAPTURED_BY_LABLE = "#CAPTURED_BY_LABEL#";
    private final String MODE_LABLE = "#MODE_LABEL#";
    private final String MESSAGE_INFO = "#MESSAGE_INFO#";

    private final String CREATED_AT = "#CREATED_AT#";
    private final String VALUE = "#VALUE#";
    private final String CREATED_BY = "#CREATED_BY#";
    private final String MODE = "#MODE#";

    private Context context;

    public VitalPdfGenerator(Context context) {
        this.context = context;
    }


    public String generatePdfFor(List<VitalsApiResponseModel> pdfList, CommonUserApiResponseModel commonUserApiResponseModel) {
        String itemsList = generateList(pdfList);
        String headerDetail = generateHeaderDetail(pdfList, commonUserApiResponseModel);

        String displayHtml = headerDetail.replace(MESSAGE_INFO, itemsList);
        return displayHtml;
    }

    private String generateHeaderDetail(List<VitalsApiResponseModel> pdfList, CommonUserApiResponseModel commonUserApiResponseModel) {
        String headerString = htmlHeaderDetail;

        String nameLable = "Name : ";
        String dobLable = "Dob : ";
        String genderLable = "Gender : ";
        String periodLable = "Period : ";
        String createdAtLable = "DATE";
        String valueLable = "VITALS";
        String capturedByLable = "DONE BY";
        String modeLable = "MODE";
        String pdfTitle = context.getString(SupportedMeasurementType.getTitle(pdfList.get(0).getType())) + " Report";
        String pdfDate = Utils.getCurrentFomatedDate();
        String icon;
        if (BuildConfig.FLAVOR.equals(Constants.BUILD_PATIENT)) {
            icon = "app_icon_patient.png";
        } else {
            icon = "app_icon_doctor.png";
        }
        String patientName;
        String dob;
        String gender;
        String period = calculatePeriod(pdfList);
        if (commonUserApiResponseModel != null) {
            patientName = commonUserApiResponseModel.getUserDisplay_name();
            dob = commonUserApiResponseModel.getDob().replace("DoB : ", "");
            gender = commonUserApiResponseModel.getGender();
        } else {
            patientName = UserDetailPreferenceManager.getUserDisplayName();
            dob = UserDetailPreferenceManager.getDob();
            gender = UserDetailPreferenceManager.getGender();
        }

        headerString = headerString.replace(PATIENT_NAME_LABLE, nameLable)
                .replace(PATIENT_DOB_LABLE, dobLable)
                .replace(PATIENT_GENDER_LABLE, genderLable)
                .replace(PERIOD_LABLE, periodLable)
                .replace(CREATED_AT_LABLE, createdAtLable)
                .replace(VALUE_LABLE, valueLable)
                .replace(CAPTURED_BY_LABLE, capturedByLable)
                .replace(MODE_LABLE, modeLable)
                .replace(PDF_TITLE, pdfTitle)
                .replace(PDF_DATE, pdfDate)
                .replace(PDF_ICON, icon)
                .replace(PDF_BACKGROUND_COLOR, context.getString(R.string.app_gradient_start))
                .replace(PATIENT_NAME, patientName)
                .replace(PATIENT_DOB, dob)
                .replace(PATIENT_GENDER, gender)
                .replace(PERIOD, period);


        return headerString;
    }

    private String calculatePeriod(List<VitalsApiResponseModel> pdfList) {
        return Utils.getCurrentFomatedDate() + " - " + Utils.getDayMonthYear(pdfList.get(pdfList.size() - 1).getCreated_at());
    }

    private String generateList(List<VitalsApiResponseModel> pdfList) {
        StringBuilder listString = new StringBuilder();
        for (VitalsApiResponseModel response : pdfList) {
            String listItem = htmlList;

            String createdAt = Utils.getFormatedDateTime(response.getCreated_at());
            String value = response.getValue() + " " + SupportedMeasurementType.getVitalUnit(response.getType());
            String mode = "";
            switch (response.getMode()) {
                case VitalsConstant.VITAL_MODE_DEVICE:
                    mode = VitalsConstant.LABLE_AUTOMATED;
                    break;
                case VitalsConstant.VITAL_MODE_DOCTOR:
                case VitalsConstant.VITAL_MODE_PATIENT:
                    mode = VitalsConstant.LABLE_MANUAL;
                    break;
            }

            String createdBy = "";
            if (response.getDisplay_name() != null && !response.getDisplay_name().isEmpty()) {
                createdBy = response.getDisplay_name();
            } else {
                switch (response.getMode()) {
                    case VitalsConstant.VITAL_MODE_DOCTOR:
                        createdBy = VitalsConstant.LABLE_DOCTOR;
                        break;
                    case VitalsConstant.VITAL_MODE_DEVICE:
                    case VitalsConstant.VITAL_MODE_PATIENT:
                        createdBy = VitalsConstant.LABLE_PATIENT;
                        break;
                }
            }

            listItem = listItem.replace(CREATED_AT, createdAt).replace(VALUE, value).replace(CREATED_BY, createdBy).replace(MODE, mode);
            listString.append(listItem);
        }
        return listString.toString();
    }
}
