package com.thealer.telehealer.apilayer.models.orders;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

/**
 * Created by Aswin on 22,November,2018
 */
public class OrdersApiResponseModel extends BaseApiResponseModel implements Serializable {

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
