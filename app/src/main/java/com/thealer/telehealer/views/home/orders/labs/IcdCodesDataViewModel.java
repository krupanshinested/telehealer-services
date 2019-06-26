package com.thealer.telehealer.views.home.orders.labs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 11,December,2018
 */
public class IcdCodesDataViewModel extends ViewModel {

    //for selected icd code list with description
    public MutableLiveData<HashMap<String, IcdCodeApiResponseModel.ResultsBean>> icdCodeDetailHashMap = new MutableLiveData<>();

    //for selected icd code list
    public MutableLiveData<List<String>> selectedIcdCodeList = new MutableLiveData<>();

    public MutableLiveData<HashMap<String, IcdCodeApiResponseModel.ResultsBean>> getIcdCodeDetailHashMap() {
        return icdCodeDetailHashMap;
    }

    public void setIcdCodeDetailHashMap(MutableLiveData<HashMap<String, IcdCodeApiResponseModel.ResultsBean>> icdCodeDetailHashMap) {
        this.icdCodeDetailHashMap = icdCodeDetailHashMap;
    }

    public MutableLiveData<List<String>> getSelectedIcdCodeList() {
        return selectedIcdCodeList;
    }

    public void setSelectedIcdCodeList(MutableLiveData<List<String>> selectedIcdCodeList) {
        this.selectedIcdCodeList = selectedIcdCodeList;
    }
}
