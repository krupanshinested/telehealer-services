package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by rsekar on 1/28/19.
 */

public class AppUpdateActivity extends BaseActivity implements View.OnClickListener {

    public static String EXTRA_IS_HARD_UPDATE = "isHardUpdate";
    public static String EXTRA_UPDATE_MESSAGE = "updateMessage";

    private boolean isHardUpdate;

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
        Button skip_btn = findViewById(R.id.skip_btn);

        action_btn.setOnClickListener(this);

        title_tv.setText(getString(R.string.app_update));

        String updateDescription = getIntent().getStringExtra(EXTRA_UPDATE_MESSAGE);
        if (updateDescription != null && !updateDescription.isEmpty()) {
            sub_title_tv.setText(HtmlCompat.fromHtml(updateDescription, HtmlCompat.FROM_HTML_MODE_COMPACT));
            sub_title_tv.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            sub_title_tv.setText(getString(R.string.app_update_description));

        action_btn.setText(getString(R.string.UPDATE));
        isHardUpdate = getIntent().getBooleanExtra(EXTRA_IS_HARD_UPDATE, false);
        if (isHardUpdate)
            skip_btn.setVisibility(View.GONE);
        else {
            skip_btn.setOnClickListener(this);
        }
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
            case R.id.skip_btn:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //disable back press if hard update
        if (!isHardUpdate)
            super.onBackPressed();
    }
}
