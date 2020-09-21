package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserDetailBean;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;

/**
 * Created by Aswin on 23,October,2018
 */
public class DoctorListFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private TextView searchCountTv;
    private RecyclerView resultListRv;
    private TextView registerManuallyTv;
    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private GetDoctorsApiViewModel getDoctorsApiViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_list, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDoctorsApiViewModel = ViewModelProviders.of(getActivity()).get(GetDoctorsApiViewModel.class);

        getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.observe(this, new Observer<GetDoctorsApiResponseModel>() {
            @Override
            public void onChanged(@Nullable GetDoctorsApiResponseModel doctorsApiResponseModel) {
                if (doctorsApiResponseModel != null) {
                    if (doctorsApiResponseModel.getData().size() == 0) {
                        setSearchCount(0);
                        createManually();
                        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);
                    } else {
                        setSearchCount(doctorsApiResponseModel.getData().size());
                    }
                }else {
                    setSearchCount(0);
                }
            }
        });

    }

    private void setSearchCount(int count) {
        searchCountTv.setText(count + " " + getString(R.string.result));
    }

    private void createManually() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_CREATE_MANUALLY, true);

        if (getArguments() != null) {
            bundle.putString(Constants.SEARCH_KEY, getArguments().getString(Constants.SEARCH_KEY));
        }

        clearUserRequest();

        onActionCompleteInterface.onCompletionResult(null, true, bundle);

    }

    private void clearUserRequest() {
        CreateUserRequestModel createUserRequestModel = ViewModelProviders.of(getActivity()).get(CreateUserRequestModel.class);
        createUserRequestModel.getUser_detail().getData().getLicenses().clear();
        createUserRequestModel.getUser_detail().getData().getPractices().clear();
        createUserRequestModel.getUser_detail().getData().getSpecialties().clear();
        createUserRequestModel.setUser_data(new CreateUserRequestModel.UserDataBean());
        createUserRequestModel.setUser_detail(new UserDetailBean());
        createUserRequestModel.getHasValidLicensesList().clear();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    private void initView(View view) {

        onViewChangeInterface.hideOrShowNext(false);

        searchCountTv = (TextView) view.findViewById(R.id.search_count_tv);
        resultListRv = (RecyclerView) view.findViewById(R.id.result_list_rv);
        registerManuallyTv = (TextView) view.findViewById(R.id.register_manually_tv);

        resultListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultListRv.setAdapter(new DoctorResultListAdapter(getActivity()));


        registerManuallyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createManually();
            }
        });

    }

    @Override
    public void doCurrentTransaction() {

    }
}
