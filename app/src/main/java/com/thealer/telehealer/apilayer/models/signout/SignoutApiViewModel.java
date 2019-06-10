package com.thealer.telehealer.apilayer.models.signout;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.pubNub.PubnubUtil;

/**
 * Created by Aswin on 14,June,2019
 */
public class SignoutApiViewModel extends BaseApiViewModel {
    public SignoutApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void signOut() {
        getAuthApiService().signOut().compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        EventRecorder.recordUserSession("logout");
                        PubnubUtil.shared.unsubscribe();
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });
    }
}
