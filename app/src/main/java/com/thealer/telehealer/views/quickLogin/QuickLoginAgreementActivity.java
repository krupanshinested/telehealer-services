package com.thealer.telehealer.views.quickLogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by Aswin on 05,March,2019
 */
public class QuickLoginAgreementActivity extends BaseActivity {
    private TextView titleTv;
    private TextView agreeTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklogin_agreement);
        initView();
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.title_tv);
        agreeTv = (TextView) findViewById(R.id.agree_tv);
        agreeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
