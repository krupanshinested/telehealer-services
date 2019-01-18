package com.thealer.telehealer.apilayer.models.addConnection;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 19,November,2018
 */
public class AddConnectionApiViewModel extends BaseApiViewModel {
    public AddConnectionApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void connectUser(String userId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    AddConnectionRequestModel addConnectionRequestModel = new AddConnectionRequestModel();
                    addConnectionRequestModel.setRequestee_id(userId);
                    addConnectionRequestModel.setType(Constants.ADD_CONNECTION_REQ_TYPE);
                    addConnectionRequestModel.setMessage(Constants.ADD_CONNECTION_REQ_MSG);

                    getAuthApiService().addConnection(addConnectionRequestModel)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                    EventRecorder.recordNotification("CONNECTION_REQUEST");
                                    EventRecorder.recordConnection("CONNECTION_REQUESTED");

                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
