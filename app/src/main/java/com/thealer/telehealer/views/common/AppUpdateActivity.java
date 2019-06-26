package com.thealer.telehealer.views.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by rsekar on 1/28/19.
 */

public class AppUpdateActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreenMode();

        setContentView(R.layout.activity_app_update);
        initView();
    }

    private void initView() {
        TextView title_tv = findViewById(R.id.title_tv);
        TextView sub_title_tv = findViewById(R.id.sub_title_tv);
        CustomButton action_btn = findViewById(R.id.action_btn);

        action_btn.setOnClickListener(this);

        title_tv.setText(getString(R.string.app_update));
        sub_title_tv.setText(getString(R.string.app_update_description));
        action_btn.setText(getString(R.string.UPDATE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_btn:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //disable back press
    }
}
