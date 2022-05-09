package com.thealer.telehealer.views.call;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.manager.RetrofitManager;
import com.thealer.telehealer.apilayer.models.feedback.SubmitResponse;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonObject;
import com.thealer.telehealer.common.Feedback.FeedbackCallback;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.home.chat.ChatActivity;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Aswin on 09,August,2019
 */
public class CallMessageActivity extends ContentActivity {
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_btn) {
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.USER_GUID, getIntent().getStringExtra(ArgumentKeys.USER_GUID));
            bundle.putString(ArgumentKeys.DOCTOR_GUID, getIntent().getStringExtra(ArgumentKeys.DOCTOR_GUID));

            Intent intent = new Intent(this, ChatActivity.class).putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonObject.showDialog(this, TeleHealerApplication.questiondata, TeleHealerApplication.callrequest, TeleHealerApplication.popsessionId, TeleHealerApplication.popto_guid, TeleHealerApplication.popdoctorGuid, feedbackCallback);

    }

    FeedbackCallback feedbackCallback = new FeedbackCallback() {
        @Override
        public void onActionSuccess(HashMap<String, Object> param) {
            submitFeedback(param);
        }
    };

    private void submitFeedback(HashMap<String, Object> param) {
        this.showProgressDialog();
        Call<SubmitResponse> call = RetrofitManager.getInstance(getApplication()).getAuthApiService().submitFeedback(param);
        call.enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                Log.d("TAG", "onResponse: " + response.body());
                dismissProgressDialog();
                SubmitResponse submitResponse = response.body();
                if (submitResponse.getSuccess()) {
                    Toast.makeText(CallMessageActivity.this, "" + submitResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {
                call.cancel();
                Log.d("TAG", "onFailure: ");
                dismissProgressDialog();
            }
        });

    }
}
