package com.thealer.telehealer.views.home.monitoring.diet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.apilayer.models.diet.DietApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.DatePickerDialogFragment;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;
import com.thealer.telehealer.views.common.DateBroadcastReceiver;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
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
    private boolean isPdfGeneration = false;

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
    private DietApiViewModel dietApiViewModel;
    private DietViewPagerAdapter dietViewPagerAdapter;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private CommonUserApiResponseModel commonUserApiResponseModel;
    private CustomRecyclerView dietListCrv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) context;
        attachObserverInterface = (AttachObserverInterface) context;
        showSubFragmentInterface = (ShowSubFragmentInterface) context;
        dietApiViewModel = ViewModelProviders.of(this).get(DietApiViewModel.class);
        attachObserverInterface.attachObserver(dietApiViewModel);

        dietApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    dietApiResponseModelArrayList = (ArrayList<DietApiResponseModel>) (Object) baseApiResponseModels;
                    if (!isPdfGeneration) {
                        listMap.put(selectedDate, dietApiResponseModelArrayList);
                        setData();
                    } else {
                        isPdfGeneration = false;
                        addAllDataToList(dietApiResponseModelArrayList);
                    }

                }
            }
        });
    }

    private void addAllDataToList(ArrayList<DietApiResponseModel> dietApiResponseModelArrayList) {
        listMap.clear();

        for (DietApiResponseModel dietResponse : dietApiResponseModelArrayList) {
            String date = Utils.getDayMonthYear(dietResponse.getDate());
            date = getFormatedDate(date);

            if (date != null) {
                ArrayList<DietApiResponseModel> modelArrayList = new ArrayList<>();
                if (!listMap.containsKey(date)) {
                    listMap.put(date, modelArrayList);
                }

                modelArrayList = listMap.get(date);

                if (modelArrayList == null) {
                    modelArrayList = new ArrayList<>();
                }
                modelArrayList.add(dietResponse);

                listMap.put(date, modelArrayList);
            }
        }

        showDietPrintOptions();
        Log.e(TAG, "addAllDataToList: " + listMap.keySet().toString());
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
            toolbarTitle.setText("Today");
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
                        getAllDietList();
                        break;
                }
                return true;
            }
        });

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
            getDietList();
        }

    }

    private void showDietPrintOptions() {
        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(getActivity(), getString(R.string.choose_time_period),
                DietConstant.dietPrintOptions,
                new PickerListener() {
                    @Override
                    public void didSelectedItem(int position) {
                        generatePdf(position);
                    }

                    @Override
                    public void didCancelled() {

                    }
                });
        itemPickerDialog.setCancelable(false);
        itemPickerDialog.show();
    }

    private void generatePdf(int position) {
        Calendar calendar = Calendar.getInstance();

        Map<String, ArrayList<DietApiResponseModel>> pdfList = new HashMap<>();

        Set<String> dates = listMap.keySet();

        if (DietConstant.dietPrintOptions.get(position).equals(DietConstant.DIET_PRINT_ALL)) {
            for (String date : dates) {
                String utcdate = Utils.getUTCFormat(date, "yyyy-MM-dd");
                pdfList.put(utcdate, listMap.get(date));
            }

        } else {
            switch (DietConstant.dietPrintOptions.get(position)) {
                case DietConstant.PRINT_1_WEEK:
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
                case DietConstant.PRINT_2_WEEK:
                    calendar.add(Calendar.DAY_OF_MONTH, -14);
                    break;
                case DietConstant.PRINT_1_MONTH:
                    calendar.add(Calendar.MONTH, -1);
                    break;
            }

            for (String date : dates) {
                String utcdate = Utils.getUTCFormat(date, "yyyy-MM-dd");
                Log.e(TAG, "generatePdf: " + date + "\t" + utcdate + "\t" + calendar.getTime());
                if (Utils.getDateFromString(utcdate).compareTo(calendar.getTime()) >= 0) {
                    pdfList.put(utcdate, listMap.get(date));
                }
            }
        }

        if (pdfList.size() > 0) {
            DietPdfGenerator dietPdfGenerator = new DietPdfGenerator(getActivity());
            String htmlContent = dietPdfGenerator.getPdfHtmlContent(pdfList, commonUserApiResponseModel);

            PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentKeys.HTML_FILE, htmlContent);
            bundle.putString(ArgumentKeys.PDF_TITLE, getString(R.string.diet_report));
            pdfViewerFragment.setArguments(bundle);
            showSubFragmentInterface.onShowFragment(pdfViewerFragment);

        } else {
            Utils.showAlertDialog(getActivity(), getString(R.string.alert), getString(R.string.no_data_available_for) + " " + DietConstant.dietPrintOptions.get(position),
                    getString(R.string.ok), null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
        }

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
                            dietListVp.setCurrentItem(1);
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
        dietApiViewModel.getUserDietDetails(selectedDate, userGuid, null, null, true);
    }

    private void getAllDietList() {
        isPdfGeneration = true;
        dietApiViewModel.getUserDietDetails(null, userGuid, null, null, true);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dateBroadcastReceiver);
    }
}
