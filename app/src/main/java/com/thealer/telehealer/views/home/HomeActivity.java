package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.createuser.LicensesBean;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.signout.SignoutApiViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CommonInterface.ToolBarInterface;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OnUpdateListener;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.stripe.PaymentContentActivity;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.home.monitoring.MonitoringFragment;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
import com.thealer.telehealer.views.home.pendingInvites.PendingInvitesActivity;
import com.thealer.telehealer.views.home.recents.RecentDetailView;
import com.thealer.telehealer.views.home.recents.RecentFragment;
import com.thealer.telehealer.views.home.schedules.ScheduleCalendarFragment;
import com.thealer.telehealer.views.home.schedules.SchedulesListFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;
import com.thealer.telehealer.views.home.vitals.vitalReport.VitalReportFragment;
import com.thealer.telehealer.views.inviteUser.SendInvitationFragment;
import com.thealer.telehealer.views.notification.NotificationActivity;
import com.thealer.telehealer.views.quickLogin.QuickLoginActivity;
import com.thealer.telehealer.views.settings.ProfileSettingsActivity;
import com.thealer.telehealer.views.signin.SigninActivity;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;
import com.thealer.telehealer.views.subscription.SubscriptionActivity;
import com.thealer.telehealer.views.subscription.SubscriptionPlanFragment;

import java.util.Calendar;
import java.util.List;

import flavor.GoogleFit.VitalsListWithGoogleFitFragment;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;
import static com.thealer.telehealer.TeleHealerApplication.application;
import static com.thealer.telehealer.TeleHealerApplication.isInForeGround;

