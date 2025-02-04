package com.thealer.telehealer.views.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rsekar on 1/4/19.
 */

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView icon, close_iv;
    private TextView title_tv, sub_title_tv, bottom_tv;
    private CustomButton action_btn;
    private Button skip_btn;
    private CheckBox check_box;
    private TextView check_box_tv;
    private LinearLayout check_box_view;
    private CircleImageView userAvatarCiv;
    private TextView userNameTv;
    private TextView helpTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreenMode();
        initView();
        Utils.hideKeyboard(this);

    }

    public void initView() {
        setContentView(R.layout.activity_content);
        icon = findViewById(R.id.icon);
        close_iv = findViewById(R.id.close_iv);
        title_tv = findViewById(R.id.title_tv);
        sub_title_tv = findViewById(R.id.sub_title_tv);
        action_btn = findViewById(R.id.action_btn);
        skip_btn = findViewById(R.id.skip_btn);

        check_box = findViewById(R.id.check_box);
        check_box_tv = findViewById(R.id.check_box_tv);
        check_box_view = findViewById(R.id.check_box_view);
        userAvatarCiv = (CircleImageView) findViewById(R.id.user_avatar_civ);
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        helpTv = (TextView) findViewById(R.id.help_tv);
        bottom_tv = (TextView) findViewById(R.id.bottom_tv);

        close_iv.setOnClickListener(this);
        action_btn.setOnClickListener(this);
        skip_btn.setOnClickListener(this);
        helpTv.setOnClickListener(this);

        int resource = getIntent().getIntExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.app_icon);
        icon.setImageDrawable(getDrawable(resource));

        title_tv.setText(getIntent().getStringExtra(ArgumentKeys.TITLE));

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_SHOW_CIRCULAR_AVATAR, false)) {

            userAvatarCiv.setVisibility(View.VISIBLE);
            userNameTv.setVisibility(View.VISIBLE);

            icon.setVisibility(View.INVISIBLE);

            String userAvatar = getIntent().getStringExtra(ArgumentKeys.CIRCULAR_AVATAR);

            String username = getIntent().getStringExtra(ArgumentKeys.USER_NAME);

            userNameTv.setText("Hi " + username);

            boolean isAuthRequred = getIntent().getBooleanExtra(ArgumentKeys.IS_AUTH_REQUIRED, false);

            Utils.setImageWithGlide(getApplicationContext(), userAvatarCiv, userAvatar, getDrawable(R.drawable.profile_placeholder), isAuthRequred, true);

        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false)) {
            sub_title_tv.setText(Utils.fromHtml(getIntent().getStringExtra(ArgumentKeys.DESCRIPTION)));
            sub_title_tv.setClickable(true);
            sub_title_tv.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            sub_title_tv.setText(getIntent().getStringExtra(ArgumentKeys.DESCRIPTION));
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_HAVE_CUSTOM_CLICK, false)) {

            sub_title_tv.setText("");
            String start = getIntent().getStringExtra(ArgumentKeys.START);
            String end = getIntent().getStringExtra(ArgumentKeys.END);
            Log.d("START_INDEX", "" + start);
            Log.d("END_INDEX", "" + end);
            makeTextClickable(Integer.parseInt(start), Integer.parseInt(end), getIntent().getStringExtra(ArgumentKeys.DESCRIPTION));
        }

        String actionTitle = getIntent().getStringExtra(ArgumentKeys.OK_BUTTON_TITLE);
        if (TextUtils.isEmpty(actionTitle)) {
            action_btn.setText(getString(R.string.ok));
        } else {
            action_btn.setText(actionTitle);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_SKIP_NEEDED, false)) {
            skip_btn.setVisibility(View.VISIBLE);
        } else {
            skip_btn.setVisibility(View.GONE);
        }

        String skipTitle = getIntent().getStringExtra(ArgumentKeys.SKIP_TITLE);
        if (TextUtils.isEmpty(skipTitle)) {
            skip_btn.setText(getString(R.string.skip));
        } else {
            skip_btn.setText(skipTitle);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_CLOSE_NEEDED, false)) {
            close_iv.setVisibility(View.VISIBLE);
        } else {
            close_iv.setVisibility(View.GONE);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_BUTTON_NEEDED, true)) {
            action_btn.setVisibility(View.VISIBLE);
        } else {
            action_btn.setVisibility(View.GONE);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false)) {
            check_box_view.setVisibility(View.VISIBLE);
            check_box_tv.setText(getIntent().getStringExtra(ArgumentKeys.CHECK_BOX_TITLE));
        } else {
            check_box_view.setVisibility(View.GONE);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_SHOW_HELP, false)) {
            helpTv.setVisibility(View.VISIBLE);
        } else {
            helpTv.setVisibility(View.GONE);
        }

        if (getIntent().getBooleanExtra(ArgumentKeys.IS_BOTTOM_TEXT_NEEDED, false)) {
            bottom_tv.setVisibility(View.VISIBLE);
        } else {
            bottom_tv.setVisibility(View.GONE);
        }

        if (getIntent().getIntExtra(ArgumentKeys.SUBTITLE_ALIGNMENT, 0) != 0) {
            sub_title_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        sub_title_tv.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getIntExtra(ArgumentKeys.SUBTITLE_ALIGNMENT, 0) != 0)
                    return;
                int lineCount = sub_title_tv.getLineCount();
                if (lineCount == 1) {
                    sub_title_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    sub_title_tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_iv:
                performCloseClick();
                break;
            case R.id.action_btn:
                performActionClick();
                break;
            case R.id.skip_btn:
                performSkipClick();
                break;
            case R.id.help_tv:
                performHelpClick();
                break;
        }
    }

    public void performCloseClick() {
        Intent intent = new Intent();
        intent.putExtra(ArgumentKeys.IS_SKIPPED, false);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void performActionClick() {
        Intent intent = new Intent();
        intent.putExtra(ArgumentKeys.IS_CHECK_BOX_CLICKED, check_box.isChecked());
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    public void performSkipClick() {
        Intent intent = new Intent();
        intent.putExtra(ArgumentKeys.IS_SKIPPED, true);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void performHelpClick() {
        showStillNeedHelp();
    }

    private void showStillNeedHelp() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        bundle.putString(ArgumentKeys.TITLE, getString(R.string.more_help));
        bundle.putString(ArgumentKeys.DESCRIPTION, getHelpDesciption());
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, false);
        bundle.putBoolean(ArgumentKeys.IS_SHOW_HELP, false);
        bundle.putBoolean(ArgumentKeys.IS_BOTTOM_TEXT_NEEDED, false);

        bundle.putBoolean(ArgumentKeys.IS_HAVE_CUSTOM_CLICK, true);
        bundle.putString(ArgumentKeys.START, String.valueOf(getHelpDesciption().length() - getString(R.string.click_here).length()));
        bundle.putString(ArgumentKeys.END, String.valueOf(getHelpDesciption().length()));

        startActivity(new Intent(this, ContentActivity.class).putExtras(bundle));
    }

    String getHelpDesciption() {
        Spannable span = new SpannableString(getString(R.string.if_this_emergency));
        span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, getString(R.string.if_this_emergency).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span + "\n\n" + getString(R.string.for_help_scheduling) + "\n\n" + getString(R.string.for_technical_help) + " " + getString(R.string.click_here);
    }


    void makeTextClickable(int start, int end, String text) {
        Log.d("makeTextClickable", "" + start + " " + end);
        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Utils.sendHelpEmail(ContentActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                int color = ContextCompat.getColor(ContentActivity.this, R.color.color_blue);
                ds.setColor(color);
                ds.setUnderlineText(true);
            }
        };

        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(Color.RED), 0, getString(R.string.if_this_emergency).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sub_title_tv.setMovementMethod(LinkMovementMethod.getInstance());
        sub_title_tv.setHighlightColor(Color.TRANSPARENT);
        sub_title_tv.setText(ss);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onClick(close_iv);
    }
}
