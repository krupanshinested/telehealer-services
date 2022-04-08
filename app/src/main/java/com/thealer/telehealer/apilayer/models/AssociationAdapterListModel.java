package com.thealer.telehealer.apilayer.models;

import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;

public class AssociationAdapterListModel {
        private int type;
        private String title;
        private boolean selectedFlag =  false;
        private CommonUserApiResponseModel commonUserApiResponseModel;

        public boolean isSelectedFlag() {
            return selectedFlag;
        }

        public void setSelectedFlag(boolean selectedFlag) {
            this.selectedFlag = selectedFlag;
        }

        public AssociationAdapterListModel(int type, String title) {
            this.type = type;
            this.title = title;
        }

        public AssociationAdapterListModel(int type, CommonUserApiResponseModel commonUserApiResponseModel) {
            this.type = type;
            this.commonUserApiResponseModel = commonUserApiResponseModel;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public CommonUserApiResponseModel getCommonUserApiResponseModel() {
            return commonUserApiResponseModel;
        }

        public void setCommonUserApiResponseModel(CommonUserApiResponseModel commonUserApiResponseModel) {
            this.commonUserApiResponseModel = commonUserApiResponseModel;
        }

    }