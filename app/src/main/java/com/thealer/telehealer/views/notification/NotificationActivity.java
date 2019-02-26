package com.thealer.telehealer.views.notification;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
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

    private AddConnectionApiViewModel addConnectionApiViewModel;
    private CommonUserApiResponseModel commonUserApiResponseModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addConnectionApiViewModel = ViewModelProviders.of(this).get(AddConnectionApiViewModel.class);
        attachObserver(addConnectionApiViewModel);
        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                    if (baseApiResponseModel.isSuccess()) {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_success_prefix) + " " + commonUserApiResponseModel.getFirst_name() + " " + getString(R.string.add_connection_success_suffix));
                    } else {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_failure_prefix) + " " + commonUserApiResponseModel.getFirst_name() + " " + getString(R.string.add_connection_failure_suffix));
                    }

                    LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                    LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
                }
            }
        });

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
        if (success) {
            int selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            String userGuid = bundle.getString(ArgumentKeys.USER_GUID);
            commonUserApiResponseModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);

            bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            addConnectionApiViewModel.connectUser(userGuid, String.valueOf(selectedId));

        }
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
