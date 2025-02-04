package com.thealer.telehealer.views.notification;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OnFilterSelectedInterface;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.HomeActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 07,January,2019
 */
public class NotificationActivity extends BaseActivity implements AttachObserverInterface,
        OnActionCompleteInterface, OnOrientationChangeInterface, OnCloseActionInterface, ShowSubFragmentInterface,
        SuccessViewInterface, ChangeTitleInterface, ToolBarInterface, OnViewChangeInterface {

    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout fragmentHolder;
    private LinearLayout subFragmentHolder;

    private AddConnectionApiViewModel addConnectionApiViewModel;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private boolean[] selectedItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        addConnectionApiViewModel = new ViewModelProvider(this).get(AddConnectionApiViewModel.class);
        attachObserver(addConnectionApiViewModel);
        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {

                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_STATUS, baseApiResponseModel.isSuccess());

                    if (baseApiResponseModel.isSuccess()) {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_success), commonUserApiResponseModel.getFirst_name()));
                    } else {
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, String.format(getString(R.string.add_connection_failure), commonUserApiResponseModel.getFirst_name()));
                    }

                    LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(NotificationActivity.this).sendBroadcast(intent);
                    }
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

        List<String> filterOptions = NotificationConstants.getNotificationDisplayFilters(this);
        List<String> filterTypes = NotificationConstants.getNotificationFilterTypes(this);
        selectedItem = new boolean[filterOptions.size()];

        toolbar.inflateMenu(R.menu.filter_menu);
        MenuItem filterItem = toolbar.getMenu().findItem(R.id.menu_filter);
        View view = filterItem.getActionView();
        ImageView filterIv = view.findViewById(R.id.filter_iv);
        ImageView filterIndicatorIv = view.findViewById(R.id.filter_indicatior_iv);

        filterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> selectionOptions = new ArrayList<>();

                Utils.showMultichoiseItemSelectAlertDialog(NotificationActivity.this, getString(R.string.filter_by_type), filterOptions.toArray(new String[0]), selectedItem,
                        getString(R.string.ok), getString(R.string.Cancel), new Utils.OnMultipleChoiceInterface() {
                            @Override
                            public void onSelected(boolean[] selectedList) {
                                selectedItem = selectedList;
                                for (int i = 0; i < selectedList.length; i++) {
                                    if (selectedList[i]) {
                                        selectionOptions.add(filterTypes.get(i));
                                    }
                                }

                                if (selectionOptions.isEmpty()){
                                    filterIndicatorIv.setVisibility(View.GONE);
                                }else {
                                    filterIndicatorIv.setVisibility(View.VISIBLE);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ArgumentKeys.SELECTED_ITEMS, selectionOptions);
                                Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentHolder.getId());
                                if (fragment instanceof OnFilterSelectedInterface)
                                    ((OnFilterSelectedInterface) fragment).onFilterSelected(bundle);
                            }
                        });
            }
        });
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
        if (!isPreviousActivityAvailable()) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
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
        if (string.equals(RequestID.REQ_ADD_CONNECTION) && success) {
            int selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            String userGuid = bundle.getString(ArgumentKeys.USER_GUID);
            String doctorGuid = bundle.getString(ArgumentKeys.DOCTOR_GUID);
            commonUserApiResponseModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);

            bundle = new Bundle();
            bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(bundle);

            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
            String designation = bundle.getString(Constants.DESIGNATION);
            String currentUserGuid=userGuid;
            if(!UserType.isUserAssistant())
                currentUserGuid="";

            addConnectionApiViewModel.connectUser(currentUserGuid,userGuid, doctorGuid, String.valueOf(selectedId),designation);
        } else if (string.equals(RequestID.REQ_SHOW_DETAIL_VIEW)) {
            bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
            DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
            doctorPatientDetailViewFragment.setArguments(bundle);
            onShowFragment(doctorPatientDetailViewFragment);
        }
    }

    @Override
    public void onDataReceived(Bundle bundle) {

    }

    @Override
    public void enableNext(boolean enabled) {

    }

    @Override
    public void hideOrShowNext(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowClose(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowToolbarTile(boolean hideOrShow) {

    }

    @Override
    public void hideOrShowBackIv(boolean hideOrShow) {

    }

    @Override
    public void updateNextTitle(String nextTitle) {

    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void hideOrShowOtherOption(boolean hideOrShow) {

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

    @Override
    protected void onResume() {
        super.onResume();
        removeAllNotification();
    }
}
