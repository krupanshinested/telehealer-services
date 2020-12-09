package com.thealer.telehealer.apilayer.models.Braintree;

import com.google.gson.annotations.SerializedName;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

public class DefaultCardResp extends BaseApiResponseModel {

    @SerializedName("card_detail")
    private CardDetail cardDetail;

    public CardDetail getCardDetail() {
        return cardDetail;
    }

    public static class CardDetail {

        @SerializedName("card_no")
        private String cardNo;

        @SerializedName("cardId")
        private String cardId;

        @SerializedName("brand")
        private String brand;

        public String getCardNo() {
            return cardNo;
        }

        public String getCardId() {
            return cardId;
        }

        public String getBrand() {
            return brand;
        }
    }
}