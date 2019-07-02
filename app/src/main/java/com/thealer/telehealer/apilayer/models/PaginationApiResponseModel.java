package com.thealer.telehealer.apilayer.models;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

/**
 * Created by Aswin on 27,December,2018
 */
public class PaginationApiResponseModel extends BaseApiResponseModel {

    private int total_count;
    private int result_count;
    private int page_size;
    private int count;
    private Object current_page;
    private Object prev_page;
    private Object next_page;
    private Object prev;
    private Object next;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getResult_count() {
        return result_count;
    }

    public void setResult_count(int result_count) {
        this.result_count = result_count;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public Object getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(Object current_page) {
        this.current_page = current_page;
    }

    public Object getPrev_page() {
        return prev_page;
    }

    public void setPrev_page(Object prev_page) {
        this.prev_page = prev_page;
    }

    public Object getNext_page() {
        return next_page;
    }

    public void setNext_page(Object next_page) {
        this.next_page = next_page;
    }

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
