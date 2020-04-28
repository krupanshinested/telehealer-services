package com.thealer.telehealer.apilayer.models.associationlist;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Aswin on 13,November,2018
 */
public class AssociationApiViewModel extends BaseApiViewModel {

    public AssociationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAssociationList(String name, int page, String doctorGuid, boolean showProgress, boolean isMedicalAssistant) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getAssociations(true, page, Constants.PAGINATION_SIZE, name, isMedicalAssistant, doctorGuid)
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
