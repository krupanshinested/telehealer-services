package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 13,November,2018
 */
public class DoctorPatientListingFragment extends BaseFragment implements View.OnClickListener, DoCurrentTransactionInterface {

    private RecyclerView doctorPatientListRv;
    private AssociationApiViewModel associationApiViewModel;
    private int page = 1;
    private AttachObserverInterface attachObserverInterface;
    private DoctorPatientListAdapter doctorPatientListAdapter;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private CustomRecyclerView doctorPatientListCrv;
    private boolean isApiRequested = false;
    private FloatingActionButton addFab;
    private View topView;
    private EditText searchEt;
    private View bottomView;
    private ImageView searchClearIv;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout searchLl;
    private CardView searchCv;
    private OnCloseActionInterface onCloseActionInterface;
    private boolean isDietView, isResumed;
    private String doctorGuid = null;
    private AssociationApiResponseModel associationApiResponseModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();

        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);

        attachObserverInterface.attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                doctorPatientListCrv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {
                    associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;


                    if (associationApiResponseModel.getResult().size() == 0) {

                        doctorPatientListCrv.showOrhideEmptyState(true);

                        if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_ASSOCIATION)) {

                            appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_ASSOCIATION, true);

                            DismissListener dismissListener = new DismissListener() {
                                @Override
                                public void onDismiss(@org.jetbrains.annotations.Nullable String s) {

                                }

                                @Override
                                public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                                }
                            };

                            if (UserType.isUserDoctor()) {
                                Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_PATIENT, dismissListener);
                            } else {
                                Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_DOCTOR, dismissListener);
                            }
                        }
                    }

                    if (doctorPatientListAdapter != null) {

                        doctorPatientListCrv.setNextPage(associationApiResponseModel.getNext());

                        if (associationApiResponseModel.getResult().size() > 0) {
                            doctorPatientListCrv.showOrhideEmptyState(false);
                        }
                        doctorPatientListAdapter.setData(associationApiResponseModel.getResult(), page);

                        if (associationApiResponseModel.getResult().size() > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.USER_DETAIL, associationApiResponseModel.getResult().get(0));
                            onOrientationChangeInterface.onDataReceived(bundle);
                        }
                        associationApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

                    }

                    if (UserType.isUserAssistant() && !associationApiResponseModel.getResult().isEmpty()) {
                        List<String> doctorGuidList = new ArrayList<>();
                        for (int i = 0; i < associationApiResponseModel.getResult().size(); i++) {
                            if (!doctorGuidList.contains(associationApiResponseModel.getResult().get(i).getUser_guid())) {
                                doctorGuidList.add(associationApiResponseModel.getResult().get(i).getUser_guid());
                            }
                        }
                        String doctorGuids = doctorGuidList.toString().replace("[", "").replace("]", "").trim();
                        appPreference.setString(PreferenceConstants.ASSOCIATION_GUID_LIST, doctorGuids);

                    }
                }
                isApiRequested = false;
                doctorPatientListCrv.setScrollable(true);
                doctorPatientListCrv.hideProgressBar();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_patient_listing, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        doctorPatientListCrv = (CustomRecyclerView) view.findViewById(R.id.doctor_patient_list_crv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        topView = (View) view.findViewById(R.id.top_view);
        searchLl = view.findViewById(R.id.search_ll);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);

        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);

        searchClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText(null);
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    searchClearIv.setVisibility(View.VISIBLE);
                    doctorPatientListCrv.setScrollable(false);
                    showSearchList(s.toString().toLowerCase());
                } else {
                    searchClearIv.setVisibility(View.GONE);
                    doctorPatientListCrv.setScrollable(true);
                    if (associationApiResponseModel != null &&
                            associationApiResponseModel.getResult().size() > 0) {
                        doctorPatientListAdapter.setData(associationApiResponseModel.getResult(), page);
                        doctorPatientListCrv.showOrhideEmptyState(false);
                    } else {
                        doctorPatientListCrv.showOrhideEmptyState(true);
                    }
                }
            }
        });

        addFab.setOnClickListener(this);

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.patients));
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
            }
            isDietView = getArguments().getBoolean(ArgumentKeys.IS_DIET_VIEW);

            CommonUserApiResponseModel commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (commonUserApiResponseModel != null) {
                doctorGuid = commonUserApiResponseModel.getUser_guid();
            }
            if (getArguments().getBoolean(ArgumentKeys.HIDE_SEARCH, false)) {
                searchLl.setVisibility(View.GONE);
            }
            if (isDietView || getArguments().getBoolean(ArgumentKeys.HIDE_ADD)) {
                addFab.hide();
            }
        }

        if (UserType.isUserPatient() || UserType.isUserAssistant()) {
            doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_WITH_BTN);
        } else if (UserType.isUserDoctor()) {
            doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_WITH_BTN);
        }

        doctorPatientListRv = doctorPatientListCrv.getRecyclerView();

        doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity(), isDietView, getArguments());

        doctorPatientListRv.setAdapter(doctorPatientListAdapter);

        doctorPatientListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getAssociationsList(null, false);
                isApiRequested = true;
                doctorPatientListCrv.setScrollable(false);
            }
        });

        doctorPatientListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAssociationsList(null, false);
            }
        });

        doctorPatientListCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAssociationsList(null, true);
            }
        });

        doctorPatientListCrv.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isApiRequested = false;

        if (isVisibleToUser && isResumed) {
            getAssociationsList(null, true);
        }
    }

    private void showSearchList(String search) {
        List<CommonUserApiResponseModel> searchList = new ArrayList<>();
        for (CommonUserApiResponseModel usermodel : associationApiResponseModel.getResult()) {
            if (usermodel.getUserDisplay_name().toLowerCase().contains(search)) {
                searchList.add(usermodel);
            }
        }

        if (searchList.size() > 0) {
            doctorPatientListCrv.showOrhideEmptyState(false);
            doctorPatientListAdapter.setData(searchList, 1);
        } else {
            doctorPatientListCrv.showOrhideEmptyState(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }

        if (UserType.isUserPatient()) {
            Boolean isCallPermitted = PermissionChecker.with(getActivity()).isGranted(PermissionConstants.PERMISSION_CAM_MIC);
            if (!appPreference.getBoolean(PreferenceConstants.PATIENT_VIDEO_FEED) && isCallPermitted) {
                appPreference.setBoolean(PreferenceConstants.PATIENT_VIDEO_FEED, true);

                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra(ArgumentKeys.OK_BUTTON_TITLE, getString(R.string.ok));
                intent.putExtra(ArgumentKeys.IS_ATTRIBUTED_DESCRIPTION, false);
                intent.putExtra(ArgumentKeys.RESOURCE_ICON, R.drawable.call_kit_education);
                intent.putExtra(ArgumentKeys.IS_SKIP_NEEDED, false);
                intent.putExtra(ArgumentKeys.TITLE, getString(R.string.enable_video_feed));
                intent.putExtra(ArgumentKeys.DESCRIPTION, getString(R.string.video_feed_description, getString(R.string.organization_name)));
                intent.putExtra(ArgumentKeys.IS_CHECK_BOX_NEEDED, false);
                intent.putExtra(ArgumentKeys.IS_CLOSE_NEEDED, false);

                startActivity(intent);
            }
        }
    }

    private void getAssociationsList(String name, boolean isShowProgress) {
        if (!isApiRequested) {
            doctorPatientListCrv.showOrhideEmptyState(false);
            associationApiViewModel.getAssociationList(name, page, doctorGuid, isShowProgress, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fab:
                if (!UserType.isUserDoctor()) {
                    startActivity(new Intent(getActivity(), AddConnectionActivity.class));
                } else {
                    Utils.showInviteAlert(getActivity(), null);
                }
                break;
        }
    }

    @Override
    public void doCurrentTransaction() {
        getAssociationsList(null, false);
    }
}
