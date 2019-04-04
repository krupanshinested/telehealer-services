package com.thealer.telehealer.views.home.monitoring.diet;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.common.CustomRecyclerView;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Aswin on 21,March,2019
 */
public class DietViewPagerAdapter extends PagerAdapter {

    private FragmentActivity activity;
    private Fragment fragment;
    private Map<String, ArrayList<DietApiResponseModel>> listMap = new HashMap<>();
    private String selectedDate;
    private int count = 3;

    private CustomRecyclerView primaryCrv;
    private DietListAdapter dietListAdapter;


    public DietViewPagerAdapter(FragmentActivity activity, Fragment fragment, String selectedDate) {
        this.activity = activity;
        this.fragment = fragment;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_diet_view_pager, container, false);
        container.addView(view);

        CustomRecyclerView dietListCrv = (CustomRecyclerView) view.findViewById(R.id.diet_list_crv);

        dietListCrv.setEmptyState(EmptyViewConstants.EMPTY_DIET);
        dietListCrv.showOrhideEmptyState(true);
        dietListCrv.getSwipeLayout().setEnabled(false);

        Log.e("aswin", "instantiateItem: " + count);

        if (position == 1) {
            primaryCrv = dietListCrv;
            dietListAdapter = new DietListAdapter(activity, fragment);
            primaryCrv.getRecyclerView().setAdapter(dietListAdapter);
            setData(listMap, selectedDate);
        }

        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    public void setData(Map<String, ArrayList<DietApiResponseModel>> listMap, String selectedDate) {
        this.listMap = listMap;
        this.selectedDate = selectedDate;

        if (UserType.isUserPatient()) {
            setAdapter();
        } else {
            if (listMap.containsKey(selectedDate) && !listMap.get(selectedDate).isEmpty()) {
                setAdapter();
            } else {
                if (dietListAdapter != null) {
                    dietListAdapter.clearData();
                    primaryCrv.showOrhideEmptyState(true);
                }
            }
        }
    }

    private void setAdapter() {
        List<DietListAdapterModel> finalList = createList(listMap.get(selectedDate));
        Log.e("aswin", "setAdapter: " + new Gson().toJson(finalList));
        if (!finalList.isEmpty()) {
            dietListAdapter.setData(finalList, getUtcDate(selectedDate));
            primaryCrv.showOrhideEmptyState(false);
        } else {
            primaryCrv.showOrhideEmptyState(true);
        }
    }

    public static List<DietListAdapterModel> createList(ArrayList<DietApiResponseModel> dietApiResponseModelArrayList) {
        List<DietListAdapterModel> finalList = new ArrayList<>();

        List<DietListAdapterModel> breakFastList = new ArrayList<>();
        List<DietListAdapterModel> lunchList = new ArrayList<>();
        List<DietListAdapterModel> dinnerList = new ArrayList<>();
        List<DietListAdapterModel> snacksList = new ArrayList<>();

        DietListAdapterModel dietListAdapterModel;

        dietListAdapterModel = new DietListAdapterModel();
        dietListAdapterModel.setType(DietConstant.TYEP_HEADER);
        dietListAdapterModel.setTitle(DietConstant.TYPE_BREAKFAST);

        breakFastList.add(dietListAdapterModel);

        dietListAdapterModel = new DietListAdapterModel();
        dietListAdapterModel.setType(DietConstant.TYEP_HEADER);
        dietListAdapterModel.setTitle(DietConstant.TYPE_LUNCH);
        lunchList.add(dietListAdapterModel);

        dietListAdapterModel = new DietListAdapterModel();
        dietListAdapterModel.setType(DietConstant.TYEP_HEADER);
        dietListAdapterModel.setTitle(DietConstant.TYPE_DINNER);
        dinnerList.add(dietListAdapterModel);

        dietListAdapterModel = new DietListAdapterModel();
        dietListAdapterModel.setType(DietConstant.TYEP_HEADER);
        dietListAdapterModel.setTitle(DietConstant.TYPE_SNACKS);
        snacksList.add(dietListAdapterModel);

        if (dietApiResponseModelArrayList != null) {
            for (int i = 0; i < dietApiResponseModelArrayList.size(); i++) {
                dietListAdapterModel = new DietListAdapterModel();
                dietListAdapterModel.setType(DietConstant.TYEP_DATA);
                dietListAdapterModel.setData(dietApiResponseModelArrayList.get(i));

                if (dietApiResponseModelArrayList.get(i).getMeal_type().equals(DietConstant.TYPE_BREAKFAST.toLowerCase())) {
                    breakFastList.add(dietListAdapterModel);
                } else if (dietApiResponseModelArrayList.get(i).getMeal_type().equals(DietConstant.TYPE_LUNCH.toLowerCase())) {
                    lunchList.add(dietListAdapterModel);
                } else if (dietApiResponseModelArrayList.get(i).getMeal_type().equals(DietConstant.TYPE_DINNER.toLowerCase())) {
                    dinnerList.add(dietListAdapterModel);
                } else if (dietApiResponseModelArrayList.get(i).getMeal_type().equals(DietConstant.TYPE_SNACKS.toLowerCase())) {
                    snacksList.add(dietListAdapterModel);
                }
            }
        }

        if (UserType.isUserPatient()) {
            dietListAdapterModel = new DietListAdapterModel();
            dietListAdapterModel.setType(DietConstant.TYEP_ADD_NEW);
            dietListAdapterModel.setTitle(DietConstant.TYPE_BREAKFAST);

            breakFastList.add(dietListAdapterModel);

            dietListAdapterModel = new DietListAdapterModel();
            dietListAdapterModel.setType(DietConstant.TYEP_ADD_NEW);
            dietListAdapterModel.setTitle(DietConstant.TYPE_LUNCH);
            lunchList.add(dietListAdapterModel);

            dietListAdapterModel = new DietListAdapterModel();
            dietListAdapterModel.setType(DietConstant.TYEP_ADD_NEW);
            dietListAdapterModel.setTitle(DietConstant.TYPE_DINNER);
            dinnerList.add(dietListAdapterModel);

            dietListAdapterModel = new DietListAdapterModel();
            dietListAdapterModel.setType(DietConstant.TYEP_ADD_NEW);
            dietListAdapterModel.setTitle(DietConstant.TYPE_SNACKS);
            snacksList.add(dietListAdapterModel);
        }

        if (breakFastList.size() > 1)
            finalList.addAll(breakFastList);

        if (lunchList.size() > 1)
            finalList.addAll(lunchList);

        if (dinnerList.size() > 1)
            finalList.addAll(dinnerList);

        if (snacksList.size() > 1)
            finalList.addAll(snacksList);

        return finalList;
    }

    private String getUtcDate(String formatedDate) {
        DateFormat inputFormat = new SimpleDateFormat(Utils.yyyy_mm_dd);
        inputFormat.setTimeZone(TimeZone.getDefault());
        DateFormat outputFormat = new SimpleDateFormat(Utils.UTCFormat);
        outputFormat.setTimeZone(Utils.UtcTimezone);
        try {
            Date date = inputFormat.parse(formatedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
            calendar.set(Calendar.MILLISECOND, Calendar.getInstance().get(Calendar.MILLISECOND));
            return outputFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
