package com.thealer.telehealer.views.settings.primaryPhysician;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DefaultPhysicianModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.SuccessViewDialogFragment;
import com.thealer.telehealer.views.settings.primaryPhysician.adapter.PhysicianAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class PrimaryPhysicianFragment extends BaseFragment implements View.OnClickListener {

    private TextView toolbarTitle;
    private ImageView backIv;
    private Button submitBtn;
    private RecyclerView recyclerView;
    private OnCloseActionInterface onCloseActionInterface;
    private AssociationApiViewModel associationApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private PhysicianAdapter physicianAdapter;
    private DefaultPhysicianModel defaultPhysician;
    private int physicianId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_primary_physician, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);

        associationApiViewModel.defaultPhysicianMutableLiveData.observe(this, new Observer<DefaultPhysicianModel>() {
            @Override
            public void onChanged(DefaultPhysicianModel data) {
                defaultPhysician = data;
                physicianId = Math.toIntExact(data.getData().getPhysicianID());
                associationApiViewModel.getAssociationList(null, true, null);
            }
        });

        associationApiViewModel.updateDefaultPhysicianMutableLiveData.observe(this, new Observer<DefaultPhysicianModel>() {
            @Override
            public void onChanged(DefaultPhysicianModel data) {
                if(data.isSuccess()) {
                    SuccessViewDialogFragment successViewDialogFragment = new SuccessViewDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.SUCCESS_VIEW_STATUS, true);
                    bundle.putString(Constants.SUCCESS_VIEW_TITLE, getString(R.string.success));
                    bundle.putString(Constants.SUCCESS_VIEW_DESCRIPTION, getString(R.string.physician_seleted));
                    bundle.putBoolean(Constants.SUCCESS_VIEW_DONE_BUTTON, false);
                    successViewDialogFragment.setArguments(bundle);
                    successViewDialogFragment.show(getActivity().getSupportFragmentManager(), successViewDialogFragment.getClass().getSimpleName());
                }
            }
        });

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;
                    for (CommonUserApiResponseModel currentItem : commonUserApiResponseModels) {
                        if (currentItem.getUser_id() == defaultPhysician.getData().getPhysicianID()) {
                            currentItem.setSelection(true);
                        } else {
                            currentItem.setSelection(false);
                        }
                    }
                    setAdapter(commonUserApiResponseModels);
                }
            }
        });
    }

    private void initView(View view) {
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        submitBtn = (Button) view.findViewById(R.id.submit_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        toolbarTitle.setText(getString(R.string.select_primary_physician));

        backIv.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        getDoctorsList();

    }

    void setAdapter(ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels) {
        physicianAdapter = new PhysicianAdapter(getActivity(), commonUserApiResponseModels, id -> {
            physicianId = id;
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(physicianAdapter);
        physicianAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.submit_button:
                HashMap<String, Object> req = new HashMap<>();
                req.put("physicianId", physicianId);
                associationApiViewModel.saveDefaultPhysician(req);
                break;
        }
    }

    private void getDoctorsList() {
        associationApiViewModel.getDefaultPhysician();
    }
}