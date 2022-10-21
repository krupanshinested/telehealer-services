package com.thealer.telehealer.apilayer.models.OpenTok;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.BuildConfig;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.signin.SigninApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.OpenTok.CallSettings;
import com.thealer.telehealer.common.OpenTok.openTokInterfaces.OpenTokTokenFetcher;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;

/**
 * Created by rsekar on 12/27/18.
 */

public class OpenTokViewModel extends BaseApiViewModel {

    public OpenTokViewModel(@NonNull Application application) {
        super(application);
    }

    public void getTokenForSession(String sessionId, @Nullable OpenTokTokenFetcher fetcher) {
        if (BaseApiViewModel.isAuthExpired()) {
            getAuthApiService().refreshToken(appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN), true,BuildConfig.VERSION_NAME,true)
                    .compose(applySchedulers())
                    .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                        @Override
                        public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                            Utils.updateLastLogin();
                            Utils.storeLastActiveTime();
                            SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                            if (signinApiResponseModel.isSuccess()) {

                                //Setting the temp token
                                String expiredToken = appPreference.getString(PreferenceConstants.USER_AUTH_TOKEN);
                                appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                                new WhoAmIApiViewModel(application).assignWhoAmI();
                                getToken(sessionId,expiredToken, fetcher);
                            }

                            EventRecorder.updateVersion();

                        }
                    });
        } else {
            getToken(sessionId,null, fetcher);
        }
    }

    private void getToken(String sessionId,@Nullable String expiredAuthToken, @Nullable  OpenTokTokenFetcher fetcher) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getOpenTokToken(sessionId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<CallSettings>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(CallSettings callSettings) {
                                    callSettings.expiredTokenWhileCallPlaced = expiredAuthToken;
                                    baseApiResponseModelMutableLiveData.setValue(callSettings);
                                    if (fetcher != null) {
                                        fetcher.didFetched(callSettings);
                                    }
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
                    getAuthApiService().updateCallStatus(sessionId, params)
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

    public void updateScreenshot(String sessionId, Bitmap bitmap) {
        try {
            String path = CameraUtil.getBitmapFilePath(getApplication(), bitmap);

            fetchToken(new BaseViewInterface() {
                @Override
                public void onStatus(boolean status) {
                    if (status) {
                        getAuthApiService().uploadScreenshot(sessionId, getMultipartFile("audio_stream_screenshot", path))
                                .compose(applySchedulers())
                                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                    @Override
                                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {

                                    }
                                });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void stopArchieve(String sessionId) {
        Log.d("openTok", "stopArchieve "+sessionId);
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Log.d("openTok", "stopArchieve "+status);
                    getAuthApiService().stopArchive(sessionId)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    Log.d("openTok", "stopArchieve onSuccess");
                                }
                            });
                }
            }
        });
    }

    public void postaVoipCall(String userGuid,@Nullable String doctorGuid, String toGuid,
                              @Nullable String scheduleId, String callType) {

        HashMap<String, String> result = new HashMap<>();
        result.put("user_guid", toGuid);
        if (scheduleId != null) {
            result.put("schedule_id", scheduleId);
        }
        result.put("from", UserDetailPreferenceManager.getUser_guid());
        result.put("type", callType);

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if(UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.MAKE_CALLS_CODE);
                    }
                    getAuthApiService().postaVOIPCall(headers,doctorGuid, result)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<CallSettings>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(CallSettings tokenFetchModel) {

                                    EventRecorder.recordNotification("REQUEST_CALL");
                                    baseApiResponseModelMutableLiveData.setValue(tokenFetchModel);
                                }
                            });
                }
            }
        });
    }

    public void postTranscript(String orderId, String transcript, Boolean isCaller) {
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
    public void refreshToken() {
        String refreshToken = TeleHealerApplication.appPreference.getString(PreferenceConstants.USER_REFRESH_TOKEN);
        getAuthApiService().refreshToken(refreshToken,false,BuildConfig.VERSION_NAME,true)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        Utils.updateLastLogin();
                        Utils.storeLastActiveTime();
                        SigninApiResponseModel signinApiResponseModel = (SigninApiResponseModel) baseApiResponseModel;
                        if (signinApiResponseModel.isSuccess()) {
                            appPreference.setString(PreferenceConstants.USER_AUTH_TOKEN, signinApiResponseModel.getToken());
                        }
                    }
                });
    }
}
