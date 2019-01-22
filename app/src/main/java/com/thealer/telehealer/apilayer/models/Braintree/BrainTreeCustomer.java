package com.thealer.telehealer.apilayer.models.Braintree;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rsekar on 1/22/19.
 */

public class BrainTreeCustomer extends BaseApiResponseModel implements Serializable {

    private String id;
    private String merchantId;

    private ArrayList<BrainTreeCard> creditCards;
    private ArrayList<BrainTreeCard> paymentMethods;

    public String getId() {
        return id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public ArrayList<BrainTreeCard> getCreditCards() {
        return creditCards;
    }

    public ArrayList<BrainTreeCard> getPaymentMethods() {
        return paymentMethods;
    }
}
