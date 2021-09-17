package com.thealer.telehealer.apilayer.models.addConnection;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aswin on 19,November,2018
 */
public class AddConnectionApiViewModel extends BaseApiViewModel {
    public AddConnectionApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void connectUser(String userGuid,String toGuid, String doctorGuid, String userId,String designation) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.INVITE_OTHERS_CODE);
                    }

                    AddConnectionRequestModel addConnectionRequestModel = new AddConnectionRequestModel();
                    addConnectionRequestModel.setRequestee_id(userId);
                    addConnectionRequestModel.setType(Constants.ADD_CONNECTION_REQ_TYPE);
                    addConnectionRequestModel.setMessage(Constants.ADD_CONNECTION_REQ_MSG);
                    addConnectionRequestModel.setDesignation(designation);
                    
                    getAuthApiService().addConnection(headers,addConnectionRequestModel, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);

                                    EventRecorder.recordNotification("CONNECTION_REQUEST");
                                    EventRecorder.recordConnection("CONNECTION_REQUESTED");
                                }
                            });
                }
            }
        });
    }
    public void addPatientDocConnection(String toGuid, String doctorGuid, String userId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    AddConnectionRequestModel addConnectionRequestModel = new AddConnectionRequestModel();
                    addConnectionRequestModel.setRequestee_id(userId);
                    addConnectionRequestModel.setType(Constants.ADD_CONNECTION_REQ_TYPE);
                    addConnectionRequestModel.setMessage(Constants.ADD_CONNECTION_REQ_MSG);

                    getAuthApiService().addPatientDocConnection(addConnectionRequestModel, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);

                                    EventRecorder.recordNotification("CONNECTION_REQUEST");
                                    EventRecorder.recordConnection("CONNECTION_REQUESTED");
                                }
                            });
                }
            }
        });
    }
}
