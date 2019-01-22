package com.thealer.telehealer.apilayer.models.Payments;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;

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
