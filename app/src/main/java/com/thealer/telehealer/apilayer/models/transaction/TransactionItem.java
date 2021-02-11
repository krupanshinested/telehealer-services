package com.thealer.telehealer.apilayer.models.transaction;

import com.thealer.telehealer.common.Constants;

public class TransactionItem {

    private int status;

    public TransactionItem(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() {
        switch (status) {
            case Constants.ChargeStatus.CHARGE_ADDED:
                return "Charge Added";
            case Constants.ChargeStatus.CHARGE_PROCESS_FAILED:
                return "Payment Failed";
            case Constants.ChargeStatus.CHARGE_PROCESSED:
                return "Success";

        }
        return null;
    }
}
