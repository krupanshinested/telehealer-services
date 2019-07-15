package com.thealer.telehealer.views.call;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.apilayer.models.procedure.ProcedureModel;
import com.thealer.telehealer.apilayer.models.visits.UpdateVisitRequestModel;
import com.thealer.telehealer.apilayer.models.visits.VisitsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.Procedure.ProcedureConstants;
import com.thealer.telehealer.views.Procedure.SelectProcedureBottomSheetDialogFragment;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CCMItemView;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by rsekar on 1/11/19.
 */

public class CallFeedBackActivity extends BaseActivity implements View.OnClickListener, OnListItemSelectInterface {

    private TextView app_name_tv, info_tv, quality_tv, ccm_tv;
    private ImageView close_iv;
    private RatingBar rating_bar;
    private EditText rating_et;
    private ConstraintLayout ccm_view;
    private CCMItemView ccm_item, rpm_item, bhi_item;
    private CustomButton submit_btn;
    private CCMItemView telehealthItem;
    private CCMItemView newItem;
    private TextView moreTv;

    private String sessionId, to_guid, selectedItem, doctorGuid;
    private Date startedDate = new Date(), endedDate = new Date();

    private OpenTokViewModel openTokViewModel;
    private SelectProcedureBottomSheetDialogFragment selectProcedureBottomSheetDialogFragment;

    private VisitsApiViewModel visitsApiViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        sessionId = getIntent().getStringExtra(ArgumentKeys.ORDER_ID);
        to_guid = getIntent().getStringExtra(ArgumentKeys.TO_USER_GUID);
        startedDate = (Date) getIntent().getSerializableExtra(ArgumentKeys.STARTED_DATE);
        endedDate = (Date) getIntent().getSerializableExtra(ArgumentKeys.ENDED_DATE);

        openTokViewModel = ViewModelProviders.of(this).get(OpenTokViewModel.class);

        openTokViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {

                if (baseApiResponseModel != null) {
                    finish();
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

        visitsApiViewModel = ViewModelProviders.of(this).get(VisitsApiViewModel.class);
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

        ccm_item = findViewById(R.id.ccm_item);
        rpm_item = findViewById(R.id.rpm_item);
        bhi_item = findViewById(R.id.bhi_item);
        telehealthItem = (CCMItemView) findViewById(R.id.telehealth_item);
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

            ccm_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.CCM_20), false);
            ccm_item.setOnClickListener(this);
            ccm_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.CCM_20);
                }
            });

            rpm_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.RPM_20), false);
            rpm_item.setOnClickListener(this);
            rpm_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.RPM_20);
                }
            });

            bhi_item.update(ProcedureConstants.getTitle(this, ProcedureConstants.BHI_20), false);
            bhi_item.setOnClickListener(this);
            bhi_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.BHI_20);
                }
            });

            telehealthItem.update(ProcedureConstants.getTitle(this, ProcedureConstants.TELEHEALTH), true);
            telehealthItem.setOnClickListener(this);
            telehealthItem.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ProcedureConstants.TELEHEALTH);
                }
            });

            newItem.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(selectedItem);
                }
            });

            selectedItem = ProcedureConstants.TELEHEALTH;

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
                finish();
                break;
            case R.id.ccm_item:
                setSelected(ccm_item);
                selectedItem = ProcedureConstants.CCM_20;
                break;
            case R.id.bhi_item:
                setSelected(bhi_item);
                selectedItem = ProcedureConstants.BHI_20;
                break;
            case R.id.rpm_item:
                setSelected(rpm_item);
                selectedItem = ProcedureConstants.RPM_20;
                break;
            case R.id.telehealth_item:
                setSelected(telehealthItem);
                selectedItem = ProcedureConstants.TELEHEALTH;
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
        telehealthItem.update(false);
        ccm_item.update(false);
        rpm_item.update(false);
        bhi_item.update(false);
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

        switch (selectedItem) {
            case ProcedureConstants.TELEHEALTH:
                setSelected(telehealthItem);
                break;
            case ProcedureConstants.CCM_20:
                setSelected(ccm_item);
                break;
            case ProcedureConstants.BHI_20:
                setSelected(bhi_item);
                break;
            case ProcedureConstants.RPM_20:
                setSelected(rpm_item);
                break;
            default:
                setSelected(newItem);
                newItem.update(ProcedureConstants.getTitle(this, selectedItem), true);
                break;
        }

        selectProcedureBottomSheetDialogFragment.dismiss();
    }
}
