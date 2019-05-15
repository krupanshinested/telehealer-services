package com.thealer.telehealer.apilayer.models.chat;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 20,May,2019
 */
public class PrecannedMessageApiResponse extends BaseApiResponseModel implements Serializable {

    private int count;
    private List<String> messages;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
