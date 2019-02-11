package com.thealer.telehealer.apilayer.models.addConnection;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 19,November,2018
 */
public class ConnectionListApiViewModel extends BaseApiViewModel {
    private int page_size = Constants.PAGINATION_SIZE;
    private boolean paginate = true;

    public ConnectionListApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUnconnectedList(String name, int page, boolean showProgress, boolean isMedicalAssistant) {

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    int progress = Constants.SHOW_NOTHING;
                    if (showProgress) {
                        progress = Constants.SHOW_PROGRESS;
                    }

                    getAuthApiService().getUnConnectedUsers(paginate, page, page_size, name, isMedicalAssistant)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(progress) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
