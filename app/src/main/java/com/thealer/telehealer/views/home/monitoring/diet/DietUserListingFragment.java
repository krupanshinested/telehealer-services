package com.thealer.telehealer.views.home.monitoring.diet;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.diet.DietUserListApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 04,June,2019
 */
public class DietUserListingFragment extends BaseFragment {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout searchLl;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;
    private CustomRecyclerView doctorPatientListCrv;

    private DietApiViewModel dietApiViewModel;
    private DietUserListApiResponseModel dietUserListApiResponseModel;
    private DietUserListAdapter dietUserListAdapter;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;
    private OnCloseActionInterface onCloseActionInterface;

    private String doctorGuid = null;
    private static String selectedFilter;
    private String startDate = null;
    private String endDate = null;
    private String selectedItem;
    private List<UserBean> searchList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) context;
        attachObserverInterface = (AttachObserverInterface) context;
        onCloseActionInterface = (OnCloseActionInterface) context;

        dietApiViewModel = ViewModelProviders.of(this).get(DietApiViewModel.class);
        attachObserverInterface.attachObserver(dietApiViewModel);

        dietApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    dietUserListApiResponseModel = (DietUserListApiResponseModel) baseApiResponseModel;
                    setAdapterData(dietUserListApiResponseModel.getResult());
                }
            }
        });
    }

    private void setAdapterData(List<UserBean> userBeanList) {
        if (userBeanList.isEmpty()) {
            doctorPatientListCrv.showOrhideEmptyState(true);
        } else {
            doctorPatientListCrv.showOrhideEmptyState(false);
            dietUserListAdapter.setData(userBeanList);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        searchLl = (LinearLayout) view.findViewById(R.id.search_ll);
        topView = (View) view.findViewById(R.id.top_view);
        searchCv = (CardView) view.findViewById(R.id.search_cv);
        searchEt = (EditText) view.findViewById(R.id.search_et);
        searchClearIv = (ImageView) view.findViewById(R.id.search_clear_iv);
        bottomView = (View) view.findViewById(R.id.bottom_view);
        doctorPatientListCrv = (CustomRecyclerView) view.findViewById(R.id.doctor_patient_list_crv);

        toolbar.inflateMenu(R.menu.add_visit_menu);
        toolbar.getMenu().findItem(R.id.menu_print).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_next).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_filter:
                        showFilterDialog();
                        break;
                }
                return true;
            }
        });

        searchClearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
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
                    for (int i = 0; i < dietUserListApiResponseModel.getResult().size(); i++) {
                        if (dietUserListApiResponseModel.getResult().get(i).getName().toLowerCase().trim().contains(s.toString().toLowerCase().trim())) {
                            searchList.add(dietUserListApiResponseModel.getResult().get(i));
                        }
                    }

                    setAdapterData(searchList);
                } else {
                    setAdapterData(dietUserListApiResponseModel.getResult());
                }
            }
        });

        doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);

        dietUserListAdapter = new DietUserListAdapter(getActivity(), getArguments(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                DietListingFragment dietListingFragment = new DietListingFragment();
                bundle.putString(ArgumentKeys.SELECTED_DATE, selectedItem);
                bundle.putString(ArgumentKeys.SEARCH_TYPE, selectedFilter);
                bundle.putString(ArgumentKeys.START_DATE, startDate);
                bundle.putString(ArgumentKeys.END_DATE, endDate);
                bundle.putBoolean(ArgumentKeys.SHOW_PRINT_FILTER, false);

                dietListingFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(dietListingFragment);
            }
        });

        doctorPatientListCrv.getRecyclerView().setAdapter(dietUserListAdapter);
        doctorPatientListCrv.getSwipeLayout().setEnabled(false);
        doctorPatientListCrv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDietUserList(true);
            }
        });
        doctorPatientListCrv.setErrorModel(this, dietApiViewModel.getErrorModelLiveData());

        if (getArguments() != null) {
            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                setToolbarTitle(getString(R.string.last_week));
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCloseActionInterface.onClose(false);
                    }
                });
            }

            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);
            if (doctorDetail != null) {
                doctorGuid = doctorDetail.getUser_guid();
            }
        }

        getDietUserList(true);
    }

    private void setToolbarTitle(String text) {
        if (text.equals(getString(R.string.all))) {
            toolbarTitle.setText(getString(R.string.diet));
        } else {
            toolbarTitle.setText(String.format(getString(R.string.diet) + " (%s)", text));
        }
    }

    private void getDietUserList(boolean isShowProgress) {
        dietApiViewModel.getDietUserList(selectedFilter, startDate, endDate, null, doctorGuid, isShowProgress);
    }

    private void showFilterDialog() {

        Utils.showMonitoringFilter(null, getActivity(), new OnListItemSelectInterface() {
            @Override
            public void onListItemSelected(int position, Bundle bundle) {
                selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                startDate = null;
                endDate = null;

                if (selectedItem != null) {
                    if (selectedItem.equals(getString(R.string.last_week))) {
                        doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_LAST_WEEK);
                        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
                    } else if (selectedItem.equals(getString(R.string.all))) {
                        doctorPatientListCrv.setEmptyState(EmptyViewConstants.EMPTY_DOCTOR_VITAL_SEARCH);
                        selectedFilter = VitalReportApiViewModel.ALL;
                    } else {
                        selectedFilter = null;
                        startDate = bundle.getString(ArgumentKeys.START_DATE);
                        endDate = bundle.getString(ArgumentKeys.END_DATE);

                        String title = EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_VITAL_FROM_TO);

                        doctorPatientListCrv.setEmptyStateTitle(String.format(title, Utils.getDayMonthYear(startDate), Utils.getDayMonthYear(endDate)));
                    }
                    setToolbarTitle(selectedItem);
                }
                getDietUserList(true);
            }
        });
    }

}
