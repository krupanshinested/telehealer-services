package com.thealer.telehealer.apilayer.models.vitals;

import com.thealer.telehealer.common.BaseAdapterObjectModel;
import com.thealer.telehealer.common.Utils;

import java.util.Date;
import java.util.HashMap;

public class BPTrack extends BaseAdapterObjectModel {
    double dia = 0.0;
    double sys = 0.0;
    double heartRate = 0.0;
    Date date = new Date();
    String dataID = "";

    public  BPTrack(HashMap<String,String> map) {
        dia = Double.parseDouble(map.get("dia"));
        sys = Double.parseDouble(map.get("sys"));
        heartRate = Double.parseDouble(map.get("heartRate"));

        Date date = Utils.getDateFromPossibleFormat(map.get("date"));
        if (date != null) {
            this.date = date;
        }

        dataID = map.get("dataID");
    }

    public BPTrack() {

    }

    public BPTrack(double sys,double dia,double heartRate,Date date,String dataID) {
        this.sys = sys;
        this.dia = dia;
        this.heartRate = heartRate;
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

    public double getDia() {
        return dia;
    }

    public double getSys() {
        return sys;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public Date getDate() {
        return date;
    }

    public String getDataID() {
        return dataID;
    }
}
