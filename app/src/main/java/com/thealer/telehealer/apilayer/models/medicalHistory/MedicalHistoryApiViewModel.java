package com.thealer.telehealer.apilayer.models.medicalHistory;

import android.app.Application;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 22,January,2019
 */
public class MedicalHistoryApiViewModel extends BaseApiViewModel {
    public MedicalHistoryApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateQuestionary(@NonNull String userGuid, @NonNull UpdateQuestionaryBodyModel updateQuestionaryBodyModel, boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().updateUserQuestionnaire(userGuid, updateQuestionaryBodyModel)
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
}
