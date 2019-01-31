package com.thealer.telehealer.views.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.schedules.ScheduleDetailViewFragment;

/**
 * Created by Aswin on 31,January,2019
 */
public class NotificationDetailActivity extends BaseActivity implements OnCloseActionInterface, AttachObserverInterface {
    private ConstraintLayout fragmentHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        initView();
    }

    private void initView() {
        fragmentHolder = (ConstraintLayout) findViewById(R.id.fragment_holder);

        if (getIntent() != null &&
                getIntent().getStringExtra(ArgumentKeys.SCHEDULE_DETAIL) != null) {
            showScheduleDetail();
        }
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
}
