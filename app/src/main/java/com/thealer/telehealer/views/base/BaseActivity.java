package com.thealer.telehealer.views.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.FireBase.EventRecorder;
import com.thealer.telehealer.common.Logs;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.Util.InternalLogging.TeleLogger;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.home.HomeActivity;

/**
 * Created by Aswin on 08,October,2018
 */
public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "aswin";
    private int showScreenType;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void attachObserver(BaseApiViewModel mViewModel) {

        mViewModel.getErrorModelLiveData().observe(this, errorModel -> {
            Logs.D(TAG, "inside Error model observer");
            handleErrorResponse(errorModel);
        });

        mViewModel.getShowScreen().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer type) {
                //show the required screen
                Logs.D(TAG, "inside show screen observer");
                showScreenType = type;
            }
        });

        mViewModel.getIsLoadingLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoad) {
                Logs.D(TAG, "inside getIsLoadingLiveData");

                switch (showScreenType) {
                    case Constants.SHOW_PROGRESS:
                        if (isLoad) {
                            if (relativeLayout == null || relativeLayout.getVisibility() != View.VISIBLE) {
                                showProgressDialog();
                            }
                        } else
                            dismissProgressDialog();

                        break;
                    case Constants.SHOW_SCREEN:
                        if (isLoad)
                            showScreen();
                        else
                            dismissScreen();
                        break;
                    default:
                        dismissProgressDialog();
                }

            }
        });

    }

    public void handleErrorResponse(ErrorModel errorModel) {
        Logs.D(TAG, "inside handle error response");
        dismissProgressDialog();
        if (errorModel != null && errorModel.getMessage() != null) {
//            do something
        } else {
//            do something
        }
    }

    public void showProgressDialog() {
        Logs.D(TAG, "inside showProgressDialog");
        dismissProgressDialog();

        relativeLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.setClickable(true);

        CardView cardView = new CardView(this);
        cardView.setCardBackgroundColor(getColor(R.color.colorWhite));
        cardView.setAlpha(0.9f);
        cardView.setRadius(12.0f);


        ImageView imageView = new ImageView(this);
        DrawableImageViewTarget drawableImageViewTarget = new DrawableImageViewTarget(imageView);


        Glide.with(getApplicationContext())
                .load(R.raw.throbber)
                .into(drawableImageViewTarget);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(160, 160);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        imageView.setLayoutParams(params);
        cardView.setLayoutParams(params);

        relativeLayout.addView(cardView);
        cardView.addView(imageView);

        this.addContentView(relativeLayout, layoutParams);
        relativeLayout.setVisibility(View.VISIBLE);

    }

    public void dismissProgressDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Logs.D(TAG, "inside dismissProgressDialog");
                if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Do you want to close ?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    public void showSnack() {
        //create a snack bar view and show here
        Logs.D(TAG, "inside show snack");
    }

    public void showScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.loader_view, null);
        builder.setView(view);
        ImageView iv = view.findViewById(R.id.loader_iv);
        Glide.with(getApplicationContext()).load(R.drawable.loader_medical).into(iv);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(250, 250);
    }

    public void dismissScreen() {
        //dismiss the showing screen here
        Logs.D(TAG, "inside dismiss screen");
    }


    /**
     * This method will enable full screen window
     * Note : This method should be called before setContentView
     */
    public void requestFullScreenMode() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public void showOrHideSoftInputWindow(FragmentActivity activity, boolean showOrHide) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (showOrHide)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        else
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public boolean isDeviceXLarge() {
        return getResources().getBoolean(R.bool.isXlarge);
    }

    public boolean isModeLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isSplitModeNeeded() {
        return isDeviceXLarge() && isModeLandscape();
    }

    public Dialog showAlertDialog(String title, String message,
                                  @Nullable String positiveTitle,
                                  @Nullable String negativeTitle,
                                  @Nullable DialogInterface.OnClickListener positiveListener,
                                  @Nullable DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaseActivity.this);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (positiveTitle != null) {
            alertDialog.setPositiveButton(positiveTitle, positiveListener);
        }

        if (negativeTitle != null) {
            alertDialog.setNegativeButton(negativeTitle, negativeListener);
        }
        Dialog dialog = alertDialog.create();
        dialog.show();
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            switch (requestCode) {
                case PermissionConstants.PERMISSION_CAM_MIC:
                    EventRecorder.recordPermissionDenined("cam_mic_disabled");
                    break;
                case PermissionConstants.PERMISSION_MICROPHONE:
                    EventRecorder.recordPermissionDenined("mic_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
                    EventRecorder.recordPermissionDenined("cam_mic_notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAM_PHOTOS:
                    EventRecorder.recordPermissionDenined("cam_photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_CAMERA:
                    EventRecorder.recordPermissionDenined("cam_disabled");
                    break;
                case PermissionConstants.PERMISSION_GALLERY:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION:
                    EventRecorder.recordPermissionDenined("location_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
                    EventRecorder.recordPermissionDenined("location_photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_LOCATION_VITALS:
                    EventRecorder.recordPermissionDenined("location_disabled");
                    break;
                case PermissionConstants.PERMISSION_MIC_NITIFICATION:
                    EventRecorder.recordPermissionDenined("mic_notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_NOTIFICATION:
                    EventRecorder.recordPermissionDenined("notification_disabled");
                    break;
                case PermissionConstants.PERMISSION_STORAGE:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
                case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                    EventRecorder.recordPermissionDenined("photos_disabled");
                    break;
            }
        }

        switch (requestCode) {
            case PermissionConstants.PERMISSION_CAM_MIC:
            case PermissionConstants.PERMISSION_MICROPHONE:
            case PermissionConstants.PERMISSION_CAM_MIC_NOTIFICATION:
            case PermissionConstants.PERMISSION_CAM_PHOTOS:
            case PermissionConstants.PERMISSION_CAMERA:
            case PermissionConstants.PERMISSION_GALLERY:
            case PermissionConstants.PERMISSION_LOCATION:
            case PermissionConstants.PERMISSION_LOCATION_STORAGE_VITALS:
            case PermissionConstants.PERMISSION_LOCATION_VITALS:
            case PermissionConstants.PERMISSION_MIC_NITIFICATION:
            case PermissionConstants.PERMISSION_NOTIFICATION:
            case PermissionConstants.PERMISSION_STORAGE:
            case PermissionConstants.PERMISSION_WRITE_STORAGE_VITALS:
                TeleLogger.shared.initialLog();
        }

    }

    public boolean isPreviousActivityAvailable() {
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (mngr != null) {
                return mngr.getAppTasks().get(0).getTaskInfo().numActivities > 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void goToHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void showSuccessView(int requestId) {
        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    public void sendSuccessViewBroadCast(Context context, boolean status, String title, String description) {

        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, status);
        bundle.putString(Constants.SUCCESS_VIEW_TITLE, title);
        bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, description);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}

