package com.thealer.telehealer.apilayer.models.vitalReport;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalReportApiViewModel extends BaseApiViewModel {

    public static final String LAST_WEEK = "last_week";
    public static final String LAST_TWO_WEEK = "last_two_weeks";
    public static final String LAST_MONTH = "last_month";
    public static final String ALL = "all";

    public VitalReportApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getVitalUsers(String type, String startDate, String endDate, String doctorGuid, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                getAuthApiService().getVitalUsers(type, doctorGuid, startDate, endDate)
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
}
