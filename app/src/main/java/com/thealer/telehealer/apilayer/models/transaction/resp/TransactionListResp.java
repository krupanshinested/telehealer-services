package com.thealer.telehealer.apilayer.models.transaction.resp;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.util.List;

public class TransactionListResp extends BaseApiResponseModel {

    private int count;
    private Object prev;
    private Object next;

    private List<TransactionItem> result;

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

    public List<TransactionItem> getResult() {
        return result;
    }

    public void setResult(List<TransactionItem> result) {
        this.result = result;
    }
}
