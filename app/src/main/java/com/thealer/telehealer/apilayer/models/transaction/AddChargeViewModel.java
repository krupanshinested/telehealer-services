package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.apilayer.models.transaction.resp.AddChargeResp;
import com.thealer.telehealer.apilayer.models.transaction.resp.TransactionItem;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddChargeViewModel extends BaseApiViewModel {


    private final ArrayList<MasterResp.MasterItem> listChargeTypes = new ArrayList<>();

    private int chargeId = -1;

    private int patientId;
    private int doctorId;
    private int fees;

    private boolean isOnlyVisit;
    private boolean isSaveAndProcess;

    private TransactionItem addedTransaction;

    private String orderId;

    private final List<ReasonOption> reasonOptions = new ArrayList<>();


    public AddChargeViewModel(@NonNull Application application) {
        super(application);
        setUpReason();
    }

    public void setUpChargeTypeFromMasters(MasterResp resp) {
        HashMap<Integer, ArrayList<MasterResp.MasterItem>> filteredMaster = new HashMap<>();
        MasterResp.MasterItem chargeTypeParentMaster = null;
        for (MasterResp.MasterItem masterItem : resp.getData()) {
            if (masterItem.getParentId() == 0) {
                filteredMaster.put(masterItem.getId(), new ArrayList<>());
                if (Constants.MasterCodes.TYPE_OF_CHARGE.equals(masterItem.getCode())) {
                    chargeTypeParentMaster = masterItem;
                }
            } else {
                if (filteredMaster.get(masterItem.getParentId()) == null)
                    filteredMaster.put(masterItem.getParentId(), new ArrayList<>());
                filteredMaster.get(masterItem.getParentId()).add(masterItem);
            }
        }
        if (chargeTypeParentMaster != null) {
            listChargeTypes.addAll(filteredMaster.get(chargeTypeParentMaster.getId()));
        }

    }

    public void setSelectedReason(int selectedReason) {
        if (selectedReason != -1) {
            for (ReasonOption reason : reasonOptions) {
                if (reason.getValue() == selectedReason)
                    reason.setSelected(true);
            }
        }
    }


    public void setUpReason() {
        reasonOptions.clear();
        reasonOptions.add(new SingleDateReasonOption(Constants.ChargeReason.VISIT, getApplication().getString(R.string.visit), 0));
        if (!isOnlyVisit) {
            reasonOptions.add(new DateRangeReasonOption(Constants.ChargeReason.CCM, getApplication().getString(R.string.ccm), 0));
            reasonOptions.add(new DateRangeReasonOption(Constants.ChargeReason.RPM, getApplication().getString(R.string.rpm), 0));
            reasonOptions.add(new DateRangeReasonOption(Constants.ChargeReason.BHI, getApplication().getString(R.string.bhi), 0));
            reasonOptions.add(new TextFieldReasonOption(Constants.ChargeReason.MEDICINE, getApplication().getString(R.string.lbl_medicine), new ArrayList<>()));
            reasonOptions.add(new TextFieldReasonOption(Constants.ChargeReason.SUPPLIES, getApplication().getString(R.string.lbl_supplies), new ArrayList<>()));
            reasonOptions.add(new DateRangeReasonOption(Constants.ChargeReason.CONCIERGE, getApplication().getString(R.string.lbl_concierge), 0));
        } else {
            reasonOptions.get(0).setDisableSelection(true);
            reasonOptions.get(0).setSelected(true);
        }
    }

    @Nullable
    public ReasonOption getReasonByValue(int value) {
        for (ReasonOption option : reasonOptions) {
            if (option.getValue() == value)
                return option;
        }
        return null;
    }

    @Nullable
    public MasterResp.MasterItem getChargeTypeById(int id) {
        for (MasterResp.MasterItem option : listChargeTypes) {
            if (option.getId() == id)
                return option;
        }
        return null;
    }

    public boolean isReasonSelected(int value) {
        for (ReasonOption option : reasonOptions) {
            if (option.getValue() == value)
                return option.isSelected();
        }
        return false;
    }

    public int getSelectedReasonCount() {
        int count = 0;
        for (ReasonOption reasonOption : reasonOptions) {
            if (reasonOption.isSelected())
                count++;
        }
        return count;
    }

    public ArrayList<MasterResp.MasterItem> getListChargeTypes() {
        return listChargeTypes;
    }

    public List<ReasonOption> getReasonOptions() {
        return reasonOptions;
    }

    public void addCharge(AddChargeReq req, boolean isUpdate) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    if (isUpdate) {
                        getAuthApiService().updateCharge(chargeId, req).compose(applySchedulers())
                                .subscribe(new RAObserver<AddChargeResp>(Constants.SHOW_PROGRESS) {
                                    @Override
                                    public void onSuccess(AddChargeResp baseApiResponseModel) {
                                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                    }
                                });
                    } else {
                        getAuthApiService().addCharge(req).compose(applySchedulers())
                                .subscribe(new RAObserver<AddChargeResp>(Constants.SHOW_PROGRESS) {
                                    @Override
                                    public void onSuccess(AddChargeResp baseApiResponseModel) {
                                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                    }
                                });
                    }


                }
            }
        });
    }


    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public boolean isOnlyVisit() {
        return isOnlyVisit;
    }

    public void setOnlyVisit(boolean onlyVisit) {
        isOnlyVisit = onlyVisit;
    }

    public int getChargeId() {
        return chargeId;
    }

    public void setChargeId(int chargeId) {
        this.chargeId = chargeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isSaveAndProcess() {
        return isSaveAndProcess;
    }

    public void setSaveAndProcess(boolean saveAndProcess) {
        isSaveAndProcess = saveAndProcess;
    }

    public TransactionItem getAddedTransaction() {
        return addedTransaction;
    }

    public void setAddedTransaction(TransactionItem addedTransaction) {
        this.addedTransaction = addedTransaction;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

}
