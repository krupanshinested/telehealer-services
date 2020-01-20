package com.thealer.telehealer.views.home.recents;

import android.content.Context;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.recents.DownloadTranscriptResponseModel;
import com.thealer.telehealer.apilayer.models.recents.TranscriptionApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aswin on 26,December,2018
 */
public class TranscriptionPdfGenerator {
    private String transcriptionHeader;

    {
        transcriptionHeader = "<!DOCTYPE html>\n" +
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
                "        footer {\n" +
                "            /*position: fixed;\n" +
                "            height: 80px;\n" +
                "            bottom: 0px;\n" +
                "            left: 20px;\n" +
                "            right: 20px;*/\n" +
                "            margin-top: 20px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        \n" +
                "        </style>\n" +
                "        <table width=\"100%\" height=\"20\">\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"60%\">\n" +
                "                    <h2><b><font face=\"Helvetica Neue\">#CHAT_TITLE#</font><b></h2>\n" +
                "                    <h4><font face=\"Helvetica Neue\" color=\"gray\">#CHAT_DATE#</font></h4>\n" +
                "                </td>\n" +
                "                <td width=\"40%\" align = \"right\">\n" +
                "                    <table><tr><td><img src=\"#LOGO_DATA#\" alt=\"LOGO\" height=\"60\" width=\"60\"></td><td> <h2><b><font face=\"Helvetica Neue\">#APP_NAME#</font><b></h2></td></tr></table>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <hr color = \"#BACKGROUND_COLOR#\">\n" +
                "        <br>\n" +
                "        <table width=\"100%\" height=\"20\">\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"20%\" align=\"right\"><font face=\"Helvetica Neue\" size=\"4\">#DOCTOR_NAME_LABEL#</font></td>\n" +
                "                <td width=\"80%\" style=\"padding-left: 20px;\"><h4><b><font face=\"Helvetica Neue\">#DOCTOR_NAME#</font></b></h4></td>\n" +
                "            </tr>\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"20%\" align=\"right\"><font face=\"Helvetica Neue\" size=\"4\">#PATIENT_NAME_LABEL#</font></td>\n" +
                "                <td width=\"80%\" style=\"padding-left: 20px;\"><h4><b><font face=\"Helvetica Neue\">#PATIENT_NAME#</font></b></h4></td>\n" +
                "            </tr>\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"20%\" align=\"right\"><font face=\"Helvetica Neue\" size=\"4\">#TYPE_LABEL#</font></td>\n" +
                "                <td width=\"80%\" style=\"padding-left: 20px;\"><h4><b><font face=\"Helvetica Neue\">#TYPE#</font></b></h4></td>\n" +
                "            </tr>\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"20%\" align=\"right\"><font face=\"Helvetica Neue\" size=\"4\">#DATE_LABEL#</font></td>\n" +
                "                <td width=\"80%\" style=\"padding-left: 20px;\"><h4><b><font face=\"Helvetica Neue\">#DATE#</font></b></h4></td>\n" +
                "            </tr>\n" +
                "            <tr style=\"padding-left: 4px; padding-right: 4px;\">\n" +
                "                <td width=\"20%\" align=\"right\"><font face=\"Helvetica Neue\" size=\"4\">#DURATION_LABEL#</font></td>\n" +
                "                <td width=\"80%\" style=\"padding-left: 20px;\"><h4><b><font face=\"Helvetica Neue\">#DURATION#</font></b></h4></td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "        <br>\n" +
                "        <hr color = \"#BACKGROUND_COLOR#\">\n" +
                "        <table width = \"100%\" height = \"50\">\n" +
                "            #MESSAGE_INFO#\n" +
                "        </table>\n" +
                "        <footer>\n" +
                "            <hr color = \"#BACKGROUND_COLOR#\">\n" +
                "            <h4><font face=\"Helvetica Neue\" color=\"gray\" size=\"2\">#TRANSCRIPT_NOTE#</font></h4>\n" +
                "            <br>\n" +
                "        </footer>\n" +
                "    </body>\n" +
                "</html>\n" +
                "\n" +
                "\n";
    }

    private String transcriptionBody;

    {
        transcriptionBody = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
                "    <td width=\"20%\" align=\"right\" style=\"vertical-align: top; white-space: nowrap;\n" +
                "        overflow: hidden;\"><font face=\"Helvetica Neue\" size=\"4\">#SENDER#</font></td>\n" +
                "    <td width=\"80%\" style=\"padding-left: 20px; vertical-align: top;\"><font face=\"Helvetica Neue\" size=\"4\">#MESSAGE#</font></td>\n" +
                "</tr>";
    }

