package com.thealer.telehealer.apilayer.models.Payments;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rsekar on 1/23/19.
 */

public class VitalVisitResponse extends BaseApiResponseModel implements Serializable {

    private ArrayList<VitalVisit> result;

    public ArrayList<VitalVisit> getResult() {
        return result;
    }
}
