package com.thealer.telehealer.views.home.vitals.vitalReport;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;

import java.io.Serializable;

public class VitalBulkPdfApiResponseModel extends BaseApiResponseModel implements Serializable {
    private String combined_pdf_path;

    public String getCombined_pdf_path() {
        return combined_pdf_path;
    }

    public void setCombined_pdf_path(String combined_pdf_path) {
        this.combined_pdf_path = combined_pdf_path;
    }
}
