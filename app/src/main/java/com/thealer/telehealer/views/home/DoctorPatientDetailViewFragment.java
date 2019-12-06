package com.thealer.telehealer.views.home;

import android.annotation.SuppressLint;

import Flavor.GoogleFit.VitalsListWithGoogleFitFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.associationlist.UpdateAssociationRequestModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.OnUpdateListener;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.chat.ChatActivity;
import com.thealer.telehealer.views.home.monitoring.MonitoringFragment;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.home.recents.RecentFragment;
import com.thealer.telehealer.views.home.schedules.CreateNewScheduleActivity;
import com.thealer.telehealer.views.home.schedules.SchedulesListFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 14,November,2018
 */
public class DoctorPatientDetailViewFragment extends BaseFragment {

    private AppBarLayout appbarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView userProfileIv;
    private ImageView genderIv;
    private TextView userNameTv;
    private TextView userDobTv;
    private RelativeLayout collapseBackgroundRl;
    private TabLayout userDetailTab;
    private ViewPager viewPager;
    private ImageView backIv;
    private Button actionBtn;
    private ViewPagerAdapter viewPagerAdapter;
    private FloatingActionButton addFab;

    private CommonUserApiResponseModel resultBean, doctorModel;
    private OnCloseActionInterface onCloseActionInterface;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private OnActionCompleteInterface onActionCompleteInterface;
    private AttachObserverInterface attachObserverInterface;
    private ConnectionStatusApiViewModel connectionStatusApiViewModel;
    private ConnectionStatusApiResponseModel connectionStatusApiResponseModel;
    private AssociationApiViewModel associationApiViewModel;

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private AppBarLayout userDetailAppbarLayout;
    private TextView nextTv;
    private LinearLayout bottomView;
    private BottomNavigationView userDetailBnv;
    private String view_type;
    private String doctorGuid = null, userGuid = null;
    private boolean isConnectionStatusChecked = false;
    private boolean checkStatus, isUserDataFetched = false;
    private ImageView favoriteIv;
    private CircleImageView statusCiv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();

        addConnectionApiViewModel = new ViewModelProvider(getActivity()).get(AddConnectionApiViewModel.class);
        connectionStatusApiViewModel = new ViewModelProvider(this).get(ConnectionStatusApiViewModel.class);

        attachObserverInterface.attachObserver(connectionStatusApiViewModel);

        connectionStatusApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    isConnectionStatusChecked = true;
                    connectionStatusApiResponseModel = (ConnectionStatusApiResponseModel) baseApiResponseModel;
                    if (connectionStatusApiResponseModel.getConnection_status() == null || !connectionStatusApiResponseModel.getConnection_status().equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
                        view_type = Constants.VIEW_CONNECTION;
                    } else {
                        view_type = Constants.VIEW_ASSOCIATION_DETAIL;
                    }
                    updateView(resultBean);
                    updateDateConnectionStatus(connectionStatusApiResponseModel.getConnection_status());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.CONNECTION_STATUS_RECEIVER).putExtra(ArgumentKeys.CONNECTION_STATUS, connectionStatusApiResponseModel.getConnection_status()));
                }
            }
        });

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        actionBtn.setText(getString(R.string.add_connection_pending));
                        actionBtn.setEnabled(false);
                    }

                    addConnectionApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);
                }
            }
        });

        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> responseModelArrayList = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;
                    for (CommonUserApiResponseModel model : responseModelArrayList) {
                        if (model.getRole().equals(Constants.ROLE_PATIENT)) {
                            resultBean = model;
                        }
                    }

                    updateView(resultBean);
                    updateDateConnectionStatus(resultBean.getConnection_status());
                }
            }
        });

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null && baseApiResponseModel.isSuccess()) {
                    resultBean.setFavorite(!favoriteIv.isSelected());
                    favoriteIv.setSelected(resultBean.getFavorite());
                    if (getActivity() instanceof OnUpdateListener)
                        ((OnUpdateListener) getActivity()).onUpdate();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_patient_detail, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(callStartReceiver, new IntentFilter(Constants.CALL_STARTED_BROADCAST));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(callEndReceiver, new IntentFilter(Constants.CALL_ENDED_BROADCAST));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(callEndReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(callStartReceiver);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        userProfileIv = (ImageView) view.findViewById(R.id.user_profile_iv);
        genderIv = (ImageView) view.findViewById(R.id.gender_iv);
        userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
        userDobTv = (TextView) view.findViewById(R.id.user_dob_tv);
        collapseBackgroundRl = (RelativeLayout) view.findViewById(R.id.collapse_background_rl);
        userDetailTab = (TabLayout) view.findViewById(R.id.user_detail_tab);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        actionBtn = (Button) view.findViewById(R.id.action_btn);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        userDetailAppbarLayout = (AppBarLayout) view.findViewById(R.id.user_detail_appbar_layout);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        bottomView = (LinearLayout) view.findViewById(R.id.bottom_view);
        userDetailBnv = (BottomNavigationView) view.findViewById(R.id.user_detail_bnv);
        favoriteIv = (ImageView) view.findViewById(R.id.favorite_iv);
        statusCiv = (CircleImageView) view.findViewById(R.id.status_civ);

        userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.GONE);

        if (TokBox.shared.isActiveCallPreset()) {
            enableOrDisableCall(false);
        } else {
            enableOrDisableCall(true);
        }

        userDetailBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Bundle bundle = getArguments();
                switch (menuItem.getItemId()) {
                    case R.id.menu_chat:
                        startActivity(new Intent(getActivity(), ChatActivity.class).putExtras(bundle));
                        break;
                    case R.id.menu_schedules:
                        startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class).putExtras(getArguments()));
                        break;
                    case R.id.menu_call:
                        if (TokBox.shared.isActiveCallPreset()) {
                            return false;
                        }

                        CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                        if (commonUserApiResponseModel == null) {
                            commonUserApiResponseModel = resultBean;
                        }

                        ArrayList<String> callTypes = new ArrayList<>();
                        if (resultBean.isAvailable()) {
                            callTypes.add(getString(R.string.audio_call));
                            callTypes.add(getString(R.string.video_call));
                        }
                        callTypes.add(getString(R.string.one_way_call));
                        CommonUserApiResponseModel finalCommonUserApiResponseModel = commonUserApiResponseModel;
                        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_call_type), callTypes, new PickerListener() {
                            @Override
                            public void didSelectedItem(int position) {

                                String callType;
                                switch (position) {
                                    case 0:
                                        callType = OpenTokConstants.audio;
                                        break;
                                    case 1:
                                        callType = OpenTokConstants.video;
                                        break;
                                    default:
                                        callType = OpenTokConstants.oneWay;
                                        break;
                                }

                                String doctorGuid = null, doctorName = null;
                                if (doctorModel != null) {
                                    doctorGuid = doctorModel.getUser_guid();
                                    doctorName = doctorModel.getUserDisplay_name();
                                }

                                CallInitiateModel callInitiateModel = new CallInitiateModel(finalCommonUserApiResponseModel.getUser_guid(), finalCommonUserApiResponseModel, doctorGuid, doctorName, null, callType);

                                Intent intent = new Intent(getActivity(), CallPlacingActivity.class);
                                intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callInitiateModel);
                                startActivity(intent);

                            }

                            @Override
                            public void didCancelled() {

                            }
                        });
                        itemPickerDialog.setCancelable(false);
                        itemPickerDialog.show();

                        break;
                    case R.id.menu_upload:
                        if (bundle == null)
                            bundle = new Bundle();

                        bundle.putString(Constants.SELECTED_ITEM, OrderConstant.ORDER_DOCUMENTS);
                        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
                        startActivity(new Intent(getActivity(), CreateOrderActivity.class).putExtras(bundle));
                        break;
                }
                return false;
            }
        });

        if (UserType.isUserAssistant()) {
            addFab.show();
            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showInviteAlert(getActivity(), getArguments());
                }
            });

            userDetailBnv.setVisibility(View.GONE);
        } else {
            userDetailBnv.setVisibility(View.VISIBLE);
        }

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    toolbarTitle.setVisibility(View.VISIBLE);
                    collapseBackgroundRl.setVisibility(View.INVISIBLE);

                } else if (verticalOffset == 0) {
                    // Expanded
                    toolbarTitle.setVisibility(View.GONE);
                    collapseBackgroundRl.setVisibility(View.VISIBLE);
                } else {
                    // Somewhere in between
                    toolbarTitle.setVisibility(View.GONE);
                    collapseBackgroundRl.setVisibility(View.VISIBLE);
                }
            }
        });

        if (getArguments() != null) {

            view_type = getArguments().getString(Constants.VIEW_TYPE);
            checkStatus = getArguments().getBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, false);

            if (getArguments().getSerializable(Constants.USER_DETAIL) != null &&
                    getArguments().getSerializable(Constants.USER_DETAIL) instanceof CommonUserApiResponseModel) {

                resultBean = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

                updateView(resultBean);

                if (checkStatus) {
                    checkConnectionStatus();
                } else {
                    if (resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
//                        Set<String> set = new HashSet<>();
//                        set.add(resultBean.getUser_guid());
                        Log.e(TAG, "initView: need to get user detail");
//                        getUserDetail(set);
                    }
                }

            } else {
                userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);

                Set<String> set = new HashSet<>();
                if (doctorGuid != null) {
                    set.add(doctorGuid);
                }
                set.add(userGuid);
                getUserDetail(set);
            }
        }

        backIv = (ImageView) view.findViewById(R.id.back_iv);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });
    }

    private void getUserDetail(Set<String> guidSet) {
        if (!isUserDataFetched) {
            GetUserDetails
                    .getInstance(getActivity())
                    .getDetails(guidSet)
                    .getHashMapMutableLiveData().observe(this, new Observer<HashMap<String, CommonUserApiResponseModel>>() {
                @Override
                public void onChanged(@Nullable HashMap<String, CommonUserApiResponseModel> userDetailHashMap) {
                    if (userDetailHashMap != null) {
                        isUserDataFetched = true;

                        for (String guid : guidSet) {
                            CommonUserApiResponseModel model = userDetailHashMap.get(guid);
                            if (model != null) {
                                switch (model.getRole()) {
                                    case Constants.ROLE_DOCTOR:
                                        doctorModel = model;
                                        if (UserType.isUserPatient() && resultBean == null) {
                                            resultBean = doctorModel;
                                        }
                                        break;
                                    case Constants.ROLE_PATIENT:
                                    case Constants.ROLE_ASSISTANT:
                                        resultBean = model;
                                        break;
                                }
                            }
                        }
                        if (checkStatus) {
                            if (!UserType.isUserPatient()) {
                                associationApiViewModel.getUserAssociationDetail(userGuid, doctorGuid, true);
                            } else {
                                checkConnectionStatus();
                            }
                        } else {
                            updateView(resultBean);
                        }
                    }
                }
            });
        }
    }

    private void checkConnectionStatus() {
        if (!isConnectionStatusChecked) {
            connectionStatusApiViewModel.getConnectionStatus(userGuid, doctorGuid, true);
        }
    }

    private BroadcastReceiver callStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enableOrDisableCall(false);
        }
    };

    private BroadcastReceiver callEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enableOrDisableCall(true);
        }
    };

    private void enableOrDisableCall(Boolean isEnabled) {
        if (isEnabled) {
            userDetailBnv.findViewById(R.id.menu_call).setEnabled(true);
            userDetailBnv.findViewById(R.id.menu_call).setAlpha(1);
        } else {
            userDetailBnv.findViewById(R.id.menu_call).setEnabled(false);
            userDetailBnv.findViewById(R.id.menu_call).setAlpha(0.5f);
        }

    }

    private void updateUserStatus(CommonUserApiResponseModel userApiResponseModel) {
        if (userApiResponseModel.getRole().equals(Constants.ROLE_PATIENT)) {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.VISIBLE);
        } else {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.GONE);
        }

        Log.e(TAG, "updateUserStatus: " + userApiResponseModel.isAvailable());
        if (!userApiResponseModel.isAvailable()) {
            Utils.greyoutProfile(userProfileIv);
        } else {
            Utils.removeGreyoutProfile(userProfileIv);
        }

        statusCiv.setImageDrawable(new ColorDrawable(getActivity().getColor(userApiResponseModel.getStatusColorCode())));
    }

    private void updateView(CommonUserApiResponseModel resultBean) {
        if (UserType.isUserAssistant()) {
            if (resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
                addFab.show();
            } else {
                addFab.hide();
            }
        }
        if (resultBean != null) {
            userGuid = resultBean.getUser_guid();
            toolbarTitle.setText(resultBean.getUserDisplay_name());
            userNameTv.setText(resultBean.getUserDisplay_name());
            userDobTv.setText(resultBean.getDisplayInfo());
            Utils.setGenderImage(getActivity(), genderIv, resultBean.getGender());
            Utils.setImageWithGlide(getActivity().getApplicationContext().getApplicationContext(), userProfileIv, resultBean.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true, true);

            updateUserStatus(resultBean);
            if (getArguments().getBoolean(ArgumentKeys.SHOW_FAVORITES, false) && resultBean.getFavorite() != null) {
                favoriteIv.setSelected(resultBean.getFavorite());
                favoriteIv.setVisibility(View.VISIBLE);
                favoriteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateFavorites(resultBean.getUser_guid(), !favoriteIv.isSelected());
                    }
                });
            } else {
                favoriteIv.setVisibility(View.GONE);
            }

            if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                favoriteIv.setVisibility(View.GONE);
            }

            switch (resultBean.getRole()) {
                case Constants.ROLE_PATIENT:
                    userDetailBnv.getMenu().findItem(R.id.menu_upload).setVisible(true);
                    userDetailBnv.setVisibility(View.VISIBLE);
                    userDetailTab.setTabMode(TabLayout.MODE_SCROLLABLE);
                    break;
                case Constants.ROLE_DOCTOR:
                    genderIv.setVisibility(View.GONE);
                    if (UserType.isUserAssistant()) {
                        userDetailTab.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }
                    break;
                case Constants.ROLE_ASSISTANT:
                    userDetailBnv.setVisibility(View.GONE);
                    userDetailTab.setVisibility(View.GONE);
                    break;
            }

            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }


            fragmentList = new ArrayList<>();
            titleList = new ArrayList<String>();

            AboutFragment aboutFragment = new AboutFragment();
            addFragment(getString(R.string.about), aboutFragment);

            if (view_type != null) {
                if (view_type.equals(Constants.VIEW_CONNECTION)) {

                    updateDateConnectionStatus(resultBean.getConnection_status());

                } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {
                    Bundle bundle;

                    if (!UserType.isUserPatient()) {
                        VitalsListFragment vitalsFragment = new VitalsListWithGoogleFitFragment();
                        addFragment(getString(R.string.vitals), vitalsFragment);
                    }

                    if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
                        fragmentList.clear();
                        titleList.clear();

                        bundle = new Bundle();
                        bundle.putBoolean(ArgumentKeys.HIDE_ADD, true);

                        SchedulesListFragment schedulesListFragment = new SchedulesListFragment();
                        schedulesListFragment.setArguments(bundle);
                        addFragment(getString(R.string.schedules), schedulesListFragment);

                        bundle.putBoolean(ArgumentKeys.HIDE_SEARCH, true);

                        DoctorPatientListingFragment doctorPatientListingFragment = new DoctorPatientListingFragment();
                        doctorPatientListingFragment.setArguments(bundle);
                        addFragment(getString(R.string.patients), doctorPatientListingFragment);
                    }

                    if (resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                        DocumentListFragment documentListFragment = new DocumentListFragment();
                        bundle = new Bundle();
                        bundle.putBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, true);
                        documentListFragment.setArguments(bundle);
                        addFragment(getString(R.string.documents), documentListFragment);
                    }

                    RecentFragment recentFragment = new RecentFragment();
                    addFragment(getString(R.string.visits), recentFragment);

                    OrdersListFragment ordersFragment = new OrdersListFragment();
                    addFragment(getString(R.string.orders), ordersFragment);

                    if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
                        MonitoringFragment monitoringFragment = new MonitoringFragment();
                        addFragment(getString(R.string.monitoring), monitoringFragment);
                    }

                    if (resultBean.getRole().equals(Constants.ROLE_ASSISTANT)) {
                        userDetailTab.setVisibility(View.GONE);
                        userDetailBnv.setVisibility(View.GONE);
                        fragmentList.clear();
                        titleList.clear();
                        addFragment(getString(R.string.about), aboutFragment);
                    }
                }

                viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
                viewPager.setAdapter(viewPagerAdapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        viewPager.setCurrentItem(i);
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                userDetailTab.setupWithViewPager(viewPager);
                userDetailTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

            }
        }
    }

    private void updateFavorites(String user_guid, boolean isFavorite) {
        associationApiViewModel.updateAssociationDetail(user_guid, new UpdateAssociationRequestModel(isFavorite), false);
    }

    private void updateDateConnectionStatus(String connection_status) {
        Log.e(TAG, "updateDateConnectionStatus: " + connection_status + " " + view_type);

        if (connection_status == null) {
            connection_status = "";
        }

        switch (connection_status) {
            case Constants.CONNECTION_STATUS_ACCEPTED:
                actionBtn.setVisibility(View.GONE);
                break;
            case Constants.CONNECTION_STATUS_OPEN:
                actionBtn.setText(getString(R.string.add_connection_pending));
                actionBtn.setEnabled(false);
                break;
            default:
                actionBtn.setText(getString(R.string.add_connection_connect));
                actionBtn.setEnabled(true);
                if (UserType.isUserPatient() && resultBean.getRole().equals(Constants.ROLE_ASSISTANT)) {
                    actionBtn.setVisibility(View.GONE);
                }
        }

        if (view_type.equals(Constants.VIEW_CONNECTION) && !connection_status.equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
            userDetailTab.setVisibility(View.GONE);
            actionBtn.setVisibility(View.VISIBLE);
            userDetailBnv.setVisibility(View.GONE);
            favoriteIv.setVisibility(View.GONE);
            addFab.hide();

            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.vibrate(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ADD_CONNECTION_ID, resultBean.getUser_id());
                    bundle.putString(ArgumentKeys.USER_GUID, resultBean.getUser_guid());
                    bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                    bundle.putSerializable(Constants.USER_DETAIL, resultBean);
                    bundle.putBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, true);
                    bundle.putBoolean(ArgumentKeys.CONNECT_USER, true);

                    onActionCompleteInterface.onCompletionResult(RequestID.REQ_ADD_CONNECTION, true, bundle);

                }
            });
        }
    }

    private void addFragment(String title, Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        if (resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
            bundle.putSerializable(Constants.DOCTOR_DETAIL, resultBean);
        } else {
            bundle.putSerializable(Constants.DOCTOR_DETAIL, doctorModel);
            bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        }

        bundle.putString(Constants.VIEW_TYPE, view_type);

        fragment.setArguments(bundle);

        fragmentList.add(fragment);
        titleList.add(title);
    }
}
