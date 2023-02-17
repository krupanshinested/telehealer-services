package com.thealer.telehealer.views.inviteUser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getUsers.GetUsersApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.OnItemEndListener;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.home.DesignationListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SendInvitationFragment extends BaseFragment implements View.OnClickListener {

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private ChangeTitleInterface changeTitleInterface;
    private CustomButton patientCb, saCb, providerCb;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;
    private int page = 1;
    private OnActionCompleteInterface onActionCompleteInterface;
    private boolean isApiRequested = false;
    int selectedposition = 0;
    List<String> physicianList = new ArrayList<>();
    private View clickView = null;
    private List<CommonUserApiResponseModel> physicianMasterList = new ArrayList<>();
    private DesignationListAdapter physicianListAdapter;
    private int lastSelectedPosition = 0;
    private GetUsersApiViewModel getUsersApiViewModel;
    private CommonUserApiResponseModel resultBean, doctorModel;

    public SendInvitationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) context;
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        getUsersApiViewModel = new ViewModelProvider(this).get(GetUsersApiViewModel.class);
        attachObserverInterface.attachObserver(getUsersApiViewModel);
        attachObserverInterface.attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                    }


                    didReceivedResult();
                }
            }
        });

        getUsersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                CommonUserApiResponseModel model = (CommonUserApiResponseModel) baseApiResponseModel;
                resultBean = model;
                if (UserType.isUserAssistant()) {
                    if (resultBean.getRole().equals(Constants.ROLE_DOCTOR)) {
                        Constants.finalDoctor = resultBean;
                    }

                    doctorModel = resultBean;

                    if (doctorModel.getPermissions().size() > 0) {
                        Constants.isCallEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.MAKE_CALLS_CODE);
                        Constants.isScheduleEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.SCHEDULING_CODE);
                        Constants.isChatEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.CHAT_CODE);
                        Constants.isVitalsAddEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.ADD_VITALS_CODE);
                        Constants.isVitalsViewEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.VIEW_VITALS_CODE);
                        Constants.isInviteEnable = Utils.checkPermissionStatus(doctorModel.getPermissions(), ArgumentKeys.INVITE_OTHERS_CODE);
                    }

                    if (Constants.isInviteEnable) {
                        showInviteDialog();
                    }else {
                        Utils.displayPermissionMsg(getActivity());
                    }

                }
            }
        });

    }

    private void showInviteDialog() {
        if (physicianListAdapter != null) {
            String designation = physicianListAdapter.getSpecialistInfo();
            lastSelectedPosition = physicianListAdapter.getCurrentSelected();
        }
        Bundle inviteBundle = new Bundle();
        inviteBundle.putString(ArgumentKeys.USER_GUID, physicianMasterList.get(lastSelectedPosition).getUser_guid());
        inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_PATIENT);
        inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE, false);
        Utils.showInviteAlert(getActivity(), inviteBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_invitation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        patientCb = view.findViewById(R.id.patient_cb);
        saCb = view.findViewById(R.id.sa_cb);
        providerCb = view.findViewById(R.id.provider_cb);

        patientCb.setOnClickListener(this);
        saCb.setOnClickListener(this);
        providerCb.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Bundle inviteBundle = new Bundle();
        switch (v.getId()) {
            case R.id.patient_cb:
                clickView = v;
                managePatient();
                break;
            case R.id.sa_cb:
                inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_ASSISTANT);
                inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE, false);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
            case R.id.provider_cb:
                inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_DOCTOR);
                inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE, false);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
        }
    }

    private void managePatient() {
        if (UserType.isUserAssistant()) {
            if (physicianList.size() > 0) {
                selectPhysician();
            } else {
                displayProviderListDailog(true);
            }
        } else {
            Bundle inviteBundle = new Bundle();
            inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_PATIENT);
            inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE, false);
            Utils.showInviteAlert(getActivity(), inviteBundle);
        }
    }

    private void displayProviderListDailog(boolean isShowProgress) {
        associationApiViewModel.getAssociationList("", page, null, isShowProgress, false);
    }

    private void didReceivedResult() {
        boolean isItemsPresent = false;
        if (associationApiResponseModel != null) {
            isItemsPresent = associationApiResponseModel.getResult().size() != 0;
        }

        if (isItemsPresent) {
            physicianMasterList.addAll(associationApiResponseModel.getResult());
            for (int i = 0; i < associationApiResponseModel.getResult().size(); i++) {
                physicianList.add(associationApiResponseModel.getResult().get(i).getDisplayName());
            }
            if (page == 1) {
                selectPhysician();
            } else {
                physicianListAdapter.notifyDataSetChanged();
            }

        }

    }


    //Allow physician to view list of support staff. Also physician can request to add them.
    private void selectPhysician() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View layoutInflateView = layoutInflater.inflate
                (R.layout.designation_alert, (ViewGroup) clickView.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle = layoutInflateView.findViewById(R.id.header_title);
        RecyclerView rvDesignation = layoutInflateView.findViewById(R.id.rv_designation);
        rvDesignation.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button btnYes = layoutInflateView.findViewById(R.id.btn_yes);
        TextView noRecordFound = layoutInflateView.findViewById(R.id.no_record_found);
        Button btnCancel = layoutInflateView.findViewById(R.id.btn_cancel);
        View viewDevider = layoutInflateView.findViewById(R.id.view_devider);
        headerTitle.setText(getString(R.string.str_select_your_physician));

        if (physicianList.size() == 0) {
            rvDesignation.setVisibility(View.GONE);
            noRecordFound.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.GONE);
            viewDevider.setVisibility(View.GONE);
        } else {
            rvDesignation.setVisibility(View.VISIBLE);
            noRecordFound.setVisibility(View.GONE);
            btnYes.setVisibility(View.VISIBLE);
            viewDevider.setVisibility(View.VISIBLE);
        }
        physicianListAdapter = new DesignationListAdapter(getActivity(), physicianList, new OnItemEndListener() {
            @Override
            public void itemEnd(int position) {
                page++;
                displayProviderListDailog(false);
            }
        });
        rvDesignation.setAdapter(physicianListAdapter);

        physicianListAdapter.setCurrentSelected(lastSelectedPosition);
        physicianListAdapter.notifyDataSetChanged();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersApiViewModel.getUserDetail(physicianMasterList.get(lastSelectedPosition).getUser_guid(),false, null);
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}