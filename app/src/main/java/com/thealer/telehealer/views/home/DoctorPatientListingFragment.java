package com.thealer.telehealer.views.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.audio.AudioCapabilitiesReceiver;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
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
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ContentActivity;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;

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
    private View bottomView;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OnCloseActionInterface onCloseActionInterface;
    private boolean isDietView, isResumed;
    private String doctorGuid = null;
    private AssociationApiResponseModel associationApiResponseModel;
    private ArrayList<DoctorGroupedAssociations> doctorGroupedAssociations;
    private ChangeTitleInterface changeTitleInterface;
    @Nullable
    private SearchCellView search_view;

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) context;

        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);

        attachObserverInterface.attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                doctorGroupedAssociations = new ArrayList(baseApiResponseModels);

                didReceivedResult();
            }
        });

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                doctorPatientListCrv.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {

                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                    }


                    didReceivedResult();
                }
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
        bottomView = (View) view.findViewById(R.id.bottom_view);
        search_view = view.findViewById(R.id.search_view);

        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);

        addFab.setOnClickListener(this);

        if (UserType.isUserPatient()){
            search_view.setSearchHint(getString(R.string.search_doctors));
        }
        else {
            search_view.setSearchHint(getString(R.string.search_associations));
        }

        search_view.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                getAssociationsList(true);
            }
        });

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
                search_view.setVisibility(View.GONE);
            }
            if (!getArguments().getBoolean(ArgumentKeys.SHOW_FAB_ADD, true)){
                addFab.setVisibility(View.GONE);
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
                getAssociationsList( false);
                isApiRequested = true;
                doctorPatientListCrv.setScrollable(false);
            }
        });

        doctorPatientListCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getAssociationsList( false);
            }
        });

        doctorPatientListCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAssociationsList( true);
            }
        });

        doctorPatientListCrv.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isApiRequested = false;

        if (isVisibleToUser && isResumed) {
            getAssociationsList( true);
        }
    }

    private void didReceivedResult() {
        if (page == 1) {
            String title;
            if (UserType.isUserPatient() || UserType.isUserAssistant()) {
                title = getString(R.string.Doctors);
            } else {
                title = getString(R.string.Patients);
            }

            if (associationApiResponseModel != null) {
                changeTitleInterface.onTitleChange(Utils.getPaginatedTitle(title, associationApiResponseModel.getCount()));
            } else {
                changeTitleInterface.onTitleChange(title);
            }

        }

        boolean isItemsPresent = false;
        if (associationApiResponseModel != null) {
            isItemsPresent = associationApiResponseModel.getResult().size() != 0;
        } else {
            isItemsPresent = doctorGroupedAssociations.size() != 0;
        }

        if (!isItemsPresent) {

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


            if (isItemsPresent) {
                doctorPatientListCrv.showOrhideEmptyState(false);
            }

            if (associationApiResponseModel != null) {
                doctorPatientListCrv.setNextPage(associationApiResponseModel.getNext());
                doctorPatientListAdapter.setData(associationApiResponseModel.getResult(), page);
            } else {
                doctorPatientListCrv.setNextPage(null);
                doctorPatientListAdapter.setData(doctorGroupedAssociations);
            }

            if (isItemsPresent) {
                CommonUserApiResponseModel firstObject = null;
                if (associationApiResponseModel != null && associationApiResponseModel.getResult().size() > 0) {
                    firstObject = associationApiResponseModel.getResult().get(0);
                } else if (doctorGroupedAssociations != null && doctorGroupedAssociations.size() > 0) {
                    firstObject = doctorGroupedAssociations.get(0).getDoctors().get(0);
                }

                if (firstObject != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.USER_DETAIL, firstObject);
                    onOrientationChangeInterface.onDataReceived(bundle);
                }
            }
            associationApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

        }

        if (UserType.isUserAssistant() && associationApiResponseModel != null && !associationApiResponseModel.getResult().isEmpty()) {
            List<String> doctorGuidList = new ArrayList<>();
            for (int i = 0; i < associationApiResponseModel.getResult().size(); i++) {
                if (!doctorGuidList.contains(associationApiResponseModel.getResult().get(i).getUser_guid())) {
                    doctorGuidList.add(associationApiResponseModel.getResult().get(i).getUser_guid());
                }
            }
            String doctorGuids = doctorGuidList.toString().replace("[", "").replace("]", "").trim();
            appPreference.setString(PreferenceConstants.ASSOCIATION_GUID_LIST, doctorGuids);

        }
        isApiRequested = false;
        doctorPatientListCrv.setScrollable(true);
        doctorPatientListCrv.hideProgressBar();
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

    private void getAssociationsList(boolean isShowProgress) {
        if (!isApiRequested) {
            doctorPatientListCrv.setScrollable(true);
            doctorPatientListCrv.showOrhideEmptyState(false);
            if (UserType.isUserPatient()) {
                associationApiViewModel.getDoctorGroupedAssociations(isShowProgress);
            } else {
                associationApiViewModel.getAssociationList(search_view.getCurrentSearchResult(), page, doctorGuid, isShowProgress, false);
            }
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
        getAssociationsList( false);
    }
}
