package com.thealer.telehealer.views.settings;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.CustomSwipeRefreshLayout;
import com.thealer.telehealer.common.OnPaginateInterface;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.SearchCellView;
import com.thealer.telehealer.views.common.SearchInterface;
import com.thealer.telehealer.views.home.ProviderListAdapter;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.listener.DismissListener;

public class MonitoringProviderFragment extends BaseFragment {

    private TextView toolbarTitle;
    private CustomRecyclerView rvProvide;
    private ImageView backIv;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private SearchCellView search_view;
    private AttachObserverInterface attachObserverInterface;
    private AssociationApiViewModel associationApiViewModel;
    private AssociationApiResponseModel associationApiResponseModel;
    private List<DoctorGroupedAssociations> doctorGroupedAssociations;
    private RecyclerView ProviderListRv;
    private int page = 1;
    private ChangeTitleInterface changeTitleInterface;
    private ProviderListAdapter providerListAdapter;

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        associationApiViewModel = new ViewModelProvider(this).get(AssociationApiViewModel.class);
        attachObserverInterface.attachObserver(associationApiViewModel);
        changeTitleInterface = (ChangeTitleInterface) context;

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (doctorGroupedAssociations != null) {
                    doctorGroupedAssociations.clear();
                }
                associationApiResponseModel = null;
                doctorGroupedAssociations = new ArrayList(baseApiResponseModels);

                hideKeyboard(getActivity());
                didReceivedResult();
            }
        });

        associationApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                rvProvide.getSwipeLayout().setRefreshing(false);
                if (baseApiResponseModel != null) {

                    if (baseApiResponseModel instanceof AssociationApiResponseModel) {
                        if (associationApiResponseModel != null) {
                            associationApiResponseModel.getResult().clear();
                        }
                        associationApiResponseModel = (AssociationApiResponseModel) baseApiResponseModel;
                    }

                    hideKeyboard(getActivity());
                    didReceivedResult();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring_provider, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_view = view.findViewById(R.id.search_view);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        rvProvide = (CustomRecyclerView) view.findViewById(R.id.rvProvide);
        toolbarTitle.setText(getString(R.string.default_physician));
        search_view.setSearchHint(getString(R.string.search_doctors));
        rvProvide.showOrhideEmptyState(true);
        rvProvide.setEmptyState(EmptyViewConstants.EMPTY_PATIENT_WITH_BTN);

        getAssociationsList(true, false);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnCloseActionInterface) getActivity()).onClose(false);
            }
        });

        ProviderListRv = rvProvide.getRecyclerView();

        search_view.setSearchInterface(new SearchInterface() {
            @Override
            public void doSearch() {
                page = 1;
                getAssociationsList(true, true);
            }
        });
        providerListAdapter = new ProviderListAdapter(getActivity());
        ProviderListRv.setAdapter(providerListAdapter);

        rvProvide.setOnPaginateInterface(new OnPaginateInterface() {
            @Override
            public void onPaginate() {
                page = page + 1;
                getAssociationsList(false, true);
                rvProvide.setScrollable(false);
            }
        });

        rvProvide.getSwipeLayout().setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getAssociationsList(false, true);
            }
        });

        rvProvide.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAssociationsList(true, true);
            }
        });

        rvProvide.setErrorModel(this, associationApiViewModel.getErrorModelLiveData());

    }

    private void didReceivedResult() {
        if (page == 1) {
            String title;
            title = getString(R.string.Patients);

            if (associationApiResponseModel != null) {
                changeTitleInterface.onTitleChange(title/*Utils.getPaginatedTitle(title, associationApiResponseModel.getCount())*/);
            } else {
                changeTitleInterface.onTitleChange(title);
            }

        }

        boolean isItemsPresent = false;
        if (associationApiResponseModel != null) {
            isItemsPresent = associationApiResponseModel.getResult().size() != 0;
        } else {
            isItemsPresent = doctorGroupedAssociations.size() != 0;
        }

        if (!isItemsPresent) {

            rvProvide.showOrhideEmptyState(true);

            if (!appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_ASSOCIATION)) {

                appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_ASSOCIATION, true);

                DismissListener dismissListener = new DismissListener() {
                    @Override
                    public void onDismiss(@org.jetbrains.annotations.Nullable String s) {

                    }

                    @Override
                    public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                    }
                };


                Utils.showOverlay(getActivity(), null, OverlayViewConstants.OVERLAY_NO_DOCTOR, dismissListener);
            }
        }

        if (providerListAdapter != null) {


            if (isItemsPresent) {
                rvProvide.showOrhideEmptyState(false);
            }

            if (associationApiResponseModel != null) {
                rvProvide.setNextPage(associationApiResponseModel.getNext());
                providerListAdapter.setData(associationApiResponseModel.getResult(), page);
            } else {
                rvProvide.setNextPage(null);
                providerListAdapter.setData(doctorGroupedAssociations);
            }

            if (isItemsPresent) {
                CommonUserApiResponseModel firstObject = null;
                if (associationApiResponseModel != null && associationApiResponseModel.getResult().size() > 0) {
                    firstObject = associationApiResponseModel.getResult().get(0);
                } else if (doctorGroupedAssociations != null && doctorGroupedAssociations.size() > 0) {
                    firstObject = doctorGroupedAssociations.get(0).getDoctors().get(0);
                }

                if (firstObject != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.USER_DETAIL, firstObject);
                }
            }
            associationApiViewModel.baseApiResponseModelMutableLiveData.setValue(null);

        }

        if (UserType.isUserAssistant() && associationApiResponseModel != null && !associationApiResponseModel.getResult().isEmpty()) {
            List<String> doctorGuidList = new ArrayList<>();
            for (int i = 0; i < associationApiResponseModel.getResult().size(); i++) {
                if (!doctorGuidList.contains(associationApiResponseModel.getResult().get(i).getUser_guid())) {
                    doctorGuidList.add(associationApiResponseModel.getResult().get(i).getUser_guid());
                }
            }
            String doctorGuids = doctorGuidList.toString().replace("[", "").replace("]", "").trim();
            appPreference.setString(PreferenceConstants.ASSOCIATION_GUID_LIST, doctorGuids);

        }
        rvProvide.setScrollable(true);
        rvProvide.hideProgressBar();
    }

    private void getAssociationsList(boolean isShowProgress, boolean isfromsearch) {
        rvProvide.setScrollable(true);
        rvProvide.showOrhideEmptyState(false);
        if (UserType.isUserPatient()) {
            if (isfromsearch) {
                if (search_view.getCurrentSearchResult() == null || search_view.getCurrentSearchResult().isEmpty()) {
                    associationApiViewModel.getDoctorGroupedAssociations(isShowProgress);
                } else {
                    associationApiViewModel.getAssociationList(search_view.getCurrentSearchResult(), page, null, isShowProgress, false);
                }
            } else {
                associationApiViewModel.getDoctorGroupedAssociations(isShowProgress);
            }
        } else {
            associationApiViewModel.getAssociationList(search_view.getCurrentSearchResult(), page, null, isShowProgress, false);
        }
    }
}