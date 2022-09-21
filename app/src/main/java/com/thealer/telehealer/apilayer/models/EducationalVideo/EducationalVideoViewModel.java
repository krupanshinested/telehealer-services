package com.thealer.telehealer.apilayer.models.EducationalVideo;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EducationalVideoViewModel extends BaseApiViewModel {
    public EducationalVideoViewModel(@NonNull Application application) {
        super(application);
    }

    public void getEducationalVideo(@Nullable String search, @Nullable String doctorGuid, int page) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getEducationalVideo(search, true, doctorGuid, page, Constants.PAGINATION_SIZE)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void getEducationalVideos(@Nullable String doctorGuid, String ids, String userGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getEducationalVideos(!UserType.isUserPatient(), ids, userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<EducationalVideoOrder>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(ArrayList<EducationalVideoOrder> data) {
                                    ArrayList<BaseApiResponseModel> apiResponseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(apiResponseModels);
                                }
                            });
                }
            }
        });
    }

    public void uploadScreenshot(String sessionId, Bitmap bitmap) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String path = CameraUtil.getBitmapFilePath(getApplication(), bitmap);
                    getAuthApiService().uploadVideoScreenshot(sessionId, getMultipartFile("audio_stream_screenshot", path))
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }


    public void postEducationalVideo(String doctorGuid, EducationalVideoRequest request) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if (UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.EDUCATIONAL_VIDEOS_CODE);
                    }
                    getAuthApiService().postEducationalVideo(headers,request,doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void patchEducationalVideo(String id) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Object> req = new HashMap<>();
                    req.put("viewed", true);
                    getAuthApiService().patchEducationalVideo(id, req)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_NOTHING) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void postEducationalVideoOrder(String doctorGuid, String userGuid, ArrayList<String> selectedIds) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_guid", userGuid);
                    map.put("videos", selectedIds);
                    getAuthApiService().postEducationalOrder(doctorGuid, map)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void updateEducationalVideo(String doctorGuid, String title, String description, int videoId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Object> details = new HashMap<>();
                    details.put("title", title);
                    details.put("description", description);
                    Map<String, String> headers = new HashMap<>();
                    if (UserType.isUserAssistant()) {
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.EDUCATIONAL_VIDEOS_CODE);
                    }
                    getAuthApiService().updateEducationalVideo(headers, videoId + "", details,doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void deleteEducationalVideo(String userGuid, int videoId) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    Map<String, String> headers = new HashMap<>();
                    if (userGuid != null && !userGuid.isEmpty()) {
                        headers.put(ArgumentKeys.USER_GUID, userGuid);
                        headers.put(ArgumentKeys.MODULE_CODE, ArgumentKeys.EDUCATIONAL_VIDEOS_CODE);
                    }
                    getAuthApiService().deleteEducationalVideo(headers, videoId + "")
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }

    public void unAssociateEducationalVideoOrder(@Nullable String doctorGuid, String userGuid, ArrayList<Integer> ids) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    HashMap<String, Object> req = new HashMap<>();
                    req.put("user_guid", userGuid);
                    req.put("videos", ids);
                    getAuthApiService().unAssociateEducationalVideoOrder(doctorGuid, req)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(Constants.SHOW_PROGRESS) {
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
