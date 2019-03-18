package com.thealer.telehealer.apilayer.models.diet.food;

import android.app.Application;
import android.support.annotation.NonNull;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogExternalAPI;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 21,February,2019
 */
public class FoodApiViewModel extends BaseApiViewModel {

    public FoodApiViewModel(@NonNull Application application) {
        super(application);
    }

    public void getNutrientsDetail(String foodId, String measureUri, int quantity, boolean isShowProgress) {
        List<NutrientsDetailRequestModel> modelList = new ArrayList<>();
        modelList.add(new NutrientsDetailRequestModel(quantity, measureUri, foodId));

        Map<String, List<NutrientsDetailRequestModel>> param = new HashMap<>();
        param.put("ingredients", modelList);

        getFoodApiService()
                .getFoodDetail(getApplication().getString(R.string.food_db_app_id), getApplication().getString(R.string.food_db_app_key), param)
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);

                        HashMap<String, String> details = new HashMap<>();
                        details.put("status", "success");
                        details.put("event", "foodDetail");
                        TeleLogger.shared.log(TeleLogExternalAPI.edamam, details);

                    }
                });

    }

    public void searchFood(String query, int session, boolean isShowProgress) {
        getFoodApiService().getFoodList(query, session, getApplication().getString(R.string.food_db_app_id), getApplication().getString(R.string.food_db_app_key))
                .compose(applySchedulers())
                .subscribe(new RAObserver<BaseApiResponseModel>(getProgress(isShowProgress)) {
                    @Override
                    public void onSuccess(BaseApiResponseModel baseApiResponseModel) {
                        baseApiResponseModelMutableLiveData.setValue(baseApiResponseModel);
                        HashMap<String, String> details = new HashMap<>();
                        details.put("status", "success");
                        details.put("event", "getFoodItems");
                        TeleLogger.shared.log(TeleLogExternalAPI.edamam, details);
                    }
                });
    }
}
