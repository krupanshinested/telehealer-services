package com.thealer.telehealer.views.notification;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.schedules.ScheduleDetailViewFragment;
import com.thealer.telehealer.views.home.vitals.VitalsDetailListFragment;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

/**
 * Created by Aswin on 31,January,2019
 */
public class NotificationDetailActivity extends BaseActivity implements OnCloseActionInterface, AttachObserverInterface,
        ShowSubFragmentInterface, OnActionCompleteInterface, OnViewChangeInterface, ToolBarInterface {

    private ConstraintLayout fragmentHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        initView();
    }

    private void initView() {
        fragmentHolder = (ConstraintLayout) findViewById(R.id.fragment_holder);

        if (getIntent() != null) {
            if (getIntent().getStringExtra(ArgumentKeys.SCHEDULE_DETAIL) != null) {
                showScheduleDetail();
            }

            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(ArgumentKeys.VIEW_ABNORMAL_VITAL, false)) {
                showUserVitalDetail();
            }
        }
    }

    private void showUserVitalDetail() {
        VitalsDetailListFragment vitalsDetailListFragment = new VitalsDetailListFragment();
        vitalsDetailListFragment.setArguments(getIntent().getExtras());
        showFragment(vitalsDetailListFragment);
    }

    private void showScheduleDetail() {
        ScheduleDetailViewFragment scheduleDetailViewFragment = new ScheduleDetailViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ArgumentKeys.SCHEDULE_DETAIL, new Gson().fromJson(getIntent().getStringExtra(ArgumentKeys.SCHEDULE_DETAIL), SchedulesApiResponseModel.ResultBean.class));
        scheduleDetailViewFragment.setArguments(bundle);
        showFragment(scheduleDetailViewFragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (isPreviousActivityAvailable()) {
                finish();
            } else {
                goToHomeActivity();
            }
        }
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        showFragment(fragment);
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {

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
    public ImageView getExtraOption() {
        return null;
    }

    @Override
    public void updateSubTitle(String subTitle, int visibility) {

    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

    }
}
