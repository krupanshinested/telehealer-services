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
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.home.DesignationListAdapter;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.listener.DismissListener;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

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
    int selectedposition=0;
    List<String> physicianList=new ArrayList<>();
    private View clickView=null;

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
        Bundle inviteBundle=new Bundle();
        switch (v.getId()) {
            case R.id.patient_cb:
                clickView=v;
                managePatient();
                break;
            case R.id.sa_cb:
                inviteBundle.putString(ArgumentKeys.ROLE, Constants.ROLE_ASSISTANT);
                inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE,false);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
            case R.id.provider_cb:
                inviteBundle.putString(ArgumentKeys.ROLE,Constants.ROLE_DOCTOR);
                inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE,false);
                Utils.showInviteAlert(getActivity(), inviteBundle);
                break;
        }
    }

    private void managePatient() {
        if(UserType.isUserAssistant()){
            displayProviderListDailog(true);
        }else{
            Bundle inviteBundle=new Bundle();
            inviteBundle.putString(ArgumentKeys.ROLE,Constants.ROLE_PATIENT);
            inviteBundle.putBoolean(ArgumentKeys.IS_INVITED_VISIBLE,false);
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

        if(isItemsPresent && page==1){
            List<CommonUserApiResponseModel> doctorList = associationApiResponseModel.getResult();

            for(int i=0;i<doctorList.size();i++){
                physicianList.add(doctorList.get(i).getDisplayName());
            }
            selectPhysician();
        }

    }


    //Allow physician to view list of support staff. Also physician can request to add them.
    private void selectPhysician() {
        LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
        View layoutInflateView=layoutInflater.inflate
                (R.layout.designation_alert,(ViewGroup)clickView.findViewById(R.id.cl_root));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(layoutInflateView);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        TextView headerTitle=layoutInflateView.findViewById(R.id.header_title);
        RecyclerView rvDesignation=layoutInflateView.findViewById(R.id.rv_designation);
        rvDesignation.setLayoutManager(new LinearLayoutManager(getActivity()));
        Button btnYes=layoutInflateView.findViewById(R.id.btn_yes);
        TextView noRecordFound=layoutInflateView.findViewById(R.id.no_record_found);
        Button btnCancel=layoutInflateView.findViewById(R.id.btn_cancel);
        View viewDevider=layoutInflateView.findViewById(R.id.view_devider);

        if(physicianList.size()==0) {
            rvDesignation.setVisibility(View.GONE);
            noRecordFound.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.GONE);
            viewDevider.setVisibility(View.GONE);
        } else{
            rvDesignation.setVisibility(View.VISIBLE);
            noRecordFound.setVisibility(View.GONE);
            btnYes.setVisibility(View.VISIBLE);
            viewDevider.setVisibility(View.VISIBLE);
        }
        DesignationListAdapter designationListAdapter=new DesignationListAdapter(getActivity(),physicianList);
        rvDesignation.setAdapter(designationListAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(designationListAdapter!=null) {
                    String designation = designationListAdapter.getSpecialistInfo();
                    Log.e(TAG, "onClick: "+designation );
                }
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}