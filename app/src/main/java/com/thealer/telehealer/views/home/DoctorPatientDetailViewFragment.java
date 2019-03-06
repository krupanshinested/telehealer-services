package com.thealer.telehealer.views.home;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallInitiateModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiResponseModel;
import com.thealer.telehealer.apilayer.models.userStatus.ConnectionStatusApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.OpenTok.TokBox;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.CreateOrderActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.home.recents.RecentFragment;
import com.thealer.telehealer.views.home.schedules.CreateNewScheduleActivity;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;
import com.thealer.telehealer.views.inviteUser.InviteUserActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private CommonUserApiResponseModel resultBean;
    private OnCloseActionInterface onCloseActionInterface;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private OnActionCompleteInterface onActionCompleteInterface;
    private GetUsersApiViewModel getUsersApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private ConnectionStatusApiViewModel connectionStatusApiViewModel;
    private ConnectionStatusApiResponseModel connectionStatusApiResponseModel;

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private AppBarLayout userDetailAppbarLayout;
    private TextView nextTv;
    private LinearLayout bottomView;
    private BottomNavigationView userDetailBnv;
    private String view_type;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();

        getUsersApiViewModel = ViewModelProviders.of(this).get(GetUsersApiViewModel.class);
        addConnectionApiViewModel = ViewModelProviders.of(getActivity()).get(AddConnectionApiViewModel.class);
        connectionStatusApiViewModel = ViewModelProviders.of(this).get(ConnectionStatusApiViewModel.class);

        attachObserverInterface.attachObserver(getUsersApiViewModel);
        attachObserverInterface.attachObserver(connectionStatusApiViewModel);

        connectionStatusApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    connectionStatusApiResponseModel = (ConnectionStatusApiResponseModel) baseApiResponseModel;
                    if (connectionStatusApiResponseModel.getConnection_status() == null) {
                        view_type = Constants.VIEW_CONNECTION;
                    }
                    updateView(resultBean);
                    updateDateConnectionStatus(connectionStatusApiResponseModel.getConnection_status());
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

        getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

                    if (commonUserApiResponseModels.size() > 0) {
                        if (resultBean == null) {
                            resultBean = commonUserApiResponseModels.get(0);
                            updateView(resultBean);
                        }

                        updateUserStatus(commonUserApiResponseModels.get(0));
                    }

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

        userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.GONE);

        if (TokBox.shared.isActiveCallPreset()) {
            enableOrDisableCall(false);
        } else {
            enableOrDisableCall(true);
        }


        if (!UserType.isUserPatient()) {
            userDetailTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        userDetailBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
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
                        callTypes.add(getString(R.string.audio_call));
                        callTypes.add(getString(R.string.video_call));
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

                                CallInitiateModel callInitiateModel = new CallInitiateModel(finalCommonUserApiResponseModel.getUser_guid(), finalCommonUserApiResponseModel, null, null, null, callType);

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
                        Bundle bundle = new Bundle();
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
                    startActivity(new Intent(getActivity(), InviteUserActivity.class).putExtras(getArguments()));
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

            if (getArguments().getSerializable(Constants.USER_DETAIL) != null &&
                    getArguments().getSerializable(Constants.USER_DETAIL) instanceof CommonUserApiResponseModel) {

                resultBean = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                updateView(resultBean);

                if (resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                    userDetailBnv.getMenu().findItem(R.id.menu_upload).setVisible(true);
                }

                if (getArguments().getBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, false)) {
                    connectionStatusApiViewModel.getConnectionStatus(resultBean.getUser_guid(), true);
                } else {

                    if (resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                        Set<String> set = new HashSet<>();
                        set.add(resultBean.getUser_guid());
                        getUsersApiViewModel.getUserByGuid(set);
                    } else {
                        updateUserStatus(resultBean);
                    }
                }

            } else {
                String userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                Set<String> set = new HashSet<>();
                set.add(userGuid);
                getUsersApiViewModel.getUserByGuid(set);
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
        if (userApiResponseModel.isAvailable() && userApiResponseModel.getRole().equals(Constants.ROLE_PATIENT)) {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.VISIBLE);
        } else {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.GONE);
        }

        userDobTv.setCompoundDrawablesWithIntrinsicBounds(userApiResponseModel.getStatusColorCode(), 0, 0, 0);
    }

    private void updateView(CommonUserApiResponseModel resultBean) {
        toolbarTitle.setText(resultBean.getUserDisplay_name());
        userNameTv.setText(resultBean.getUserDisplay_name());

        if (resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
            genderIv.setVisibility(View.GONE);
        }
        userDobTv.setText(resultBean.getDisplayInfo());

        Utils.setGenderImage(getActivity(), genderIv, resultBean.getGender());
        Utils.setImageWithGlide(getActivity().getApplicationContext(), userProfileIv, resultBean.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true);

        fragmentList = new ArrayList<>();
        titleList = new ArrayList<String>();


        AboutFragment aboutFragment = new AboutFragment();
        addFragment(getString(R.string.about), aboutFragment);

        if (view_type != null) {
            if (view_type.equals(Constants.VIEW_CONNECTION)) {

                userDetailTab.setVisibility(View.GONE);
                actionBtn.setVisibility(View.VISIBLE);
                userDetailBnv.setVisibility(View.GONE);

                updateDateConnectionStatus(resultBean.getConnection_status());

                actionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrate(getActivity());
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.ADD_CONNECTION_ID, resultBean.getUser_id());
                        bundle.putString(ArgumentKeys.USER_GUID, resultBean.getUser_guid());
                        bundle.putSerializable(Constants.USER_DETAIL, resultBean);

                        onActionCompleteInterface.onCompletionResult(null, true, bundle);

                    }
                });

                userDetailTab.setVisibility(View.GONE);

            } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {


                if (!UserType.isUserPatient()) {
                    VitalsListFragment vitalsFragment = new VitalsListFragment();
                    addFragment(getString(R.string.vitals), vitalsFragment);
                }

                if (resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                    DocumentListFragment documentListFragment = new DocumentListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, true);
                    documentListFragment.setArguments(bundle);
                    addFragment(getString(R.string.documents), documentListFragment);
                }

                RecentFragment recentFragment = new RecentFragment();
                addFragment(getString(R.string.recents), recentFragment);

                OrdersListFragment ordersFragment = new OrdersListFragment();
                addFragment(getString(R.string.orders), ordersFragment);
            }

            viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
            viewPager.setAdapter(viewPagerAdapter);
            userDetailTab.setupWithViewPager(viewPager);
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

        }
    }

    private void updateDateConnectionStatus(String connection_status) {
        Log.e("aswin", "updateDateConnectionStatus: " + connection_status);
        if (connection_status == null) {
            actionBtn.setText(getString(R.string.add_connection_connect));
            actionBtn.setEnabled(true);
        } else {
            if (connection_status.equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
                actionBtn.setVisibility(View.GONE);
            } else if (connection_status.equals(Constants.CONNECTION_STATUS_OPEN)) {
                actionBtn.setText(getString(R.string.add_connection_pending));
                actionBtn.setEnabled(false);
            } else {
                actionBtn.setText(getString(R.string.add_connection_connect));
                actionBtn.setEnabled(true);
            }
        }
    }

    private void addFragment(String title, Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        bundle.putString(Constants.VIEW_TYPE, view_type);
        fragment.setArguments(bundle);
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