    private String CHAT_TITLE_LABEL = "#CHAT_TITLE#";
    private String CHAT_DATE_LABEL = "#CHAT_DATE#";
    private String APP_LOGO = "#LOGO_DATA#";
    private String PDF_BACKGROUND_COLOR = "#BACKGROUND_COLOR#";
    private String DATE_LABEL = "#DATE_LABEL#";
    private String DURATION_LABEL = "#DURATION_LABEL#";
    private String TYPE_LABEL = "#TYPE_LABEL#";
    private String PATIENT_NAME_LABEL = "#PATIENT_NAME_LABEL#";
    private String DOCTOR_NAME_LABEL = "#DOCTOR_NAME_LABEL#";
    private String DATE = "#DATE#";
    private String DURATION = "#DURATION#";
    private String TYPE = "#TYPE#";
    private String PATIENT_NAME = "#PATIENT_NAME#";
    private String DOCTOR_NAME = "#DOCTOR_NAME#";
    private String MESSAGE_INFO = "#MESSAGE_INFO#";
    private String SENDER = "#SENDER#";
    private String MESSAGE = "#MESSAGE#";
    private String TRANSCRIPTION_NOTE = "#TRANSCRIPT_NOTE#";
    private String APP_NAME = "#APP_NAME#";

    private Context context;

    public TranscriptionPdfGenerator(Context context) {
        this.context = context;
    }

    public String getTranscriptPdf(TranscriptionApiResponseModel transcriptionApiResponseModel,
                                   DownloadTranscriptResponseModel downloadTranscriptResponseModel, String transcript_info) {

        String chatDetail = getChatDetails(transcriptionApiResponseModel);
        String transcriptDetail = getTranscriptDetails(transcriptionApiResponseModel, downloadTranscriptResponseModel);

        return chatDetail.replace(MESSAGE_INFO, transcriptDetail).replace(TRANSCRIPTION_NOTE, "*" + transcript_info);
    }

    private String getTranscriptDetails(TranscriptionApiResponseModel transcriptionApiResponseModel, DownloadTranscriptResponseModel downloadTranscriptResponseModel) {
        StringBuilder transcriptDetail = new StringBuilder();

        for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {

            String transcriptList = transcriptionBody;

            String message = downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript();

            transcriptList = transcriptList.replace(SENDER, downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeakerName(transcriptionApiResponseModel))
                    .replace(MESSAGE, message);

            transcriptDetail = transcriptDetail.append(transcriptList);
        }

        return transcriptDetail.toString();
    }

    private String getChatDetails(TranscriptionApiResponseModel transcriptionApiResponseModel) {
        String chatDetails = transcriptionHeader;

        String date = Utils.getDayMonthYear(transcriptionApiResponseModel.getOrder_start_time());
        String duration = getDuration(transcriptionApiResponseModel.getOrder_start_time(), transcriptionApiResponseModel.getOrder_end_time());
        String type = transcriptionApiResponseModel.getType();
        String patientName = Utils.getPatientDisplayName(transcriptionApiResponseModel.getPatient().getFirst_name(), transcriptionApiResponseModel.getPatient().getLast_name());
        String doctorName = Utils.getDoctorDisplayName(transcriptionApiResponseModel.getDoctor().getFirst_name(), transcriptionApiResponseModel.getDoctor().getLast_name(), "");

        String icon = "pdf_icon.png";

        chatDetails = chatDetails
                .replace(CHAT_TITLE_LABEL, context.getString(R.string.chat_transcript))
                .replace(CHAT_DATE_LABEL, Utils.getCurrentFomatedDate())
                .replace(APP_LOGO, icon)
                .replace(DATE_LABEL, context.getString(R.string.date).concat(" :"))
                .replace(PDF_BACKGROUND_COLOR, context.getString(R.string.app_gradient_start))
                .replace(DURATION_LABEL, context.getString(R.string.duration).concat(" :"))
                .replace(TYPE_LABEL, context.getString(R.string.type).concat(" :"))
                .replace(PATIENT_NAME_LABEL, context.getString(R.string.patient_name).concat(" :"))
                .replace(DOCTOR_NAME_LABEL, context.getString(R.string.doctor_name).concat(" :"))
                .replace(DATE, date)
                .replace(DURATION, duration)
                .replace(TYPE, type)
                .replace(PATIENT_NAME, patientName)
                .replace(DOCTOR_NAME, doctorName)
                .replace(APP_NAME,context.getString(R.string.organization_name));

        return chatDetails;
    }

    private String getDuration(String order_start_time, String order_end_time) {
        DateFormat dateFormat = new SimpleDateFormat(Utils.UTCFormat);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        try {
            Date startDate = dateFormat.parse(order_start_time);
            Date endDate = dateFormat.parse(order_end_time);
            long difference = endDate.getTime() - startDate.getTime();

            long elapsedHour = difference / hoursInMilli;
            difference = difference % hoursInMilli;

            long elapsedMinute = difference / minutesInMilli;
            difference = difference % minutesInMilli;

            long elapsedSecods = difference / secondsInMilli;
            difference = difference % secondsInMilli;

            return elapsedMinute + " minutes " + elapsedSecods + " seconds";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
