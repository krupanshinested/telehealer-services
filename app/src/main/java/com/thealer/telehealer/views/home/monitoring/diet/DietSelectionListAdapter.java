package com.thealer.telehealer.views.home.monitoring.diet;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.diet.DietApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aswin on 15,April,2019
 */
public class DietSelectionListAdapter extends RecyclerView.Adapter<DietSelectionListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private int mode;
    private Map<String, List<DietApiResponseModel>> dietApiResponseModelMap = new HashMap<>();
    private List<String> datesList = new ArrayList<>();
    private OnListItemSelectInterface onListItemSelectInterface;

    public DietSelectionListAdapter(FragmentActivity activity, int mode, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.mode = mode;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_diet_selection_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.headerTv.setText(datesList.get(i));

        List<DietApiResponseModel> dietApiResponseModelList = dietApiResponseModelMap.get(datesList.get(i));

        double calories = 0, carbs = 0, fat = 0, protien = 0;

        if (dietApiResponseModelList != null) {
            Map<String, Double> dietMap = DietApiResponseModel.getDisplayDiets(dietApiResponseModelList);

            calories = dietMap.get(FoodConstant.FOOD_ENERGY);
            carbs = dietMap.get(FoodConstant.FOOD_CARBS);
            fat = dietMap.get(FoodConstant.FOOD_FAT);
            protien = dietMap.get(FoodConstant.FOOD_PROTEIN);
        }

        if (calories > 0) {
            viewHolder.energyUnitTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.energyUnitTv.setVisibility(View.GONE);
        }
        viewHolder.energyCountTv.setText(DietApiResponseModel.getCalorieValue(calories));
        viewHolder.energyUnitTv.setText(DietApiResponseModel.getCalorieUnit(activity));

        if (carbs == 0) {
            viewHolder.carbsUnitTv.setVisibility(View.GONE);
        } else {
            viewHolder.carbsUnitTv.setVisibility(View.VISIBLE);
        }
        viewHolder.carbsCountTv.setText(DietApiResponseModel.getDisplayValue(carbs));
        viewHolder.carbsUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, carbs));

        if (fat == 0) {
            viewHolder.fatUnitTv.setVisibility(View.GONE);
        } else {
            viewHolder.fatUnitTv.setVisibility(View.VISIBLE);
        }
        viewHolder.fatCountTv.setText(DietApiResponseModel.getDisplayValue(fat));
        viewHolder.fatUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, fat));

        if (protien == 0) {
            viewHolder.proteinUnitTv.setVisibility(View.GONE);
        } else {
            viewHolder.proteinUnitTv.setVisibility(View.VISIBLE);
        }
        viewHolder.proteinCountTv.setText(DietApiResponseModel.getDisplayValue(protien));
        viewHolder.proteinUnitTv.setText(DietApiResponseModel.getDisplayUnit(activity, protien));

        viewHolder.dietRootLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ArgumentKeys.SELECTED_DATE, datesList.get(i));

                switch (mode) {
                    case Constants.EDIT_MODE:
                        if (dietApiResponseModelMap.get(datesList.get(i)).get(0).getOrder_id() == null) {
                            viewHolder.visitCb.setChecked(!viewHolder.visitCb.isChecked());
                            onListItemSelectInterface.onListItemSelected(i, bundle);
                        }

                        break;
                    case Constants.VIEW_MODE:
                        onListItemSelectInterface.onListItemSelected(i, bundle);
                        break;
                }
            }
        });

        switch (mode) {
            case Constants.VIEW_MODE:
                viewHolder.visitCb.setVisibility(View.GONE);
                break;
            case Constants.EDIT_MODE:
                if (dietApiResponseModelMap.get(datesList.get(i)).get(0).getOrder_id() == null) {
                    viewHolder.visitCb.setVisibility(View.VISIBLE);
                    viewHolder.itemCv.setCardBackgroundColor(activity.getColor(R.color.colorWhite));
                } else {
                    viewHolder.visitCb.setVisibility(View.GONE);
                    viewHolder.itemCv.setCardBackgroundColor(activity.getColor(R.color.colorGrey_light));
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public void setData(Map<String, List<DietApiResponseModel>> dietApiResponseModelMap) {
        this.dietApiResponseModelMap = dietApiResponseModelMap;
        datesList.clear();
        datesList.addAll(dietApiResponseModelMap.keySet());
        Collections.sort(this.datesList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Utils.getDateFromString(o2, Utils.defaultDateFormat).compareTo(Utils.getDateFromString(o1, Utils.defaultDateFormat));
            }
        });
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;
        private CardView itemCv;
        private ConstraintLayout dietDetailCl;
        private TextView energyCountTv;
        private TextView carbsCountTv;
        private TextView fatCountTv;
        private TextView proteinCountTv;
        private TextView energyUnitTv;
        private TextView carbsUnitTv;
        private TextView fatUnitTv;
        private TextView proteinUnitTv;
        private CheckBox visitCb;
        private LinearLayout dietRootLl;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dietRootLl = (LinearLayout) itemView.findViewById(R.id.diet_root_ll);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            dietDetailCl = (ConstraintLayout) itemView.findViewById(R.id.diet_detail_cl);
            energyCountTv = (TextView) itemView.findViewById(R.id.energy_count_tv);
            carbsCountTv = (TextView) itemView.findViewById(R.id.carbs_count_tv);
            fatCountTv = (TextView) itemView.findViewById(R.id.fat_count_tv);
            proteinCountTv = (TextView) itemView.findViewById(R.id.protein_count_tv);
            energyUnitTv = (TextView) itemView.findViewById(R.id.energy_unit_tv);
            carbsUnitTv = (TextView) itemView.findViewById(R.id.carbs_unit_tv);
            fatUnitTv = (TextView) itemView.findViewById(R.id.fat_unit_tv);
            proteinUnitTv = (TextView) itemView.findViewById(R.id.protein_unit_tv);

            headerTv.setVisibility(View.VISIBLE);
        }
    }
}
