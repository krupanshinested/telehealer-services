package com.thealer.telehealer.views.signup.doctor;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

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
    private GetDoctorsApiResponseModel getDoctorsApiResponseModel;

    private int page = 0, totalCount = 0;
    private String searchName;
    private ImageView progressbar;
    private DoctorResultListAdapter doctorResultListAdapter;
    private boolean isApiRequested = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        getDoctorsApiViewModel = new ViewModelProvider(this).get(GetDoctorsApiViewModel.class);
        onViewChangeInterface.attachObserver(getDoctorsApiViewModel);

        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    getDoctorsApiResponseModel = (GetDoctorsApiResponseModel) baseApiResponseModel;
                    totalCount = getDoctorsApiResponseModel.getTotal_count();
                    setDoctorsList();
                }
                isApiRequested = false;
                progressbar.setVisibility(View.GONE);
            }
        });

        getDoctorsApiViewModel.getErrorModelLiveData().observe(this,
                new Observer<ErrorModel>() {
                    @Override
                    public void onChanged(@Nullable ErrorModel errorModel) {
                        isApiRequested = false;
                        progressbar.setVisibility(View.GONE);
                    }
                });
    }

    private void setDoctorsList() {
        setSearchCount(getDoctorsApiResponseModel.getTotal_count());
        if (getDoctorsApiResponseModel.getData().size() > 0) {
            doctorResultListAdapter.setData(getDoctorsApiResponseModel.getData(), page);
        } else {
            if (page == 1) {
                createManually();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        onViewChangeInterface.hideOrShowNext(false);
        page = 1;

        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_KEY);
        }

        searchCountTv = (TextView) view.findViewById(R.id.search_count_tv);
        resultListRv = (RecyclerView) view.findViewById(R.id.result_list_rv);
        registerManuallyTv = (TextView) view.findViewById(R.id.register_manually_tv);
        progressbar = (ImageView) view.findViewById(R.id.progressbar);
        Glide.with(getActivity().getApplicationContext()).load(R.raw.throbber).into(progressbar);

        registerManuallyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createManually();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        resultListRv.setLayoutManager(linearLayoutManager);
        doctorResultListAdapter = new DoctorResultListAdapter(getActivity());
        resultListRv.setAdapter(doctorResultListAdapter);

        resultListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < totalCount) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        page = page + 1;
                        getDoctors(false);
                        progressbar.setVisibility(View.VISIBLE);
                    } else {
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });


        if (getDoctorsApiResponseModel == null) {
            getDoctors(true);
        } else if (getDoctorsApiResponseModel.getData().size() > 0) {
            page = 1;
            setDoctorsList();
        }
    }

    private void getDoctors(boolean isShowProgress) {
        if (!isApiRequested) {
            isApiRequested = true;
            getDoctorsApiViewModel.getDoctorsDetailList(page, searchName, isShowProgress);
        }
    }


    private void setSearchCount(int count) {
        searchCountTv.setText(count + " " + getString(R.string.result));
    }

    private void createManually() {
        Bundle bundle = getArguments();

        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putBoolean(Constants.IS_CREATE_MANUALLY, true);

        onActionCompleteInterface.onCompletionResult(null, true, bundle);

    }

    @Override
    public void doCurrentTransaction() {

    }

}
