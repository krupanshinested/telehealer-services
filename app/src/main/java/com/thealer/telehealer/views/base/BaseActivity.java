package com.thealer.telehealer.views.base;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Logs;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 08,October,2018
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
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
                        if (isLoad)
                            showProgressDialog();
                        else
                            dismissProgressDialog();
                        break;
                    case Constants.SHOW_SCREEN:
                        if (isLoad)
                            showScreen();
                        else
                            dismissScreen();
                        break;
                }

            }
        });

    }

    public void handleErrorResponse(ErrorModel errorModel) {
        Logs.D(TAG, "inside handle error response");
        if (errorModel != null && errorModel.getMessage() != null) {
//            do something
        } else {
//            do something
        }
    }

    public void showProgressDialog() {
        Logs.D(TAG, "inside showProgressDialog");
        dismissProgressDialog();

        if (relativeLayout == null) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
            progressBar.animate();
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            relativeLayout = new RelativeLayout(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.setClickable(true);

            relativeLayout.addView(progressBar);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            progressBar.setLayoutParams(params);
            progressBar.setClickable(true);

            this.addContentView(relativeLayout, layoutParams);
        } else
            relativeLayout.setVisibility(View.VISIBLE);

    }

    public void dismissProgressDialog() {
        Logs.D(TAG, "inside dismissProgressDialog");
        if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE)
            relativeLayout.setVisibility(View.GONE);

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
        Glide.with(this).load(R.drawable.loader_medical).into(iv);
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


//    public void showOrHideSoftInputWindow(FragmentActivity activity, boolean showOrHide){
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (showOrHide)
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        else
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//    }

    public boolean isDeviceXLarge() {
        return getResources().getBoolean(R.bool.isXlarge);
    }

    public boolean isModeLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isSplitModeNeeded() {
        return isDeviceXLarge() && isModeLandscape();
    }
}
