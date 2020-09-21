package com.thealer.telehealer.views.home.orders.radiology;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by Aswin on 11,December,2018
 */
public class RadiologyListViewModel extends ViewModel {

    MutableLiveData<List<RadiologyListModel>> selectedRadiologyListLiveData = new MutableLiveData<>();
    MutableLiveData<List<String>> selectedIdList = new MutableLiveData<>();

    public MutableLiveData<List<String>> getSelectedIdList() {
        return selectedIdList;
    }

    public void setSelectedIdList(MutableLiveData<List<String>> selectedIdList) {
        this.selectedIdList = selectedIdList;
    }

    public MutableLiveData<List<RadiologyListModel>> getSelectedRadiologyListLiveData() {
        return selectedRadiologyListLiveData;
    }

    public void setSelectedRadiologyListLiveData(MutableLiveData<List<RadiologyListModel>> selectedRadiologyListLiveData) {
        this.selectedRadiologyListLiveData = selectedRadiologyListLiveData;
    }
}
