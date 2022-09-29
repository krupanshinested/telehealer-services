package com.thealer.telehealer.views.settings.serviceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.thealer.telehealer.views.home.AddConnectionActivity;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

public class ServiceProviderListFragment extends BaseFragment {

    private OnCloseActionInterface onCloseActionInterface;

    private CustomRecyclerView spAssistantCrv;
    private FloatingActionButton addFab;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private SearchCellView searchView;
    private DoctorPatientListAdapter doctorPatientListAdapter;
    private int page = 1;
    private boolean isApiRequested = false;
    private TextView nextTv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
//        attachObserverInterface = (AttachObserverInterface) getActivity();
//
//        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
//        attachObserverInterface.attachObserver(associationApiViewModel);
//        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
//            @Override
//            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_provider_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        nextTv = (TextView) view.findViewById(R.id.next_tv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        spAssistantCrv = (CustomRecyclerView) view.findViewById(R.id.sp_assistant_crv);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        searchView = view.findViewById(R.id.search_view);

        searchView.setSearchHint(getString(R.string.search_sp));
        toolbarTitle.setText(getString(R.string.my_team));

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        spAssistantCrv.setEmptyState(EmptyViewConstants.EMPTY_MEDICAL_ASSISTANT_WITH_BTN);

        doctorPatientListAdapter = new DoctorPatientListAdapter(getActivity(), false, true,getArguments(), null,true);

        spAssistantCrv.getRecyclerView().setAdapter(doctorPatientListAdapter);

        spAssistantCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                spAssistantCrv.setScrollable(false);
                page = page + 1;
                getSPList(false);
            }
        });

        spAssistantCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSPList(true);
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddServiceProviderActivity.class));
            }
        });

        if (getArguments() != null && getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR, false)) {
            appbarLayout.setVisibility(View.VISIBLE);
            backIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCloseActionInterface.onClose(false);
                }
            });
            nextTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

//        spAssistantCrv.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

    }

    private void getSPList(boolean b) {

    }
}