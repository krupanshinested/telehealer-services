package com.thealer.telehealer.apilayer.models.associationlist;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

/**
 * Created by Aswin on 13,November,2018
 */
public class AssociationApiViewModel extends BaseApiViewModel {

    public AssociationApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAssociationList(int page, boolean showProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getAssociations(true, page, Constants.PAGINATION_SIZE)
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
    public void getAssociationList(boolean showProgress, String doctorGuid) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {

                    getAuthApiService().getAssociations(false, doctorGuid)
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
}
