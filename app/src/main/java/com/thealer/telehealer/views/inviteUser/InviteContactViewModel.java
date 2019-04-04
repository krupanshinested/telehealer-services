package com.thealer.telehealer.views.inviteUser;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 19,February,2019
 */
public class InviteContactViewModel extends ViewModel {

    MutableLiveData<List<SelectedContactModel>> selectedContactList = new MutableLiveData<>();
    Map<String, String> selectedIdList = new HashMap<>();

    public MutableLiveData<List<SelectedContactModel>> getSelectedContactList() {
        return selectedContactList;
    }

    public void setSelectedContactList(MutableLiveData<List<SelectedContactModel>> selectedContactList) {
        this.selectedContactList = selectedContactList;
    }
}
