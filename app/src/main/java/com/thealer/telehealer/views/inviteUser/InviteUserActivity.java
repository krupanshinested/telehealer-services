package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;

/**
 * Created by Aswin on 17,December,2018
 */
public class InviteUserActivity extends BaseActivity implements ChangeTitleInterface,
        View.OnClickListener, AttachObserverInterface, SuccessViewInterface {
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ConstraintLayout fragmentHolder;
    private TextView infoTv;
    private CustomButton emailCb;
    private CustomButton phoneCb;
    private CustomButton demographicsCb;

    private Bundle bundle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (ConstraintLayout) findViewById(R.id.fragment_holder);
        infoTv = (TextView) findViewById(R.id.info_tv);
        emailCb = (CustomButton) findViewById(R.id.email_cb);
        phoneCb = (CustomButton) findViewById(R.id.phone_cb);
        demographicsCb = (CustomButton) findViewById(R.id.demographics_cb);

        onTitleChange(getString(R.string.invite_a_user));

        backIv.setOnClickListener(this);
        emailCb.setOnClickListener(this);
        phoneCb.setOnClickListener(this);
        demographicsCb.setOnClickListener(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }

    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.email_cb:
                InviteByEmailFragment inviteByEmailFragment = new InviteByEmailFragment();
                inviteByEmailFragment.setArguments(bundle);
                showFragment(inviteByEmailFragment);
                break;
            case R.id.phone_cb:
                InviteByPhoneFragment inviteByPhoneFragment = new InviteByPhoneFragment();
                inviteByPhoneFragment.setArguments(bundle);
                showFragment(inviteByPhoneFragment);
                break;
            case R.id.demographics_cb:
                InviteByDemographicFragment inviteByDemographicFragment = new InviteByDemographicFragment();
                inviteByDemographicFragment.setArguments(bundle);
                showFragment(inviteByDemographicFragment);
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(fragmentHolder.getId(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            onTitleChange(getString(R.string.invite_a_user));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }
}
