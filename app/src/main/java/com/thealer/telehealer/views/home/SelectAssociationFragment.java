package com.thealer.telehealer.views.home;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
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
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.home.orders.AssociationListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 28,November,2018
 */
public class SelectAssociationFragment extends BaseFragment implements OnListItemSelectInterface {

    private TextView titleTv;
    private ImageView backIv;
    private AppBarLayout appBarLayout;
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
    private boolean isFromHome, isShowToolbar, isCloseNeeded;
    private String selectionType, userName;
    private SearchCellView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        getDoctorsApiViewModel = new ViewModelProvider(this).get(GetDoctorsApiViewModel.class);

        attachObserverInterface.attachObserver(associationApiViewModel);
        attachObserverInterface.attachObserver(getDoctorsApiViewModel);

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;

                    associationRv.setNextPage(associationApiResponseModel.getNext());

                    associationListAdapter.setCommonUserApiResponseModelList(associationApiResponseModel.getResult(), page);
                }
                associationRv.updateView();
                associationRv.setScrollable(true);
                associationRv.hideProgressBar();
            }
        });

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModelArrayList = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;
                    associationRv.setNextPage(null);
                    List<CommonUserApiResponseModel> responseModelList = new ArrayList<>(commonUserApiResponseModelArrayList);
                    associationListAdapter.setCommonUserApiResponseModelList(responseModelList, page);
                }
                associationRv.updateView();
                associationRv.setScrollable(true);
                associationRv.hideProgressBar();
            }
        });

        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    getDoctorsApiResponseModel = (GetDoctorsApiResponseModel) baseApiResponseModel;

                    associationRv.setNextPage(getDoctorsApiResponseModel.getNext_page());

                    associationListAdapter.setDoctorsApiResponseModel(getDoctorsApiResponseModel.getData(), page);

                    if (getDoctorsApiResponseModel.getTotal_count() > 0) {
                        associationRv.showOrhideEmptyState(false);
                    } else {
                        associationRv.showOrhideEmptyState(true);
                    }
                }
                associationRv.updateView();
                associationRv.setScrollable(true);
                associationRv.hideProgressBar();
            }
        });

        associationApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        associationRv.setScrollable(true);
                        associationRv.hideProgressBar();
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        associationRv.setScrollable(true);
                        associationRv.hideProgressBar();
                    }
                }
            }
        });

        getDoctorsApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    if (errorModel.geterrorCode() == null){
                        associationRv.setScrollable(false);
                        associationRv.hideProgressBar();
                    }else if (!errorModel.geterrorCode().isEmpty() && !errorModel.geterrorCode().equals("SUBSCRIPTION")) {
                        associationRv.setScrollable(false);
                        associationRv.hideProgressBar();
                    }
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
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        titleTv = (TextView) view.findViewById(R.id.toolbar_title);
        associationRv = (CustomRecyclerView) view.findViewById(R.id.association_rv);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        searchView = view.findViewById(R.id.search_view);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });


        associationRv.getSwipeLayout().setEnabled(false);
        associationRv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                associationRv.setScrollable(false);
                page = page + 1;
                associationRv.showProgressBar();

                fetch();
            }
        });

        associationApiResponseModel = new AssociationApiResponseModel();

        if (getArguments() != null) {

            Bundle bundle = getArguments();

            isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);
            isShowToolbar = getArguments().getBoolean(ArgumentKeys.IS_SHOW_TOOLBAR, false);
            isCloseNeeded = getArguments().getBoolean(ArgumentKeys.IS_CLOSE_NEEDED, true);
            userName = getArguments().getString(ArgumentKeys.USER_NAME);

            if (isShowToolbar) {
                appBarLayout.setVisibility(View.VISIBLE);
                titleTv.setText(Utils.getPaginatedTitle(userName, associationApiResponseModel.getCount()));
            }
            if (!isFromHome) {
                commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            }

            searchView.setSearchInterface(new SearchInterface() {
                @Override
                public void doSearch() {
                    page = 1;
                    fetch();
                }
            });
            selectionType = getArguments().getString(ArgumentKeys.SEARCH_TYPE);
            Log.e(TAG, "initView: " + selectionType);

            if (selectionType != null) {

                switch (selectionType) {
                    case ArgumentKeys.SEARCH_COPY_TO:
                        searchView.setSearchHint(getString(R.string.search_doctors));
                        changeTitleInterface.onTitleChange(getString(R.string.copy_to));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_SPECIALIST);
                        fetch();
                        break;
                    case ArgumentKeys.SEARCH_DOCTOR:
                        searchView.setSearchHint(getString(R.string.search_doctors));
                        changeTitleInterface.onTitleChange(getString(R.string.specialist));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_SPECIALIST);
                        fetch();
                        break;
                    case ArgumentKeys.SEARCH_ASSOCIATION:
                        searchView.setSearchHint(getString(R.string.search_associations));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_SEARCH);
                        changeTitleInterface.onTitleChange(getString(R.string.choose_patient));
                        fetch();
                        break;
                    case ArgumentKeys.SEARCH_ASSOCIATION_DOCTOR:
                        changeTitleInterface.onTitleChange(getString(R.string.choose_doctor));
                        associationRv.setEmptyState(EmptyViewConstants.EMPTY_SPECIALIST);
                        fetch();
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

    private void fetch() {
        if (selectionType.equals(ArgumentKeys.SEARCH_DOCTOR) || selectionType.equals(ArgumentKeys.SEARCH_COPY_TO)) {
            getSpecialist(false);
        } else {
            getAssociationList(true);
        }
    }

    private void getSpecialist(boolean isShowProgress) {
        getDoctorsApiViewModel.getDoctorsDetailList(page, searchView.getCurrentSearchResult(), isShowProgress);
    }

    private void getAssociationList(boolean isShowProgress) {
        String doctorGuid = null;
        if (getArguments() != null && getArguments().getString(ArgumentKeys.DOCTOR_GUID) != null) {
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
        }
        associationApiViewModel.getAssociationList(searchView.getCurrentSearchResult(), page, doctorGuid, isShowProgress, false);
    }

    @Override
    public void onListItemSelected(int position, Bundle bundle) {
        if (getTargetFragment() == null) {
            getActivity().setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
            getActivity().finish();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent().putExtras(bundle));
            if (isCloseNeeded) {
                onCloseActionInterface.onClose(false);
            }
        }
    }
}
