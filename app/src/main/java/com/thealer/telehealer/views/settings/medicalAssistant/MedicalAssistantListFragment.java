package com.thealer.telehealer.views.settings.medicalAssistant;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.home.AddConnectionActivity;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

/**
 * Created by Aswin on 11,February,2019
 */
public class MedicalAssistantListFragment extends BaseFragment {
    private CustomRecyclerView medicalAssistantCrv;
    private FloatingActionButton addFab;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private DoctorPatientListAdapter doctorPatientListAdapter;
    private int page = 1;
    private boolean isApiRequested = false;

    private AssociationApiViewModel associationApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private AssociationApiResponseModel associationApiResponseModel;
    private OnCloseActionInterface onCloseActionInterface;
    private SearchCellView searchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();

        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);
        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                    if (page == 1) {
                        toolbarTitle.setText(Utils.getPaginatedTitle(getString(R.string.medical_assistant), associationApiResponseModel.getCount()));
                    }
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
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        medicalAssistantCrv = (CustomRecyclerView) view.findViewById(R.id.medical_assistant_crv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        searchView = view.findViewById(R.id.search_view);

        searchView.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                page = 1;
                getMAList(true);
            }
        });

        searchView.setSearchHint(getString(R.string.search_ma));
        toolbarTitle.setText(getString(R.string.medical_assistant));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddConnectionActivity.class).putExtra(ArgumentKeys.IS_MEDICAL_ASSISTANT, true));
            }
        });

        medicalAssistantCrv.setEmptyState(EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN);

        doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity(), false, true,getArguments(), null);

        medicalAssistantCrv.getRecyclerView().setAdapter(doctorPatientListAdapter);

        medicalAssistantCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                medicalAssistantCrv.setScrollable(false);
                page = page + 1;
                getMAList(false);
            }
        });

        medicalAssistantCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMAList(true);
            }
        });

        medicalAssistantCrv.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

    }

    private void getMAList(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            associationApiViewModel.getAssociationList(searchView.getCurrentSearchResult(), page, null, isShowProgress, true);
        }
    }

    private void updateAdapter() {

        if (doctorPatientListAdapter != null) {

            medicalAssistantCrv.setNextPage(associationApiResponseModel.getNext());

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
