package com.thealer.telehealer.apilayer.models.vitals;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nimesh Patel
 * Created Date: 25,June,2021
 **/
public class VitalThresholdModel extends BaseApiResponseModel {
    public Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        List<String> users = null;
        public List<VitalsThreshold> vitals_thresholds = null;

        public List<VitalsCron> vitals_cron = null;
        public Boolean is_rpm_enabled;
        public Boolean is_notify_on_capture;

        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }

        public List<VitalsThreshold> getVitals_thresholds() {
            return vitals_thresholds;
        }

        public void setVitals_thresholds(List<VitalsThreshold> vitals_thresholds) {
            this.vitals_thresholds = vitals_thresholds;
        }

        public List<VitalsCron> getVitals_cron() {
            return vitals_cron;
        }

        public void setVitals_cron(List<VitalsCron> vitals_cron) {
            this.vitals_cron = vitals_cron;
        }

        public Boolean getIs_rpm_enabled() {
            return is_rpm_enabled;
        }

        public void setIs_rpm_enabled(Boolean is_rpm_enabled) {
            this.is_rpm_enabled = is_rpm_enabled;
        }

        public Boolean getIs_notify_on_capture() {
            return is_notify_on_capture;
        }

        public void setIs_notify_on_capture(Boolean is_notify_on_capture) {
            this.is_notify_on_capture = is_notify_on_capture;
        }
    }

    public class VitalsCron {

        public String value;

        public String vital_type;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getVital_type() {
            return vital_type;
        }

        public void setVital_type(String vital_type) {
            this.vital_type = vital_type;
        }
    }

    public class VitalsThreshold {

        public String vital_type;
        public List<Range> ranges = null;
        public  boolean rangeVisible = false;

        public boolean isRangeVisible() {
            return rangeVisible;
        }

        public void setRangeVisible(boolean rangeVisible) {
            this.rangeVisible = rangeVisible;
        }

        public String getVital_type() {
            return vital_type;
        }

        public void setVital_type(String vital_type) {
            this.vital_type = vital_type;
        }

        public List<Range> getRanges() {
            return ranges;
        }

        public void setRanges(List<Range> ranges) {
            this.ranges = ranges;
        }

    }

    public static class Range {

        public String range_type;

        public String high_value;

        public String message="";

        public Boolean abnormal=false;

        public String low_value;

        public String getRange_type() {
            return range_type;
        }

        public void setRange_type(String range_type) {
            this.range_type = range_type;
        }

        public String getHigh_value() {
            return high_value;
        }

        public void setHigh_value(String high_value) {
            this.high_value = high_value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getAbnormal() {
            return abnormal;
        }

        public void setAbnormal(Boolean abnormal) {
            this.abnormal = abnormal;
        }

        public String getLow_value() {
            return low_value;
        }

        public void setLow_value(String low_value) {
            this.low_value = low_value;
        }
    }

}
