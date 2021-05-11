package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.ChangeTitleInterface;

public class InvitedListActivity extends BaseActivity implements ChangeTitleInterface, View.OnClickListener {

    private Bundle bundle = null;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ConstraintLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_list);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (ConstraintLayout) findViewById(R.id.fragment_holder);

        onTitleChange(getString(R.string.invited_user_list));

        backIv.setOnClickListener(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
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