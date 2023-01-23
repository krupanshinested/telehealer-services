package com.thealer.telehealer.apilayer.models.pendingInvites;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.api.ApiInterface;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionRequestModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.notification.NewNotificationListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesApiViewModel extends BaseApiViewModel {
    public PendingInvitesApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getPendingInvites(int page, boolean isRequestor, boolean isRequestee, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(ApiInterface.TYPE, NewNotificationListAdapter.REQUEST_TYPE_CONNECTION);
                    params.put(ApiInterface.STATUS, NewNotificationListAdapter.REQUEST_STATUS_OPEN);

                    if (isRequestor)
                        params.put("requestor", isRequestor);

                    if (isRequestee)
                        params.put("requestee", isRequestee);

                    getAuthApiService().getPendingInvites(params, true, page, Constants.PAGINATION_SIZE)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getNonRegisteredUserInvites(int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNonRegisteredUserInvites(true, page, Constants.PAGINATION_SIZE, false)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public  void getNonRegisteredUserInvitesByROLE(int page,String role,boolean isShowProgress){
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getNonRegisteredUserInvitesByROLE(true,page,Constants.PAGINATION_SIZE,role)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)){

                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void inviteUserByEmailPhone(String doctor_user_guid, InviteByEmailPhoneRequestModel emailPhoneRequestModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().inviteUserByEmailPhone(doctor_user_guid,emailPhoneRequestModel)
                        .compose(applySchedulers())
                        .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                            @Override
                            public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                            }
                        });
            }
        });
    }

    public void connectUser(String doctorGuid, String userId,String designation) {
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

}
