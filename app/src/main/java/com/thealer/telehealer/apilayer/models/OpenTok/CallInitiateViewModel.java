package com.thealer.telehealer.apilayer.models.OpenTok;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by rsekar on 1/4/19.
 */

public class CallInitiateViewModel extends BaseApiViewModel {

    public CallInitiateViewModel(@NonNull Application application) {
        super(application);
    }

    public void getTokenForTestSession() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().getSessionId("true")
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
