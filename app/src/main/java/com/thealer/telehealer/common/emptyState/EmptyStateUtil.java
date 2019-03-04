package com.thealer.telehealer.common.emptyState;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 20,November,2018
 */
public class EmptyStateUtil {

    public static String getTitle(String emptyType) {
        switch (emptyType) {
            case EmptyViewConstants.EMPTY_NO_NETWORK:
                return "No Internet Connection";
            case EmptyViewConstants.EMPTY_SERVER_DOWN:
                return "Server Down";
            case EmptyViewConstants.EMPTY_VITALS:
            case EmptyViewConstants.EMPTY_VITALS_WITH_BTN:
                return "No Vitals";
            case EmptyViewConstants.EMPTY_DOCUMENTS:
            case EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN:
                return "No Documents";
            case EmptyViewConstants.EMPTY_MISC:
            case EmptyViewConstants.EMPTY_MISC_WITH_BTN:
                return "No Miscellaneous";
            case EmptyViewConstants.EMPTY_PRESCRIPTION:
            case EmptyViewConstants.EMPTY_PRESCRIPTION_WITH_BTN:
                return "No Prescription";
            case EmptyViewConstants.EMPTY_HISTORY:
            case EmptyViewConstants.EMPTY_HISTORY_WITH_BTN:
                return "No History";
            case EmptyViewConstants.EMPTY_APPOINTMENTS:
            case EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN:
                return "No Appointment";
            case EmptyViewConstants.EMPTY_BP:
            case EmptyViewConstants.EMPTY_BP_WITH_BTN:
                return "No Blood Pressure values";
            case EmptyViewConstants.EMPTY_WEIGHT:
            case EmptyViewConstants.EMPTY_WEIGHT_WITH_BTN:
                return "No Weight values";
            case EmptyViewConstants.EMPTY_GULCOSE:
            case EmptyViewConstants.EMPTY_GULCOSE_WITH_BTN:
                return "No Blood Glucose";
            case EmptyViewConstants.EMPTY_TEMPERATURE:
            case EmptyViewConstants.EMPTY_TEMPERATURE_WITH_BTN:
                return "No Body Temperature";
            case EmptyViewConstants.EMPTY_PULSE:
            case EmptyViewConstants.EMPTY_PULSE_WITH_BTN:
                return "No Pulse";
            case EmptyViewConstants.EMPTY_CHATS:
                return "No Chats";
            case EmptyViewConstants.EMPTY_CALLS:
                return "No Calls";
            case EmptyViewConstants.EMPTY_NOTIFICATIONS:
                return "No Notifications";
            case EmptyViewConstants.EMPTY_LABS:
            case EmptyViewConstants.EMPTY_LABS_WITH_BTN:
                return "No Labs";
            case EmptyViewConstants.EMPTY_SPECIALIST:
            case EmptyViewConstants.EMPTY_SPECIALIST_WITH_BTN:
                return "No Specialists";
            case EmptyViewConstants.EMPTY_SEARCH:
                return "No Search";
            case EmptyViewConstants.EMPTY_XRAY:
            case EmptyViewConstants.EMPTY_XRAY_WITH_BTN:
                return "No XRays";
            case EmptyViewConstants.EMPTY_FORMS:
            case EmptyViewConstants.EMPTY_FORMS_WITH_BTN:
                return "No Forms";
            case EmptyViewConstants.EMPTY_TRANSCRIPTS:
            case EmptyViewConstants.EMPTY_TRANSCRIPTS_WITH_BTN:
                return "No Transcripts";
            case EmptyViewConstants.EMPTY_PATIENT_HISTORY:
                return "No History Records";
            case EmptyViewConstants.EMPTY_HEART_RATE:
            case EmptyViewConstants.EMPTY_HEART_RATE_WITH_BTN:
                return "No Heart Rate";
            case EmptyViewConstants.EMPTY_PAYMENTS:
                return "No Payments/Billing";
            case EmptyViewConstants.EMPTY_CARDS:
                return "No Cards";
            case EmptyViewConstants.EMPTY_PATIENT_SEARCH:
            case EmptyViewConstants.EMPTY_PATIENT:
            case EmptyViewConstants.EMPTY_PATIENT_WITH_BTN:
                return "No Patients";
            case EmptyViewConstants.EMPTY_DOCTOR:
            case EmptyViewConstants.EMPTY_DOCTOR_WITH_BTN:
                return "No Doctors";
            case EmptyViewConstants.EMPTY_CALL_LOGS:
                return "No Calls";
            case EmptyViewConstants.EMPTY_VITAL_LOGS:
                return "No Vitals";
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT:
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN:
                return "No Medical Assistant";
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH:
                return "No Vital Measurements";
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK:
                return "No Vital Measurements for last week";
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_TWO_WEEK:
                return "No Vital Measurements for last two week";
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_MONTH:
                return "No Vital Measurements for last month";
            case EmptyViewConstants.EMPTY_RECEIVED_PENDING_INVITES:
            case EmptyViewConstants.EMPTY_SENT_PENDING_INVITES:
                return "No Pending Invites";
            default:
                return null;
        }
    }

