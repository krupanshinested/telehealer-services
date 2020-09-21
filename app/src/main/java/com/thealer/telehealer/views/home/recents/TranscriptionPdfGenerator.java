package com.thealer.telehealer.views.home.recents;

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
    private String transcriptionHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "    <head>\n" +
            "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
            "        <title>Chat Details</title>\n" +
            "    </head>\n" +
            "    <body style=\"margin:40px; overflow: visible;\" >\n" +
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
            "        </style>\n" +
            "        <table width=\"100%\" height=\"250\">\n" +
            "            <div style=\"background-color:black; padding-left: 4px; padding-right: 4px;\">\n" +
            "                <h4 align = \"left\" style = \"padding-top : 8px;padding-bottom : 8px;\">\n" +
            "                    <font color = \"white\"> Chat Details</font> </h4>\n" +
            "            </div>\n" +
            "            <tr>\n" +
            "                <td width=\"25%\">Date</td>\n" +
            "                <td width=\"75%\"><h4><b>#DATE#</b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td width=\"25%\">Duration</td>\n" +
            "                <td width=\"75%\"><h4><b>#DURATION#</b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td width=\"25%\">Type</td>\n" +
            "                <td width=\"75%\"><h4><b>#TYPE#</b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td width=\"25%\">Patient</td>\n" +
            "                <td width=\"75%\"><h4><b>#PATIENT_NAME#</b></h4></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td width=\"25%\">Doctor</td>\n" +
            "                <td width=\"75%\"><h4><b>#DOCTOR_NAME#</b></h4></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "        <br>\n" +
            "        <div><span>Transcript Begins</span></div>\n" +
            "        <br>\n" +
            "        <table width = \"100%\" height = \"50\">\n" +
            "            #MESSAGE_INFO#\n" +
            "        </table>\n" +
            "        \n" +
            "    </body>\n" +
            "</html>";
    private String transcriptionBody = "<tr style=\"padding-left: 4px; padding-right: 4px; margin-top:5px; margin-top:5px;\">\n" +
            "    <td style=\"width: 25% ; vertical-align:top\"><h4><b>#SENDER#</b></h4></td>\n" +
            "    <td width=\"75%\">#MESSAGE#</td>\n" +
            "</tr>";

    private String DATE = "#DATE#";
    private String DURATION = "#DURATION#";
    private String TYPE = "#TYPE#";
    private String PATIENT_NAME = "#PATIENT_NAME#";
    private String DOCTOR_NAME = "#DOCTOR_NAME#";
    private String MESSAGE_INFO = "#MESSAGE_INFO#";
    private String SENDER = "#SENDER#";
    private String MESSAGE = "#MESSAGE#";


    public TranscriptionPdfGenerator() {
    }

    public String getTranscriptPdf(TranscriptionApiResponseModel transcriptionApiResponseModel,
                                   DownloadTranscriptResponseModel downloadTranscriptResponseModel) {

        String chatDetail = getChatDetails(transcriptionApiResponseModel);
        String transcriptDetail = getTranscriptDetails(downloadTranscriptResponseModel);

        return chatDetail.replace(MESSAGE_INFO, transcriptDetail);
    }

    private String getTranscriptDetails(DownloadTranscriptResponseModel downloadTranscriptResponseModel) {
        StringBuilder transcriptDetail = new StringBuilder();

        for (int i = 0; i < downloadTranscriptResponseModel.getSpeakerLabels().size(); i++) {

            String transcriptList = transcriptionBody;

            String speaker = downloadTranscriptResponseModel.getSpeakerLabels().get(i).getSpeaker_label().replace("spk_", "");
            int person = Integer.parseInt(speaker) + 1;

            String sender = "Speaker " + person + ":";
            String message = downloadTranscriptResponseModel.getSpeakerLabels().get(i).getTranscript();

            transcriptList = transcriptList.replace(SENDER, sender)
                    .replace(MESSAGE, message);

            transcriptDetail = transcriptDetail.append(transcriptList);
        }

        return transcriptDetail.toString();
    }

    private String getChatDetails(TranscriptionApiResponseModel transcriptionApiResponseModel) {
        String chatDetails = transcriptionHeader;

        String date = Utils.getDayMonthYear(transcriptionApiResponseModel.getOrder_start_time());
        String duration = getDuration(transcriptionApiResponseModel.getOrder_start_time(), transcriptionApiResponseModel.getOrder_end_time());
        String type = transcriptionApiResponseModel.getType().toUpperCase();
        String patientName = transcriptionApiResponseModel.getPatient().getFirst_name() + " " + transcriptionApiResponseModel.getPatient().getLast_name();
        String doctorName = "Dr. " + transcriptionApiResponseModel.getDoctor().getFirst_name() + " " + transcriptionApiResponseModel.getDoctor().getLast_name();

        chatDetails = chatDetails.replace(DATE, date)
                .replace(DURATION, duration)
                .replace(TYPE, type)
                .replace(PATIENT_NAME, patientName)
                .replace(DOCTOR_NAME, doctorName);

        return chatDetails;
    }

    private String getDuration(String order_start_time, String order_end_time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
