package com.thealer.telehealer.views.home.vitals;

import android.content.Context;
import android.util.Log;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.common.VitalCommon.VitalsConstant;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            "        #VITAL_BODY#\n" +
            "    </body>\n" +
            "</html>";
    private final String htmlBody = "</br>\n" +
            "<b><font face=\"Helvetica Neue\" color = \"black\" size=\"4\">#VITAL_CATEGORY_TITLE#</font></b>" +
            "</b></br>\n" +
            "<table width = \"100%\" height = \"40\">\n" +
            "    <tr style=\"background-color:#EEEEEE; padding-left: 4px; padding-right: 4px;\">\n" +
            "        <td width=\"30%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\" >#CREATED_AT_LABEL#</font></td>\n" +
            "        <td width=\"20%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#VALUE_LABEL#</font></td>\n" +
            "        <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#CAPTURED_BY_LABEL#</font></td>\n" +
            "        <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#MODE_LABEL#</font></td>\n" +
            "    </tr>\n" +
            "</table>\n" +
            "<table width = \"100%\" height = \"50\">\n" +
            "    #MESSAGE_INFO#\n" +
            "</table>";
    private final String htmlList = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
            "    <td width=\"30%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#CREATED_AT#</font></td>\n" +
            "    <td width=\"20%\" align = \"center\"><b><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#VALUE#</font></b></td>\n" +
            "    <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#CREATED_BY#</font></td>\n" +
            "    <td width=\"25%\" align = \"center\"><font face=\"Helvetica Neue\" color = \"black\" size=\"3\">#MODE#</font></td>\n" +
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
    private final String PDF_BODY = "#VITAL_BODY#";

    private final String VITAL_CATEGORY_TITLE = "#VITAL_CATEGORY_TITLE#";
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
    private boolean isVitalReport;
    String createdAt, value, capturedBy, mode, category = "", startDate, endDate;

    public VitalPdfGenerator(Context context) {
        this.context = context;
        createdAt = context.getString(R.string.date).toUpperCase();
        value = context.getString(R.string.vitals).toUpperCase();
        capturedBy = context.getString(R.string.done_by).toUpperCase();
        mode = context.getString(R.string.mode).toUpperCase();
    }


    public String generatePdfFor(List<VitalsApiResponseModel> pdfList, CommonUserApiResponseModel commonUserApiResponseModel, boolean isVitalReport, String startDate, String endDate) {
        this.isVitalReport = isVitalReport;
        this.startDate = startDate;
        this.endDate = endDate;

        String headerDetail = generateHeaderDetail(pdfList, commonUserApiResponseModel);

        String body = generateBody(pdfList);

        String displayHtml = headerDetail.replace(PDF_BODY, body);

        return displayHtml;
    }

    private String generateBody(List<VitalsApiResponseModel> pdfList) {
        Map<String, List<VitalsApiResponseModel>> vitalsMap = new HashMap<>();

        for (VitalsApiResponseModel vitalsApiResponseModel : pdfList) {

            if (vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
                continue;
            }

            List<VitalsApiResponseModel> apiResponseModelList = new ArrayList<>();

            if (!vitalsMap.containsKey(vitalsApiResponseModel.getType())) {
                vitalsMap.put(vitalsApiResponseModel.getType(), apiResponseModelList);
            }

            apiResponseModelList = vitalsMap.get(vitalsApiResponseModel.getType());

            apiResponseModelList.add(vitalsApiResponseModel);

            vitalsMap.put(vitalsApiResponseModel.getType(), apiResponseModelList);
        }

        StringBuilder body = new StringBuilder();

        for (String key : vitalsMap.keySet()) {

            String pdfBody = htmlBody;

            String itemsList = generateList(vitalsMap.get(key));

            if (isVitalReport) {
                category = context.getString(getCategoryTitle(key)) + " " + context.getString(R.string.values);
            }

            pdfBody = pdfBody
                    .replace(VITAL_CATEGORY_TITLE, category)
                    .replace(CREATED_AT_LABLE, createdAt)
                    .replace(VALUE_LABLE, value)
                    .replace(CAPTURED_BY_LABLE, capturedBy)
                    .replace(MODE_LABLE, mode)
                    .replace(MESSAGE_INFO, itemsList);

            body = body.append(pdfBody);
        }

        return body.toString();
    }

    private int getCategoryTitle(String key) {
        switch (key) {
            case SupportedMeasurementType.bp:
                return R.string.blood_pressure;
            case SupportedMeasurementType.weight:
                return R.string.weight;
            case SupportedMeasurementType.temperature:
                return R.string.bodyTemperature;
            case SupportedMeasurementType.gulcose:
                return R.string.bloodGlucose;
            case SupportedMeasurementType.pulseOximeter:
                return R.string.pulseOximeter;
            case SupportedMeasurementType.heartRate:
                return R.string.heartRate;
            default:
                return R.string.vitals_report;
        }
    }

    private String generateHeaderDetail(List<VitalsApiResponseModel> pdfList, CommonUserApiResponseModel commonUserApiResponseModel) {
        String headerString = htmlHeaderDetail;

        String nameLable = context.getString(R.string.pdf_label_name);
        String dobLable = context.getString(R.string.pdf_label_dob);
        String genderLable = context.getString(R.string.pdf_label_gender);
        String periodLable = context.getString(R.string.pdf_label_period);

        String pdfTitle = context.getString(R.string.vitals_report);
        if (!isVitalReport)
            pdfTitle = context.getString(SupportedMeasurementType.getTitle(pdfList.get(0).getType())) + " " + context.getString(R.string.report);

        String pdfDate = Utils.getCurrentFomatedDate();
        String icon = "pdf_icon.png";
        String patientName;
        String dob;
        String gender;
        String period = calculatePeriod();
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
                .replace(CREATED_AT_LABLE, createdAt)
                .replace(VALUE_LABLE, value)
                .replace(CAPTURED_BY_LABLE, capturedBy)
                .replace(MODE_LABLE, mode)
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

    private String calculatePeriod() {
        Log.e("aswin", "calculatePeriod: " + startDate);
        Log.e("aswin", "calculatePeriod: " + endDate);
        if (startDate == null && endDate == null) {
            Calendar calendar = Calendar.getInstance();
            endDate = Utils.getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());
            calendar.add(Calendar.DAY_OF_WEEK, -7);
            startDate = Utils.getUTCfromGMT(new Timestamp(calendar.getTimeInMillis()).toString());
        }
        return Utils.getDayMonthYear(startDate) + " - " + Utils.getDayMonthYear(endDate);

    }

    private String generateList(List<VitalsApiResponseModel> pdfList) {
        StringBuilder listString = new StringBuilder();
        for (VitalsApiResponseModel response : pdfList) {
            String listItem = htmlList;

            String createdAt = Utils.getFormatedDateTime(response.getCreated_at());
            String value = response.getValue().toString() + " " + SupportedMeasurementType.getVitalUnit(response.getType());
            String mode = "";
            switch (response.getMode()) {
                case VitalsConstant.VITAL_MODE_DEVICE:
                    mode = context.getString(VitalsConstant.LABLE_AUTOMATED);
                    break;
                case VitalsConstant.VITAL_MODE_DOCTOR:
                case VitalsConstant.VITAL_MODE_PATIENT:
                    mode = context.getString(VitalsConstant.LABLE_MANUAL);
                    break;
            }

            String createdBy = "";
            if (response.getDisplay_name() != null && !response.getDisplay_name().isEmpty()) {
                createdBy = response.getDisplay_name();
            } else {
                switch (response.getMode()) {
                    case VitalsConstant.VITAL_MODE_DOCTOR:
                        createdBy = context.getString(VitalsConstant.LABLE_DOCTOR);
                        break;
                    case VitalsConstant.VITAL_MODE_DEVICE:
                    case VitalsConstant.VITAL_MODE_PATIENT:
                        createdBy = context.getString(VitalsConstant.LABLE_PATIENT);
                        break;
                }
            }

            listItem = listItem.replace(CREATED_AT, createdAt).replace(VALUE, value).replace(CREATED_BY, createdBy).replace(MODE, mode);
            listString.append(listItem);
        }
        return listString.toString();
    }
}
