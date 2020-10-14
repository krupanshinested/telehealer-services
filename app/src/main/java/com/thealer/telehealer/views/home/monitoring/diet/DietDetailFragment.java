package com.thealer.telehealer.views.home.monitoring.diet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.PDFUrlResponse;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.apilayer.models.vitalReport.VitalReportApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.DateUtil;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 20,February,2019
 */
public class DietDetailFragment extends BaseFragment implements View.OnClickListener {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private ConstraintLayout dietDetailCl;
    private TextView energyCountTv;
    private TextView carbsCountTv;
    private TextView fatCountTv;
    private TextView proteinCountTv;
    private TextView energyLabel;
    private TextView carbsLabel;
    private TextView fatLabel;
    private TextView proteinLabel;
    private TextView energyUnitTv;
    private TextView carbsUnitTv;
    private TextView fatUnitTv;
    private TextView proteinUnitTv;
    private MaterialCalendarView calendarview;
    private ViewPager dietListVp;

    private String userGuid = null;
    private String selectedDate, displayDate;
    private ArrayList<DietApiResponseModel> dietApiResponseModelArrayList;
    private double calories = 0, carbs = 0, fat = 0, protien = 0;
    private Map<String, ArrayList<DietApiResponseModel>> listMap = new HashMap<>();

    private ArrayList<String> dietPrintOptions;

    private OnCloseActionInterface onCloseActionInterface;
    private AttachObserverInterface attachObserverInterface;
    private DateBroadcastReceiver dateBroadcastReceiver = new DateBroadcastReceiver() {
        @Override
        public void onDateReceived(String formatedDate) {
            displayDate = formatedDate;
            selectedDate = getFormatedDate(formatedDate);
            String[] calendar = selectedDate.split("-");
            CalendarDay calendarDay = CalendarDay.from(Integer.parseInt(calendar[0]), Integer.parseInt(calendar[1]), Integer.parseInt(calendar[2]));
            calendarview.clearSelection();
            calendarview.setCurrentDate(calendarDay, true);
            calendarview.setDateSelected(calendarDay, true);
            setToolbarTitle();

            if (listMap.containsKey(selectedDate)) {
                dietApiResponseModelArrayList = listMap.get(selectedDate);
                setData();
            } else {
                getDietList();
            }
        }

    };

    private BroadcastReceiver onDietRemove = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String removedDate = intent.getStringExtra(Constants.EXTRA_REMOVED_DATE);
                if (removedDate != null) {
                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        String formattedDate = outputFormat.format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(removedDate));
                        listMap.remove(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    };