public class HomeActivity extends BaseActivity implements AttachObserverInterface,
        OnActionCompleteInterface, NavigationView.OnNavigationItemSelectedListener, OnOrientationChangeInterface,
        OnCloseActionInterface, ShowSubFragmentInterface, SuccessViewInterface, ChangeTitleInterface, ToolBarInterface, OnViewChangeInterface,
        OnUpdateListener {
    private static final String TAG = "aswin";
    private Toolbar toolbar;
    private LinearLayout fragmentHolder, subFragmentHolder;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView notificationCountTv;
    private ImageView notificationIv;
    private LinearLayout addPatient;
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
    private boolean isCheckLicense = true;
    private boolean isPropserShown = false;
    private boolean isSigningOutInProcess = false;
    private static boolean onAuthenticated = false;

    private NotificationApiViewModel notificationApiViewModel;

    private WhoAmIApiViewModel whoAmIApiViewModel;
    private SignoutApiViewModel signoutApiViewModel;

    private BroadcastReceiver NotificationCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationCount = notificationCount + 1;
            showOrHideNotificationCount(true, notificationCount);
        }
    };
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private AddConnectionApiViewModel addConnectionApiViewModel;

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
            initViewModels();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(isPropserShownListner, new IntentFilter(getString(R.string.APP_LIFECYCLE_STATUS)));
        LocalBroadcastManager.getInstance(this).registerReceiver(profileListener, new IntentFilter(getString(R.string.profile_picture_updated)));
        LocalBroadcastManager.getInstance(this).registerReceiver(NotificationCountReceiver, new IntentFilter(Constants.NOTIFICATION_COUNT_RECEIVER));

    }

    private void initViewModels() {
        addConnectionApiViewModel = new ViewModelProvider(this).get(AddConnectionApiViewModel.class);
        whoAmIApiViewModel = new ViewModelProvider(this).get(WhoAmIApiViewModel.class);
        signoutApiViewModel = new ViewModelProvider(this).get(SignoutApiViewModel.class);

        attachObserver(addConnectionApiViewModel);
        attachObserver(whoAmIApiViewModel);
        attachObserver(signoutApiViewModel);

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
                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);
                }
            }
        });

        addConnectionApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                    intent.putExtra(Constants.SUCCESS_VIEW_STATUS, false);
                    intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                    intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                    LocalBroadcastManager.getInstance(HomeActivity.this).sendBroadcast(intent);
                }
            }
        });

        whoAmIApiViewModel.getBaseApiResponseModelMutableLiveData().observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    WhoAmIApiResponseModel whoAmIApiResponseModel = (WhoAmIApiResponseModel) baseApiResponseModel;
                    if (Constants.ROLE_DOCTOR.equals(whoAmIApiResponseModel.getRole()))
                        AppPaymentCardUtils.handleCardCasesFromPaymentInfo(HomeActivity.this, whoAmIApiResponseModel.getPayment_account_info(), null);
                }
            }
        });
        whoAmIApiViewModel.assignWhoAmI();


        signoutApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                isSigningOutInProcess = false;
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    appPreference.setBoolean(PreferenceConstants.IS_USER_LOGGED_IN, false);
                    startActivity(new Intent(HomeActivity.this, SigninActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        });
        signoutApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
                isSigningOutInProcess = false;
            }
        });


    }

    private void checkNotification() {
        notificationApiViewModel = new ViewModelProvider(this).get(NotificationApiViewModel.class);
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

        notificationApiViewModel.getNotifications(null, 1, false, null, null);
    }

    private void showOrHideNotificationCount(boolean isShow, int unread_count) {
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
//            if (!appPreference.getBoolean(PreferenceConstants.IS_USER_ACTIVATED)) {
//                startActivity(new Intent(this, DoctorOnBoardingActivity.class));
//                finish();
//                return false;
//            } else if(!appPreference.getBoolean(PreferenceConstants.IS_USER_PURCHASED)) {
//                startActivity(new Intent(this, SubscriptionActivity.class));
//                finish();
//                return false;
//            }

        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(profileListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isPropserShownListner);
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
            navigationView.getMenu().removeItem(R.id.menu_monitoring);
            navigationView.getMenu().findItem(R.id.menu_schedules).setChecked(true);
            selecteMenuItem = R.id.menu_schedules;
        }

        attachView();
    }

    private void showMedicalHistory() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.VIEW_TYPE, ArgumentKeys.HISTORY_UPDATE);

        startActivity(new Intent(this, ProfileSettingsActivity.class).putExtras(bundle));
    }

    private void attachView() {
        String openAutomaticType = getIntent().getStringExtra(ArgumentKeys.OPEN_AUTOMATICALLY);
        if (openAutomaticType != null) {
            showMonitoringView(openAutomaticType);
        } else {
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
                case R.id.menu_invite:
                    showSendInvitation();
                    break;
            }
        }

        checkForDocumentUpload();
    }

    private void checkForDocumentUpload() {
        if (Constants.sharedPath != null) {
            Utils.showAlertDialog(this, getString(R.string.found_documents), getString(R.string.do_you_want_to_upload), getString(R.string.upload), getString(R.string.cancel),
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

    public BroadcastReceiver profileListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateProfilePic();
        }
    };
    public BroadcastReceiver isPropserShownListner = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(ArgumentKeys.APP_LIFECYCLE_STATUS, false);
            if (!status) {
                isPropserShown = false;
            }

        }
    };

    private void updateProfilePic() {
        if (navigationView == null) {
            return;
        }

        View view = navigationView.getHeaderView(0);
        ImageView userProfileIv = view.findViewById(R.id.home_header_iv);
        Utils.setImageWithGlide(getApplicationContext(), userProfileIv, UserDetailPreferenceManager.getUser_avatar(), getDrawable(R.drawable.profile_placeholder), true, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.appbar_home_menu, menu);

        if (UserType.isUserAssistant()) {
            optionsMenu.findItem(R.id.menu_overflow).setVisible(false);
            optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
            optionsMenu.findItem(R.id.menu_schedules).setVisible(true);
        } else if (UserType.isUserPatient()) {
            optionsMenu.findItem(R.id.menu_overflow).setVisible(true);
            optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
        } else {
            optionsMenu.findItem(R.id.menu_overflow).setVisible(true);
            optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
        }

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
        MenuItem menuItemAddPatient = menu.findItem(R.id.menu_addPatients);
        View addPatientiew = menuItemAddPatient.getActionView();
        ((TextView) addPatientiew.findViewById(R.id.add_patients)).setText((UserType.isUserPatient() || UserType.isUserAssistant()) ? getString(R.string.lbl_add_providers) : getString(R.string.lbl_add_patients));
        ((LinearLayout) addPatientiew.findViewById(R.id.btnAddPatient)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentById(fragmentHolder.getId()) instanceof DoctorPatientListingFragment) {
                    ((DoctorPatientListingFragment) getSupportFragmentManager().findFragmentById(fragmentHolder.getId())).onClick(v);
                }
            }
        });

        if (UserType.isUserPatient()) {
            setToolbarTitle(getString(R.string.Doctors));
        } else if (UserType.isUserAssistant()) {
            setToolbarTitle(getString(R.string.schedules));
        } else {
            setToolbarTitle(getString(R.string.Patients));
        }

        if (getSupportFragmentManager().findFragmentById(fragmentHolder.getId()) instanceof DoctorPatientListingFragment) {
            showPendingInvitesOption(true);
        } else {
            showPendingInvitesOption(false);
        }

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
            case R.id.menu_overflow:
                showDoctorsOverflowMenu();
                break;
            case R.id.menu_pending_invites:
                startActivity(new Intent(this, PendingInvitesActivity.class));
                break;
            case R.id.menu_notification:
                showNotificationFragment();
                showOrHideNotificationCount(false, 0);
                notificationCount = 0;
                removeAllNotification();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void showDoctorsOverflowMenu() {
        Bundle bundle=new Bundle();
        if(UserType.isUserAssistant()) {
            bundle.putString(ArgumentKeys.ROLE, Constants.ROLE_ASSISTANT);
        }else if(UserType.isUserPatient()) {
            bundle.putString(ArgumentKeys.ROLE, Constants.ROLE_PATIENT);
        }else {
            bundle.putString(ArgumentKeys.ROLE, Constants.ROLE_DOCTOR);
        }

        Utils.showDoctorOverflowMenu(this,bundle);
    }

    private void showNotificationFragment() {
        startActivity(new Intent(this, NotificationActivity.class));
    }

    private void showHelpContent() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.banner_help);
        bundle.putString(ArgumentKeys.TITLE, new HelpContent(this).getTitle(helpContent));
        bundle.putString(ArgumentKeys.DESCRIPTION, new HelpContent(this).getContent(helpContent));
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
        bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, false);
        bundle.putBoolean(ArgumentKeys.IS_SHOW_HELP, true);
        bundle.putBoolean(ArgumentKeys.IS_BOTTOM_TEXT_NEEDED, true);

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
        showPendingInvitesOption(true);
        helpContent = HelpContent.HELP_DOC_PATIENT;
        setDoctorPatientTitle();
        DoctorPatientListingFragment doctorPatientListingFragment = new DoctorPatientListingFragment();
        setFragment(doctorPatientListingFragment);

        /*TransactionListFragment transactionListFragment = new TransactionListFragment();
        setFragment(transactionListFragment);*/
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

        Fragment f = fragmentManager.findFragmentById(R.id.sub_fragment_holder);
        boolean isSubFragmentVisible = f == null;

        if (!isSubFragmentVisible) {
            if (getResources().getBoolean(R.bool.isXlarge) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                finishAffinity();
            } else {
                fragmentManager.popBackStackImmediate();
                if (fragmentManager.findFragmentById(R.id.sub_fragment_holder) == null)
                    updateToolbarOptions(fragmentManager.findFragmentById(R.id.fragment_holder), false);
            }
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onCompletionResult(String string, Boolean success, Bundle bundle) {
        if (bundle.getBoolean(ArgumentKeys.CONNECT_USER, false)) {

            Bundle succesBundle = new Bundle();
            succesBundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
            succesBundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.add_connection_requesting));

            SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
            successViewDialogFragment.setArguments(succesBundle);
            successViewDialogFragment.show(getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());

            int selectedId = bundle.getInt(Constants.ADD_CONNECTION_ID);
            String userGuid = bundle.getString(ArgumentKeys.USER_GUID);
            String doctorGuid = bundle.getString(ArgumentKeys.DOCTOR_GUID);
            commonUserApiResponseModel = (CommonUserApiResponseModel) bundle.getSerializable(Constants.USER_DETAIL);
            String designation = bundle.getString(Constants.DESIGNATION);
            String currentUserGuid=userGuid;
            if(!UserType.isUserAssistant())
                currentUserGuid="";

            addConnectionApiViewModel.connectUser(currentUserGuid,userGuid, doctorGuid, String.valueOf(selectedId), designation);

        } else {
            showDetailView(bundle);
        }
    }

    private void showDetailView(Bundle bundle) {
        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
        doctorPatientDetailViewFragment.setArguments(bundle);
        setSubFragment(doctorPatientDetailViewFragment);
    }

    private void setFragment(Fragment fragment) {

        Bundle bundle = fragment.getArguments();
        if (bundle == null)
            bundle = new Bundle();

        bundle.putBoolean(ArgumentKeys.IS_FROM_HOME, true);
        fragment.setArguments(bundle);

        removeAllFragments();
        fragmentManager
                .beginTransaction()
                .replace(fragmentHolder.getId(), fragment)
                .commit();
        Log.e(TAG, "setFragment: " + getSupportFragmentManager().getFragments().toString());
        updateToolbarOptions(fragment, false);
    }

    private void updateToolbarOptions(Fragment fragment, boolean isSubFragment) {
        Log.e(TAG, "updateToolbarOptions: 1 ");
        if (optionsMenu != null) {
            if (fragment instanceof ScheduleCalendarFragment) {
                optionsMenu.findItem(R.id.menu_schedules).setVisible(true);
                optionsMenu.findItem(R.id.menu_event).setVisible(false);
                Log.e(TAG, "updateToolbarOptions: 2");
            } else if (fragment instanceof SchedulesListFragment) {
                optionsMenu.findItem(R.id.menu_schedules).setVisible(false);
                optionsMenu.findItem(R.id.menu_event).setVisible(true);
                Log.e(TAG, "updateToolbarOptions: 3");
            } else {
                if (!isSubFragment) {
                    optionsMenu.findItem(R.id.menu_schedules).setVisible(false);
                    optionsMenu.findItem(R.id.menu_event).setVisible(false);
                }
                Log.e(TAG, "updateToolbarOptions: 4");
            }
        } else {
            Log.e(TAG, "updateToolbarOptions: 5");
        }
    }

    private void removeAllFragments() {

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void setSubFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.fragment_remove_animation, R.anim.fragment_remove_exit)
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(subFragmentHolder.getId(), fragment)
                .commit();

        isChildVisible = true;
        updateToolbarOptions(fragment, true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.e(TAG, "onNavigationItemSelected: ");
        if (menuItem.getItemId() == R.id.menu_sign_out) {
            performSignOut();
            toggleDrawer();
            return true;
        }
        if (menuItem.getItemId() != R.id.menu_profile_settings) {
            showPendingInvitesOption(false);
        }
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
            case R.id.menu_monitoring:
                showMonitoringView(null);
                break;
            case R.id.menu_invite:
                selecteMenuItem = R.id.menu_invite;
                showSendInvitation();
                break;
        }
        toggleDrawer();
        return true;
    }

    private void showSendInvitation() {
        helpContent = HelpContent.HELP_INVITATION;
        setToolbarTitle(getString(R.string.send_invitation));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FROM_HOME, true);

        SendInvitationFragment sendInvitationFragment = new SendInvitationFragment();
        sendInvitationFragment.setArguments(bundle);
        setFragment(sendInvitationFragment);

    }

    private void performSignOut() {
        if (!isSigningOutInProcess && !CallManager.shared.isActiveCallPresent()) {
            isSigningOutInProcess = true;
            signoutApiViewModel.signOut();
        }
    }


    private void showMonitoringView(@Nullable String openAutomatically) {
        helpContent = HelpContent.HELP_MONITORING;
        setToolbarTitle(getString(R.string.monitoring));
        MonitoringFragment monitoringFragment = new MonitoringFragment();

        if (openAutomatically != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.OPEN_AUTOMATICALLY, openAutomatically);
            monitoringFragment.setArguments(bundle);
        }

        setFragment(monitoringFragment);
    }

    private void showVitalReportView() {
        helpContent = HelpContent.HELP_VITAL_REPORT;
        setToolbarTitle(getString(R.string.vitals));
        VitalReportFragment vitalReportFragment = new VitalReportFragment();
        setFragment(vitalReportFragment);
    }

    private void showScheduleToolbarOptions(boolean visible, int scheduleType) {
        Log.e(TAG, "showScheduleToolbarOptions: " + visible + " " + scheduleType);
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

    private void showPendingInvitesOption(boolean visible) {
        if (optionsMenu != null) {
            if (visible) {
                if (UserType.isUserPatient()) {
                    optionsMenu.findItem(R.id.menu_overflow).setVisible(true);
                    optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
                } else if (UserType.isUserDoctor()) {
                    optionsMenu.findItem(R.id.menu_overflow).setVisible(true);
                    optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
                }else if(UserType.isUserAssistant()){
                    optionsMenu.findItem(R.id.menu_overflow).setVisible(true);
                    optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
                }
            } else {
                optionsMenu.findItem(R.id.menu_overflow).setVisible(false);
                optionsMenu.findItem(R.id.menu_pending_invites).setVisible(false);
            }
        }
        showAddPatientsOption(visible);
    }

    private void showAddPatientsOption(boolean visible) {
        if (optionsMenu != null) {
            if (visible) {
                optionsMenu.findItem(R.id.menu_addPatients).setVisible(true);
            } else {
                optionsMenu.findItem(R.id.menu_addPatients).setVisible(false);
            }
        }
    }

    private void showSchedulesFragment(int scheduleType) {
        helpContent = HelpContent.HELP_SCHEDULES;
//        showScheduleToolbarOptions(true, scheduleType);
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

        VitalsListFragment vitalsListFragment = new VitalsListWithGoogleFitFragment();
        vitalsListFragment.setArguments(bundle);
        setFragment(vitalsListFragment);

    }

    public void showRecentView() {
        helpContent = HelpContent.HELP_RECENTS;
        setToolbarTitle(getString(R.string.visits));
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FROM_HOME, true);
        bundle.putBoolean(ArgumentKeys.IS_ONLY_CALLS, true);
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
        if (Constants.isRedirectProfileSetting) {
            Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
            this.startActivity(intent);
        }
        if (isRefreshRequired) {
            attachView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.ErrorFlag = false;
        checkNotification();
        application.addShortCuts();
        if (isInForeGround) {
            Log.d("Home_Called", "showHelpScreen");
            showHelpScreen();
        }
    }

    private void showHelpScreen() {
        if (UserType.isUserDoctor() && isCheckLicense && !appConfig.getInstallType().equals(getString(R.string.install_type_india)) && checkIsLicenseExpired()) {
            Log.d("HelpScreen", "CheckLicense");
            startActivityForResult(new Intent(this, ContentActivity.class)
                    .putExtra(ArgumentKeys.IS_SHOW_CIRCULAR_AVATAR, true)
                    .putExtra(ArgumentKeys.CIRCULAR_AVATAR, UserDetailPreferenceManager.getWhoAmIResponse().getUser_avatar())
                    .putExtra(ArgumentKeys.IS_AUTH_REQUIRED, true)
                    .putExtra(ArgumentKeys.USER_NAME, UserDetailPreferenceManager.getWhoAmIResponse().getFirst_name())
                    .putExtra(ArgumentKeys.TITLE, getString(R.string.license_expired))
                    .putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.license_expired_message))
                    .putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed))
                    .putExtra(ArgumentKeys.IS_BUTTON_NEEDED, true), RequestID.REQ_LICENSE_EXPIRED);

        } else if (!isPropserShown) {

            if (!PermissionChecker.with(this).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
                Log.d("HelpScreen", "Permission");
                isPropserShown = true;
            }
        } else if (!appPreference.getBoolean(PreferenceConstants.IS_HEALTH_SUMMARY_SHOWN) && (!application.isFromRegistration) && UserType.isUserPatient() && UserDetailPreferenceManager.getWhoAmIResponse() != null && (UserDetailPreferenceManager.getWhoAmIResponse().getQuestionnaire() == null || !UserDetailPreferenceManager.getWhoAmIResponse().getQuestionnaire().isQuestionariesEmpty())) {

            appPreference.setBoolean(PreferenceConstants.IS_HEALTH_SUMMARY_SHOWN, true);

            Log.d("HelpScreen", "Health Summary");
            Bundle bundle = new Bundle();
            bundle.putInt(ArgumentKeys.RESOURCE_ICON, R.drawable.ic_health_heart);
            bundle.putString(ArgumentKeys.TITLE, getString(R.string.health_summary));
            bundle.putString(ArgumentKeys.DESCRIPTION, getString(R.string.add_health_info_content));
            bundle.putBoolean(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, true);
            bundle.putBoolean(ArgumentKeys.IS_SKIP_NEEDED, true);
            bundle.putBoolean(ArgumentKeys.IS_BUTTON_NEEDED, true);
            bundle.putString(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.proceed));
            bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);

            startActivityForResult(new Intent(HomeActivity.this, ContentActivity.class).putExtras(bundle), RequestID.REQ_CONTENT_VIEW);

        } else if (UserDetailPreferenceManager.getWhoAmIResponse() == null || !UserDetailPreferenceManager.getWhoAmIResponse().isEmail_verified()) {
            Log.d("HelpScreen", "Email");
            Calendar currentTime = Calendar.getInstance();
            Calendar updateTime;
            boolean show = false;

            if (appPreference.getString(PreferenceConstants.EMAIL_VERIFICATION_SHOWN_TIME).isEmpty()) {

                show = true;
            } else {
                updateTime = Calendar.getInstance();
                updateTime.setTimeInMillis(Long.parseLong(appPreference.getString(PreferenceConstants.EMAIL_VERIFICATION_SHOWN_TIME)));

                if ((currentTime.get(Calendar.DAY_OF_MONTH) > updateTime.get(Calendar.DAY_OF_MONTH)) ||
                        (currentTime.get(Calendar.MONTH) > updateTime.get(Calendar.MONTH))) {
                    show = true;
                }
            }
            if ((!application.isFromRegistration) && show) {
                appPreference.setString(PreferenceConstants.EMAIL_VERIFICATION_SHOWN_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
                startActivity(new Intent(this, EmailVerificationActivity.class));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean checkIsLicenseExpired() {
        boolean updateLicense = false;
        WhoAmIApiResponseModel whoAmIApiResponseModel = UserDetailPreferenceManager.getWhoAmIResponse();

        if (whoAmIApiResponseModel != null &&
                whoAmIApiResponseModel.getUser_detail() != null &&
                whoAmIApiResponseModel.getUser_detail().getData() != null &&
                whoAmIApiResponseModel.getUser_detail().getData().getLicenses() != null) {

            List<LicensesBean> licensesBeanList = whoAmIApiResponseModel.getUser_detail().getData().getLicenses();

            if (licensesBeanList.isEmpty()) {
                updateLicense = true;

            } else {

                for (int i = 0; i < licensesBeanList.size(); i++) {

                    if (!Utils.isDateExpired(licensesBeanList.get(i).getEnd_date())) {
                        updateLicense = true;
                        break;
                    }
                }
            }
        }
        Log.d("updateLicense", "" + updateLicense);
        return updateLicense;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestID.REQ_CONTENT_VIEW && resultCode == Activity.RESULT_OK) {
            showMedicalHistory();
        }

        if (requestCode == RequestID.REQ_LICENSE_EXPIRED && resultCode == Activity.RESULT_OK) {
            showLicenUpdate();
        }

        if (requestCode == PermissionConstants.PERMISSION_CAM_MIC) {
            attachView();
        }

    }

    private void showLicenUpdate() {
        Bundle bundle = new Bundle();
        bundle.putInt(ArgumentKeys.VIEW_TYPE, ArgumentKeys.LICENCE_UPDATE);
        bundle.putBoolean(ArgumentKeys.DISABLE_BACk, true);

        startActivity(new Intent(this, ProfileSettingsActivity.class).putExtras(bundle));

        isCheckLicense = false;
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

    @Override
    public void onUpdate() {
        if (getCurrentFragment() instanceof DoCurrentTransactionInterface) {
            ((DoCurrentTransactionInterface) getCurrentFragment()).doCurrentTransaction();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
    }

    private void checkGoogleFitData() {

    }

}