    public static String getMessage(String emptyType) {
        switch (emptyType) {
            case EmptyViewConstants.EMPTY_NO_NETWORK:
                return "Connect your device with Internet inorder to access this info.";
            case EmptyViewConstants.EMPTY_SERVER_DOWN:
                return "Sorry for inconvience,our server is down for while.";
            case EmptyViewConstants.EMPTY_VITALS:
                return "Vitals measurement will eventually show up here";
            case EmptyViewConstants.EMPTY_VITALS_WITH_BTN:
                return "Connect your vitals device to measure your vitals by tapping the '+' button";
            case EmptyViewConstants.EMPTY_DOCUMENTS:
                return "Documents will eventually show up here";
            case EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN:
                return "Add a Document by tapping the '+' button";
            case EmptyViewConstants.EMPTY_MISC:
                return "Miscellaneous will eventually show up here";
            case EmptyViewConstants.EMPTY_MISC_WITH_BTN:
                return "Add a Miscellaneous by tapping the '+' button";
            case EmptyViewConstants.EMPTY_PRESCRIPTION:
                return "Prescription orders will eventually show up here";
            case EmptyViewConstants.EMPTY_PRESCRIPTION_WITH_BTN:
                return "Add a Prescription by tapping the '+' button";
            case EmptyViewConstants.EMPTY_HISTORY:
            case EmptyViewConstants.EMPTY_HISTORY_WITH_BTN:
                return "Inbound and Outbound calls and messages will eventually show up here";
            case EmptyViewConstants.EMPTY_APPOINTMENTS:
                return "Appointments will eventually show up here";
            case EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN:
                return "Add an appointment by tapping the '+' button";
            case EmptyViewConstants.EMPTY_BP:
                return "BP results will eventually show up here";
            case EmptyViewConstants.EMPTY_BP_WITH_BTN:
                return "Connect your vitals device to measure your BP";
            case EmptyViewConstants.EMPTY_WEIGHT:
                return "Weight results will eventually show up here";
            case EmptyViewConstants.EMPTY_WEIGHT_WITH_BTN:
                return "Connect your vitals device to measure your Weight";
            case EmptyViewConstants.EMPTY_GULCOSE:
                return "Blood Glucose results will eventually show up here";
            case EmptyViewConstants.EMPTY_GULCOSE_WITH_BTN:
                return "Connect your vitals device to measure your Blood Glucose";
            case EmptyViewConstants.EMPTY_TEMPERATURE:
                return "Body Temperature results will eventually show up here";
            case EmptyViewConstants.EMPTY_TEMPERATURE_WITH_BTN:
                return "Connect your vitals device to measure your Body Temperature";
            case EmptyViewConstants.EMPTY_PULSE:
                return "Pulse results will eventually show up here";
            case EmptyViewConstants.EMPTY_PULSE_WITH_BTN:
                return "Connect your vitals device to measure your Pulse";
            case EmptyViewConstants.EMPTY_CHATS:
                return "Messages will eventually show up here";
            case EmptyViewConstants.EMPTY_CALLS:
                return "Inbound and Outbound calls will eventually show up here";
            case EmptyViewConstants.EMPTY_NOTIFICATIONS:
                return "Notifications and requests will eventually show up here";
            case EmptyViewConstants.EMPTY_LABS:
                return "Lab orders will eventually show up here";
            case EmptyViewConstants.EMPTY_LABS_WITH_BTN:
                return "You can add a Lab record by tapping the '+' button";
            case EmptyViewConstants.EMPTY_SPECIALIST:
                return "Specialist referrals will eventually show up here";
            case EmptyViewConstants.EMPTY_SPECIALIST_WITH_BTN:
                return "Add a Specialist by tapping the '+' button";
            case EmptyViewConstants.EMPTY_PATIENT:
                return "Patient referrals will eventually show up here";
            case EmptyViewConstants.EMPTY_PATIENT_WITH_BTN:
                return "Add a Patient by tapping the '+' button";
            case EmptyViewConstants.EMPTY_DOCTOR:
                return "Doctor referrals will eventually show up here";
            case EmptyViewConstants.EMPTY_DOCTOR_WITH_BTN:
                return "Add a Doctor by tapping the '+' button";
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT:
                return "Medical Assistant referrals will eventually show up here";
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN:
                return "Add a Medical Assistant by tapping the '+' button";
            case EmptyViewConstants.EMPTY_SEARCH:
                return "No search result found";
            case EmptyViewConstants.EMPTY_XRAY:
                return "XRays will eventually show up here";
            case EmptyViewConstants.EMPTY_XRAY_WITH_BTN:
                return "Add a XRay by tapping the '+' button";
            case EmptyViewConstants.EMPTY_FORMS:
                return "Forms will eventually show up here";
            case EmptyViewConstants.EMPTY_FORMS_WITH_BTN:
                return "Add a Forms by tapping the '+' button";
            case EmptyViewConstants.EMPTY_TRANSCRIPTS:
            case EmptyViewConstants.EMPTY_TRANSCRIPTS_WITH_BTN:
                return "Transcripts will eventually show up here";
            case EmptyViewConstants.EMPTY_PATIENT_HISTORY:
                return "Patient History will eventually show up here";
            case EmptyViewConstants.EMPTY_HEART_RATE:
                return "Heart Rate results will eventually show up here";
            case EmptyViewConstants.EMPTY_HEART_RATE_WITH_BTN:
                return "Connect your vitals device to measure your Heart Rate";
            case EmptyViewConstants.EMPTY_PAYMENTS:
                return "Payment/Billing information will eventually show up here";
            case EmptyViewConstants.EMPTY_CARDS:
                return "Card Information will eventually show up here";
            case EmptyViewConstants.EMPTY_PATIENT_SEARCH:
                return "No Patient are available on searched name. Try to search with some other name";
            case EmptyViewConstants.EMPTY_CALL_LOGS:
                return "No calls were made on selected month";
            case EmptyViewConstants.EMPTY_VITAL_LOGS:
                return "No vitals were visited on selected month";
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_TWO_WEEK:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_MONTH:
                return "Patients who measured their vitals will eventually show up here";
            case EmptyViewConstants.EMPTY_RECEIVED_PENDING_INVITES:
                return "Invites which waiting for your action will eventually show up here";
            case EmptyViewConstants.EMPTY_SENT_PENDING_INVITES:
                return "Invites which you created and waiting for other person's action will eventually show up here";
            default:
                return null;
        }
    }

