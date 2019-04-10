package com.thealer.telehealer.apilayer.models.orders;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 26,April,2019
 */
public class OrdersIdListApiResponseModel extends BaseApiResponseModel implements Serializable {
    private List<OrdersLabApiResponseModel.LabsResponseBean> labs = new ArrayList<>();
    private List<GetRadiologyResponseModel.ResultBean> xrays = new ArrayList<>();
    private List<OrdersSpecialistApiResponseModel.ResultBean> specialists = new ArrayList<>();
    private List<OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptions = new ArrayList<>();
    private List<MiscellaneousApiResponseModel.ResultBean> miscellaneous = new ArrayList<>();

    public List<OrdersLabApiResponseModel.LabsResponseBean> getLabs() {
        return labs;
    }

    public void setLabs(List<OrdersLabApiResponseModel.LabsResponseBean> labs) {
        this.labs = labs;
    }

    public List<GetRadiologyResponseModel.ResultBean> getXrays() {
        return xrays;
    }

    public void setXrays(List<GetRadiologyResponseModel.ResultBean> xrays) {
        this.xrays = xrays;
    }

    public List<OrdersSpecialistApiResponseModel.ResultBean> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<OrdersSpecialistApiResponseModel.ResultBean> specialists) {
        this.specialists = specialists;
    }

    public List<OrdersPrescriptionApiResponseModel.OrdersResultBean> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<OrdersPrescriptionApiResponseModel.OrdersResultBean> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public List<MiscellaneousApiResponseModel.ResultBean> getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(List<MiscellaneousApiResponseModel.ResultBean> miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public boolean isEmpty() {
        return getLabs().isEmpty() && getPrescriptions().isEmpty() && getSpecialists().isEmpty() && getXrays().isEmpty() && getMiscellaneous().isEmpty();
    }
}
