package com.thealer.telehealer.views.home;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
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

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private AppBarLayout userDetailAppbarLayout;
    private TextView nextTv;
    private LinearLayout bottomView;
    private BottomNavigationView userDetailBnv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        getUsersApiViewModel = ViewModelProviders.of(this).get(GetUsersApiViewModel.class);
        attachObserverInterface.attachObserver(getUsersApiViewModel);
        addConnectionApiViewModel = ViewModelProviders.of(getActivity()).get(AddConnectionApiViewModel.class);
        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        actionBtn.setText(getString(R.string.add_connection_pending));
                        actionBtn.setEnabled(false);
                    }
                }
            }
        });

        getUsersApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;
                    resultBean = commonUserApiResponseModels.get(0);
                    updateView(resultBean);
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

        if (UserType.isUserPatient()) {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.GONE);
        } else {
            userDetailBnv.findViewById(R.id.menu_call).setVisibility(View.VISIBLE);
        }

        userDetailBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_schedules:
                        startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class).putExtras(getArguments()));
                        break;
                    case R.id.menu_call:
                        CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                        ArrayList<String> callTypes = new ArrayList<>();
                        callTypes.add(getString(R.string.audio_call));
                        callTypes.add(getString(R.string.video_call));
                        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_call_type), callTypes, new PickerListener() {
                            @Override
                            public void didSelectedItem(int position) {

                                CallInitiateModel callInitiateModel = new CallInitiateModel(commonUserApiResponseModel.getUser_guid(), commonUserApiResponseModel, null, null, null, position == 0 ? OpenTokConstants.audio : OpenTokConstants.video);

                                Intent intent = new Intent(getActivity(), CallPlacingActivity.class);
                                intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL,callInitiateModel);
                                startActivity(intent);

                            }

                            @Override
                            public void didCancelled() {

                            }
                        });
                        itemPickerDialog.setCancelable(false);
                        itemPickerDialog.show();

                        break;
                }
                return true;
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
            if (getArguments().getSerializable(Constants.USER_DETAIL) != null &&
                    getArguments().getSerializable(Constants.USER_DETAIL) instanceof CommonUserApiResponseModel) {

                resultBean = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                updateView(resultBean);

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

    private void updateView(CommonUserApiResponseModel resultBean) {
        toolbarTitle.setText(resultBean.getUserDisplay_name());
        userNameTv.setText(resultBean.getUserDisplay_name());

        if (resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
            userDobTv.setText(resultBean.getDoctorSpecialist());
            genderIv.setVisibility(View.GONE);
        } else {
            userDobTv.setText(resultBean.getDob());
        }

        Utils.setGenderImage(getActivity(), genderIv, resultBean.getGender());
        Utils.setImageWithGlide(getActivity().getApplicationContext(), userProfileIv, resultBean.getUser_avatar(), getActivity().getDrawable(R.drawable.profile_placeholder), true);


        fragmentList = new ArrayList<>();
        titleList = new ArrayList<String>();

        AboutFragment aboutFragment = new AboutFragment();
        addFragment(getString(R.string.about), aboutFragment);


        String view_type = getArguments().getString(Constants.VIEW_TYPE);
        if (view_type != null) {
            if (view_type.equals(Constants.VIEW_CONNECTION)) {

                userDetailTab.setVisibility(View.GONE);
                actionBtn.setVisibility(View.VISIBLE);
                userDetailBnv.setVisibility(View.GONE);

                if (resultBean.getConnection_status() != null) {
                    actionBtn.setText(getString(R.string.add_connection_pending));
                    actionBtn.setEnabled(false);
                } else {
                    actionBtn.setText(getString(R.string.add_connection_connect));
                    actionBtn.setEnabled(true);
                }

                        actionBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.vibrate(getActivity());
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.ADD_CONNECTION_ID, String.valueOf(resultBean.getUser_id()));
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

    private void addFragment(String title, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.USER_DETAIL, resultBean);
        fragment.setArguments(bundle);
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