    private DietApiViewModel dietApiViewModel;
    private DietViewPagerAdapter dietViewPagerAdapter;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private UserBean commonUserApiResponseModel;
    private CustomRecyclerView dietListCrv;
    private String startDate = null;
    private String endDate = null;
    private String selectedItem, selectedFilter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) context;
        attachObserverInterface = (AttachObserverInterface) context;
        showSubFragmentInterface = (ShowSubFragmentInterface) context;
        dietApiViewModel = new ViewModelProvider(this).get(DietApiViewModel.class);
        attachObserverInterface.attachObserver(dietApiViewModel);

        dietApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    dietApiResponseModelArrayList = (ArrayList<DietApiResponseModel>) (Object) baseApiResponseModels;
                    listMap.put(selectedDate, dietApiResponseModelArrayList);
                    setData();
                }
            }
        });

        dietApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel instanceof PDFUrlResponse) {
                        PDFUrlResponse pdfUrlResponse = (PDFUrlResponse) baseApiResponseModel;

                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.diet_report));
                        bundle.putString(ArgumentKeys.PDF_URL, pdfUrlResponse.getUrl());
                        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                        pdfViewerFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);

                    }
                }
            }
        });
    }


    private void setData() {
        dietViewPagerAdapter.setData(listMap, selectedDate);
        dietListVp.setCurrentItem(1, false);

        calculateCalories();
    }

    private void calculateCalories() {
        calories = 0;
        carbs = 0;
        fat = 0;
        protien = 0;

        for (int i = 0; i < dietApiResponseModelArrayList.size(); i++) {
            if (dietApiResponseModelArrayList.get(i).getFood() != null &&
                    dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients() != null) {
                if (dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY) != null)
                    calories = calories + dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_ENERGY).getQuantity();

                if (dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS) != null)
                    carbs = carbs + dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_CARBS).getQuantity();

                if (dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT) != null)
                    fat = fat + dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_FAT).getQuantity();

                if (dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN) != null)
                    protien = protien + dietApiResponseModelArrayList.get(i).getFood().getTotalNutrients().get(FoodConstant.FOOD_PROTEIN).getQuantity();
            }
        }

        setTotalValues();

    }

    private void setToolbarTitle() {
        if (selectedDate.equals(getFormatedDate(Utils.getCurrentFomatedDate())))
            toolbarTitle.setText(getString(R.string.today));
        else
            toolbarTitle.setText(displayDate);
    }

    private void setTotalValues() {

        if (calories == 0) {
            energyCountTv.setText("-");
            energyUnitTv.setVisibility(View.GONE);
        } else {
//            String value;
//
//            if (calories >= 1000) {
//                value = String.format("%.1f", (float) calories / 1000);
//            } else {
//                value = String.valueOf((int) calories);
//            }
            energyCountTv.setText(String.valueOf((int) calories));
            energyUnitTv.setText(getString(R.string.cal));
            energyUnitTv.setVisibility(View.VISIBLE);
        }

        if (carbs == 0) {
            carbsCountTv.setText("-");
            carbsUnitTv.setVisibility(View.GONE);
        } else {
            String value;

            if (carbs >= 1000) {
                value = String.format("%.1f", (float) carbs / 1000);
            } else {
                value = String.valueOf((int) carbs);
            }
            carbsCountTv.setText(value);
            carbsUnitTv.setText((carbs > 1000) ? getString(R.string.kg) : getString(R.string.g));
            carbsUnitTv.setVisibility(View.VISIBLE);
        }

        if (fat == 0) {
            fatCountTv.setText("-");
            fatUnitTv.setVisibility(View.GONE);
        } else {
            String value;

            if (fat >= 1000) {
                value = String.format("%.1f", (float) fat / 1000);
            } else {
                value = String.valueOf((int) fat);
            }
            fatCountTv.setText(value);
            fatUnitTv.setText((fat > 1000) ? getString(R.string.kg) : getString(R.string.g));
            fatUnitTv.setVisibility(View.VISIBLE);
        }

        if (protien == 0) {
            proteinCountTv.setText("-");
            proteinUnitTv.setVisibility(View.GONE);
        } else {
            String value;

            if (protien >= 1000) {
                value = String.format("%.1f", (float) protien);
            } else {
                value = String.valueOf((int) protien);
            }
            proteinCountTv.setText(value);
            proteinUnitTv.setText((protien > 1000) ? getString(R.string.kg) : getString(R.string.g));
            proteinUnitTv.setVisibility(View.VISIBLE);
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
        View view = inflater.inflate(R.layout.fragment_diet_detail, container, false);
        initView(view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        dietDetailCl = (ConstraintLayout) view.findViewById(R.id.diet_detail_cl);
        energyCountTv = (TextView) view.findViewById(R.id.energy_count_tv);
        carbsCountTv = (TextView) view.findViewById(R.id.carbs_count_tv);
        fatCountTv = (TextView) view.findViewById(R.id.fat_count_tv);
        proteinCountTv = (TextView) view.findViewById(R.id.protein_count_tv);
        energyLabel = (TextView) view.findViewById(R.id.energy_label);
        carbsLabel = (TextView) view.findViewById(R.id.carbs_label);
        fatLabel = (TextView) view.findViewById(R.id.fat_label);
        proteinLabel = (TextView) view.findViewById(R.id.protein_label);
        energyUnitTv = (TextView) view.findViewById(R.id.energy_unit_tv);
        carbsUnitTv = (TextView) view.findViewById(R.id.carbs_unit_tv);
        fatUnitTv = (TextView) view.findViewById(R.id.fat_unit_tv);
        proteinUnitTv = (TextView) view.findViewById(R.id.protein_unit_tv);
        calendarview = (MaterialCalendarView) view.findViewById(R.id.calendarview);
        dietListVp = (ViewPager) view.findViewById(R.id.diet_list_vp);
        dietListCrv = (CustomRecyclerView) view.findViewById(R.id.diet_list_crv);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        toolbar.getMenu().removeItem(R.id.send_fax_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:

                        Utils.showMonitoringFilter(null, getActivity(), new OnListItemSelectInterface() {
                            @Override
                            public void onListItemSelected(int position, Bundle bundle) {
                                selectedItem = bundle.getString(Constants.SELECTED_ITEM);
                                startDate = null;
                                endDate = null;

                                if (selectedItem != null) {
                                    if (selectedItem.equals(getString(R.string.last_week))) {
                                        selectedFilter = VitalReportApiViewModel.LAST_WEEK;
                                    } else if (selectedItem.equals(getString(R.string.all))) {
                                        selectedFilter = VitalReportApiViewModel.ALL;
                                    } else {
                                        selectedFilter = null;
                                        startDate = bundle.getString(ArgumentKeys.START_DATE);
                                        endDate = bundle.getString(ArgumentKeys.END_DATE);

                                    }
                                }

                                generatePdf();

                            }
                        });
                        break;
                }
                return true;
            }
        });

        dietPrintOptions = DietConstant.getDietPrintOptions(getActivity());

        calendarview.state().edit()
                .setMaximumDate(CalendarDay.today())
                .setMinimumDate(CalendarDay.from(2019, 1, 1))
                .commit();

        calendarview.setTopbarVisible(false);
        calendarview.setCurrentDate(CalendarDay.today());
        calendarview.setDateSelected(CalendarDay.today(), true);
        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                setCurrentDate(calendarDay);
            }
        });

        selectedDate = getFormatedDate(Utils.getCurrentFomatedDate());
        setToolbarTitle();

        boolean isViewMode = false;

        if (!UserType.isUserPatient()) {
            if (getArguments() != null) {
                if (getArguments().getSerializable(Constants.USER_DETAIL) != null) {
                    commonUserApiResponseModel = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
                    if (commonUserApiResponseModel != null) {
                        userGuid = commonUserApiResponseModel.getUser_guid();
                    }
                }

                if (getArguments().getSerializable(ArgumentKeys.DIET_ITEM) != null) {
                    isViewMode = true;

                    toolbar.getMenu().clear();
                    toolbarTitle.setCompoundDrawables(null, null, null, null);

                    DietDetailModel dietDetailModel = (DietDetailModel) getArguments().getSerializable(ArgumentKeys.DIET_ITEM);

                    String date = dietDetailModel.getDate();
                    selectedDate = getFormatedDate(date);
                    toolbarTitle.setText(date);

                    calendarview.setVisibility(View.GONE);
                    dietListVp.setVisibility(View.GONE);


                    dietApiResponseModelArrayList = new ArrayList<>();
                    dietApiResponseModelArrayList.addAll(dietDetailModel.getDietApiResponseModelList());
                    calculateCalories();

                    dietListCrv.setVisibility(View.VISIBLE);
                    dietListCrv.setEmptyState(EmptyViewConstants.EMPTY_DIET);
                    dietListCrv.getSwipeLayout().setEnabled(false);

                    DietListAdapter dietListAdapter = new DietListAdapter(getActivity(), this);
                    dietListCrv.getRecyclerView().setAdapter(dietListAdapter);

                    List<DietListAdapterModel> adapterModelList = DietViewPagerAdapter.createList(dietApiResponseModelArrayList);
                    if (!adapterModelList.isEmpty()) {
                        dietListAdapter.setData(adapterModelList, Utils.getUTCFormat(selectedDate, Utils.yyyy_mm_dd));
                        dietListCrv.showOrhideEmptyState(false);
                    } else {
                        dietListCrv.showOrhideEmptyState(true);
                    }
                }
            }
        }


        backIv.setOnClickListener(this);
        if (!isViewMode) {
            toolbarTitle.setOnClickListener(this);
            setUpViewPager();
            dietListVp.postDelayed(() -> {
                if (calendarview.getSelectedDate() != null)
                    setCurrentDate(calendarview.getSelectedDate());
                else
                    setCurrentDate(CalendarDay.today());

            }, 100);
        }

    }

    private void showDietPrintOptions() {
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_time_period),
                dietPrintOptions,
                new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
