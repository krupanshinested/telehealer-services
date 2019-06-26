package com.thealer.telehealer.apilayer.models.signature;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.CameraUtil;
import com.thealer.telehealer.views.base.BaseViewInterface;

/**
 * Created by Aswin on 18,January,2019
 */
public class SignatureApiViewModel extends BaseApiViewModel {
    public SignatureApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void uploadSignature(boolean isShowProgress, Bitmap signature) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    String path = CameraUtil.getBitmapFilePath(getApplication(), signature);
                    getAuthApiService().uploadSignature(getMultipartFile("signature", path))
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

    public void deleteSignature(boolean isShowProgress) {
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().deleteSignature()
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
