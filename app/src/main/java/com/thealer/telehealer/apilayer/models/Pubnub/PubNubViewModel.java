package com.thealer.telehealer.apilayer.models.Pubnub;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.ResultFetcher;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by rsekar on 12/26/18.
 */

public class PubNubViewModel extends BaseApiViewModel {

    public PubNubViewModel(@NonNull Application application) {
        super(application);
    }

    public void grantPubNub(String channel,String token) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().grantPubnubAccess(channel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    PubnubUtil.shared.enablePushOnChannel(token, channel);
                                    PubnubUtil.shared.enableVoipOnChannel(token,channel);
                                }
                            });
                }
            }
        });
    }

    public void grantPubNubAccess(String channel,ResultFetcher fetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().grantPubnubAccess(channel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    fetcher.didFetched(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

}
