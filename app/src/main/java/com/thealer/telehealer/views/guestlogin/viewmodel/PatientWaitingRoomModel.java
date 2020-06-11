package com.thealer.telehealer.views.guestlogin.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;

/**
 * Created by rsekar on 12/18/18.
 */

public class PatientWaitingRoomModel extends BaseApiViewModel {

    public PatientWaitingRoomModel(@NonNull Application application) {
        super(application);
    }

    public void kickOutPatient(Patientinfo patientinfo) {
        Log.d("PatientWaitingRoomModel","kickOutPatient");
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().kickOutPatient(patientinfo.getSessionId(),true)
                            .compose(applySchedulers())
                            .subscribe(new BaseApiViewModel.RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    Log.d("PatientWaitingRoomModel","onSuccess");
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

}