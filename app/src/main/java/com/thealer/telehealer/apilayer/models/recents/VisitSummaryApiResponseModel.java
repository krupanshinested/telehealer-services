package com.thealer.telehealer.apilayer.models.recents;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

/**
 * Created by Aswin on 17,July,2019
 */
public class VisitSummaryApiResponseModel extends BaseApiResponseModel {

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {

        private String visit_summary;

        public String getVisit_summary() {
            return visit_summary;
        }

        public void setVisit_summary(String visit_summary) {
            this.visit_summary = visit_summary;
        }
    }
}
