package com.thealer.telehealer.apilayer.models.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKey;
import com.thealer.telehealer.common.Signal.SignalModels.SignalKeyPostModel;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.pubNub.PubNubNotificationPayload;
import com.thealer.telehealer.common.pubNub.PubnubUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;
import com.thealer.telehealer.views.home.chat.PubnubUserStatusInterface;

/**
 * Created by Aswin on 15,May,2019
 */
public class ChatApiViewModel extends BaseApiViewModel {
    public ChatApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserKeys(@NonNull String userGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserKeys(userGuid)
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

    public void postUserKeys(@NonNull SignalKeyPostModel signalKey, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postUserKeys(signalKey)
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

    public void getPreviousChat(String patientGuid, int page, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getChatMessages(patientGuid, true, page, Constants.PAGINATION_SIZE)
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

    public void sendMessage(ChatMessageRequestModel chatMessageRequestModel) {
        PubnubUtil.shared.getUserStatus(chatMessageRequestModel.getTo(), new PubnubUserStatusInterface() {
            @Override
            public void userStatus(int chatStatus) {
                fetchToken(new BaseViewInterface() {
                    @Override
                    public void onStatus(boolean status) {
                        if (status) {
                            getAuthApiService().sendMessage(chatStatus != PubnubUtil.CHAT_STATUS_ACTIVE,chatMessageRequestModel)
                                    .compose(applySchedulers())
                                    .subscribe(new RAObserver<BaseApiResponseModel>() {
                                        @Override
                                        public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                            if (chatStatus == PubnubUtil.CHAT_STATUS_ACTIVE) {
                                                PubnubUtil.shared.sendPubnubMessage(chatMessageRequestModel.getTo(), chatMessageRequestModel.getMessages().get(0).getReceiver_one_message());
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    public void getPrecannedMessages() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String type = !UserType.isUserPatient() ? "medical" : null;
                    getPublicApiService().getPrecannedMessages(type)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>() {
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
