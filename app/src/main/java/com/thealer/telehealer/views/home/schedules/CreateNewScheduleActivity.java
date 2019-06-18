package com.thealer.telehealer.views.home.schedules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;

/**
 * Created by Aswin on 19,December,2018
 */
public class CreateNewScheduleActivity extends BaseActivity implements ChangeTitleInterface,
        View.OnClickListener, ShowSubFragmentInterface, OnCloseActionInterface, AttachObserverInterface, SuccessViewInterface {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout fragmentHolder;

    private Bundle bundle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        initView();
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);

        backIv.setOnClickListener(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        if (UserType.isUserPatient()) {
            showPatientDisclaimer();
        } else {
            showCreateSchedule();
        }
    }

    private void showCreateSchedule() {
        CreateAppointmentFragment createAppointmentFragment = new CreateAppointmentFragment();
        createAppointmentFragment.setArguments(bundle);
        addFragment(createAppointmentFragment, true);
    }

    private void showPatientDisclaimer() {
        PatientDisclaimerFragment patientDisclaimerFragment = new PatientDisclaimerFragment();
        patientDisclaimerFragment.setArguments(bundle);
        addFragment(patientDisclaimerFragment, false);
    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onTitleChange(getString(R.string.new_appointment));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        addFragment(fragment, true);
    }

    private void addFragment(Fragment fragment, boolean isAddtoBackStack) {
        if (isAddtoBackStack) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                    .replace(fragmentHolder.getId(), fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(fragmentHolder.getId(), fragment)
                    .commit();
        }
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            finish();
        }
    }
}
