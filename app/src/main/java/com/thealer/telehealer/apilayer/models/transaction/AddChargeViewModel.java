package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.master.MasterResp;
import com.thealer.telehealer.common.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddChargeViewModel extends BaseApiViewModel {


    private final ArrayList<MasterResp.MasterItem> listChargeTypes = new ArrayList<>();

    private int selectedChargeTypeId = -1;



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


}
