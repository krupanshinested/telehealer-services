package com.thealer.telehealer.apilayer.models.Payments;

import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rsekar on 1/23/19.
 */

public class VitalVisit implements Serializable {
    private String user_guid;
    private String timestamp;

    public String getUser_guid() {
        return user_guid;
    }

    public String getDate() {
        return Utils.getDayMonthTime(timestamp);
    }
}
