package com.thealer.telehealer.apilayer.models.addConnection;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Aswin on 19,November,2018
 */
public class ConnectionListResponseModel extends BaseApiResponseModel implements Serializable {

    private int count;
    private Object prev;
    private Object next;
    private List<CommonUserApiResponseModel> result;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public List<CommonUserApiResponseModel> getResult() {
        return result;
    }

    public void setResult(List<CommonUserApiResponseModel> result) {
        this.result = result;
    }
}
