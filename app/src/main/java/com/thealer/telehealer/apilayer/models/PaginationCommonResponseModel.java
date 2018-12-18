package com.thealer.telehealer.apilayer.models;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 18,December,2018
 */
public class PaginationCommonResponseModel extends BaseApiResponseModel {

    private int count;
    private Object prev;
    private Object next;

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
}
