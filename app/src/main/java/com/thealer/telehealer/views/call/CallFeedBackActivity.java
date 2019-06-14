package com.thealer.telehealer.views.call;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.OpenTokViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.CCMItemView;

import java.util.Date;
import java.util.HashMap;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by rsekar on 1/11/19.
 */

public class CallFeedBackActivity extends BaseActivity implements View.OnClickListener {

    private TextView app_name_tv, info_tv, quality_tv, ccm_tv;
    private ImageView close_iv;
    private RatingBar rating_bar;
    private EditText rating_et;
    private ConstraintLayout ccm_view;
    private CCMItemView ccm_item, rpm_item, bhi_item;
    private CustomButton submit_btn;

    private String sessionId, to_guid;
    private Date startedDate = new Date(), endedDate = new Date();

    private OpenTokViewModel openTokViewModel;

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


        attachObserver(openTokViewModel);

        setUp();

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

    private void setUp() {
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

            ccm_item.update(getString(R.string.ccm), false);
            ccm_item.setOnClickListener(this);
            ccm_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(ccm_item.getId());
                }
            });

            rpm_item.update(getString(R.string.rpm), false);
            rpm_item.setOnClickListener(this);
            rpm_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(rpm_item.getId());
                }
            });

            bhi_item.update(getString(R.string.bhi), false);
            bhi_item.setOnClickListener(this);
            bhi_item.setListenerForInfo(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailDialog(bhi_item.getId());
                }
            });

        }
    }

    private void showDetailDialog(int id) {
        String title = "";
        String message = "";

        switch (id) {
            case R.id.ccm_item:
                title = getString(R.string.ccm_title);
                message = getString(R.string.ccm_message);
                break;
            case R.id.rpm_item:
                title = getString(R.string.rpm_title);
                message = getString(R.string.rpm_message);
                break;
            case R.id.bhi_item:
                title = getString(R.string.bhi_title);
                message = getString(R.string.bhi_message);
                break;
        }

        Utils.showAlertDialog(this, title, message, getString(R.string.ok), null, new DialogInterface.OnClickListener() {
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

                if (ccm_item.getIsSelected()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("category", OpenTokConstants.ccm);
                    openTokViewModel.updateCallStatus(sessionId, params);
                } else if (bhi_item.getIsSelected()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("category", OpenTokConstants.bhi);
                    openTokViewModel.updateCallStatus(sessionId, params);
                } else if (rpm_item.getIsSelected()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("category", OpenTokConstants.rpm);
                    openTokViewModel.updateCallStatus(sessionId, params);
                }

                HashMap<String, Object> params = new HashMap<>();
                params.put("order_id", sessionId);
                params.put("rating", rating_bar.getRating());
                params.put("given_to", to_guid);
                params.put("review", rating_et.getText().toString());

                openTokViewModel.postCallReview(params);
                break;
            case R.id.close_iv:
                finish();
                break;
            case R.id.ccm_item:
                if (ccm_item.getIsSelected()) {
                    ccm_item.update(false);
                } else {
                    ccm_item.update(true);
                }

                bhi_item.update(false);
                rpm_item.update(false);
                break;
            case R.id.bhi_item:

                if (bhi_item.getIsSelected()) {
                    bhi_item.update(false);
                } else {
                    bhi_item.update(true);
                }

                ccm_item.update(false);
                rpm_item.update(false);
                break;
            case R.id.rpm_item:

                if (rpm_item.getIsSelected()) {
                    rpm_item.update(false);
                } else {
                    rpm_item.update(true);
                }

                ccm_item.update(false);
                bhi_item.update(false);

                break;
        }
    }
}
