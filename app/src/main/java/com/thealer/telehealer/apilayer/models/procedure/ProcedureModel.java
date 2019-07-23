package com.thealer.telehealer.apilayer.models.procedure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 23,July,2019
 */
public class ProcedureModel implements Serializable {

    private List<CPTCodesBean> CPT_codes = new ArrayList<>();

    public ProcedureModel() {
    }

    public ProcedureModel(List<CPTCodesBean> CPT_codes) {
        this.CPT_codes = CPT_codes;
    }

    public List<CPTCodesBean> getCPT_codes() {
        return CPT_codes;
    }

    public void setCPT_codes(List<CPTCodesBean> CPT_codes) {
        this.CPT_codes = CPT_codes;
    }

    public static class CPTCodesBean implements Serializable {

        private String code;
        private String description;

        public CPTCodesBean() {
        }

        public CPTCodesBean(String code, String description) {
            this.code = code;
            this.description = description;
        }

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
