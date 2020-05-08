package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiReponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Util.TimerInterface;
import com.thealer.telehealer.common.Util.TimerRunnable;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalReportFragment extends BaseFragment {
    private LinearLayout searchLl;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;
    private ImageView filterIv;
    private CustomRecyclerView patientListCrv;

    private AttachObserverInterface attachObserverInterface;
    private VitalReportApiViewModel vitalReportApiViewModel;

    private VitalReportApiReponseModel vitalReportApiReponseModel;
    private VitalReportUserListAdapter vitalReportUserListAdapter;
    private static String selectedFilter, title;
    private String startDate = null;
    private String endDate = null;
    private List<CommonUserApiResponseModel> searchList = new ArrayList<>();
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private OnCloseActionInterface onCloseActionInterface;
    private String doctorGuid;
    private ShowSubFragmentInterface showSubFragmentInterface;

    @Nullable
    private TimerRunnable uiToggleTimer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        vitalReportApiViewModel = new ViewModelProvider(this).get(VitalReportApiViewModel.class);
        attachObserverInterface.attachObserver(vitalReportApiViewModel);
        vitalReportApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    vitalReportApiReponseModel = (VitalReportApiReponseModel) baseApiResponseModel;
                    if (vitalReportApiReponseModel.getResult().size() > 0) {
                        vitalReportUserListAdapter.setData(vitalReportApiReponseModel.getResult());
                        patientListCrv.showOrhideEmptyState(false);
                    } else {
                        patientListCrv.showOrhideEmptyState(true);
                    }
                }
            }
        });

        vitalReportApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    showToast(errorModel.getMessage());
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_report, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        searchLl = (LinearLayout) view.findViewById(R.id.search_ll);
        topView = (View) view.findViewById(R.id.top_view);
        searchCv = (CardView) view.findViewById(R.id.search_cv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        filterIv = (ImageView) view.findViewById(R.id.filter_iv);
        patientListCrv = (CustomRecyclerView) view.findViewById(R.id.patient_list_crv);

        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        filterIv.setVisibility(View.GONE);

        toolbar.inflateMenu(R.menu.filter_menu);
        MenuItem filterItem = toolbar.getMenu().findItem(R.id.menu_filter);
        View filterView = filterItem.getActionView();
        ImageView filterIv = filterView.findViewById(R.id.filter_iv);
        ImageView filterIndicatorIv = filterView.findViewById(R.id.filter_indicatior_iv);
        filterIndicatorIv.setVisibility(View.VISIBLE);

        filterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                searchList.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (vitalReportApiReponseModel != null) {

                        if (uiToggleTimer != null) {
                        uiToggleTimer.setStopped(true);
                        uiToggleTimer = null;
                    }

                    Handler handler = new Handler();
                    TimerRunnable runnable = new TimerRunnable(new TimerInterface() {
                        @Override
                        public void run() {
                            for (int i = 0; i < vitalReportApiReponseModel.getResult().size(); i++) {
                                if (vitalReportApiReponseModel.getResult().get(i).getUserDisplay_name().toLowerCase().contains(searchEt.getText().toString())) {
                                    searchList.add(vitalReportApiReponseModel.getResult().get(i));
                                }
                            }
                        }
                    });
                    uiToggleTimer = runnable;
                    handler.postDelayed(runnable, ArgumentKeys.SEARCH_INTERVAL);

                        if (searchList.size() > 0) {
                            if (vitalReportUserListAdapter != null) {
                                vitalReportUserListAdapter.setData(searchList);
                                patientListCrv.showOrhideEmptyState(false);
                            }
                        } else {
                            patientListCrv.showOrhideEmptyState(true);
                        }
                    } else {
                        patientListCrv.showOrhideEmptyState(true);
                    }
                } else {
                    if (vitalReportApiReponseModel != null) {
                        vitalReportUserListAdapter.setData(vitalReportApiReponseModel.getResult());
                        patientListCrv.showOrhideEmptyState(false);
                    } else {
                        patientListCrv.showOrhideEmptyState(true);
                    }
                }
            }
        });

        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);

        if (getArguments() != null) {
            CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }

            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                setToolbarTitle(getString(R.string.last_week));
                onCloseActionInterface = (OnCloseActionInterface) getActivity();
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
            } else {
                appbarLayout.setVisibility(View.GONE);
            }
        }
        vitalReportUserListAdapter = new VitalReportUserListAdapter(getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {

                VitalUserReportListFragment vitalUserReportListFragment = new VitalUserReportListFragment();
                bundle.putString(ArgumentKeys.SEARCH_TYPE, selectedFilter);
                bundle.putString(ArgumentKeys.START_DATE, startDate);
                bundle.putString(ArgumentKeys.END_DATE, endDate);
                bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                bundle.putString(ArgumentKeys.TITLE, title);
                vitalUserReportListFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(vitalUserReportListFragment);

            }
        });

        patientListCrv.getRecyclerView().setAdapter(vitalReportUserListAdapter);

        patientListCrv.getSwipeLayout().setEnabled(false);

        patientListCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersList(selectedFilter, startDate, endDate);
            }
        });

        patientListCrv.setErrorModel(this, vitalReportApiViewModel.getErrorModelLiveData());

        getUsersList(selectedFilter, startDate, endDate);

    }

    private void showFilterDialog() {

        Utils.showMonitoringFilter(null, getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                String selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                startDate = null;
                endDate = null;

                if (selectedItem != null) {
                    setToolbarTitle(selectedItem);
                    if (selectedItem.equals(getString(R.string.last_week))) {
                        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);
                        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
                    } else if (selectedItem.equals(getString(R.string.all))) {
                        patientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH);
                        selectedFilter = VitalReportApiViewModel.ALL;
                    } else {
                        selectedFilter = null;
                        startDate = bundle.getString(ArgumentKeys.START_DATE);
                        endDate = bundle.getString(ArgumentKeys.END_DATE);

                        setToolbarTitle(Utils.getMonitoringTitle(startDate, endDate));

                        String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_VITAL_FROM_TO);

                        patientListCrv.setEmptyStateTitle(String.format(title, Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));
                    }
                }
                getUsersList(selectedFilter, startDate, endDate);
            }
        });
    }

    private void getUsersList(String filter, String startDate, String endDate) {
        vitalReportApiViewModel.getVitalUsers(filter, startDate, endDate, doctorGuid, true);
    }

    private void setToolbarTitle(String text) {
        title = text;
        if (text.equals(getString(R.string.all))) {
            toolbarTitle.setText(getString(R.string.vitals));
        } else {
            toolbarTitle.setText(String.format(getString(R.string.vitals) + " (%s)", text));
        }
    }

}
