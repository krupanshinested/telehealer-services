package com.thealer.telehealer.views.home;

import android.annotation.SuppressLint;

import flavor.GoogleFit.VitalsListWithGoogleFitFragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.OpenTok.CallRequest;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.associationlist.UpdateAssociationRequestModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestLoginApiResponseModel;
import com.thealer.telehealer.apilayer.models.guestviewmodel.GuestloginViewModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiResponseModel;
import com.thealer.telehealer.apilayer.models.whoami.WhoAmIApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.GetUserDetails;
import com.thealer.telehealer.common.OnUpdateListener;
import com.thealer.telehealer.common.OpenTok.CallManager;
import com.thealer.telehealer.common.OpenTok.OpenTokConstants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.PatientInvite;
import com.thealer.telehealer.common.pubNub.models.Patientinfo;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CallPlacingActivity;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.common.SuccessViewInterface;
import com.thealer.telehealer.views.guestlogin.GuestLoginActivity;
import com.thealer.telehealer.views.guestlogin.screens.GuestLoginScreensActivity;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Aswin on 14,November,2018
 */
public class DoctorPatientDetailViewFragment extends BaseFragment implements View.OnClickListener {

    private AppBarLayout appbarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView toolbarSearch;
    private ImageView userProfileIv;
    private ImageView genderIv;
    private ImageView appPlatform;
    private TextView appVersion;
    private TextView userNameTv;
    private TextView userDobTv;
    private RelativeLayout collapseBackgroundRl;
    private TabLayout userDetailTab;
    private ViewPager viewPager;
    private ImageView backIv;
    private Button actionBtn;
    private ViewPagerAdapter viewPagerAdapter;
    private FloatingActionButton addFab;

    private GetUsersApiViewModel getUsersApiViewModel;
    private CommonUserApiResponseModel resultBean, doctorModel;
    private OnCloseActionInterface onCloseActionInterface;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private OnActionCompleteInterface onActionCompleteInterface;
    private AttachObserverInterface attachObserverInterface;
    private AssociationApiViewModel associationApiViewModel;
    private GuestloginViewModel guestloginViewModel;
    private GuestLoginApiResponseModel guestLoginApiResponseModel;

    private boolean isNotWantToAddCard;

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private AppBarLayout userDetailAppbarLayout;
    private TextView nextTv;
    private LinearLayout bottomView;
    private BottomNavigationView userDetailBnv;
    private String view_type;
    private String doctorGuid = null, userGuid = null;
    private boolean isUserDataFetched = false;
    private ImageView favoriteIv, searchIV, hasCardIV;
    private CircleImageView statusCiv;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private PatientInvite patientInvite;

    private final int aboutTab = 0, visitTab = 1, schedulesTab = 2, patientTab = 3,
            orderTab = 4, monitorTab = 5, vitalTab = 6, documentTab = 7;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();

