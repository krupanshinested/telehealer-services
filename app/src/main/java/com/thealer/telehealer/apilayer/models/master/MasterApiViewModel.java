package com.thealer.telehealer.apilayer.models.master;

import android.app.Application;

import androidx.annotation.NonNull;

import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseViewInterface;

import java.util.ArrayList;

public class MasterApiViewModel extends BaseApiViewModel {

    private static ArrayList<MasterResp.MasterItem> masters = new ArrayList<>();

    public MasterApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchMasters() {
        if (masters.size() > 0) {
            for (MasterResp.MasterItem masterItem : masters) {
                masterItem.setSelected(false);
            }
            MasterResp resp = new MasterResp();
            resp.setData(masters);
            baseApiResponseModelMutableLiveData.setValue(resp);
            return;
        }
        fetchToken(new BaseViewInterface() {
            @Override
            public void onStatus(boolean status) {
                if (status) {
                    getAuthApiService().fetchMasters()
                            .compose(applySchedulers())
                            .subscribe(new RAObserver<MasterResp>(Constants.SHOW_PROGRESS) {
                                @Override
                                public void onSuccess(MasterResp baseApiResponseModel) {
                                    if (baseApiResponseModel != null) {
                                        masters.clear();
                                        masters.addAll(baseApiResponseModel.getData());
                                    }
                                    baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                                }
                            });

                }
            }
        });
    }
}
