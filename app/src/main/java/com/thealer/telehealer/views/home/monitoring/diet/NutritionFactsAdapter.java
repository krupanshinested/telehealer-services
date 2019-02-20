package com.thealer.telehealer.views.home.monitoring.diet;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.diet.NutrientsDetailModel;
import com.thealer.telehealer.apilayer.models.diet.food.NutrientsDetailBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aswin on 14,March,2019
 */
public class NutritionFactsAdapter extends RecyclerView.Adapter<NutritionFactsAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<NutrientsDetailModel> nutrientsDetailModelList;
    private int count = 1;
    private Map<String, NutrientsDetailBean> totalNutrients;

    public NutritionFactsAdapter(FragmentActivity activity) {
        this.activity = activity;
        nutrientsDetailModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_nutrition_fact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (nutrientsDetailModelList.get(i).isSubData()) {
            viewHolder.nutritionTv.setTextColor(activity.getColor(R.color.colorGrey));
            viewHolder.nutritionAmountTv.setTextColor(activity.getColor(R.color.colorGrey));
        }

        viewHolder.nutritionTv.setText(nutrientsDetailModelList.get(i).getLabel());
        float total = (float) (nutrientsDetailModelList.get(i).getTotal() * count);
        viewHolder.nutritionAmountTv.setText(String.format("%.2f", total) + " " + nutrientsDetailModelList.get(i).getUnit());
    }

    @Override
    public int getItemCount() {
        return nutrientsDetailModelList.size();
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    public void setData(Map<String, NutrientsDetailBean> totalNutrients) {
        this.totalNutrients = totalNutrients;
        generateList();
    }

    private void generateList() {
        nutrientsDetailModelList.clear();

        Set<String> keySet = totalNutrients.keySet();

        List<NutrientsDetailModel> fatSubList = new ArrayList<>();

        for (String key : FoodConstant.FAT_SET) {
            if (keySet.contains(key)) {
                NutrientsDetailModel nutrientsDetailModel = new NutrientsDetailModel(totalNutrients.get(key).getQuantity(),
                        totalNutrients.get(key).getLabel(),
                        totalNutrients.get(key).getLabel(),
                        totalNutrients.get(key).getQuantity(),
                        false,
                        totalNutrients.get(key).getUnit());
                nutrientsDetailModel.setSubData(true);
                fatSubList.add(nutrientsDetailModel);

                keySet.remove(key);
            }
        }

        List<NutrientsDetailModel> carbsSubList = new ArrayList<>();

        for (String key : FoodConstant.CARBS_SET) {
            if (keySet.contains(key)) {
                NutrientsDetailModel nutrientsDetailModel = new NutrientsDetailModel(totalNutrients.get(key).getQuantity(),
                        totalNutrients.get(key).getLabel(),
                        totalNutrients.get(key).getLabel(),
                        totalNutrients.get(key).getQuantity(),
                        false,
                        totalNutrients.get(key).getUnit());
                nutrientsDetailModel.setSubData(true);
                carbsSubList.add(nutrientsDetailModel);

                keySet.remove(key);
            }
        }

        for (String key : keySet) {
            NutrientsDetailModel nutrientsDetailModel = new NutrientsDetailModel(totalNutrients.get(key).getQuantity(),
                    totalNutrients.get(key).getLabel(),
                    totalNutrients.get(key).getLabel(),
                    totalNutrients.get(key).getQuantity(),
                    false,
                    totalNutrients.get(key).getUnit());

            nutrientsDetailModelList.add(nutrientsDetailModel);

            if (key.equals(FoodConstant.FOOD_FAT)) {
                nutrientsDetailModelList.addAll(fatSubList);
            } else if (key.equals(FoodConstant.FOOD_CARBS)) {
                nutrientsDetailModelList.addAll(carbsSubList);
            }
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nutritionTv;
        private TextView nutritionAmountTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nutritionTv = (TextView) itemView.findViewById(R.id.nutrition_tv);
            nutritionAmountTv = (TextView) itemView.findViewById(R.id.nutrition_amount_tv);
        }
    }
}
