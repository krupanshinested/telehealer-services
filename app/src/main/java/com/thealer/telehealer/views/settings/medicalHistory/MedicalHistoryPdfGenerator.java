package com.thealer.telehealer.views.settings.medicalHistory;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.QuestionnaireBean;
import com.thealer.telehealer.common.Utils;

import java.util.Locale;

/**
 * Created by Aswin on 09,April,2019
 */
public class MedicalHistoryPdfGenerator {
    private Context context;
    private String html = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "    <head>\n" +
            "        <meta charset=\"utf-8\" />\n" +
            "        <title>Health Profile</title>\n" +
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
            "        </table>\n" +
            "        #BODY#\n" +
            "    </body>\n" +
            "</html>";

    private String body = "</br>\n" +
            "<b><font face=\"Helvetica Neue\" color = \"black\" size=\"4\">#CATEGORY_TITLE#</font></b>\n" +
            "</br>\n" +
            "<font face=\"Helvetica Neue\" size=\"3\">#DETAIL#</font>";

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
    private final String PDF_BODY = "#BODY#";
    private final String CATEGORY_TITLE = "#CATEGORY_TITLE#";
    private final String DETAIL = "#DETAIL#";

    private QuestionnaireBean questionnaireBean;
    private CommonUserApiResponseModel userModel;

    public MedicalHistoryPdfGenerator(Context context) {
        this.context = context;
    }

    public String getPdfHtml(CommonUserApiResponseModel commonUserApiResponseModel) {

        userModel = commonUserApiResponseModel;
        questionnaireBean = commonUserApiResponseModel.getQuestionnaire();

        String bodyData = getBody();

        String pdfContent = getContent();

        return pdfContent.replace(PDF_BODY, bodyData);
    }

    private String getBody() {
        StringBuilder builder = new StringBuilder();

        if (questionnaireBean.getMedication() != null &&
                !questionnaireBean.getMedication().getItems().isEmpty()) {

            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getMedication().getItems().size(); i++) {
                String medicationDetail = ((questionnaireBean.getMedication().getItems().get(i).getDirection() == null || questionnaireBean.getMedication().getItems().get(i).getDirection().isEmpty()) ? 0 : questionnaireBean.getMedication().getItems().get(i).getDirection()) + " tablet " + questionnaireBean.getMedication().getItems().get(i).getDirectionType1() + " " + questionnaireBean.getMedication().getItems().get(i).getDirectionType2();

                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getMedication().getItems().get(i).getDrugName(), medicationDetail));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_MEDICATION)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);
        }

        if (questionnaireBean.getSurgeries() != null &&
                !questionnaireBean.getSurgeries().getItems().isEmpty()) {

            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s</br>";

            for (int i = 0; i < questionnaireBean.getSurgeries().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getSurgeries().getItems().get(i).getTitle()));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_SURGERIES)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);

        }

        if (questionnaireBean.getFamilyHistoryBean() != null &&
                !questionnaireBean.getFamilyHistoryBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getFamilyHistoryBean().getItems().size(); i++) {
                detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getFamilyHistoryBean().getItems().get(i).getTitle(),
                        questionnaireBean.getFamilyHistoryBean().getItems().get(i).getSelectedRelationsString()));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_FAMILY_HISTORY)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);

        }

        if (questionnaireBean.getSexualHistoryBean() != null &&
                !questionnaireBean.getSexualHistoryBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getSexualHistoryBean().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getSexualHistoryBean().getItems().get(i).getTitle(),
                        questionnaireBean.getSexualHistoryBean().getItems().get(i).getDetailString((FragmentActivity) context)));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_SEXUAL_HISTORY)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);

        }

        if (questionnaireBean.getPersonalHistoryBean() != null &&
                !questionnaireBean.getPersonalHistoryBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getPersonalHistoryBean().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getPersonalHistoryBean().getItems().get(i).getTitle(),
                        questionnaireBean.getPersonalHistoryBean().getItems().get(i).getDetailString()));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_PERSONAL_HISTORY)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);
        }

        if (questionnaireBean.getHealthHabitBean() != null &&
                !questionnaireBean.getHealthHabitBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getHealthHabitBean().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getHealthHabitBean().getItems().get(i).getTitle(),
                        questionnaireBean.getHealthHabitBean().getItems().get(i).getDetailString((FragmentActivity) context)));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_HEALTH_HABITS)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);
        }

        if (questionnaireBean.getRecentImmunizationBean() != null &&
                !questionnaireBean.getRecentImmunizationBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s - %s</br>";

            for (int i = 0; i < questionnaireBean.getRecentImmunizationBean().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getRecentImmunizationBean().getItems().get(i).getTitle(),
                        questionnaireBean.getRecentImmunizationBean().getItems().get(i).getAdditionalInformation()));
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_RECENT_IMMUNIZATION)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);
        }

        if (questionnaireBean.getPastMedicalHistoryBean() != null &&
                !questionnaireBean.getPastMedicalHistoryBean().getItems().isEmpty()) {
            String bodyData = body;
            StringBuilder detail = new StringBuilder();
            String dataItem = "%d) %s";

            for (int i = 0; i < questionnaireBean.getPastMedicalHistoryBean().getItems().size(); i++) {
                detail = detail.append(String.format(Locale.getDefault(), dataItem, (i + 1), questionnaireBean.getPastMedicalHistoryBean().getItems().get(i).getTitle()));

                if (questionnaireBean.getPastMedicalHistoryBean().getItems().get(i).getAdditionalInformation() != null &&
                        !questionnaireBean.getPastMedicalHistoryBean().getItems().get(i).getAdditionalInformation().isEmpty()) {
                    detail = detail.append(String.format(" - %s", questionnaireBean.getPastMedicalHistoryBean().getItems().get(i).getAdditionalInformation()));
                }

                detail = detail.append("</br>");
            }

            bodyData = bodyData.replace(CATEGORY_TITLE, MedicalHistoryConstants.MH_PAST_MEDICAL_HISTORY)
                    .replace(DETAIL, detail.toString());

            builder = builder.append(bodyData);
        }

        return builder.toString();
    }

    private String getContent() {
        String htmlContent = html;

        String nameLable = context.getString(R.string.pdf_label_name);
        String dobLable = context.getString(R.string.pdf_label_dob);
        String genderLable = context.getString(R.string.pdf_label_gender);
        String title = context.getString(R.string.health_summary);
        String date = Utils.getCurrentFomatedDate();
        String icon = "pdf_icon.png";
        String backgroundColor = context.getString(R.string.app_gradient_start);
        String name, dob, gender;
        name = userModel.getUserDisplay_name();
        dob = userModel.getDob().replace("DoB : ", "");
        gender = userModel.getGender();


        return htmlContent
                .replace(PDF_TITLE, title)
                .replace(PDF_DATE, date)
                .replace(PDF_ICON, icon)
                .replace(PDF_BACKGROUND_COLOR, backgroundColor)
                .replace(PATIENT_NAME_LABLE, nameLable)
                .replace(PATIENT_DOB_LABLE, dobLable)
                .replace(PATIENT_GENDER_LABLE, genderLable)
                .replace(PATIENT_NAME, name)
                .replace(PATIENT_DOB, dob)
                .replace(PATIENT_GENDER, gender);
    }
}
