package com.thealer.telehealer.views.settings.serviceProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;

public class ServiceProviderListFragment extends BaseFragment {

    private OnCloseActionInterface onCloseActionInterface;

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

    }
}