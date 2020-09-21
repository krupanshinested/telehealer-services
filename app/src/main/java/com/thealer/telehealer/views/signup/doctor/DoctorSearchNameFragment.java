package com.thealer.telehealer.views.signup.doctor;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
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
public class DoctorSearchNameFragment extends BaseFragment implements DoCurrentTransactionInterface {

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private EditText searchEt;
    private ImageView clearIv;
    private RecyclerView searchListRv;
    private GetDoctorsApiViewModel getDoctorsApiViewModel;
    private SearchListAdapter searchListAdapter;
    private int page = 0;
    private int nextPage;
    private ProgressBar progressbar;
    private boolean isMakeApiRequestEnabled = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_search_name, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDoctorsApiViewModel = ViewModelProviders.of(getActivity()).get(GetDoctorsApiViewModel.class);
        onViewChangeInterface.attachObserver(getDoctorsApiViewModel);

        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    GetDoctorsApiResponseModel getDoctorsApiResponseModel = (GetDoctorsApiResponseModel) baseApiResponseModel;
                    if (getDoctorsApiResponseModel.getData().size() > 0) {

                        if (getDoctorsApiResponseModel.getCurrent_page() == 1) {
                            getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.setValue(getDoctorsApiResponseModel);
                        } else {
                            if (getDoctorsApiResponseModel.getCurrent_page() > getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.getValue().getCurrent_page()) {
                                getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.getValue().getData().addAll(getDoctorsApiResponseModel.getData());
                                getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.getValue().setCurrent_page(getDoctorsApiResponseModel.getCurrent_page());
                            }
                        }

                        progressbar.setVisibility(View.GONE);
                        isMakeApiRequestEnabled = true;
                        nextPage = getDoctorsApiResponseModel.getNext_page();
                    } else {
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
    }

    @Override
    public void doCurrentTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SEARCH_KEY, searchEt.getText().toString());

        isMakeApiRequestEnabled = false;
        onActionCompleteInterface.onCompletionResult(null, true, bundle);
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        clearIv = (ImageView) view.findViewById(R.id.clear_iv);
        searchListRv = (RecyclerView) view.findViewById(R.id.search_list_rv);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchListRv.setLayoutManager(linearLayoutManager);
        searchListAdapter = new SearchListAdapter(getActivity());
        searchListRv.setAdapter(searchListAdapter);

        searchListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.getItemCount() > 0) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        page = nextPage;
                        makeDoctorsListApiCall(searchEt.getText().toString());
                        progressbar.setVisibility(View.VISIBLE);
                        isMakeApiRequestEnabled = false;
                    } else {
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });


        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
                getDoctorsApiViewModel.getDoctorsApiResponseModelMutableLiveData.setValue(null);
            }
        });

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);
        clearIv.setVisibility(View.INVISIBLE);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    isMakeApiRequestEnabled = true;
                    onViewChangeInterface.enableNext(false);
                    clearIv.setVisibility(View.INVISIBLE);
                    getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);
                } else {
                    onViewChangeInterface.enableNext(true);
                    clearIv.setVisibility(View.VISIBLE);
                    page = 1;
                    makeDoctorsListApiCall(s.toString());
                }
            }
        });


    }

    private void makeDoctorsListApiCall(String name) {
        if (!name.isEmpty() && isMakeApiRequestEnabled) {
            getDoctorsApiViewModel.getDoctorsDetailList(page, name, false);
        }
    }

}
