package com.thealer.telehealer.apilayer.models.OpenTok;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OpenTokTokenFetcher;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;

import okhttp3.MultipartBody;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 12/27/18.
 */

public class OpenTokViewModel extends BaseApiViewModel {

    public OpenTokViewModel(@NonNull Application application) {
        super(application);
    }

    public void getTokenForSession(String sessionId,OpenTokTokenFetcher fetcher) {

        if (BaseApiViewModel.isAuthExpired()) {

            getAuthApiService().refreshToken(appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN),true)
                    .compose(applySchedulers())
                    .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                        @Override
                        public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                            SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                            if (signinApiResponseModel.isSuccess()) {

                                //Setting the temp token
                                TokBox.shared.setTempToken(appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN));
                                appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());

                                getToken(sessionId,fetcher);
                            }

                            EventRecorder.updateVersion();

                        }
                    });
        } else {
            getToken(sessionId,fetcher);
        }
    }

    private void getToken(String sessionId,OpenTokTokenFetcher fetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getOpenTokToken(sessionId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<TokenFetchModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(TokenFetchModel tokenFetchModel) {

                                    fetcher.didFetched(tokenFetchModel);

                                }
                            });
                }
            }
        });
    }

    public void updateCallStatus(String sessionId, HashMap<String, String> params) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().updateCallStatus(sessionId,params)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                }
                            });
                }
            }
        });
    }

    public void postCallReview(HashMap<String, Object> params) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postReview(params)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void startArchieve(String sessionId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().startArchive(sessionId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                }
                            });
                }
            }
        });
    }

    public void postaVoipCall(@Nullable  String doctorGuid,String toGuid,
                              @Nullable String scheduleId,String callType) {

        HashMap<String,String> result = new HashMap<>();
        result.put("user_guid",toGuid);
        if (scheduleId != null) {
            result.put("schedule_id",scheduleId);
        }
        result.put("from",UserDetailPreferenceManager.getUser_guid());
        result.put("type",callType);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postaVOIPCall(doctorGuid,result)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<TokenFetchModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(TokenFetchModel tokenFetchModel) {

                                    EventRecorder.recordNotification("REQUEST_CALL");
                                    baseApiResponseModelMutableLiveData.setValue(tokenFetchModel);
                                }
                            });
                }
            }
        });
    }

    public void postTranscript(String orderId,String transcript,Boolean isCaller) {
        String fileName = isCaller ? "caller_speech" : "callee_speech";

        if (TextUtils.isEmpty(transcript)) {
            transcript = " ";
        }

        String finalTranscript = transcript;
        
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().postTranscript(orderId, MultipartBody.Part.createFormData(fileName, finalTranscript))
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                }
                            });
                }
            }
        });
    }

}