        getUsersApiViewModel = new ViewModelProvider(this).get(GetUsersApiViewModel.class);
        getUsersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                CommonUserApiResponseModel model = (CommonUserApiResponseModel) baseApiResponseModel;
                resultBean = model;
                if (doctorGuid != null) {
                    Set<String> set = new HashSet<>();
                    set.add(doctorGuid);
                    getUserDetail(set);
                } else {
                    updateView(resultBean);
                }
            }
        });
        addConnectionApiViewModel = new ViewModelProvider(this).get(AddConnectionApiViewModel.class);
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

        guestloginViewModel = new ViewModelProvider(this).get(GuestloginViewModel.class);
        guestloginViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof GuestLoginApiResponseModel) {

                        guestLoginApiResponseModel = (GuestLoginApiResponseModel) baseApiResponseModel;
                        if (guestLoginApiResponseModel.isSuccess()) {
                            if (!AppPaymentCardUtils.hasValidPaymentCard(guestLoginApiResponseModel.getPayment_account_info()) && !isNotWantToAddCard) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, false);
                                bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                                bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, "");
                                bundle.putBoolean(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
                                LocalBroadcastManager
                                        .getInstance(getActivity())
                                        .sendBroadcast(new Intent(getString(R.string.success_broadcast_receiver))
                                                .putExtras(bundle));
                                AppPaymentCardUtils.handleCardCasesFromPaymentInfo(DoctorPatientDetailViewFragment.this, guestLoginApiResponseModel.getPayment_account_info(), null);
                            } else {
                                callSuccessDialogBroadcast();
                                Patientinfo patientDetails = new Patientinfo(UserDetailPreferenceManager.getPhone(), UserDetailPreferenceManager.getEmail(), "", UserDetailPreferenceManager.getUserDisplayName(), UserDetailPreferenceManager.getUser_guid(), guestLoginApiResponseModel.getApiKey(), guestLoginApiResponseModel.getSessionId(), guestLoginApiResponseModel.getToken(), false);
                                patientDetails.setHasValidCard(AppPaymentCardUtils.hasValidPaymentCard(guestLoginApiResponseModel.getPayment_account_info()));
                                patientInvite = new PatientInvite();
                                patientInvite.setPatientinfo(patientDetails);
                                patientInvite.setDoctorDetails(guestLoginApiResponseModel.getDoctor_details());
                                patientInvite.setApiKey(guestLoginApiResponseModel.getApiKey());
                                patientInvite.setToken(guestLoginApiResponseModel.getToken());
                            }

                        } else {
                            showToast(guestLoginApiResponseModel.getMessage());
                        }

                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.failure), Toast.LENGTH_SHORT).show();
                }
            }
        });

        guestloginViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        Log.d("ErrorModel", "whoAmIApiViewModel");
                        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
                        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, false);
                        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.failure));
                        intent.putExtra(Constants.SUCCESS_VIEW_DESCRIPTION, errorModel.getMessage());
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
    public void onResume() {
        super.onResume();

        if (CallManager.shared.isActiveCallPresent()) {
            Log.d("enableOrDisableCall", "false");
            enableOrDisableCall(false);
        } else {
            Log.d("enableOrDisableCall", "true");
            enableOrDisableCall(true);
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
        toolbarSearch = (ImageView) toolbar.findViewById(R.id.toolbar_search_iv);
        userProfileIv = (ImageView) view.findViewById(R.id.user_profile_iv);
        genderIv = (ImageView) view.findViewById(R.id.gender_iv);
        appPlatform = (ImageView) view.findViewById(R.id.platform_iv);
        appVersion = (TextView) view.findViewById(R.id.version_tv);
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
        searchIV = (ImageView) view.findViewById(R.id.search_iv);
        statusCiv = (CircleImageView) view.findViewById(R.id.status_civ);
        hasCardIV = (ImageView) view.findViewById(R.id.card_iv);

        userDetailBnv.getMenu().findItem(R.id.menu_call).setVisible(false);

        userDetailBnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Bundle bundle = getArguments();
                switch (menuItem.getItemId()) {
                    case R.id.menu_chat:
                        startActivity(new Intent(getActivity(), ChatActivity.class).putExtras(bundle));
                        break;
                    case R.id.menu_schedules:

                        if (UserDetailPreferenceManager.getRole().equals(Constants.ROLE_PATIENT) &&
                                resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {

                            if (resultBean.getAppt_requests()) {
                                startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class).putExtras(getArguments()));
                            } else {

                                Utils.showAlertDialog(getActivity(), getString(R.string.no_new_appointment), String.format(getString(R.string.appointment_not_allowed_create)), resultBean.getOfficePhoneNo(), getString(R.string.ok)
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse("tel:" + resultBean.getOfficePhoneNo());
                                                startActivity(new Intent(Intent.ACTION_DIAL, uri));
                                            }
                                        }, null);
                            }

                        } else {
                            startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class).putExtras(getArguments()));
                        }

                        break;
                    case R.id.menu_call:
                        if (CallManager.shared.isActiveCallPresent()) {
                            return false;
                        }

                        CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);

                        if (commonUserApiResponseModel == null) {
                            commonUserApiResponseModel = resultBean;
                        }

                        ArrayList<String> callTypes = new ArrayList<>();
                        if (resultBean.getApp_details() != null) {
                            if (resultBean.isAvailable() && !(resultBean.getApp_details().isWebUser())) {
                                callTypes.add(getString(R.string.audio_call));
                                callTypes.add(getString(R.string.video_call));
                            }
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

                                // TODO: 3/2/21 check for patient's credit card is valid or not
                                if (UserType.isUserDoctor()) {
                                    if (UserDetailPreferenceManager.getWhoAmIResponse().isPatient_credit_card_required()) {
                                        showWithoutCardOptions(finalCommonUserApiResponseModel, callType);
                                        return;
                                    }
                                } else if (UserType.isUserAssistant()) {
                                    if (doctorModel.isPatient_credit_card_required()) {
                                        showWithoutCardOptions(finalCommonUserApiResponseModel, callType);
                                        return;
                                    }
                                }
                                startCall(finalCommonUserApiResponseModel, callType);

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
                    case R.id.menu_wating_room:
                        enterWaitingRoom();
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
        }

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    toolbarTitle.setVisibility(View.VISIBLE);
                    collapseBackgroundRl.setVisibility(View.INVISIBLE);
                    if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_DOCTOR) && viewPager.getCurrentItem() == 1) {
                        toolbarSearch.setVisibility(View.VISIBLE);
                    } else
                        toolbarSearch.setVisibility(View.GONE);

                } else if (verticalOffset == 0) {
                    // Expanded
                    toolbarTitle.setVisibility(View.GONE);
                    toolbarSearch.setVisibility(View.GONE);
                    collapseBackgroundRl.setVisibility(View.VISIBLE);
                    if (UserType.isUserAssistant() && resultBean != null && viewPager.getCurrentItem() == 1) {
                        if (resultBean.getRole().equals(Constants.ROLE_DOCTOR))
                            searchIV.setVisibility(View.VISIBLE);
                    } else
                        searchIV.setVisibility(View.GONE);
                } else {
                    // Somewhere in between
                    toolbarTitle.setVisibility(View.GONE);
                    toolbarSearch.setVisibility(View.GONE);
                    collapseBackgroundRl.setVisibility(View.VISIBLE);
                }
            }
        });

        if (getArguments() != null) {

            view_type = getArguments().getString(Constants.VIEW_TYPE);

            if (getArguments().getSerializable(Constants.USER_DETAIL) != null &&
                    getArguments().getSerializable(Constants.USER_DETAIL) instanceof CommonUserApiResponseModel) {

                userGuid = ((CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL)).getUser_guid();
                if (getArguments().getSerializable(Constants.DOCTOR_DETAIL) != null)
                    doctorGuid = ((CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL)).getUser_guid();
            } else {
                userGuid = getArguments().getString(ArgumentKeys.USER_GUID);
                doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            }
            getUsersApiViewModel.getUserDetail(userGuid, null);
        }

        backIv = (ImageView) view.findViewById(R.id.back_iv);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });
    }

    private void showWithoutCardOptions(CommonUserApiResponseModel finalCommonUserApiResponseModel, String callType) {
        ArrayList<String> options = new ArrayList<>(Arrays.asList(getString(R.string.lbl_continue_anyway), getString(R.string.lbl_ask_to_add_credit_card)));
        new ItemPickerDialog(getContext(), getString(R.string.msg_card_not_added_by_patient), options, new PickerListener() {
            @Override
            public void didSelectedItem(int position) {
                switch (position) {
                    case 0:
                        startCall(finalCommonUserApiResponseModel, callType);
                        break;
                    case 1:
                        // TODO: 3/2/21 call api to send add credit card notification.
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void didCancelled() {

            }
        }).show();
    }

    private void startCall(CommonUserApiResponseModel finalCommonUserApiResponseModel, String callType) {
        String doctorGuid = null, doctorName = null;
        if (doctorModel != null) {
            doctorGuid = doctorModel.getUser_guid();
            doctorName = doctorModel.getUserDisplay_name();
        }

        CallRequest callRequest = new CallRequest(UUID.randomUUID().toString(),
                finalCommonUserApiResponseModel.getUser_guid(), finalCommonUserApiResponseModel, doctorGuid, doctorName, null, callType, true, null);

        Intent intent = new Intent(getActivity(), CallPlacingActivity.class);
        intent.putExtra(ArgumentKeys.CALL_INITIATE_MODEL, callRequest);
        startActivity(intent);

    }

    private void enterWaitingRoom() {
        Utils.showAlertDialog(getActivity(), getString(R.string.waiting_room), getString(R.string.guest_login_bottom_title), getString(R.string.proceed), getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        proceedForWaitingRoom();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private void proceedForWaitingRoom() {
        showDialog();
        guestloginViewModel.registerUserEnterWatingRoom(resultBean.getUser_guid());
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
                        updateView(resultBean);
                    }
                }
            });
        }
    }

    private BroadcastReceiver callStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("enableOrDisable_brod", "false");
            enableOrDisableCall(false);
        }
    };

    private BroadcastReceiver callEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("enableOrDisable_brod", "false");
            enableOrDisableCall(true);
        }
    };

    private void enableOrDisableCall(Boolean isEnabled) {
        Log.d("isEnabled", "" + isEnabled);
        if (isEnabled) {
            userDetailBnv.getMenu().findItem(R.id.menu_call).setEnabled(true);
            //userDetailBnv.getMenu().findItem(R.id.menu_call).set(1);
        } else {
            userDetailBnv.getMenu().findItem(R.id.menu_call).setEnabled(false);
            //userDetailBnv.findViewById(R.id.menu_call).setAlpha(0.5f);
        }

    }

    private void updateUserStatus(CommonUserApiResponseModel userApiResponseModel) {
        if (userApiResponseModel.getRole().equals(Constants.ROLE_PATIENT)) {
            Log.d("updateUserStatus", "VISIBLE");
            userDetailBnv.getMenu().findItem(R.id.menu_call).setVisible(true);
        } else {
            Log.d("updateUserStatus", "GONE");
            userDetailBnv.getMenu().findItem(R.id.menu_call).setVisible(false);
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

            if (resultBean.getRole().equals(Constants.ROLE_PATIENT) && resultBean.getApp_details() != null) {
                appPlatform.setVisibility(View.VISIBLE);
                appVersion.setVisibility(View.VISIBLE);
                appVersion.setText(getString(R.string.version_label) + resultBean.getApp_details().getVersion());
                Utils.setPlatformImage(getActivity(), appPlatform, resultBean.getApp_details().displayPlatform());
            } else {
                appPlatform.setVisibility(View.GONE);
                appVersion.setVisibility(View.GONE);
            }

            if (getArguments() != null) {
                getArguments().putSerializable(Constants.USER_DETAIL, resultBean);
            }

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
                    userDetailTab.setTabMode(TabLayout.MODE_SCROLLABLE);
                    break;
                case Constants.ROLE_DOCTOR:
                    genderIv.setVisibility(View.GONE);
                    if (UserType.isUserAssistant()) {
                        userDetailTab.setTabMode(TabLayout.MODE_SCROLLABLE);
                    }
                    if (UserType.isUserPatient()) {
                        userDetailBnv.getMenu().findItem(R.id.menu_wating_room).setVisible(true);
                    }
                    break;
                case Constants.ROLE_ASSISTANT:
                    if (UserType.isUserPatient()) {
                        userDetailTab.setVisibility(View.GONE);
                        userDetailBnv.getMenu().findItem(R.id.menu_call).setVisible(false);
                        userDetailBnv.getMenu().findItem(R.id.menu_schedules).setVisible(false);
                    } else {
                        userDetailTab.setVisibility(View.GONE);
                    }
                    break;
            }

            if ((UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_DOCTOR)) || (UserType.isUserDoctor() && resultBean.getRole().equals(Constants.ROLE_ASSISTANT))) {
                userDetailBnv.setVisibility(View.GONE);
            } else if (resultBean.getConnection_status() != null && resultBean.getConnection_status().equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
                userDetailBnv.setVisibility(View.VISIBLE);
            } else if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                userDetailBnv.setVisibility(View.VISIBLE);
            } else {
                userDetailBnv.setVisibility(View.GONE);
            }


            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }

            fragmentList = new ArrayList<>();
            titleList = new ArrayList<String>();

            ArrayList<Integer> tabs = new ArrayList<>();

            if (view_type != null) {
                if (view_type.equals(Constants.VIEW_CONNECTION)) {
                    userDetailTab.setVisibility(View.GONE);
                    userDetailBnv.setVisibility(View.GONE);
                    tabs.add(aboutTab);
                    updateDateConnectionStatus(resultBean.getConnection_status());

                } else if (view_type.equals(Constants.VIEW_ASSOCIATION_DETAIL)) {

                    switch (resultBean.getRole()) {

                        case Constants.ROLE_PATIENT:
                            if (UserType.isUserDoctor()) {
                                tabs.add(aboutTab);
                                tabs.add(vitalTab);
                                tabs.add(documentTab);
                                tabs.add(visitTab);
                                tabs.add(orderTab);
                            } else if (UserType.isUserAssistant()) {
                                tabs.add(aboutTab);
                                tabs.add(vitalTab);
                                tabs.add(documentTab);
                                tabs.add(visitTab);
                                tabs.add(orderTab);
                            }
                            break;

                        case Constants.ROLE_DOCTOR:
                            if (UserType.isUserAssistant()) {
                                tabs.add(schedulesTab);
                                tabs.add(patientTab);
                                tabs.add(visitTab);
                                tabs.add(orderTab);
                                tabs.add(monitorTab);
                            } else if (UserType.isUserPatient()) {
                                tabs.add(aboutTab);
                                tabs.add(visitTab);
                                tabs.add(orderTab);
                            }
                            break;

                        case Constants.ROLE_ASSISTANT:
                            if (UserType.isUserPatient()) {
                                tabs.add(visitTab);
                            } else if (UserType.isUserDoctor()) {
                                tabs.add(aboutTab);
                            }
                            break;
                    }
                }

                Bundle bundle;
                for (Integer tab : tabs) {
                    switch (tab) {
                        case aboutTab:
                            AboutFragment aboutFragment = new AboutFragment();
                            addFragment(getString(R.string.about), aboutFragment);
                            break;
                        case vitalTab:
                            VitalsListFragment vitalsFragment = new VitalsListWithGoogleFitFragment();
                            addFragment(getString(R.string.vitals), vitalsFragment);
                            break;
                        case schedulesTab:
                            bundle = new Bundle();
                            bundle.putBoolean(ArgumentKeys.HIDE_ADD, true);
                            bundle.putBoolean(ArgumentKeys.HIDE_SEARCH, true);

                            SchedulesListFragment schedulesListFragment = new SchedulesListFragment();
                            schedulesListFragment.setArguments(bundle);
                            addFragment(getString(R.string.schedules), schedulesListFragment);
                            break;
                        case patientTab:
                            bundle = new Bundle();
                            bundle.putBoolean(ArgumentKeys.HIDE_SEARCH, true);
                            bundle.putBoolean(ArgumentKeys.SHOW_FAB_ADD, false);
                            DoctorPatientListingFragment doctorPatientListingFragment = new DoctorPatientListingFragment();
                            doctorPatientListingFragment.setArguments(bundle);
                            addFragment(getString(R.string.patients), doctorPatientListingFragment);
                            break;
                        case documentTab:
                            DocumentListFragment documentListFragment = new DocumentListFragment();
                            bundle = new Bundle();
                            bundle.putBoolean(ArgumentKeys.IS_HIDE_TOOLBAR, true);
                            documentListFragment.setArguments(bundle);
                            addFragment(getString(R.string.documents), documentListFragment);
                            break;
                        case visitTab:
                            bundle = new Bundle();
                            bundle.putBoolean(ArgumentKeys.HIDE_SEARCH, true);
                            RecentFragment recentFragment = new RecentFragment();
                            recentFragment.setArguments(bundle);
                            addFragment(getString(R.string.visits), recentFragment);
                            break;
                        case orderTab:
                            OrdersListFragment ordersFragment = new OrdersListFragment();
                            addFragment(getString(R.string.orders), ordersFragment);
                            break;
                        case monitorTab:
                            MonitoringFragment monitoringFragment = new MonitoringFragment();
                            addFragment(getString(R.string.monitoring), monitoringFragment);
                            break;
                    }
                }
            }
            viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
            viewPager.setAdapter(viewPagerAdapter);
            searchIV.setOnClickListener(this);
            toolbarSearch.setOnClickListener(this);
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
            if (Constants.ROLE_PATIENT.equals(resultBean.getRole())) {
                AppPaymentCardUtils.setCardStatusImage(hasCardIV, resultBean.getPayment_account_info());
            } else
                hasCardIV.setVisibility(View.GONE);
        }
    }

    private void showAssociationSelection(int requestCode, String searchType, String user_guid) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(ArgumentKeys.SEARCH_TYPE, searchType);
        bundle.putString(ArgumentKeys.DOCTOR_GUID, user_guid);
        bundle.putBoolean(ArgumentKeys.IS_SHOW_TOOLBAR, true);
        bundle.putBoolean(ArgumentKeys.IS_CLOSE_NEEDED, false);
        bundle.putString(ArgumentKeys.USER_NAME, resultBean.getUserDisplay_name());

        SelectAssociationFragment selectAssociationFragment = new SelectAssociationFragment();
        selectAssociationFragment.setArguments(bundle);
        selectAssociationFragment.setTargetFragment(this, requestCode);
        showSubFragmentInterface.onShowFragment(selectAssociationFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode", "" + requestCode);
        switch (requestCode) {

            case RequestID.REQ_SELECT_PATIENT:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                        bundle.putSerializable(Constants.USER_DETAIL, data.getSerializableExtra(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL));
                        doctorPatientDetailViewFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(doctorPatientDetailViewFragment);
                    }
                }
                break;

            case PermissionConstants.PERMISSION_CAM_MIC:
            case RequestID.REQ_SHOW_SUCCESS_VIEW:
                if (resultCode == RESULT_OK) {
                    goToWaitingScreen();
                }
                break;
            case RequestID.REQ_CARD_INFO:
            case RequestID.REQ_CARD_EXPIRE:
                if (!UserType.isUserAssistant()) {
                    if (resultCode == Activity.RESULT_OK) {
                        AppPaymentCardUtils.startPaymentIntent(getActivity(), UserType.isUserDoctor());
                    } else {
                        if (UserType.isUserPatient()) {
                            isNotWantToAddCard = true;
                            proceedForWaitingRoom();
                        }
                    }
                }
                break;


        }
    }

    private void updateFavorites(String user_guid, boolean isFavorite) {
        associationApiViewModel.updateAssociationDetail(user_guid, new UpdateAssociationRequestModel(isFavorite), false);
    }

    private void updateDateConnectionStatus(String connection_status) {
        Log.e(TAG, "updateDateConnectionStatus: " + connection_status + " " + view_type);

        if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
            actionBtn.setVisibility(View.GONE);
            return;
        }

        if (UserType.isUserPatient() && resultBean.getRole().equals(Constants.ROLE_ASSISTANT)) {
            actionBtn.setVisibility(View.GONE);
            return;
        }

        if (connection_status == null) {
            connection_status = "";
        }

        switch (connection_status) {
            case Constants.CONNECTION_STATUS_ACCEPTED:
                actionBtn.setVisibility(View.GONE);
                break;
            case Constants.CONNECTION_STATUS_OPEN:
                if (resultBean.getRole().equals(Constants.ROLE_DOCTOR) && !resultBean.getConnection_requests()) {
                    actionBtn.setVisibility(View.GONE);
                } else {
                    actionBtn.setText(getString(R.string.add_connection_pending));
                    actionBtn.setEnabled(false);
                }
                break;
            default:
                if (resultBean.getRole().equals(Constants.ROLE_DOCTOR) && !resultBean.getConnection_requests()) {
                    actionBtn.setVisibility(View.GONE);
                } else {
                    actionBtn.setText(getString(R.string.add_connection_connect));
                    actionBtn.setEnabled(true);
                    if (UserType.isUserPatient() && resultBean.getRole().equals(Constants.ROLE_ASSISTANT)) {
                        actionBtn.setVisibility(View.GONE);
                    }
                    if (UserType.isUserAssistant() && resultBean.getRole().equals(Constants.ROLE_PATIENT)) {
                        actionBtn.setVisibility(View.GONE);
                    }
                }
        }

        if (view_type.equals(Constants.VIEW_CONNECTION) && !connection_status.equals(Constants.CONNECTION_STATUS_ACCEPTED)) {
            userDetailTab.setVisibility(View.GONE);

            if (resultBean.getRole().equals(Constants.ROLE_DOCTOR) && !resultBean.getConnection_requests()) {

            } else {
                actionBtn.setVisibility(View.VISIBLE);
            }

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

    @Override
    public void onClick(View v) {
        showAssociationSelection(RequestID.REQ_SELECT_PATIENT, ArgumentKeys.SEARCH_ASSOCIATION, resultBean.getUser_guid());
    }

    private void showDialog() {
        Bundle succesBundle = new Bundle();
        succesBundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.please_wait));
        succesBundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.validating_your_waiting_room));

        SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
        successViewDialogFragment.setArguments(succesBundle);
        successViewDialogFragment.setTargetFragment(this, RequestID.REQ_SHOW_SUCCESS_VIEW);
        successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
    }

    private void callSuccessDialogBroadcast() {
        Intent intent = new Intent(getString(R.string.success_broadcast_receiver));
        intent.putExtra(Constants.SUCCESS_VIEW_STATUS, true);
        intent.putExtra(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
        intent.putExtra(Constants.SUCCESS_VIEW_AUTO_DISMISS, true);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void goToWaitingScreen() {
        if (PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_CAM_MIC)) {
            Intent i = new Intent(getActivity(), GuestLoginScreensActivity.class);
            i.putExtra(ArgumentKeys.GUEST_INFO, patientInvite);
            i.putExtra(ArgumentKeys.GUEST_SCREENTYPE, ArgumentKeys.WAITING_SCREEN);
            startActivity(i);
        }
    }

}
