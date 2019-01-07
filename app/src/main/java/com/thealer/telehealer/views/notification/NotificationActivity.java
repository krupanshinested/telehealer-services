package com.thealer.telehealer.views.notification;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.HomeActivity;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationActivity extends BaseActivity implements AttachObserverInterface,
        OnActionCompleteInterface, OnOrientationChangeInterface, OnCloseActionInterface, ShowSubFragmentInterface,
        SuccessViewInterface, ChangeTitleInterface, ToolBarInterface {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout fragmentHolder;
    private LinearLayout subFragmentHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
    }

    private void initView() {
        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);
        subFragmentHolder = (LinearLayout) findViewById(R.id.sub_fragment_holder);

        onTitleChange(getString(R.string.notifications));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showNotificationList();
    }

    private void showNotificationList() {
        NotificationListFragment notificationListFragment = new NotificationListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentHolder.getId(), notificationListFragment)
                .commit();
    }

    @Override
    public void onTitleChange(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.sub_fragment_holder);
        boolean isSubFragmentVisible = f == null;

        if (!isSubFragmentVisible) {
            if (getResources().getBoolean(R.bool.isXlarge) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gotoHomeActivity();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            gotoHomeActivity();
        }

    }

    private void gotoHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(subFragmentHolder.getId(), fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {

    }

    @Override
    public void onDataReceived(Bundle bundle) {

    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public ImageView getExtraOption() {
        return null;
    }

    @Override
    public void updateSubTitle(String subTitle, int visibility) {

    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }
}
