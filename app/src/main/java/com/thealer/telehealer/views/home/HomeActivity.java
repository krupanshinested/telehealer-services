package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
import com.thealer.telehealer.views.home.recents.RecentDetailView;
import com.thealer.telehealer.views.home.recents.RecentFragment;
import com.thealer.telehealer.views.home.schedules.ScheduleCalendarFragment;
import com.thealer.telehealer.views.home.schedules.SchedulesListFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalReportFragment;
import com.thealer.telehealer.views.notification.NotificationActivity;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

public class HomeActivity extends BaseActivity implements AttachObserverInterface,
        OnActionCompleteInterface, NavigationView.OnNavigationItemSelectedListener, OnOrientationChangeInterface,
        OnCloseActionInterface, ShowSubFragmentInterface, SuccessViewInterface, ChangeTitleInterface, ToolBarInterface, OnViewChangeInterface {
    private static final String TAG = "aswin";
    private Toolbar toolbar;
    private LinearLayout fragmentHolder, subFragmentHolder;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView notificationCountTv;
    private ImageView notificationIv;
    private Menu optionsMenu;

    private FragmentManager fragmentManager;

    private boolean isChildVisible = false;
    private int selecteMenuItem = 0;
    private final String IS_CHILD_VISIBLE = "isChildVisible";
    private final String SELECTED_MENU_ITEM = "selecteMenuItem";
    private final int scheduleTypeCalendar = 0;
    private final int scheduleTypeList = 1;
    private int helpContent = 0;
    private int notificationCount = 0;

    private NotificationApiViewModel notificationApiViewModel;

    private BroadcastReceiver NotificationCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationCount = notificationCount + 1;
            showOrHideNotificationCount(true, notificationCount);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState != null) {
            isChildVisible = savedInstanceState.getBoolean(IS_CHILD_VISIBLE);
            selecteMenuItem = savedInstanceState.getInt(SELECTED_MENU_ITEM);
        } else {
            selecteMenuItem = getIntent().getIntExtra(ArgumentKeys.SELECTED_MENU_ITEM, 0);
        }

        if (checkIsUserActivated()) {
            initView();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(profileListener, new IntentFilter(getString(R.string.profile_picture_updated)));
        LocalBroadcastManager.getInstance(this).registerReceiver(NotificationCountReceiver, new IntentFilter(Constants.NOTIFICATION_COUNT_RECEIVER));
    }

    private void checkNotification() {
        notificationApiViewModel = ViewModelProviders.of(this).get(NotificationApiViewModel.class);
        attachObserver(notificationApiViewModel);
        notificationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    NotificationApiResponseModel notificationApiResponseModel = (NotificationApiResponseModel) baseApiResponseModel;
                    if (notificationApiResponseModel.getResult().getUnread_count() > 0) {
                        showOrHideNotificationCount(true, notificationApiResponseModel.getResult().getUnread_count());
                    } else {
                        showOrHideNotificationCount(false, 0);
                    }
                } else {
                    showOrHideNotificationCount(false, 0);
                }
            }
        });

        notificationApiViewModel.getNotifications(1, false);
    }

    private void showOrHideNotificationCount(boolean isShow, int unread_count) {
        Log.e(TAG, "showOrHideNotificationCount: ");
        if (notificationCountTv != null) {
            notificationCountTv.setText(String.valueOf(unread_count));
            if (isShow) {
                notificationCountTv.setVisibility(View.VISIBLE);
            } else {
                notificationCountTv.setVisibility(View.GONE);
            }
        }
    }

    private boolean checkIsUserActivated() {
        if (UserType.isUserDoctor()) {
            if (!appPreference.getBoolean(PreferenceConstants.IS_USER_ACTIVATED)) {
                startActivity(new Intent(this, DoctorOnBoardingActivity.class));
                finish();
                return false;
            }
        }

        if (UserDetailPreferenceManager.getWhoAmIResponse() == null ||
                !UserDetailPreferenceManager.getWhoAmIResponse().isEmail_verified()) {
            startActivity(new Intent(this, EmailVerificationActivity.class));
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(profileListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(NotificationCountReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_CHILD_VISIBLE, isChildVisible);
        outState.putInt(SELECTED_MENU_ITEM, selecteMenuItem);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getDrawable(R.drawable.ic_menu_24dp));

        fragmentHolder = (LinearLayout) findViewById(R.id.fragment_holder);
        subFragmentHolder = (LinearLayout) findViewById(R.id.sub_fragment_holder);

        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.home_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateProfilePic();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorWhite));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        fragmentManager = this.getSupportFragmentManager();

        if (UserType.isUserPatient()) {
            navigationView.getMenu().removeItem(R.id.menu_patient);
        } else if (UserType.isUserDoctor()) {
            navigationView.getMenu().removeItem(R.id.menu_doctor);
        } else if (UserType.isUserAssistant()) {
            navigationView.getMenu().removeItem(R.id.menu_patient);
            navigationView.getMenu().removeItem(R.id.menu_orders);
            navigationView.getMenu().removeItem(R.id.menu_vitals);
            navigationView.getMenu().findItem(R.id.menu_schedules).setChecked(true);
            selecteMenuItem = R.id.menu_schedules;
        }

        if (PermissionChecker.with(this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC))
            attachView();

        checkForMedicalHistory();
        Log.e(TAG, "initView: ");
    }

    private void checkForMedicalHistory() {
        if (UserType.isUserPatient() && !appPreference.getBoolean(PreferenceConstants.IS_HISTORY_UPDATE_SHOWN)) {

            WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

            if (whoAmIApiResponseModel.getQuestionnaire() == null || !whoAmIApiResponseModel.getQuestionnaire().isQuestionariesEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_health_heart);
                bundle.putString(ArgumentKeys.TITLE, getString(R.string.health_profile));
                bundle.putString(ArgumentKeys.DESCRIPTION, getString(R.string.add_health_info_content));
                bundle.putBoolean(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
                bundle.putBoolean(ArgumentKeys.IS_SKIP_NEEDED, true);
                bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
                bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed));
                bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);

                appPreference.setBoolean(PreferenceConstants.IS_HISTORY_UPDATE_SHOWN, true);

                startActivityForResult(new Intent(HomeActivity.this, ContentActivity.class).putExtras(bundle), RequestID.REQ_CONTENT_VIEW);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_CONTENT_VIEW && resultCode == Activity.RESULT_OK) {
            showMedicalHistory();
        }

        if (requestCode == PermissionConstants.PERMISSION_CAM_MIC) {
            attachView();
        }
    }

    private void showMedicalHistory() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.VIEW_TYPE, ArgumentKeys.HISTORY_UPDATE);

        startActivity(new Intent(this, ProfileSettingsActivity.class).putExtras(bundle));
    }

    private void attachView() {
        switch (selecteMenuItem) {
            case 0:
            case R.id.menu_doctor:
            case R.id.menu_patient:
                navigationView.getMenu().getItem(0).setChecked(true);
                showDoctorPatientList();
                break;
            case R.id.menu_recent:
                showRecentView();
                break;
            case R.id.menu_schedules:
                showSchedulesFragment(scheduleTypeCalendar);
                break;
        }

        checkForDocumentUpload();
    }

    private void checkForDocumentUpload() {
        if (Constants.sharedPath != null) {
            showAlertDialog("Found Documents", "Do you really want to upload the documents which you shared?", getString(R.string.upload), getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ArgumentKeys.IS_SHARED_INTENT, true);
                            bundle.putString(Constants.SELECTED_ITEM, OrderConstant.ORDER_DOCUMENTS);

                            startActivity(new Intent(HomeActivity.this, CreateOrderActivity.class).putExtras(bundle));
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Constants.sharedPath = null;
                            dialog.dismiss();
                        }
                    });
        }
    }

    private BroadcastReceiver profileListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfilePic();
        }
    };

    private void updateProfilePic() {
        View view = navigationView.getHeaderView(0);
        ImageView userProfileIv = view.findViewById(R.id.home_header_iv);
        Utils.setImageWithGlide(getApplicationContext(), userProfileIv, UserDetailPreferenceManager.getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e(TAG, "onCreateOptionsMenu: ");
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.appbar_home_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_notification);
        View notificationView = menuItem.getActionView();
        notificationCountTv = notificationView.findViewById(R.id.notification_count_tv);
        notificationIv = notificationView.findViewById(R.id.notification_icon);
        notificationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer();
                break;
            case R.id.menu_event:
                showSchedulesFragment(scheduleTypeCalendar);
                break;
            case R.id.menu_schedules:
                showSchedulesFragment(scheduleTypeList);
                break;
            case R.id.menu_help:
                showHelpContent();
                break;
            case R.id.menu_notification:
                showNotificationFragment();
                showOrHideNotificationCount(false, 0);
                removeAllNotification();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeAllNotification() {
        notificationCount = 0;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void showNotificationFragment() {
        startActivity(new Intent(this, NotificationActivity.class));
        finish();
    }

    private void showHelpContent() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        bundle.putString(ArgumentKeys.TITLE, new HelpContent(this).getTitle(helpContent));
        bundle.putString(ArgumentKeys.DESCRIPTION, new HelpContent(this).getContent(helpContent));
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, false);

        startActivity(new Intent(this, ContentActivity.class).putExtras(bundle));
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    private void showDoctorPatientList() {
        helpContent = HelpContent.HELP_DOC_PATIENT;
        setDoctorPatientTitle();
        DoctorPatientListingFragment doctorPatientListingFragment = new DoctorPatientListingFragment();
        setFragment(doctorPatientListingFragment);
    }

    private void setDoctorPatientTitle() {
        if (UserType.isUserPatient() || UserType.isUserAssistant()) {
            setToolbarTitle(getString(R.string.Doctors));
        } else {
            setToolbarTitle(getString(R.string.Patients));
        }
    }

    private void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.sub_fragment_holder);
        boolean isSubFragmentVisible = f == null;

        if (!isSubFragmentVisible) {
            if (getResources().getBoolean(R.bool.isXlarge) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        showDetailView(bundle);
    }

    private void showDetailView(Bundle bundle) {
        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
        doctorPatientDetailViewFragment.setArguments(bundle);
        setSubFragment(doctorPatientDetailViewFragment);
    }

    private void setFragment(Fragment fragment) {
        removeAllFragments();
        fragmentManager
                .beginTransaction()
                .replace(fragmentHolder.getId(), fragment)
                .commit();
    }

    private void removeAllFragments() {

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void setSubFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(subFragmentHolder.getId(), fragment)
                .commit();

        isChildVisible = true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        showScheduleToolbarOptions(false, 0);
        switch (menuItem.getItemId()) {
            case R.id.menu_doctor:
            case R.id.menu_patient:
                selecteMenuItem = R.id.menu_doctor;
                showDoctorPatientList();
                break;
            case R.id.menu_schedules:
                selecteMenuItem = R.id.menu_schedules;
                showSchedulesFragment(scheduleTypeCalendar);
                break;
            case R.id.menu_recent:
                selecteMenuItem = R.id.menu_recent;
                showRecentView();
                break;
            case R.id.menu_profile_settings:
                Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menu_vitals:
                if (UserType.isUserPatient()) {
                    showVitalsView();
                } else {
                    showVitalReportView();
                }
                break;
            case R.id.menu_orders:
                showOrdersView();
                break;
        }
        toggleDrawer();
        return true;
    }

    private void showVitalReportView() {
        helpContent = HelpContent.HELP_VITAL_REPORT;
        setToolbarTitle(getString(R.string.vitals));
        VitalReportFragment vitalReportFragment = new VitalReportFragment();
        setFragment(vitalReportFragment);
    }

    private void showScheduleToolbarOptions(boolean visible, int scheduleType) {
        if (optionsMenu != null) {
            if (visible) {
                if (scheduleType == scheduleTypeCalendar) {
                    optionsMenu.findItem(R.id.menu_schedules).setVisible(true);
                    optionsMenu.findItem(R.id.menu_event).setVisible(false);
                } else if (scheduleType == scheduleTypeList) {
                    optionsMenu.findItem(R.id.menu_schedules).setVisible(false);
                    optionsMenu.findItem(R.id.menu_event).setVisible(true);

                }
            } else {
                optionsMenu.findItem(R.id.menu_schedules).setVisible(false);
                optionsMenu.findItem(R.id.menu_event).setVisible(false);
            }
        }
    }

    private void showSchedulesFragment(int scheduleType) {
        helpContent = HelpContent.HELP_SCHEDULES;
        showScheduleToolbarOptions(true, scheduleType);
        setToolbarTitle(getString(R.string.schedules));
        Fragment fragment = null;
        switch (scheduleType) {
            case scheduleTypeCalendar:
                fragment = new ScheduleCalendarFragment();
                break;
            case scheduleTypeList:
                fragment = new SchedulesListFragment();
                break;
        }
        setFragment(fragment);
    }

    private void showOrdersView() {
        helpContent = HelpContent.HELP_ORDERS;
        setToolbarTitle(getString(R.string.orders));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FROM_HOME, true);

        OrdersListFragment ordersListFragment = new OrdersListFragment();
        ordersListFragment.setArguments(bundle);
        setFragment(ordersListFragment);
    }

    private void showVitalsView() {
        helpContent = HelpContent.HELP_VITALS;
        setToolbarTitle(getString(R.string.vitals));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FROM_HOME, true);

        VitalsListFragment vitalsListFragment = new VitalsListFragment();
        vitalsListFragment.setArguments(bundle);
        setFragment(vitalsListFragment);

    }

    public void showRecentView() {
        helpContent = HelpContent.HELP_RECENTS;
        setToolbarTitle(getString(R.string.recents));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FROM_HOME, true);
        RecentFragment recentFragment = new RecentFragment();
        recentFragment.setArguments(bundle);
        setFragment(recentFragment);
    }

    @Override
    public void onDataReceived(Bundle bundle) {
        if (getResources().getBoolean(R.bool.isXlarge) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentHolder.getId());

            if (fragment instanceof DoctorPatientListingFragment)
                showDetailView(bundle);
            else if (fragment instanceof RecentFragment)
                showRecentDetailView(bundle);
        }
    }

    private void showRecentDetailView(Bundle bundle) {
        RecentDetailView recentDetailView = new RecentDetailView();
        recentDetailView.setArguments(bundle);
        setSubFragment(recentDetailView);
    }

    @Override
    public void onClose(boolean isRefreshRequired) {
        onBackPressed();
        if (isRefreshRequired) {
            attachView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotification();
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        setSubFragment(fragment);
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {

    }

    @Override
    public void onTitleChange(String title) {
        setToolbarTitle(title);
    }

    //ToolBarInterface methods
    @Override
    public void updateTitle(String title) {
        setToolbarTitle(title);
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
        //nothing
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
}
