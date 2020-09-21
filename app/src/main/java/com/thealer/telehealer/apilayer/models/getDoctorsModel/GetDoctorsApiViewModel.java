package com.thealer.telehealer.apilayer.models.getDoctorsModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 25,October,2018
 */
public class GetDoctorsApiViewModel extends BaseApiViewModel {

    public MutableLiveData<GetDoctorsApiResponseModel> getDoctorsApiResponseModelMutableLiveData = new MutableLiveData<>();

    public GetDoctorsApiViewModel(@NonNull Application application) {
        super(application);
    }


    public void getDoctorsDetailList(int page, String name, boolean isShowProgress) {

        String fields = "npi,licenses,specialties,practices,educations,hospital_affiliations,profile,uid";

        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getPublicApiService()
                            .getDoctors(page, Constants.PAGINATION_SIZE, name, fields)
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                                @Override
                                public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                                    getBaseApiResponseModelMutableLiveData().setValue(baseApiResponseModel);
                                }
                            });
                }
            }
        });
    }
}
