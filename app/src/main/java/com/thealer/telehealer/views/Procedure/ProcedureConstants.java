package com.thealer.telehealer.views.Procedure;

import android.content.Context;

import androidx.annotation.Nullable;

import com.thealer.telehealer.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 22,July,2019
 */
public class ProcedureConstants implements Serializable {

    private static HashMap<String, ProcedureConstants> items = new HashMap<String, ProcedureConstants>() {
        {
            put("99211 95", new ProcedureConstants("99211 95", "99211 95 - Evaluation and management service, for an established patient, typical time 5 minutes", "E/M - Established Patient (5 mins)"));
            put("99212 95", new ProcedureConstants("99212 95", "99212 95 - Evaluation and management service, for an established patient, based on 2 of 3 key components being met - problem focused history, problem focused exam, straightforward medical decision making, typical time 10 minutes", "E/M - Established Patient (10 mins)"));
            put("99457", new ProcedureConstants("99457", "99457 - Remote physiologic monitoring treatment management services, 20 minutes or more of clinical staff/physician/other qualified healthcare professional time in a calendar month requiring interactive communication with the patient/caregiver during the month.", "RPM (20 mins)"));
            put("99484", new ProcedureConstants("99484", "99484 - Furnished using BHI models of care other than CoCM that similarly include “core” service elements such as systematic assessment and monitoring, care plan revision for patients whose condition is not improving adequately, and a continuous relationship with a designated care team member.", "BHI (20 mins)"));
            put("99491", new ProcedureConstants("99491", "99491 - Non-complex 30-minute timed service provided by physician to coordinate care across providers and support patient accountability per calendar month.", "CCM by Physician (non-complex)"));
            put("99213 95", new ProcedureConstants("99213 95", "99213 95 - Evaluation and management service, for an established patient, based on 2 of 3 key components being met - expanded problem focused history, expanded problem focused exam, low complexity medical decision making, typical time 15 minutes", "E/M - Established Patient (15 mins)"));
            put("99214 95", new ProcedureConstants("99214 95", "99214 95 - Evaluation and management service, for an established patient, based on 2 of 3 key components being met - detailed history, detailed exam, moderate complexity medical decision making, typical time 25 minutes", "E/M - Established Patient (25 mins)"));
            put("99215 95", new ProcedureConstants("99215 95", "99215 95 - Evaluation and management service, for an established patient, based on 2 of 3 key components being met - comprehensive history, comprehensive exam, high complexity medical decision making, typical time 40 minutes", "E/M - Established Patient (40 mins)"));
            put("99201 95", new ProcedureConstants("99201 95", "99201 95 - Evaluation and management service, for a new patient, based on all 3 key components being met - problem focused history, problem focused exam, straightforward medical decision making, typical time 10 minutes", "E/M - New  Patient (10 mins)"));
            put("99202 95", new ProcedureConstants("99202 95", "99202 95 - Evaluation and management service, for an established patient, based on all 3 key components being met - problem focused history, problem focused exam, straightforward medical decision making, typical time 20 minutes", "E/M - New Patient (20 mins)"));
            put("99203 95", new ProcedureConstants("99203 95", "99203 95 - Evaluation and management service, for an established patient, based on all 3 key components being met - expanded problem focused history, expanded problem focused exam, low complexity medical decision making, typical time 30 minutes", "E/M - New Patient (30 mins)"));
            put("99204 95", new ProcedureConstants("99204 95", "99204 95 - Evaluation and management service, for an established patient, based on all 3 key components being met - detailed history, detailed exam, moderate complexity medical decision making, typical time 45 minutes", "E/M - New Patient (45 mins)"));
            put("99205 95", new ProcedureConstants("99205 95", "99205 95 - Evaluation and management service, for an established patient, based on all 3 key components being met - comprehensive history, comprehensive exam, high complexity medical decision making, typical time 60 minutes", "E/M - New Patient (60 mins)"));
            put("99421", new ProcedureConstants("99421", "99421 - Online digital evaluation and management service, for an established patient, for up to 7 days cumulative time during the 7 days; 5-10 minutes.", "Digital E/M by Physician (5-10 mins)"));
            put("99422", new ProcedureConstants("99422", "99422 - Online digital evaluation and management service, for an established patient, for up to 7 days cumulative time during the 7 days; 11-20 minutes.", "Digital E/M by Physician (11-20 mins)"));
            put("99423", new ProcedureConstants("99423", "99423 - Online digital evaluation and management service, for an established patient, for up to 7 days cumulative time during the 7 days; 21 or more minutes.", "Digital E/M by Physician (21 mins or more)"));
            put("G2061", new ProcedureConstants("G2061", "G2061 - Qualified non-physician health care professional online digital evaluation and management service, for an established patient, for up to 7 days, cumulative time during the 7 days; 5-10 minutes.", "Digital Assessment by Non-physician (5-10 mins)"));
            put("G2062", new ProcedureConstants("G2062", "G2062 - Qualified non-physician health care professional online digital evaluation and management service, for an established patient, for up to 7 days, cumulative time during the 7 days; 11-20 minutes.", "Digital Assessment by Non-physician (11-20 mins)"));
            put("G2063", new ProcedureConstants("G2063", "G2063 - Qualified non-physician health care professional online digital evaluation and management service, for an established patient, for up to 7 days, cumulative time during the 7 days; 21 or more minutes.    ", "Digital Assessment by Non-physician (21 mins or more)"));
            put("99458", new ProcedureConstants("99458", "99458 - Remote physiologic monitoring treatment management services, clinical staff/physician/other qualified health care professional time in a calendar month requiring interactive communication with the patient/caregiver during the month; additional 20 minutes.", "RPM (addnl 20 mins)"));
            put("99490", new ProcedureConstants("99490", "99490 - Non-complex 20-minute timed service provided by clinical staff to coordinate care across providers and support patient accountability per calendar month.", "CCM by Non-physician (non-complex) "));
            put("G2065", new ProcedureConstants("G2065", "G2065 - Complex 30 minutes of clinical staff time directed by a physician or other qualified health care professional, per calendar month ", "CCM (complex)"));
        }
    };


    public String shortDescription;
    public String description;
    public String code;

    public ProcedureConstants(String code, String description, String shortDescription) {
        this.shortDescription = shortDescription;
        this.description = description;
        this.code = code;
    }

    public static ArrayList<String> getItems() {
        return new ArrayList<String>(items.keySet());
    }

    public static ArrayList<String> getDefaultItems() {
        return new ArrayList<>(Arrays.asList(
                "99211 95",
                "99212 95",
                "99457",
                "99484",
                "99491"
        ));
    }

    public static String getDescription(Context context, String type) {
        ProcedureConstants procedure = items.get(type);
        if (procedure == null) {
            return "";
        }
        return procedure.description;
    }

    public static String getTitle(Context context, String type) {
        ProcedureConstants procedure = items.get(type);
        if (procedure == null) {
            return "";
        }
        return procedure.shortDescription;
    }

    @Nullable
    public static String getCategory(String type) {
        ProcedureConstants procedure = items.get(type);
        if (procedure == null) {
            return null;
        }
        if (procedure.shortDescription.toLowerCase().contains("ccm")) {
            return "CCM";
        } else if (procedure.shortDescription.toLowerCase().contains("rpm")) {
            return "RPM";
        } else if (procedure.shortDescription.toLowerCase().contains("bhi")) {
            return "BHI";
        } else {
            return null;
        }
    }


}
