package com.thealer.telehealer.common.OpenTok;

import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.TokBoxUIInterface;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Util.Array.ArrayListFilter;
import com.thealer.telehealer.common.Util.Array.ArrayListUtil;
import com.thealer.telehealer.views.call.CallActivity;

import java.util.ArrayList;

public class CallManager {

    public static CallManager shared = new CallManager();

    private ArrayList<OpenTok> calls = new ArrayList<>();

    public void addCall(OpenTok call) {
        removeCall(call);
        calls.add(call);
    }

    public void removeCall(OpenTok call) {
        ArrayListUtil<OpenTok, OpenTok> util = new ArrayListUtil<>();
        calls = util.filterList(calls, new ArrayListFilter<OpenTok>() {
            @Override
            public Boolean needToAddInFilter(OpenTok model) {
                return !model.getCurrentUUID().equals(call.getCurrentUUID());
            }
        });
    }

    @Nullable
    public OpenTok getActiveCallToShow() {
        ArrayListUtil<OpenTok, OpenTok> util = new ArrayListUtil<>();
        ArrayList<OpenTok> calls = util.filterList(this.calls, new ArrayListFilter<OpenTok>() {
            @Override
            public Boolean needToAddInFilter(OpenTok model) {
                return model.isActive();
            }
        });

        if (calls.size() > 0) {
            return calls.get(calls.size()-1);
        } else {
            return null;
        }
    }

    @Nullable
    public OpenTok getCall(String uuid) {
        ArrayListUtil<OpenTok, OpenTok> util = new ArrayListUtil<>();
        ArrayList<OpenTok> calls = util.filterList(this.calls, new ArrayListFilter<OpenTok>() {
            @Override
            public Boolean needToAddInFilter(OpenTok model) {
                return model.getCurrentUUID().equals(uuid);
            }
        });

        if (calls.size() > 0) {
            return calls.get(0);
        } else {
            return null;
        }
    }

    public boolean isActiveCallPresent() {
        return getActiveCallToShow() != null;
    }

    public boolean needToOpenCallScreenAutomatically() {
        if (isActiveCallPresent()) {
            CallRequest callRequest = getActiveCallToShow().getCallRequest();
            if (callRequest.isCallForDirectWaitingRoom()) {
                if (UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT)) {
                    return callRequest.isUserAdmitted();
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isActivityPresent() {
        if (getActiveCallToShow() != null) {
            TokBoxUIInterface tokBoxUIInterface = getActiveCallToShow().getTokBoxUIInterface();
            if (tokBoxUIInterface == null) {
                return false;
            } else {
                return (tokBoxUIInterface instanceof CallActivity) || (tokBoxUIInterface instanceof CallMinimizeService);
            }
        } else {
            return false;
        }
    }
}
