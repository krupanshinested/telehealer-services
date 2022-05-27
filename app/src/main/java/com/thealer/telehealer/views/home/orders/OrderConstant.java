package com.thealer.telehealer.views.home.orders;

import android.content.Context;

import com.thealer.telehealer.R;

/**
 * Created by Aswin on 28,November,2018
 */
public class OrderConstant {
    public static final String ORDER_TYPE = "type";
    public static final String ORDER_FORM = "Forms";
    public static final String ORDER_PRESCRIPTIONS = "Prescriptions";
    public static final String ORDER_REFERRALS = "Referrals";
    public static final String ORDER_LABS = "Labs";
    public static final String ORDER_RADIOLOGY = "Radiology";
    public static final String ORDER_DOCUMENTS = "Documents";
    public static final String ORDER_MISC = "Miscellaneous";
    public static final String ORDER_EDUCATIONAL_VIDEO = "Educational Video";

    public static final String ORDER_TYPE_FORM = "forms";
    public static final String ORDER_TYPE_PRESCRIPTIONS = "prescriptions";
    public static final String ORDER_TYPE_SPECIALIST = "specialists";
    public static final String ORDER_TYPE_LABS = "labs";
    public static final String ORDER_TYPE_X_RAY = "x-rays";
    public static final String ORDER_TYPE_FILES = "files";
    public static final String ORDER_TYPE_MISC = "miscellaneous";
    public static final String ORDER_TYPE_EDUCATIONAL_VIDEO = "educational-video";
    public static String USER_GUID = "";

    public static String CALL_STATUS_STARTED = "STARTED";
    public static String CALL_STATUS_NO_ANSWER = "NOANSWER";
    public static String CALL_STATUS_ENDED = "ENDED";
    public static String CALL_STATUS_INPROGRESS = "INPROGRESS";

    public static final String RECENT_TYPE_CHAT = "chat";
    public static final String RECENT_TYPE_AUDIO = "audio";

    public static String getDislpayTitle(Context context, String type) {
        switch (type) {
            case ORDER_FORM:
                return context.getString(R.string.form);
            case ORDER_PRESCRIPTIONS:
                return context.getString(R.string.prescription);
            case ORDER_REFERRALS:
                return context.getString(R.string.orders_referrals);
            case ORDER_LABS:
                return context.getString(R.string.labs);
            case ORDER_RADIOLOGY:
                return context.getString(R.string.radiology);
            case ORDER_DOCUMENTS:
                return context.getString(R.string.documents);
            case ORDER_MISC:
                return context.getString(R.string.lbl_medical_document);
            case ORDER_EDUCATIONAL_VIDEO:
                return context.getString(R.string.educational_video);
        }
        return "";
    }

    public static final String[] labTestList = {"Amylase",
            "BMP",
            "B-Natriureitic peptide",
            "C difficile toxin",
            "CBC",
            "Cholesterol",
            "CMP",
            "CRP",
            "CRP, highly sensitive",
            "Creatinine",
            "Cultures - Select one or more from Blood, Genital, Nail, Skin, Stool, Urine, Throat, Vaginal",
            "Nasal culture for MRSA, Other",
            "Diabetic panel MCR interim -  Select one or more from CMP, HgbA1C, Microalbumin Urine, Creatinine random urine",
            "Diabetic panel PVT or MCR annual",
            "Drug screen, urine",
            "Electrolytes",
            "Fructosamine",
            "Glucose",
            "Glucose tolerance test (GTT)",
            "Hepatic functional panel",
            "HgbA1C",
            "H-Pylori breath test",
            "INR (PT)",
            "Iron / (TIBC)* - Select one or more from Iron, TIBC",
            "Lipid panel I - Select one or more from Cholestrol, Trigylcerides, HDL, Calculated LDL",
            "Lipid panel interim - Select one or more from Measured LDL, Cholesterol",
            "Lipid panel PPO - Select one or more from Cholestrol, Triglycerides, HDL, Calculated LDL, Measured LDL",
            "Lipid, relex",
            "LPa cholesterol",
            "MMA",
            "MMR with lipids",
            "Monospot",
            "Occult blood x 3",
            "PAP, thin prep",
            "Phosphorous",
            "Pregnancy test, urine",
            "Protein, total",
            "PSA",
            "Sed Rate",
            "Strep test, rapid",
            "Thyroid panel I: - Select one or more from Thyroxine total, T3 uptake",
            "Thyroid panel II: - Select one or more from Thyroxine total, T3 uptake, TSH",
            "Triglycerides",
            "TSH",
            "Uric acid",
            "UA, automated",
            "Urine dip w/ cs if indicated",
            "Urine microalbumin, quantitative",
            "Vitamin D, 25-Hydroxy",
            "Tissue pathology",
            "Chlamydia/Gonorrhea, Urine test",
            "Chlamydia amplified, DNA probe",
            "Hepatitis panel",
            "HIV",
            "HPV",
            "HSV culture and typing",
            "HSV, type II Ab",
            "RPR, Qualitative",
            "Other"};
}
