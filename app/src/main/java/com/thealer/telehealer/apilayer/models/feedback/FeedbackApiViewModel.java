package com.thealer.telehealer.apilayer.models.feedback;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.feedback.question.FeedbackQuestionModel;
import com.thealer.telehealer.apilayer.models.feedback.setting.FeedbackSettingModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.views.base.BaseViewInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackApiViewModel extends BaseApiViewModel {

    public FeedbackApiViewModel(@NonNull Application application) {
        super(application);
    }

    public String showFeedback = "";
    public String showFeedbackRating = "";

    public FeedbackSettingModel getFeedbackSetting() {
        final FeedbackSettingModel[] feedbackSettingModel = new FeedbackSettingModel[1];
        Call<FeedbackSettingModel> call = getAuthApiService().getFeedbackSetting();
        call.enqueue(new Callback<FeedbackSettingModel>() {
            @Override
            public void onResponse(Call<FeedbackSettingModel> call, Response<FeedbackSettingModel> response) {
                Log.d("TAG", "onResponse: "+response.body());
                feedbackSettingModel[0] = response.body();
            }

            @Override
            public void onFailure(Call<FeedbackSettingModel> call, Throwable t) {
                call.cancel();
                feedbackSettingModel[0] = null;

            }
        });
        return feedbackSettingModel[0];

    }

    public FeedbackQuestionModel getFeedbackQuestion() {
        final FeedbackQuestionModel[] feedbackQuestionModel = new FeedbackQuestionModel[1];
        Call<FeedbackQuestionModel> call = getAuthApiService().getFeedbackQusetion("call");
        call.enqueue(new Callback<FeedbackQuestionModel>() {
            @Override
            public void onResponse(Call<FeedbackQuestionModel> call, Response<FeedbackQuestionModel> response) {
                Log.d("TAG", "onResponse: "+response.body());
                feedbackQuestionModel[0] = response.body();
            }

            @Override
            public void onFailure(Call<FeedbackQuestionModel> call, Throwable t) {
                call.cancel();
                feedbackQuestionModel[0] = null;
            }
        });
        return feedbackQuestionModel[0];
    }

}
