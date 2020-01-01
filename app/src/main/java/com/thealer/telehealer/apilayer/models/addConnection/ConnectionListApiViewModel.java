package com.thealer.telehealer.apilayer.models.addConnection;

import android.app.Application;
import androidx.annotation.NonNull;

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

    public void getUnconnectedList(String name, String speciality, int page, boolean showProgress, boolean isMedicalAssistant) {

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getUnConnectedUsers(paginate,true, page, page_size, name, isMedicalAssistant, Constants.ROLE_DOCTOR, speciality)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(showProgress)) {
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
