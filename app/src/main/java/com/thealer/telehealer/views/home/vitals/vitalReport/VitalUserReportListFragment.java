package com.thealer.telehealer.views.home.vitals.vitalReport;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.CustomExpandableListView;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.vitals.VitalPdfGenerator;
import com.thealer.telehealer.views.home.vitals.VitalsDetailListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aswin on 04,February,2019
 */
public class VitalUserReportListFragment extends BaseFragment {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private CustomExpandableListView vitalsListCelv;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private VitalsApiViewModel vitalsApiViewModel;

    private ArrayList<VitalsApiResponseModel> vitalsApiResponseModel;
    private List<String> headerList = new ArrayList<>();
    private HashMap<String, List<VitalsApiResponseModel>> childList = new HashMap<>();
    private VitalsDetailListAdapter vitalsDetailListAdapter;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private String filter;
    private ShowSubFragmentInterface showSubFragmentInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        vitalsApiViewModel = ViewModelProviders.of(this).get(VitalsApiViewModel.class);
        attachObserverInterface.attachObserver(vitalsApiViewModel);
        vitalsApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    vitalsApiResponseModel = (ArrayList<VitalsApiResponseModel>) (Object) baseApiResponseModels;
                    updateList(vitalsApiResponseModel);
                    enableOrDisablePrint();
                }
            }
        });
    }

    private void updateList(ArrayList<VitalsApiResponseModel> vitalsApiResponseModelArrayList) {
        headerList.clear();
        childList.clear();

        for (int i = 0; i < vitalsApiResponseModelArrayList.size(); i++) {

            String date = Utils.getDayMonthYear(vitalsApiResponseModelArrayList.get(i).getCreated_at());

            if (!headerList.contains(date)) {
                headerList.add(date);
            }
            List<VitalsApiResponseModel> vitalsApiResponseModelList = new ArrayList<>();

            if (childList.get(date) != null) {
                vitalsApiResponseModelList.addAll(childList.get(date));
            }

            vitalsApiResponseModelList.add(vitalsApiResponseModelArrayList.get(i));

            childList.put(date, vitalsApiResponseModelList);

        }

        vitalsDetailListAdapter.setData(headerList, childList);

        if (headerList.size() > 0) {
            vitalsListCelv.hideEmptyState();
            expandListView();
        } else {
            vitalsListCelv.showEmptyState();
        }
    }

    private void expandListView() {
        for (int i = 0; i < headerList.size(); i++) {
            vitalsListCelv.getExpandableView().expandGroup(i);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vital_user_report, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        vitalsListCelv = (CustomExpandableListView) view.findViewById(R.id.vitals_list_cerv);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        toolbar.getMenu().removeItem(R.id.send_fax_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:
                        generatePrintList();
                        break;
                }
                return true;
            }
        });

        enableOrDisablePrint();

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseActionInterface.onClose(false);
            }
        });

        vitalsListCelv.setEmptyState(EmptyViewConstants.EMPTY_VITALS);

        vitalsDetailListAdapter = new VitalsDetailListAdapter(getActivity(), headerList, childList, true);

        vitalsListCelv.getExpandableView().setAdapter(vitalsDetailListAdapter);

        vitalsListCelv.setErrorModel(this, vitalsApiViewModel.getErrorModelLiveData());

        vitalsListCelv.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserVitals();
            }
        });

        if (getArguments() != null) {
            commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(ArgumentKeys.USER_DETAIL);
            filter = getArguments().getString(ArgumentKeys.SEARCH_TYPE);

            if (commonUserApiResponseModel != null) {
                toolbarTitle.setText(commonUserApiResponseModel.getUserDisplay_name());
            }
        }

        getUserVitals();

    }

    private void generatePrintList() {
        VitalPdfGenerator vitalPdfGenerator = new VitalPdfGenerator(getActivity());
        String htmlContent = vitalPdfGenerator.generatePdfFor(vitalsApiResponseModel, commonUserApiResponseModel, true);

        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.vitals_report));
        pdfViewerFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(pdfViewerFragment);

    }

    private void enableOrDisablePrint() {
        if (vitalsApiResponseModel == null || vitalsApiResponseModel.size() == 0) {
            toolbar.getMenu().findItem(R.id.print_menu).setVisible(false);
        } else {
            toolbar.getMenu().findItem(R.id.print_menu).setVisible(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getUserVitals() {
        if (commonUserApiResponseModel != null) {
            vitalsListCelv.hideEmptyState();
            vitalsApiViewModel.getUserFilteredVitals(filter, commonUserApiResponseModel.getUser_guid());
        }
    }
}
