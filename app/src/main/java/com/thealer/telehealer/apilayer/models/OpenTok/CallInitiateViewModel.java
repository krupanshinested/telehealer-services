package com.thealer.telehealer.apilayer.models.OpenTok;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OpenTokTokenFetcher;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 1/4/19.
 */

public class CallInitiateViewModel extends BaseApiViewModel {

    public CallInitiateViewModel(@NonNull Application application) {
        super(application);
    }

    public void getTokenForSession(String call_quality,String doctor_guid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().getSessionId(call_quality,doctor_guid)
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<TokenFetchModel>() {
                            @Override
                            public void onSuccess(TokenFetchModel tokenFetchModel) {
                                baseApiResponseModelMutableLiveData.setValue(tokenFetchModel);
                            }
                        });
            }
        });
    }


}
