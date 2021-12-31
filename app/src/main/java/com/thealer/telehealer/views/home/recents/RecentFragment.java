package com.thealer.telehealer.views.home.recents;

import android.content.Context;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnOrientationChangeInterface;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;

import java.util.ArrayList;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 14,November,2018
 */
public class RecentFragment extends BaseFragment {

    private static final String TAG = "aswin recents ";
    private RecentsApiViewModel recentsApiViewModel;
    private int page = 1;
    private RecentsApiResponseModel recentsApiResponseModel;
    private NewRecentListAdapter recentListAdapter;
    private OnOrientationChangeInterface onOrientationChangeInterface;
    private AttachObserverInterface attachObserverInterface;
    private boolean isApiRequested = false, isResumed;
    private ImageView throbberIv;
    private CustomRecyclerView recentsCrv;
    private boolean isCalls;
    private SearchCellView searchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();

        onOrientationChangeInterface = (OnOrientationChangeInterface) getActivity();

        recentsApiViewModel = new ViewModelProvider(this).get(RecentsApiViewModel.class);

        attachObserverInterface.attachObserver(recentsApiViewModel);

        recentsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                recentsCrv.getSwipeLayout().setRefreshing(false);
                throbberIv.setVisibility(View.GONE);

                if (baseApiResponseModel != null) {

                    recentsApiResponseModel = (RecentsApiResponseModel) baseApiResponseModel;

                    if (context instanceof ChangeTitleInterface){
                        ((ChangeTitleInterface)context).onTitleChange(Utils.getPaginatedTitle(getString(R.string.visits), recentsApiResponseModel.getCount()));
                    }

                    recentsCrv.setNextPage(recentsApiResponseModel.getNext());

                    if (!recentsApiResponseModel.getResult().isEmpty()) {

                        recentListAdapter.setData(recentsApiResponseModel.getResult(), page, isCalls);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DETAIL, recentsApiResponseModel.getResult().get(0));
                        onOrientationChangeInterface.onDataReceived(bundle);
                        recentsCrv.showOrhideEmptyState(false);
                    } else {
                        recentsCrv.showOrhideEmptyState(true);
                    }
                }
                isApiRequested = false;
                recentsCrv.setScrollable(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recentsCrv = (CustomRecyclerView) view.findViewById(R.id.recents_crv);
        throbberIv = (ImageView) view.findViewById(R.id.throbber_iv);
        searchView = view.findViewById(R.id.search_view);
       // Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(throbberIv);

        recentsCrv.setEmptyState(EmptyViewConstants.EMPTY_CALLS);

        recentsCrv.hideEmptyState();

        searchView.setSearchHint(getString(R.string.search_contact));

        searchView.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                page = 1;
                makeApiCall(true);
            }
        });
        if (getUserVisibleHint()) {
            makeApiCall(true);
        }
        recentListAdapter = new NewRecentListAdapter(getActivity());
        recentsCrv.getRecyclerView().setAdapter(recentListAdapter);

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.HIDE_SEARCH, false)){
                searchView.setVisibility(View.GONE);
            }
        }

        recentsCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                throbberIv.setVisibility(View.VISIBLE);
                ++page;
                makeApiCall(false);
                isApiRequested = true;
                recentsCrv.setScrollable(false);
            }
        });

        recentsCrv.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                makeApiCall(false);
            }
        });

        recentsCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall(true);
            }
        });

        recentsCrv.setErrorModel(this, recentsApiViewModel.getErrorModelLiveData());
    }

    private void makeApiCall(boolean isShowProgress) {
        if (!isApiRequested) {
            if (recentsApiViewModel != null) {
                if (getArguments() != null) {

                    isCalls = getArguments().getBoolean(ArgumentKeys.IS_ONLY_CALLS, false);

                    if (getArguments().getBoolean(Constants.IS_FROM_HOME)) {
                        isApiRequested = true;
                        String doctorGuid = null;
                        if (UserType.isUserAssistant()) {
                            doctorGuid = appPreference.getString(PreferenceConstants.ASSOCIATION_GUID_LIST);
                            doctorGuid = doctorGuid.concat("," + UserDetailPreferenceManager.getWhoAmIResponse().getUser_guid());
                        }
                        recentsApiViewModel.getMyCorrespondentList(searchView.getCurrentSearchResult(), page, doctorGuid, isShowProgress);
                    } else {

                        CommonUserApiResponseModel userDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                        String userGuid = null;
                        if (userDetail != null) {
                            userGuid = userDetail.getUser_guid();
                        }
                        CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
                        String doctorGuid = null;
                        if (doctorDetail != null) {
                            doctorGuid = doctorDetail.getUser_guid();
                        }

                        if (UserType.isUserAssistant() && userGuid == null && doctorGuid != null){
                            isCalls = true;
                        }

                        if (UserType.isUserPatient() && userGuid == null){
                            userGuid = doctorGuid;
                            doctorGuid = null;
                        }
                        if(UserType.isUserAssistant() && doctorDetail.getPermissions()!= null && doctorDetail.getPermissions().size()>0){
                            boolean isPermissionAllowed =Utils.checkPermissionStatus(doctorDetail.getPermissions(),ArgumentKeys.VIEW_CALLS_CODE);
                            Constants.isVitalsAddEnable = Utils.checkPermissionStatus(doctorDetail.getPermissions(), ArgumentKeys.ADD_VITALS_CODE);
                            Constants.isVitalsViewEnable = Utils.checkPermissionStatus(doctorDetail.getPermissions(), ArgumentKeys.VIEW_VITALS_CODE);

                            if(isPermissionAllowed){
                                recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, null, page, isCalls, isShowProgress);
                            }else{
                                recentsCrv.showOrhideEmptyState(true);
                                Utils.displayPermissionMsg(getContext());
                            }
                        }else {
                            recentsApiViewModel.getUserCorrespondentList(userGuid, doctorGuid, null, page, isCalls, isShowProgress);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isApiRequested = false;

        if (isVisibleToUser && isResumed)
            makeApiCall(true);
    }

}