    public static int getImage(String emptyType) {
        switch (emptyType) {
            case EmptyViewConstants.EMPTY_NO_NETWORK:
            case EmptyViewConstants.EMPTY_SERVER_DOWN:
                return R.drawable.emptystate_no_network;
            case EmptyViewConstants.EMPTY_VITALS:
            case EmptyViewConstants.EMPTY_VITALS_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_DOCUMENTS:
            case EmptyViewConstants.EMPTY_DOCUMENTS_WITH_BTN:
                return R.drawable.emptystate_no_document;
            case EmptyViewConstants.EMPTY_MISC:
            case EmptyViewConstants.EMPTY_MISC_WITH_BTN:
                return R.drawable.emptystate_misc;
            case EmptyViewConstants.EMPTY_PRESCRIPTION:
            case EmptyViewConstants.EMPTY_PRESCRIPTION_WITH_BTN:
                return R.drawable.emptystate_no_prescription;
            case EmptyViewConstants.EMPTY_HISTORY:
            case EmptyViewConstants.EMPTY_HISTORY_WITH_BTN:
                return R.drawable.emptystate_no_history;
            case EmptyViewConstants.EMPTY_APPOINTMENTS:
            case EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN:
                return R.drawable.emptystate_no_schedule;
            case EmptyViewConstants.EMPTY_BP:
            case EmptyViewConstants.EMPTY_BP_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_WEIGHT:
            case EmptyViewConstants.EMPTY_WEIGHT_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_GULCOSE:
            case EmptyViewConstants.EMPTY_GULCOSE_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_TEMPERATURE:
            case EmptyViewConstants.EMPTY_TEMPERATURE_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_PULSE:
            case EmptyViewConstants.EMPTY_PULSE_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_CHATS:
                return R.drawable.emptystate_no_patient;
            case EmptyViewConstants.EMPTY_CALLS:
                return R.drawable.emptystate_no_calls;
            case EmptyViewConstants.EMPTY_NOTIFICATIONS:
                return R.drawable.emptystate_no_notification;
            case EmptyViewConstants.EMPTY_LABS:
            case EmptyViewConstants.EMPTY_LABS_WITH_BTN:
                return R.drawable.emptystate_doctor_no_lab;
            case EmptyViewConstants.EMPTY_DOCTOR:
            case EmptyViewConstants.EMPTY_DOCTOR_WITH_BTN:
            case EmptyViewConstants.EMPTY_SPECIALIST:
            case EmptyViewConstants.EMPTY_SPECIALIST_WITH_BTN:
                return R.drawable.emptystate_no_doctor;
            case EmptyViewConstants.EMPTY_SEARCH:
                return R.drawable.emptystate_no_search;
            case EmptyViewConstants.EMPTY_XRAY:
            case EmptyViewConstants.EMPTY_XRAY_WITH_BTN:
                return R.drawable.emptystate_no_xray;
            case EmptyViewConstants.EMPTY_FORMS:
            case EmptyViewConstants.EMPTY_FORMS_WITH_BTN:
                return R.drawable.emptystate_no_forms;
            case EmptyViewConstants.EMPTY_TRANSCRIPTS:
            case EmptyViewConstants.EMPTY_TRANSCRIPTS_WITH_BTN:
                return R.drawable.emptystate_no_transcribe;
            case EmptyViewConstants.EMPTY_PATIENT_HISTORY:
                return R.drawable.emptystate_no_history;
            case EmptyViewConstants.EMPTY_HEART_RATE:
            case EmptyViewConstants.EMPTY_HEART_RATE_WITH_BTN:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_PAYMENTS:
                return R.drawable.emptystate_credit_card;
            case EmptyViewConstants.EMPTY_CARDS:
                return R.drawable.emptystate_credit_card;
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_TWO_WEEK:
            case EmptyViewConstants.EMPTY_DOCTOR_VITAL_MONTH:
            case EmptyViewConstants.EMPTY_PATIENT_SEARCH:
            case EmptyViewConstants.EMPTY_PATIENT:
            case EmptyViewConstants.EMPTY_PATIENT_WITH_BTN:
                return R.drawable.emptystate_no_patient;
            case EmptyViewConstants.EMPTY_CALL_LOGS:
                return R.drawable.emptystate_no_calls;
            case EmptyViewConstants.EMPTY_VITAL_LOGS:
                return R.drawable.emptystate_no_vitals;
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT:
            case EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN:
                return R.drawable.emptystate_no_medical_assistant;
            case EmptyViewConstants.EMPTY_RECEIVED_PENDING_INVITES:
            case EmptyViewConstants.EMPTY_SENT_PENDING_INVITES:
                return R.drawable.emptystate_pending_invites;
            default:
                return 0;
        }
    }
}
