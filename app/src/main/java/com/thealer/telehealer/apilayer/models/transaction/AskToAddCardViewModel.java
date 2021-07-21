package com.thealer.telehealer.apilayer.models.transaction;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

public class AskToAddCardViewModel extends BaseApiViewModel {
    public AskToAddCardViewModel(@NonNull Application application) {
        super(application);
    }

    public void askToAddCard(String patientGuid, String doctorGuid) {

        HashMap<String, String> param = new HashMap<>();
        param.put("patient_guid", patientGuid);
        param.put("doctor_guid", doctorGuid);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().askToAddCard(param)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel model) {
                                    getBaseApiResponseModelMutableLiveData().setValue(model);
                                }
                            });
                }
            }
        });
    }

}
