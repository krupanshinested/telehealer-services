package com.thealer.telehealer.views.home.monitoring.diet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.diet.food.FoodListApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,February,2019
 */
public class FoodItemListAdapter extends RecyclerView.Adapter<FoodItemListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private List<FoodListApiResponseModel.HintsBean> hintsBeanList = new ArrayList<>();
    private String selectedDate, mealType, itemName;


    public FoodItemListAdapter(FragmentActivity activity) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_food_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.itemIv, hintsBeanList.get(i).getFood().getImage(), activity.getDrawable(R.drawable.diet_food_placeholder), false, true);
        viewHolder.itemTitleTv.setText(hintsBeanList.get(i).getFood().getLabel());
        viewHolder.itemSubTitleTv.setText(hintsBeanList.get(i).getFood().getCategory());


        viewHolder.caloriesTv.setText((int) hintsBeanList.get(i).getFood().getNutrients().getENERC_KCAL() + " " + ((hintsBeanList.get(i).getFood().getNutrients().getENERC_KCAL() > 1000) ? activity.getString(R.string.k_cals) : activity.getString(R.string.cals)));

        viewHolder.itemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.FOOD_DETAIL, hintsBeanList.get(i));
                bundle.putString(ArgumentKeys.SEARCHED_ITEM, itemName);
                bundle.putString(ArgumentKeys.SELECTED_DATE, selectedDate);
                bundle.putString(ArgumentKeys.MEAL_TYPE, mealType);

                FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
                foodDetailFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(foodDetailFragment);
            }
        });
    }

    public void setData(List<FoodListApiResponseModel.HintsBean> foodListApiResponseModel, int page, String itemName) {
        this.itemName = itemName;
        if (page == 0) {
            this.hintsBeanList = foodListApiResponseModel;
        } else {
            this.hintsBeanList.addAll(foodListApiResponseModel);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hintsBeanList.size();
    }

    public void setSelectedDate(String selectedDate, String mealType) {
        this.selectedDate = selectedDate;
        this.mealType = mealType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIv;
        private TextView itemTitleTv;
        private TextView itemSubTitleTv;
        private TextView caloriesTv;
        private View bottomView;
        private ConstraintLayout itemCl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCl = (ConstraintLayout) itemView.findViewById(R.id.item_cl);
            itemIv = (ImageView) itemView.findViewById(R.id.item_iv);
            itemTitleTv = (TextView) itemView.findViewById(R.id.item_title_tv);
            itemSubTitleTv = (TextView) itemView.findViewById(R.id.item_sub_title_tv);
            caloriesTv = (TextView) itemView.findViewById(R.id.calories_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
        }
    }
}
