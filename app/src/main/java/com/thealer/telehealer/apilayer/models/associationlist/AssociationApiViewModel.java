package com.thealer.telehealer.apilayer.models.associationlist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.DefaultPhysicianModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.unique.UniqueResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Aswin on 13,November,2018
 */
public class AssociationApiViewModel extends BaseApiViewModel {

    public AssociationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAssociationList(@Nullable String search, int page, String doctorGuid, boolean showProgress, boolean isMedicalAssistant) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getAssociations(search, true, page, Constants.PAGINATION_SIZE, isMedicalAssistant, doctorGuid)
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

    public void getAssociationList(@NonNull String search, boolean showProgress, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getAssociations(search, false, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<CommonUserApiResponseModel>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(ArrayList<CommonUserApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> responseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(responseModels);
                                }
                            });
                }
            }
        });
    }

    public void getDefaultPhysician() {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getDefaultPhysician()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<DefaultPhysicianModel>(getProgress(true)) {
                                @Override
                                public void onSuccess(DefaultPhysicianModel data) {
                                    defaultPhysicianMutableLiveData.setValue(data);
                                }
                            });
                }
            }
        });
    }

    public void saveDefaultPhysician(HashMap<String, Object> req) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().saveDefaultPhysician(req)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<DefaultPhysicianModel>(getProgress(true)) {
                                @Override
                                public void onSuccess(DefaultPhysicianModel data) {
                                    updateDefaultPhysicianMutableLiveData.setValue(data);
                                }
                            });
                }
            }
        });
    }


    public void getUniqueUrl() {
        fetchToken(status -> getAuthApiService().getUniqueUrl()
                .compose(applySchedulers())
                .subscribe(new RAObserver<UniqueResponseModel>(getProgress(true)) {
                    @Override
                    public void onSuccess(UniqueResponseModel uniqueResponseModel) {
                        baseUniqueApiResponseModelMutableLiveData.setValue(uniqueResponseModel);
                    }
                }));
    }

    public void getDoctorGroupedAssociations(boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getDoctorGroupedAssociations()
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<DoctorGroupedAssociations>(getProgress(showProgress)) {
                                @Override
                                public void onSuccess(ArrayList<DoctorGroupedAssociations> data) {
                                    ArrayList<BaseApiResponseModel> responseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(responseModels);
                                }
                            });
                }
            }
        });
    }

    public void getUserAssociationDetail(String userGuid, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().getUserAssociationDetail(userGuid, doctorGuid)
                            .compose(applySchedulers())
                            .subscribe(new RAListObserver<CommonUserApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(ArrayList<CommonUserApiResponseModel> data) {
                                    ArrayList<BaseApiResponseModel> responseModels = new ArrayList<>(data);
                                    baseApiArrayListMutableLiveData.setValue(responseModels);
                                }
                            });
                }
            }
        });
    }

    public void updateAssociationDetail(String userGuid, UpdateAssociationRequestModel requestModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().updateAssociationDetail(userGuid, requestModel)
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

    public void getAssociationUserDetails(Set<String> guidList, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService()
                        .getAssociationUserDetail(guidList.toString().replace("[", "").replace("]", ""), doctorGuid)
                        .compose(applySchedulers())
                        .subscribe(new RAListObserver<CommonUserApiResponseModel>(getProgress(isShowProgress)) {
                            @Override
                            public void onSuccess(ArrayList<CommonUserApiResponseModel> data) {
                                ArrayList<BaseApiResponseModel> responseModels = new ArrayList<>(data);
                                baseApiArrayListMutableLiveData.setValue(responseModels);
                            }
                        });
            }
        });
    }

    public void getAssociationUserDetails(Set<String> guidList, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().getAssociationUserDetail(guidList.toString().replace("[", "").replace("]", ""))
                        .compose(applySchedulers())
                        .subscribe(new RAListObserver<CommonUserApiResponseModel>(getProgress(isShowProgress)) {
                            @Override
                            public void onSuccess(ArrayList<CommonUserApiResponseModel> data) {
                                ArrayList<BaseApiResponseModel> responseModels = new ArrayList<>(data);
                                baseApiArrayListMutableLiveData.setValue(responseModels);
                            }
                        });
            }
        });
    }
}
