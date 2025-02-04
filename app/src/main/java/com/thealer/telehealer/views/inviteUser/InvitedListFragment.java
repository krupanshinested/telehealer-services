package com.thealer.telehealer.views.inviteUser;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesApiViewModel;
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesNonRegisterdApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.home.pendingInvites.PendingInvitesListAdapter;


public class InvitedListFragment extends BaseFragment {
    private CustomRecyclerView invitedListCrv;
    private PendingInvitesNonRegisterdApiResponseModel nonRegisterdApiResponseModel;
    private int nonRegisteredPage = 1;
    private boolean isApiRequested = false;

    private PendingInvitesApiViewModel pendingInvitesApiViewModel;
    public NotificationApiResponseModel invitesResponseModel;
    private AttachObserverInterface attachObserverInterface;
    public PendingInvitesListAdapter pendingInvitesListAdapter;
    String role="";


    public InvitedListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invited_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        if(this.getArguments()!=null){
            Bundle bundle = this.getArguments();
            role=bundle.getString(ArgumentKeys.ROLE,"");
        }
        invitedListCrv = (CustomRecyclerView) view.findViewById(R.id.invited_list_crv);
        invitedListCrv.setEmptyState(EmptyViewConstants.EMPTY_SENT_PENDING_INVITES);
        invitedListCrv.hideEmptyState();
        pendingInvitesListAdapter = new PendingInvitesListAdapter(getActivity(), false, new PendingInvitesListAdapter.OnItemClickListener() {
            @Override
            public void onItemRegisterClick(CommonUserApiResponseModel item) {
                Log.d("TAG", "onItemRegisterClick: "+item.getUser_id());
            }

            @Override
            public void onItemUnRegisterClick(PendingInvitesNonRegisterdApiResponseModel.ResultBean item) {
                InviteByEmailPhoneRequestModel inviteByEmailPhoneRequestModel = new InviteByEmailPhoneRequestModel();
                String mode;
                if (item.getEmail() != null) {
                    mode = item.getEmail();
                    inviteByEmailPhoneRequestModel.getInvitations().add(new InviteByEmailPhoneRequestModel.InvitationsBean(mode, null));
                }
                if (item.getPhone() != null) {
                    mode = item.getPhone();
                    inviteByEmailPhoneRequestModel.getInvitations().add(new InviteByEmailPhoneRequestModel.InvitationsBean(null, mode));
                }
                if (UserType.isUserDoctor()) {
                    inviteByEmailPhoneRequestModel.setRole(Constants.ROLE_PATIENT);
                }

                pendingInvitesApiViewModel.inviteUserByEmailPhone(UserType.isUserDoctor() ? UserDetailPreferenceManager.getUser_guid() : " ", inviteByEmailPhoneRequestModel, true);
            }
        });
        invitedListCrv.getRecyclerView().setAdapter(pendingInvitesListAdapter);

        invitedListCrv.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                invitedListCrv.setScrollable(false);
                invitedListCrv.showProgressBar();
                    nonRegisteredPage = nonRegisteredPage + 1;
                    getNonRegisteredInvites(nonRegisteredPage, false);

            }
        });

        attachObserver();
        getNonRegisteredInvites(nonRegisteredPage, true);
    }

    private void attachObserver() {
        attachObserverInterface = (AttachObserverInterface) getActivity();
        pendingInvitesApiViewModel = new ViewModelProvider(this).get(PendingInvitesApiViewModel.class);
        attachObserverInterface.attachObserver(pendingInvitesApiViewModel);

        pendingInvitesApiViewModel.baseApiResponseModelMutableLiveData.observe(this,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            if (baseApiResponseModel instanceof PendingInvitesNonRegisterdApiResponseModel) {
                                nonRegisterdApiResponseModel = (PendingInvitesNonRegisterdApiResponseModel) baseApiResponseModel;

                                if (nonRegisterdApiResponseModel.getCount() > 0) {
                                    if (nonRegisteredPage == 1) {
                                        invitedListCrv.setNextPage(nonRegisterdApiResponseModel.getNext());
                                    }
                                    pendingInvitesListAdapter.setData(nonRegisterdApiResponseModel.getResult(), nonRegisteredPage,false);
                                } else {
                                        invitedListCrv.showOrhideEmptyState(true);
                                }

                                isApiRequested = false;
                                invitedListCrv.getSwipeLayout().setRefreshing(false);
                                invitedListCrv.setScrollable(true);
                                invitedListCrv.hideProgressBar();
                            }else if (baseApiResponseModel instanceof InviteByEmailPhoneApiResponseModel) {
                                InviteByEmailPhoneApiResponseModel inviteByEmailPhoneApiResponseModel = (InviteByEmailPhoneApiResponseModel) baseApiResponseModel;
                                Toast.makeText(getActivity(), "" + inviteByEmailPhoneApiResponseModel.getResultData().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        invitedListCrv.setErrorModel(this, pendingInvitesApiViewModel.getErrorModelLiveData());

    }

    private void getNonRegisteredInvites(int page, boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            pendingInvitesApiViewModel.getNonRegisteredUserInvitesByROLE(page,role, isShowProgress);
        }
    }


}