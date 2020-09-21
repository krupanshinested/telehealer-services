package com.thealer.telehealer.views.home;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.home.orders.AssociationListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 28,November,2018
 */
public class SelectAssociationFragment extends BaseFragment implements OnListItemSelectInterface {

    private EditText searchEt;
    private CustomRecyclerView associationRv;

    private ChangeTitleInterface changeTitleInterface;
    private AttachObserverInterface attachObserverInterface;
    private AssociationApiViewModel associationApiViewModel;
    private GetDoctorsApiViewModel getDoctorsApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;
    private AssociationListAdapter associationListAdapter;
    private List<CommonUserApiResponseModel> commonUserApiResponseModelList = new ArrayList<>();
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private OnCloseActionInterface onCloseActionInterface;
    private GetDoctorsApiResponseModel getDoctorsApiResponseModel;
    private List<GetDoctorsApiResponseModel.DataBean> doctorsDataList = new ArrayList<>();
    private int page = 1;
    private boolean isFromHome;
    private String selectionType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);
        getDoctorsApiViewModel = ViewModelProviders.of(this).get(GetDoctorsApiViewModel.class);

        attachObserverInterface.attachObserver(associationApiViewModel);
        attachObserverInterface.attachObserver(getDoctorsApiViewModel);

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;

                    associationRv.setTotalCount(associationApiResponseModel.getCount());
                    associationRv.setScrollable(true);
                    associationRv.hideProgressBar();

                    associationListAdapter.setCommonUserApiResponseModelList(associationApiResponseModel.getResult(), page);
                }
            }
        });

        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    getDoctorsApiResponseModel = (GetDoctorsApiResponseModel) baseApiResponseModel;

                    associationRv.setTotalCount(getDoctorsApiResponseModel.getTotal_count());
                    associationRv.setScrollable(true);
                    associationRv.hideProgressBar();

                    associationListAdapter.setDoctorsApiResponseModel(getDoctorsApiResponseModel.getData(), page);

                }
            }
        });

        associationApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    associationRv.setScrollable(true);
                    associationRv.hideProgressBar();
                }
            }
        });

        getDoctorsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    associationRv.setScrollable(false);
                    associationRv.hideProgressBar();
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_association, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        associationRv = (CustomRecyclerView) view.findViewById(R.id.association_rv);


        associationRv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                associationRv.setScrollable(false);
                page = page + 1;
                associationRv.showProgressBar();

                if (selectionType.equals(ArgumentKeys.SEARCH_DOCTOR)) {
                    getSpecialist(searchEt.getText().toString(), false);
                } else {
                    getAssociationList(false);
                }
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
                page = 1;
                if (selectionType.equals(ArgumentKeys.SEARCH_DOCTOR)) {
                    if (s.toString().isEmpty()) {
                        getSpecialist(null, false);
                    } else {
                        getSpecialist(s.toString(), false);
                    }
                } else {
                    getAssociationList(false);
                }
            }
        });

        associationApiResponseModel = new AssociationApiResponseModel();

        if (getArguments() != null) {

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }

            selectionType = getArguments().getString(ArgumentKeys.SEARCH_TYPE);

            if (selectionType != null) {

                switch (selectionType) {
                    case ArgumentKeys.SEARCH_COPY_TO:
                        changeTitleInterface.onTitleChange(getString(R.string.copy_to));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_SPECIALIST);
                        searchEt.setHint(getString(R.string.search_doctors));
                        getSpecialist(null, true);
                        break;
                    case ArgumentKeys.SEARCH_DOCTOR:
                        changeTitleInterface.onTitleChange(getString(R.string.specialist));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_SPECIALIST);
                        searchEt.setHint(getString(R.string.search_doctors));
                        getSpecialist(null, true);
                        break;
                    case ArgumentKeys.SEARCH_ASSOCIATION:
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_SEARCH);
                        changeTitleInterface.onTitleChange(getString(R.string.patient_name));
                        getAssociationList(true);
                        break;
                }

            }

        }

        associationListAdapter = new AssociationListAdapter(getActivity(),
                commonUserApiResponseModelList,
                this,
                doctorsDataList,
                selectionType);

        associationRv.getRecyclerView().setAdapter(associationListAdapter);

    }

    private void getSpecialist(String name, boolean isShowProgress) {
        getDoctorsApiViewModel.getDoctorsDetailList(page, name, isShowProgress);
    }

    private void getAssociationList(boolean isShowProgress) {
        associationApiViewModel.getAssociationList(page, isShowProgress);
    }

    @Override
    public void onListItemSelected(int position, Bundle bundle) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtras(bundle));
        onCloseActionInterface.onClose(false);
    }
}
