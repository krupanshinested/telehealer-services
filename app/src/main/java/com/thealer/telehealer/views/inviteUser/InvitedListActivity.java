package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;

public class InvitedListActivity extends BaseActivity implements ChangeTitleInterface, View.OnClickListener, AttachObserverInterface {

    private Bundle bundle = null;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private FrameLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_list);
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
        }
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (FrameLayout) findViewById(R.id.fragment_container);

        onTitleChange(getString(R.string.invited_user_list));

        backIv.setOnClickListener(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }

        openRootFragment();
    }

    private void openRootFragment() {
        InvitedListFragment invitedListFragment = new InvitedListFragment();
        invitedListFragment.setArguments(bundle);
        setFragment(invitedListFragment, false);
    }

    private void setFragment(Fragment fragment, Boolean needToAddBackTrace) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        findViewById(fragmentHolder.getId()).bringToFront();

        if (needToAddBackTrace) {
            fragmentManager.beginTransaction().addToBackStack(fragment.getClass().getSimpleName()).replace(fragmentHolder.getId(), fragment).commit();
        } else {
            fragmentManager.beginTransaction().replace(fragmentHolder.getId(), fragment).commit();
        }

    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
        }
    }
}