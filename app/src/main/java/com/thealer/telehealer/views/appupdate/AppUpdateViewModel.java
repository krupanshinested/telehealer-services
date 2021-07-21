package com.thealer.telehealer.views.appupdate;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.pubNub.PubnubUtil;

public class AppUpdateViewModel extends BaseApiViewModel {
    public AppUpdateViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkForUpdate() {
        getAuthApiService().fetchLatestVersion(getAppTypeFromFlavor()).compose(applySchedulers())
                .subscribe(new RAObserver<AppUpdateResponse>(getProgress(true)) {
                    @Override
                    public void onSuccess(AppUpdateResponse baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                    }
                });

    }

    private int getAppTypeFromFlavor() {
        return BuildConfig.FLAVOR_TYPE.equals("doctor") ? 2 : 1;
    }
}