//                        generatePdf(position);
                    }

                    @Override
                    public void didCancelled() {

                    }
                });
        itemPickerDialog.setCancelable(false);
        itemPickerDialog.show();
    }

    private void generatePdf() {
        dietApiViewModel.getDietPdf(selectedFilter, startDate, endDate, userGuid, null, true);
    }

    private void setUpViewPager() {
        dietViewPagerAdapter = new DietViewPagerAdapter(getActivity(), this, selectedDate);
        dietListVp.setAdapter(dietViewPagerAdapter);
        dietListVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.e(TAG, "onPageSelected: " + i);
                CalendarDay calendarDay;
                switch (i) {
                    case 0:
                        calendarDay = calendarview.getSelectedDate();
                        setCurrentDate(CalendarDay.from(calendarDay.getDate().minusDays(1)));
                        break;
                    case 2:
                        if (calendarview.getSelectedDate().isBefore(CalendarDay.today())) {
                            calendarDay = calendarview.getSelectedDate();
                            setCurrentDate(CalendarDay.from(calendarDay.getDate().plusDays(1)));
                        } else {
                            calendarDay = calendarview.getSelectedDate();
                            setCurrentDate(CalendarDay.from(calendarDay.getDate()));
//                            dietListVp.setCurrentItem(1);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setCurrentDate(CalendarDay calendarDay) {
        dateBroadcastReceiver.onDateReceived(Utils.getFormatedDate(calendarDay.getYear(), calendarDay.getMonth() - 1, calendarDay.getDay()));
    }

    private void getDietList() {
        Log.e(TAG, "getDietList: " + selectedDate);
        dietApiViewModel.getUserDietDetails(selectedDate, userGuid, null, null, true);
    }

    private void getAllDietList() {
        dietApiViewModel.getUserDietDetails(null, userGuid, null, null, true);
    }

    private void getDietUserList(boolean isShowProgress) {
        dietApiViewModel.getUserDietDetails(selectedFilter, startDate, endDate, null, null, isShowProgress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_title:
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DATE_PICKER_TYPE, Constants.DIET_CALENDAR);
                datePickerDialogFragment.setArguments(bundle);
                datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), datePickerDialogFragment.getClass().getSimpleName());
                break;
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
        }
    }

    private String getFormatedDate(String formatedDate) {
        DateFormat inputFormat = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return outputFormat.format(inputFormat.parse(formatedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestID.REQ_SELECT_DIET && resultCode == Activity.RESULT_OK) {
            getDietList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dateBroadcastReceiver, new IntentFilter(Constants.DATE_PICKER_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onDietRemove, new IntentFilter(Constants.DIET_REMOVED_INTENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onDietRemove);
    }
}
