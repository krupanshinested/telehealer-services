package com.thealer.telehealer.apilayer.models.orders.lab;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 23,November,2018
 */
public class IcdCodeApiResponseModel extends BaseApiResponseModel {

    private Object next;
    private int total_count;
    private int result_count;
    private List<ResultsBean> results;

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

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

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public Map<String, String> getResultHashMap(){

        Map<String , String > stringMap = new HashMap<>();

        for (int i = 0; i < getResults().size(); i++) {
            stringMap.put(getResults().get(i).getCode(), getResults().get(i).getDescription());
        }

        return stringMap;
    }

    public static class ResultsBean {

        private String code;
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
