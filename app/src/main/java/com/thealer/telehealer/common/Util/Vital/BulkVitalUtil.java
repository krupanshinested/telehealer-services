package com.thealer.telehealer.common.Util.Vital;

import android.util.Log;

import androidx.annotation.Nullable;

import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.vitals.CreateVitalApiRequestModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;

public class BulkVitalUtil {
    public static BulkVitalUtil shared;

    private final int limit = 8000;
    private VitalsApiViewModel vitalsApiViewModel;

    private BulkVitalUtil() {
        vitalsApiViewModel = new VitalsApiViewModel(TeleHealerApplication.application);
    }

    public static BulkVitalUtil getInstance() {
        if (shared != null) {
            return shared;
        }
        shared = new BulkVitalUtil();
        return shared;
    }

    public void uploadAllVitals(int iterate,
                                 ArrayList<VitalsApiResponseModel>  vitals,
                                 @Nullable String user_guid,
                                 @Nullable String doctor_guid,
                                 @Nullable String order_id) {

        if (vitals.size() == 0) {
            return;
        }

        if (iterate == 1) {
            Log.d("VitalBulkUtil","sendBulkValues count "+ vitals.size());
        }

        int previousPostCount = (iterate - 1) * limit;
        int toCount;
        boolean needNextIterate;

        if (vitals.size() > iterate * limit) {
            toCount = (iterate * limit);
            needNextIterate = true;
        } else {
            toCount = vitals.size();
            needNextIterate = false;
        }
        ArrayList<VitalsApiResponseModel> toSendVitals = new ArrayList<>(vitals.subList(previousPostCount,toCount));

        Log.d("VitalBulkUtil","start postion "+ previousPostCount);
        Log.d("VitalBulkUtil","end postion"+ toCount);

        if (toSendVitals.size() > 0) {
            CreateVitalApiRequestModel createVitalApiRequestModel = new CreateVitalApiRequestModel(toSendVitals);
            createVitalApiRequestModel.setUser_guid(user_guid);
            createVitalApiRequestModel.setOrder_id(order_id);

            vitalsApiViewModel.sentBulkVitals(createVitalApiRequestModel, doctor_guid, new SuccessViewInterface() {
                @Override
                public void onSuccessViewCompletion(boolean success) {
                    if (needNextIterate) {
                        uploadAllVitals(iterate+1,vitals,user_guid,doctor_guid,order_id);
                    }
                }
            });
        }
    }

}
