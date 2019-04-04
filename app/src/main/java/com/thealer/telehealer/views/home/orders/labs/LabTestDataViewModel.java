package com.thealer.telehealer.views.home.orders.labs;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.thealer.telehealer.apilayer.models.orders.lab.LabsBean;

import java.util.List;

/**
 * Created by Aswin on 01,December,2018
 */
public class LabTestDataViewModel extends ViewModel {

    public String testTitle;


    //for lab req model

    public MutableLiveData<List<LabsBean>> labsBeanLiveData = new MutableLiveData<>();

    public MutableLiveData<List<LabsBean>> getLabsBeanLiveData() {
        return labsBeanLiveData;
    }

    public void setLabsBeanLiveData(MutableLiveData<List<LabsBean>> labsBeanLiveData) {
        this.labsBeanLiveData = labsBeanLiveData;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }
}
