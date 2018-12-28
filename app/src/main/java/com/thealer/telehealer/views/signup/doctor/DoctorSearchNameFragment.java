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
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiViewModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.TypeAHeadResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.DoCurrentTransactionInterface;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.signup.OnViewChangeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 23,October,2018
 */
public class DoctorSearchNameFragment extends BaseFragment implements DoCurrentTransactionInterface {
    private EditText searchEt;
    private ImageView clearIv;
    private RecyclerView searchListRv;
    private ProgressBar progressbar;

    private OnActionCompleteInterface onActionCompleteInterface;
    private OnViewChangeInterface onViewChangeInterface;
    private GetDoctorsApiViewModel getDoctorsApiViewModel;
    private TypeAHeadResponseModel typeAHeadResponseModel;

    private SearchListAdapter searchListAdapter;
    private int page = 1;
    private boolean isApiRequested = false, isSearch = true;
    private List<String> doctorsNameList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewChangeInterface = (OnViewChangeInterface) getActivity();
        onActionCompleteInterface = (OnActionCompleteInterface) getActivity();
        getDoctorsApiViewModel = ViewModelProviders.of(this).get(GetDoctorsApiViewModel.class);
        onViewChangeInterface.attachObserver(getDoctorsApiViewModel);
        getDoctorsApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                isApiRequested = false;
                progressbar.setVisibility(View.GONE);
                if (baseApiResponseModel != null) {
                    typeAHeadResponseModel = (TypeAHeadResponseModel) baseApiResponseModel;

                    if (page == 1) {
                        doctorsNameList.clear();
                        doctorsNameList.add(" ");
                    }
                    doctorsNameList.addAll(typeAHeadResponseModel.getData());
                    setSearchList();
                }
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_search_name, container, false);

        onViewChangeInterface.hideOrShowNext(true);
        onViewChangeInterface.enableNext(false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        searchEt = (EditText) view.findViewById(R.id.search_et);
        clearIv = (ImageView) view.findViewById(R.id.clear_iv);
        searchListRv = (RecyclerView) view.findViewById(R.id.search_list_rv);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSearch) {
                    page = 1;
                    if (searchListAdapter != null) {
                        searchListAdapter.setData(new ArrayList<>());
                    }
                    if (!s.toString().isEmpty()) {
                        makeApiCall(s.toString());
                        onViewChangeInterface.enableNext(true);
                        clearIv.setVisibility(View.VISIBLE);
                    } else {
                        onViewChangeInterface.enableNext(false);
                        clearIv.setVisibility(View.GONE);
                    }
                }
            }
        });

        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText(null);
                doctorsNameList.clear();
            }
        });

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        searchListRv.setLayoutManager(linearLayoutManager);

        searchListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.findFirstVisibleItemPosition() < doctorsNameList.size()) {
                            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() > 0) {
                                isSearch = false;
                                linearLayoutManager.setSmoothScrollbarEnabled(true);
                                linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.findFirstCompletelyVisibleItemPosition(), 0);
                                searchEt.setText(doctorsNameList.get(linearLayoutManager.findFirstCompletelyVisibleItemPosition()));
                                searchEt.setSelection(searchEt.getText().toString().length());
                                isSearch = true;
                            }
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.getItemCount() > 0 && linearLayoutManager.getItemCount() < typeAHeadResponseModel.getTotal_count()) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {
                        page = page + 1;
                        makeApiCall(searchEt.getText().toString());
                        progressbar.setVisibility(View.VISIBLE);
                    } else {
                        progressbar.setVisibility(View.GONE);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setSearchList() {
        searchListAdapter = new SearchListAdapter(getActivity(), doctorsNameList, new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                isSearch = false;
                searchEt.setText(doctorsNameList.get(position));
                searchEt.setSelection(searchEt.getText().toString().length());
                linearLayoutManager.scrollToPositionWithOffset(position, 0);
                isSearch = true;
            }
        });
        searchListRv.setAdapter(searchListAdapter);

    }

    private void makeApiCall(String name) {
        if (!isApiRequested) {
            isApiRequested = true;
            getDoctorsApiViewModel.getTypeAHeadResult(page, name, false);
        }
    }

    @Override
    public void doCurrentTransaction() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SEARCH_KEY, searchEt.getText().toString());

        onActionCompleteInterface.onCompletionResult(null, true, bundle);
    }
}
