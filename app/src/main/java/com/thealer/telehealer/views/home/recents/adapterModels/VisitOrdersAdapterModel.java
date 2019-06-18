package com.thealer.telehealer.views.home.recents.adapterModels;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.forms.OrdersUserFormsApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.forms.EditableFormFragment;
import com.thealer.telehealer.views.home.orders.labs.LabsDetailViewFragment;
import com.thealer.telehealer.views.home.orders.miscellaneous.MiscellaneousDetailViewFragment;
import com.thealer.telehealer.views.home.orders.prescription.PrescriptionDetailViewFragment;
import com.thealer.telehealer.views.home.orders.radiology.RadiologyDetailViewFragment;
import com.thealer.telehealer.views.home.orders.specialist.SpecialistDetailViewFragment;

import java.io.Serializable;

/**
 * Created by Aswin on 29,April,2019
 */
public class VisitOrdersAdapterModel implements Serializable {
    private String date;
    private OrdersLabApiResponseModel.LabsResponseBean labs;
    private GetRadiologyResponseModel.ResultBean xrays;
    private OrdersSpecialistApiResponseModel.ResultBean specialists;
    private OrdersPrescriptionApiResponseModel.OrdersResultBean prescriptions;
    private MiscellaneousApiResponseModel.ResultBean miscellaneous;
    private OrdersUserFormsApiResponseModel forms;

    public VisitOrdersAdapterModel(String date, OrdersLabApiResponseModel.LabsResponseBean labs) {
        this.labs = labs;
        this.date = date;
    }

    public VisitOrdersAdapterModel(String date, GetRadiologyResponseModel.ResultBean xrays) {
        this.date = date;
        this.xrays = xrays;
    }

    public VisitOrdersAdapterModel(String date, OrdersSpecialistApiResponseModel.ResultBean specialists) {
        this.date = date;
        this.specialists = specialists;
    }

    public VisitOrdersAdapterModel(String date, OrdersPrescriptionApiResponseModel.OrdersResultBean prescriptions) {
        this.prescriptions = prescriptions;
        this.date = date;
    }

    public VisitOrdersAdapterModel(String date, MiscellaneousApiResponseModel.ResultBean miscellaneous) {
        this.miscellaneous = miscellaneous;
        this.date = date;
    }

