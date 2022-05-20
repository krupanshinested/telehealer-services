package com.thealer.telehealer.views.call;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.manager.RetrofitManager;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.feedback.SubmitResponse;
import com.thealer.telehealer.apilayer.models.feedback.question.FeedbackQuestionModel;
import com.thealer.telehealer.apilayer.models.feedback.setting.FeedbackSettingModel;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonObject;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Feedback.FeedbackCallback;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.Procedure.ProcedureConstants;
import com.thealer.telehealer.views.Procedure.SelectProcedureBottomSheetDialogFragment;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CCMItemView;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.transaction.AddChargeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import config.AppConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

import org.json.JSONObject;

/**
 * Created by rsekar on 1/11/19.
 */

public class CallFeedBackActivity extends BaseActivity implements View.OnClickListener, OnListItemSelectInterface {

    private TextView app_name_tv, info_tv, quality_tv, ccm_tv;
    private ImageView close_iv;
    private RatingBar rating_bar;
    private EditText rating_et;
    private ConstraintLayout ccm_view;
    private CCMItemView first_item, second_item, third_item, fourth_item;
    private CustomButton submit_btn;
    private CCMItemView newItem;
    private TextView moreTv;

    private String sessionId, to_guid, selectedItem, doctorGuid;
    private Date startedDate = new Date(), endedDate = new Date();

    private OpenTokViewModel openTokViewModel;
    private SelectProcedureBottomSheetDialogFragment selectProcedureBottomSheetDialogFragment;

    private VisitsApiViewModel visitsApiViewModel;

    private ArrayList<String> defaultCPTCodes = ProcedureConstants.getDefaultItems();
    private FeedbackSettingModel feedbackSettingModel;
    private FeedbackQuestionModel feedbackQuestionModel;
    private CallRequest callrequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        sessionId = getIntent().getStringExtra(ArgumentKeys.ORDER_ID);
        to_guid = getIntent().getStringExtra(ArgumentKeys.TO_USER_GUID);
        startedDate = (Date) getIntent().getSerializableExtra(ArgumentKeys.STARTED_DATE);
        endedDate = (Date) getIntent().getSerializableExtra(ArgumentKeys.ENDED_DATE);
        callrequest = (CallRequest) getIntent().getSerializableExtra(ArgumentKeys.CALL_REQUEST);

        openTokViewModel = new ViewModelProvider(this).get(OpenTokViewModel.class);

        openTokViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {
                    finish();
                    if (!UserType.isUserPatient())
                        startActivity(new Intent(CallFeedBackActivity.this, AddChargeActivity.class)
                                .putExtra(AddChargeActivity.EXTRA_REASON, Constants.ChargeReason.VISIT)
                                .putExtra(AddChargeActivity.EXTRA_PATIENT_ID, getIntent().getIntExtra(ArgumentKeys.PATIENT_ID, -1))
                                .putExtra(AddChargeActivity.EXTRA_ORDER_ID, sessionId)
                                .putExtra(AddChargeActivity.EXTRA_IS_FROM_FEEDBACK, true)
                        );
                }

                submit_btn.setEnabled(true);
            }
        });

        openTokViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                submit_btn.setEnabled(true);
            }
        });

        visitsApiViewModel = new ViewModelProvider(this).get(VisitsApiViewModel.class);
        visitsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel.isSuccess()) {

                }
                submit_btn.setEnabled(true);
            }
        });

        visitsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(ErrorModel errorModel) {
                submit_btn.setEnabled(true);
            }
        });

        attachObserver(openTokViewModel);
        attachObserver(visitsApiViewModel);

        initView();

        if (savedInstanceState != null) {
            rating_bar.setRating(savedInstanceState.getFloat(ArgumentKeys.FEEDBACK_RATTING));
            rating_et.setText(savedInstanceState.getString(ArgumentKeys.FEEDBACK_STRING));
        }

        if (rating_bar.getRating() == 5.0) {
            rating_et.setVisibility(View.GONE);
        } else {
            rating_et.setVisibility(View.VISIBLE);
        }
//        if (!callrequest.getCallType().equals(OpenTokConstants.oneWay)){
            getFeedbackSetting();
