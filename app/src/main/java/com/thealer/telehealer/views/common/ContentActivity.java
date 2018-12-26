package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

/**
 * Created by rsekar on 1/4/19.
 */

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView icon,close_iv;
    private TextView title_tv,sub_title_tv;
    private CustomButton action_btn;
    private Button skip_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreenMode();

        setContentView(R.layout.activity_content);
        initView();

        int resource = getIntent().getIntExtra(ArgumentKeys.RESOURCE_ICON,R.drawable.app_icon);
        icon.setImageDrawable(getDrawable(resource));

        title_tv.setText(getIntent().getStringExtra(ArgumentKeys.TITLE));

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION,false)) {
            sub_title_tv.setText(Utils.fromHtml(getIntent().getStringExtra(ArgumentKeys.DESCRIPTION)));
        } else {
            sub_title_tv.setText(getIntent().getStringExtra(ArgumentKeys.DESCRIPTION));
        }

        String actionTitle = getIntent().getStringExtra(ArgumentKeys.OK_BUTTON_TITLE);
        if (TextUtils.isEmpty(actionTitle)) {
            action_btn.setText(getString(R.string.ok));
        } else {
            action_btn.setText(actionTitle);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_SKIP_NEEDED,false)) {
            skip_btn.setVisibility(View.VISIBLE);
        } else {
            skip_btn.setVisibility(View.GONE);
        }
    }

    private void initView() {
        icon = findViewById(R.id.icon);
        close_iv = findViewById(R.id.close_iv);
        title_tv = findViewById(R.id.title_tv);
        sub_title_tv = findViewById(R.id.sub_title_tv);
        action_btn = findViewById(R.id.action_btn);
        skip_btn = findViewById(R.id.skip_btn);

        close_iv.setOnClickListener(this);
        action_btn.setOnClickListener(this);
        skip_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.close_iv:
                intent.putExtra(ArgumentKeys.IS_SKIPPED,false);
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
                break;
            case R.id.action_btn:
                setResult(Activity.RESULT_OK,intent);
                finish();
                break;
            case R.id.skip_btn:
                intent.putExtra(ArgumentKeys.IS_SKIPPED,true);
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onClick(close_iv);
    }
}