    public VisitOrdersAdapterModel(String date, OrdersUserFormsApiResponseModel forms) {
        this.date = date;
        this.forms = forms;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrdersLabApiResponseModel.LabsResponseBean getLabs() {
        return labs;
    }

    public void setLabs(OrdersLabApiResponseModel.LabsResponseBean labs) {
        this.labs = labs;
    }

    public GetRadiologyResponseModel.ResultBean getXrays() {
        return xrays;
    }

    public void setXrays(GetRadiologyResponseModel.ResultBean xrays) {
        this.xrays = xrays;
    }

    public OrdersSpecialistApiResponseModel.ResultBean getSpecialists() {
        return specialists;
    }

    public void setSpecialists(OrdersSpecialistApiResponseModel.ResultBean specialists) {
        this.specialists = specialists;
    }

    public OrdersPrescriptionApiResponseModel.OrdersResultBean getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(OrdersPrescriptionApiResponseModel.OrdersResultBean prescriptions) {
        this.prescriptions = prescriptions;
    }

    public MiscellaneousApiResponseModel.ResultBean getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(MiscellaneousApiResponseModel.ResultBean miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public OrdersUserFormsApiResponseModel getForms() {
        return forms;
    }

    public void setForms(OrdersUserFormsApiResponseModel forms) {
        this.forms = forms;
    }

    public int getDisplayImage() {
        if (getPrescriptions() != null) {
            return R.drawable.ic_orders_prescriptions;
        } else if (getSpecialists() != null) {
            return R.drawable.ic_orders_referrals;
        } else if (getLabs() != null) {
            return R.drawable.ic_orders_labs;
        } else if (getXrays() != null) {
            return R.drawable.ic_orders_radiology;
        } else if (getMiscellaneous() != null) {
            return R.drawable.ic_orders_documents;
        } else if (getForms() != null) {
            return R.drawable.ic_orders_forms;
        }
        return 0;
    }

    public String getDisplayTitle() {

        if (getPrescriptions() != null) {
            return getPrescriptions().getDetail().getRx_drug_name();
        } else if (getSpecialists() != null) {
            return getSpecialists().getDetail().getSpecialist();
        } else if (getLabs() != null) {
            return getLabs().getDetail().getLabs().get(0).getTest_description();
        } else if (getXrays() != null) {
            return getXrays().getDetail().getLabs().get(0).getXRayTests().get(0).getItem();
        } else if (getMiscellaneous() != null) {
            return getMiscellaneous().getDetail().getNotes();
        } else if (getForms() != null) {
            return getForms().getName();
        }
        return null;
    }

    public Fragment getDetailViewFragment() {

        Fragment fragment = null;
        if (getPrescriptions() != null) {
            fragment = new PrescriptionDetailViewFragment();
        } else if (getSpecialists() != null) {
            fragment = new SpecialistDetailViewFragment();
        } else if (getLabs() != null) {
            fragment = new LabsDetailViewFragment();
        } else if (getXrays() != null) {
            fragment = new RadiologyDetailViewFragment();
        } else if (getMiscellaneous() != null) {
            fragment = new MiscellaneousDetailViewFragment();
        } else if (getForms() != null) {
            fragment = new EditableFormFragment();
        }

        return fragment;
    }

    public Bundle getDetailViewBundle() {

        Bundle bundle = new Bundle();

        if (getPrescriptions() != null) {
            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, getPrescriptions());
        } else if (getSpecialists() != null) {
            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, getSpecialists());
        } else if (getLabs() != null) {
            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, getLabs());
        } else if (getXrays() != null) {
            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, getXrays());
        } else if (getMiscellaneous() != null) {
            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, getMiscellaneous());
        } else if (getForms() != null) {
            bundle.putSerializable(ArgumentKeys.FORM_DETAIL, getForms());
        }
        return bundle;
    }

    public String getOrderType() {
        if (getPrescriptions() != null) {
            return OrderConstant.ORDER_PRESCRIPTIONS;
        } else if (getSpecialists() != null) {
            return OrderConstant.ORDER_REFERRALS;
        } else if (getLabs() != null) {
            return OrderConstant.ORDER_LABS;
        } else if (getXrays() != null) {
            return OrderConstant.ORDER_RADIOLOGY;
        } else if (getMiscellaneous() != null) {
            return OrderConstant.ORDER_MISC;
        } else if (getForms() != null) {
            return OrderConstant.ORDER_FORM;
        }
        return null;
    }

    public String getOrderId() {
        if (getPrescriptions() != null) {
            return getPrescriptions().getOrder_id();
        } else if (getSpecialists() != null) {
            return getSpecialists().getOrder_id();
        } else if (getLabs() != null) {
            return getLabs().getOrder_id();
        } else if (getXrays() != null) {
            return getXrays().getOrder_id();
        } else if (getMiscellaneous() != null) {
            return getMiscellaneous().getOrder_id();
        } else if (getForms() != null) {
            return getForms().getOrder_id();
        }
        return null;
    }

    public int getReferralId() {
        if (getPrescriptions() != null) {
            return getPrescriptions().getReferral_id();
        } else if (getSpecialists() != null) {
            return getSpecialists().getReferral_id();
        } else if (getLabs() != null) {
            return getLabs().getReferral_id();
        } else if (getXrays() != null) {
            return getXrays().getReferral_id();
        } else if (getMiscellaneous() != null) {
            return getMiscellaneous().getReferral_id();
        } else if (getForms() != null) {
            return getForms().getUser_form_id();
        }
        return -1;
    }

}