//        }else {
//            quality_tv.setVisibility(View.VISIBLE);
//            rating_bar.setVisibility(View.VISIBLE);
//            rating_et.setVisibility(View.VISIBLE);
//        }
    }

    private void getFeedbackSetting() {
        showProgressDialog();
        Call<FeedbackSettingModel> call = RetrofitManager.getInstance(getApplication()).getAuthApiService().getFeedbackSetting();
        call.enqueue(new Callback<FeedbackSettingModel>() {
            @Override
            public void onResponse(Call<FeedbackSettingModel> call, Response<FeedbackSettingModel> response) {
                feedbackSettingModel = response.body();
                String showFeedback = "", showFeedbackRating = "";
                for (FeedbackSettingModel.Datum datum : feedbackSettingModel.getData()) {
                    if (datum.getCode().equals("CALL_REVIEW")) {
                        showFeedback = datum.getValue();
                    }

                    if (datum.getCode().equals("CALL_RATING_REVIEW")) {
                        showFeedbackRating = datum.getValue();
                    }
                }

                if (showFeedback.equals("true")) {
                    getFeedbackQuestion();
                } else {
                    dismissProgressDialog();
                }

                if (showFeedbackRating.equals("true")) {
                    quality_tv.setVisibility(View.VISIBLE);
                    rating_bar.setVisibility(View.VISIBLE);
                    rating_et.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<FeedbackSettingModel> call, Throwable t) {
                call.cancel();
                dismissProgressDialog();
            }
        });

    }

    private void getFeedbackQuestion() {
        Call<FeedbackQuestionModel> call = RetrofitManager.getInstance(getApplication()).getAuthApiService().getFeedbackQusetion("call");
        call.enqueue(new Callback<FeedbackQuestionModel>() {
            @Override
            public void onResponse(Call<FeedbackQuestionModel> call, Response<FeedbackQuestionModel> response) {
                feedbackQuestionModel = response.body();
                dismissProgressDialog();
                CommonObject.dismissdialog();
                CommonObject.showDialog(CallFeedBackActivity.this, feedbackQuestionModel,callrequest,sessionId,to_guid,doctorGuid, feedbackCallback);
            }

            @Override
            public void onFailure(Call<FeedbackQuestionModel> call, Throwable t) {
                call.cancel();
                dismissProgressDialog();
            }
        });
    }

    FeedbackCallback feedbackCallback = new FeedbackCallback() {
        @Override
        public void onActionSuccess(HashMap<String, Object> data) {
            submitFeedback(data);
        }
    };

    private void submitFeedback(HashMap<String, Object> param) {
        this.showProgressDialog();
        Call<SubmitResponse> call = RetrofitManager.getInstance(getApplication()).getAuthApiService().submitFeedback(param);
        call.enqueue(new Callback<SubmitResponse>() {
            @Override
            public void onResponse(Call<SubmitResponse> call, Response<SubmitResponse> response) {
                dismissProgressDialog();
                SubmitResponse submitResponse = response.body();
                if (submitResponse.getSuccess()) {
                    Toast.makeText(CallFeedBackActivity.this, "" + submitResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubmitResponse> call, Throwable t) {
                call.cancel();
                dismissProgressDialog();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstance) {
        super.onSaveInstanceState(saveInstance);

        saveInstance.putFloat(ArgumentKeys.FEEDBACK_RATTING, rating_bar.getRating());
        saveInstance.putString(ArgumentKeys.FEEDBACK_STRING, rating_et.getText().toString());
    }

    private void initView() {
        app_name_tv = findViewById(R.id.app_name_tv);
        info_tv = findViewById(R.id.info_tv);
        quality_tv = findViewById(R.id.quality_tv);
        ccm_tv = findViewById(R.id.ccm_tv);
        close_iv = findViewById(R.id.close_iv);
        rating_bar = findViewById(R.id.rating_bar);
        rating_et = findViewById(R.id.rating_et);
        ccm_view = findViewById(R.id.ccm_view);
        submit_btn = findViewById(R.id.submit_btn);

        fourth_item = (CCMItemView) findViewById(R.id.fourth_item);
        first_item = findViewById(R.id.first_item);
        second_item = findViewById(R.id.second_item);
        third_item = findViewById(R.id.third_item);
        newItem = (CCMItemView) findViewById(R.id.new_item);
        moreTv = (TextView) findViewById(R.id.more_tv);

        doctorGuid = getIntent().getStringExtra(ArgumentKeys.DOCTOR_GUID);

        initListeners();

        String startTime = Utils.getStringFromDate(startedDate, "hh:mm a");
        String endTime = Utils.getStringFromDate(endedDate, "hh:mm a");
        String date = Utils.getStringFromDate(startedDate, "dd MMM yyyy");

        info_tv.setText(startTime + " to " + endTime + " | " + date);

    }

    private void initListeners() {
        close_iv.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 5) {
                    rating_et.setVisibility(View.VISIBLE);
                }
            }
        });

        if (UserType.isUserPatient() || appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_CCM)) {
            ccm_view.setVisibility(View.GONE);
        } else {
            ccm_view.setVisibility(View.VISIBLE);

            first_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.getDefaultItems().get(0)), false);
            first_item.setOnClickListener(this);
            first_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.getDefaultItems().get(0));
                }
            });

            second_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.getDefaultItems().get(1)), false);
            second_item.setOnClickListener(this);
            second_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.getDefaultItems().get(1));
                }
            });

            third_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.getDefaultItems().get(2)), false);
            third_item.setOnClickListener(this);
            third_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.getDefaultItems().get(2));
                }
            });

            fourth_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.getDefaultItems().get(3)), false);
            fourth_item.setOnClickListener(this);
            fourth_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.getDefaultItems().get(3));
                }
            });

            newItem.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(selectedItem);
                }
            });


            moreTv.setOnClickListener(this);
        }
    }

    private void showDetailDialog(String type) {
        Utils.showAlertDialog(this, ProcedureConstants.getTitle(this, type), ProcedureConstants.getDescription(this, type),
                getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:

                submit_btn.setEnabled(false);

                String category = ProcedureConstants.getCategory(selectedItem);

                if (category != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("category", category);
                    openTokViewModel.updateCallStatus(sessionId, params);
                }

                HashMap<String, Object> params = new HashMap<>();
                params.put("order_id", sessionId);
                params.put("rating", rating_bar.getRating());
                params.put("given_to", to_guid);
                params.put("review", rating_et.getText().toString());

                openTokViewModel.postCallReview(params);

                if (selectedItem != null) {
                    UpdateVisitRequestModel visitRequestModel = new UpdateVisitRequestModel();
                    ProcedureModel.CPTCodesBean cptCodesBean = new ProcedureModel.CPTCodesBean(selectedItem, ProcedureConstants.getDescription(this, selectedItem));
                    List<ProcedureModel.CPTCodesBean> codesBeanList = new ArrayList<>();
                    codesBeanList.add(cptCodesBean);
                    ProcedureModel procedureModel = new ProcedureModel(codesBeanList);
                    visitRequestModel.setProcedure(procedureModel);

                    visitsApiViewModel.updateOrder(sessionId, visitRequestModel, doctorGuid, true);
                }
                break;
            case R.id.close_iv:
                if (!UserType.isUserPatient())
                    startActivity(new Intent(CallFeedBackActivity.this, AddChargeActivity.class)
                            .putExtra(AddChargeActivity.EXTRA_REASON, Constants.ChargeReason.VISIT)
                            .putExtra(AddChargeActivity.EXTRA_PATIENT_ID, getIntent().getIntExtra(ArgumentKeys.PATIENT_ID, -1))
                            .putExtra(AddChargeActivity.EXTRA_ORDER_ID, sessionId)
                            .putExtra(AddChargeActivity.EXTRA_IS_FROM_FEEDBACK, true)
                    );
                finish();
                break;
            case R.id.first_item:
                setSelected(first_item);
                selectedItem = ProcedureConstants.getDefaultItems().get(0);
                break;
            case R.id.second_item:
                setSelected(second_item);
                selectedItem = ProcedureConstants.getDefaultItems().get(1);
                break;
            case R.id.third_item:
                setSelected(third_item);
                selectedItem = ProcedureConstants.getDefaultItems().get(2);
                break;
            case R.id.fourth_item:
                setSelected(fourth_item);
                selectedItem = ProcedureConstants.getDefaultItems().get(3);
                break;
            case R.id.more_tv:
                selectProcedureBottomSheetDialogFragment = new SelectProcedureBottomSheetDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.SELECTED_ITEMS, selectedItem);
                selectProcedureBottomSheetDialogFragment.setArguments(bundle);
                selectProcedureBottomSheetDialogFragment.show(getSupportFragmentManager(), selectProcedureBottomSheetDialogFragment.getClass().getSimpleName());
                break;
        }
    }

    private void setSelected(CCMItemView view) {
        fourth_item.update(false);
        first_item.update(false);
        second_item.update(false);
        third_item.update(false);
        newItem.update(false);

        view.update(true);

        if (!newItem.getIsSelected()) {
            newItem.setVisibility(View.GONE);
        } else {
            newItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListItemSelected(int position, Bundle bundle) {
        selectedItem = ProcedureConstants.getItems().get(position);

        if (selectedItem.equals(ProcedureConstants.getDefaultItems().get(0))) {
            setSelected(first_item);
        } else if (selectedItem.equals(ProcedureConstants.getDefaultItems().get(1))) {
            setSelected(second_item);
        } else if (selectedItem.equals(ProcedureConstants.getDefaultItems().get(2))) {
            setSelected(third_item);
        } else if (selectedItem.equals(ProcedureConstants.getDefaultItems().get(3))) {
            setSelected(fourth_item);
        } else {
            setSelected(newItem);
            newItem.update(ProcedureConstants.getTitle(this, selectedItem), true);
        }

        selectProcedureBottomSheetDialogFragment.dismiss();
    }
}
