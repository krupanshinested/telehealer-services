package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;

/**
 * Created by Aswin on 13,November,2018
 */
public class DoctorPatientListingFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView doctorPatientListRv;
    private AssociationApiViewModel associationApiViewModel;
    private int page = 1, totalCount = 0;
    private AttachObserverInterface attachObserverInterface;
    private DoctorPatientListAdapter doctorPatientListAdapter;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private CustomRecyclerView doctorPatientListCrv;
    private boolean isApiRequested = false;
    private FloatingActionButton addFab;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    AssociationApiResponseModel associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;

                    if (associationApiResponseModel.getResult().size() > 0) {
                        showProposer();
                    }
                    if (doctorPatientListAdapter != null) {
                        totalCount = associationApiResponseModel.getCount();

                        doctorPatientListCrv.setTotalCount(totalCount);

                        doctorPatientListAdapter.setData(associationApiResponseModel.getResult(), page);

                        if (associationApiResponseModel.getResult().size() > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.USER_DETAIL, associationApiResponseModel.getResult().get(0));
                            onOrientationChangeInterface.onDataReceived(bundle);
                        }
                        associationApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

                    }
                }
                isApiRequested = false;
                doctorPatientListCrv.setScrollable(true);
                doctorPatientListCrv.hideProgressBar();
            }
        });

    }

    private void showProposer() {
        PermissionChecker.with(getActivity()).checkPermission(PermissionConstants.PERMISSION_CAM_MIC);
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

        addFab.setOnClickListener(this);

        if (UserType.isUserPatient() || UserType.isUserAssistant()) {
            doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_WITH_BTN);
        } else if (UserType.isUserDoctor()) {
            doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_WITH_BTN);
        }

        doctorPatientListRv = doctorPatientListCrv.getRecyclerView();

        LinearLayoutManager linearLayoutManager = doctorPatientListCrv.getLayoutManager();

        doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity());

        doctorPatientListRv.setAdapter(doctorPatientListAdapter);

        doctorPatientListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getAssociationsList();
                isApiRequested = true;
                doctorPatientListCrv.setScrollable(false);
            }
        });

        getAssociationsList();

    }

    private void getAssociationsList() {
        if (!isApiRequested) {
            associationApiViewModel.getAssociationList(page, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fab:
                if (!UserType.isUserDoctor()){
                    startActivity(new Intent(getActivity(), AddConnectionActivity.class));
                }
                break;
        }
    }
}
