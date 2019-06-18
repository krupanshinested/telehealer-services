package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.schedules.WaitingRoomActivity;
import com.thealer.telehealer.views.notification.NotificationActivity;
import com.thealer.telehealer.views.notification.NotificationListFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

public class UserDetailActivity extends BaseActivity implements AttachObserverInterface,
        OnActionCompleteInterface, OnOrientationChangeInterface, OnCloseActionInterface, ShowSubFragmentInterface,
        SuccessViewInterface, ChangeTitleInterface, ToolBarInterface, OnViewChangeInterface {

    private LinearLayout fragmentHolder;
    private LinearLayout subFragmentHolder;
    private CommonUserApiResponseModel userModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        userModel = (CommonUserApiResponseModel) getIntent().getSerializableExtra(Constants.USER_DETAIL);
        initView();
        showUserDetailFragment();

        if (getIntent().getBooleanExtra(ArgumentKeys.FROM_CALL,false)) {
            LocalBroadcastManager.getInstance(UserDetailActivity.this).registerReceiver(callStartReceiver, new IntentFilter(Constants.CALL_SCREEN_MAXIMIZE));
        }
    }

    @Override
    protected void onDestroy() {
        if (getIntent().getBooleanExtra(ArgumentKeys.FROM_CALL,false)) {
            LocalBroadcastManager.getInstance(UserDetailActivity.this).unregisterReceiver(callStartReceiver);
        }
        super.onDestroy();
    }

    private void initView() {
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);
        subFragmentHolder = (LinearLayout) findViewById(R.id.sub_fragment_holder);
    }

    private void gotoHomeActivity() {
        if (!isPreviousActivityAvailable()) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    private BroadcastReceiver callStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void showUserDetailFragment() {
        Log.e(TAG, "showNotificationList: ");
        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
        bundle.putSerializable(Constants.USER_DETAIL, userModel);
        doctorPatientDetailViewFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentHolder.getId(), doctorPatientDetailViewFragment)
                .commit();
    }

    @Override
    public void onTitleChange(String title) {

    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.sub_fragment_holder);
        boolean isSubFragmentVisible = f == null;

        if (!isSubFragmentVisible) {
            if (getResources().getBoolean(R.bool.isXlarge) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gotoHomeActivity();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            gotoHomeActivity();
        }

    }

    @Override
    public void onShowFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(subFragmentHolder.getId(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (string.equals(RequestID.REQ_ADD_CONNECTION) && success) {

        } else if (string.equals(RequestID.REQ_SHOW_DETAIL_VIEW)) {
            bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
            DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
            doctorPatientDetailViewFragment.setArguments(bundle);
            onShowFragment(doctorPatientDetailViewFragment);
        }
    }

    @Override
    public void onDataReceived(Bundle bundle) {

    }

    @Override
    public void enableNext(boolean enabled) {

    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {

    }

    @Override
    public void updateNextTitle(String nextTitle) {

    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }

    @Override
    public ImageView getExtraOption() {
        return null;
    }

    @Override
    public void updateSubTitle(String subTitle, int visibility) {

    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }
}
