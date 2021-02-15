package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.io.CharTypes;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.apilayer.models.transaction.req.AddChargeReq;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddChargeViewModel extends BaseApiViewModel {


    private final ArrayList<MasterResp.MasterItem> listChargeTypes = new ArrayList<>();

    private int selectedChargeTypeId = -1;

    private int patientId;
    private int fees;

    private boolean isOnlyVisit;

    private final ArrayList<TextFieldModel> suppliers = new ArrayList<>();
    private final ArrayList<TextFieldModel> medicines = new ArrayList<>();

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


    private void setUpReason() {
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.VISIT, "Visit", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.MEDICINE, "Medicine", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.SUPPLIES, "Supplies", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.CCM, "CCM", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.RPM, "RMP", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.BHI, "BHI", 0));
        reasonOptions.add(new ReasonOption(Constants.ChargeReason.CONCIERGE, "Concierge", 0));

    }

    @Nullable
    public ReasonOption getReasonByValue(int value) {
        for (ReasonOption option : reasonOptions) {
            if (option.getValue() == value)
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

    public ArrayList<MasterResp.MasterItem> getListChargeTypes() {
        return listChargeTypes;
    }

    public List<ReasonOption> getReasonOptions() {
        return reasonOptions;
    }

    public void setSelectedChargeTypeId(int selectedChargeTypeId) {
        this.selectedChargeTypeId = selectedChargeTypeId;
    }

    public int getSelectedChargeTypeId() {
        return selectedChargeTypeId;
    }

    public ArrayList<TextFieldModel> getMedicines() {
        return medicines;
    }

    public ArrayList<TextFieldModel> getSuppliers() {
        return suppliers;
    }


    public void addCharge(AddChargeReq req) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().addCharge(req)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

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
}
