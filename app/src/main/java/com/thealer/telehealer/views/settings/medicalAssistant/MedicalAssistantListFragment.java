package com.thealer.telehealer.views.settings.medicalAssistant;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.AddConnectionActivity;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

/**
 * Created by Aswin on 11,February,2019
 */
public class MedicalAssistantListFragment extends BaseFragment {
    private CustomRecyclerView medicalAssistantCrv;
    private FloatingActionButton addFab;

    private DoctorPatientListAdapter doctorPatientListAdapter;
    private int page = 1, totalCount = 0;
    private boolean isApiRequested = false;

    private AssociationApiViewModel associationApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AssociationApiResponseModel associationApiResponseModel;
    private ChangeTitleInterface changeTitleInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();

        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                    updateAdapter();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_assistant_listing, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        medicalAssistantCrv = (CustomRecyclerView) view.findViewById(R.id.medical_assistant_crv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddConnectionActivity.class).putExtra(ArgumentKeys.IS_MEDICAL_ASSISTANT, true));
            }
        });

        medicalAssistantCrv.setEmptyState(EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN);

        doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity());

        medicalAssistantCrv.getRecyclerView().setAdapter(doctorPatientListAdapter);

        medicalAssistantCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                medicalAssistantCrv.setScrollable(false);
                page = page + 1;
                getMAList(false);
            }
        });
    }

    private void getMAList(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            associationApiViewModel.getAssociationList(null, page, isShowProgress, true);
        }
    }

    private void updateAdapter() {

        if (doctorPatientListAdapter != null) {

            totalCount = associationApiResponseModel.getCount();

            medicalAssistantCrv.setTotalCount(totalCount);

            if (associationApiResponseModel.getResult().size() > 0) {
                medicalAssistantCrv.showOrhideEmptyState(false);
            }

            doctorPatientListAdapter.setData(associationApiResponseModel.getResult(), page);

            medicalAssistantCrv.setScrollable(true);
            isApiRequested = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMAList(true);
    }
}
