package com.thealer.telehealer.apilayer.models.vitals;

import com.google.gson.internal.LinkedTreeMap;
import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BPTrack extends BaseAdapterObjectModel implements Serializable {
    int dia = 0;
    int sys = 0;
    int heartRate = 0;
    Date date = new Date();
    String dataID = "";

    public BPTrack(HashMap<String,String> map) {
        dia = (int) Double.parseDouble(map.get("dia"));
        sys = (int) Double.parseDouble(map.get("sys"));
        heartRate = (int) Double.parseDouble(map.get("heartRate"));

        Date date = Utils.getDateFromPossibleFormat(map.get("date"));
        if (date != null) {
            this.date = date;
        }

        dataID = map.get("dataID");
    }

    public BPTrack(LinkedTreeMap<String,String> map) {
        dia = (int) Double.parseDouble(map.get("dia"));
        sys = (int) Double.parseDouble(map.get("sys"));
        heartRate = (int) Double.parseDouble(map.get("heartRate"));

        Date date = Utils.getDateFromPossibleFormat(map.get("date"));
        if (date != null) {
            this.date = date;
        }

        dataID = map.get("dataID");
    }

    public BPTrack() {

    }

    public BPTrack(double sys,double dia,double heartRate,Date date,String dataID) {
        this.sys = (int) sys;
        this.dia = (int) dia;
        this.heartRate = (int) heartRate;
        this.date = date;
        this.dataID = dataID;
    }

    public HashMap<String,String> getDictionary() {
        HashMap<String,String> map = new HashMap<>();
        map.put("dia",dia+"");
        map.put("sys",sys+"");
        map.put("heartRate",heartRate+"");
        map.put("dataID",dataID);
        map.put("date",Utils.getStringFromDate(date,"yyyy-MM-dd HH:mm:ss.SSS"));
        return map;
    }

    @Override
    public String getAdapterTitle() {
        return Utils.getStringFromDate(date,"dd MMM yyyy");
    }

    @Override
    public Object getComparableObject() { return date;  }

    public int getDia() {
        return dia;
    }

    public int getSys() {
        return sys;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public Date getDate() {
        return date;
    }

    public String postDateInString() {
        DateFormat outputFormat = new SimpleDateFormat(Utils.UTCFormat);
        outputFormat.setTimeZone(Utils.UtcTimezone);
        return outputFormat.format(this.date);
    }

    public String getDataID() {
        return dataID;
    }
}
