package com.thealer.telehealer.views.home.monitoring.diet;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,February,2019
 */
public class DietListAdapter extends RecyclerView.Adapter<DietListAdapter.ViewHolder> {
    private List<DietListAdapterModel> dietListAdapterModelList = new ArrayList<>();
    private FragmentActivity activity;
    private Fragment fragment;
    private String selectedDate;

    public DietListAdapter(FragmentActivity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(activity);

        switch (viewType) {
            case DietConstant.TYEP_HEADER:
                view = inflater.inflate(R.layout.adapter_diet_header_view, viewGroup, false);
                break;
            case DietConstant.TYEP_DATA:
                view = inflater.inflate(R.layout.adapter_diet_data_view, viewGroup, false);
                break;
            case DietConstant.TYEP_ADD_NEW:
                view = inflater.inflate(R.layout.adapter_item_add_view, viewGroup, false);
                break;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (dietListAdapterModelList.get(position).getType()) {
            case DietConstant.TYEP_HEADER:
                viewHolder.headerTv.setText(dietListAdapterModelList.get(position).getTitle());
                viewHolder.bottomView.setVisibility(View.GONE);
                break;
            case DietConstant.TYEP_DATA:
                String imageUrl = null;

                if (dietListAdapterModelList.get(position).getData().getFood() != null) {
                    viewHolder.foodTitleTv.setText(dietListAdapterModelList.get(position).getData().getFood().getName());
                    imageUrl = dietListAdapterModelList.get(position).getData().getFood().getImage_url();
                }

                viewHolder.foodSubtitleTv.setText(dietListAdapterModelList.get(position).getData().getServing() + " " + dietListAdapterModelList.get(position).getData().getServing_unit());


                boolean authRequired = false;

                if (dietListAdapterModelList.get(position).getData().getImage_url() != null) {
                    imageUrl = dietListAdapterModelList.get(position).getData().getImage_url();
                    authRequired = true;
                }

                Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.foodIv, imageUrl, activity.getDrawable(R.drawable.diet_food_placeholder), authRequired, true);

                if (UserType.isUserPatient()) {
                    viewHolder.mDietItemCv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FoodDetailFragment foodDetailFragment = new FoodDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ArgumentKeys.FOOD_DETAIL, dietListAdapterModelList.get(position).getData());
                            bundle.putBoolean(ArgumentKeys.FOOD_DELETE_MODE, true);
                            foodDetailFragment.setArguments(bundle);
                            ((ShowSubFragmentInterface) activity).onShowFragment(foodDetailFragment);
                        }
                    });
                }
                break;
            case DietConstant.TYEP_ADD_NEW:
                viewHolder.btAddCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.startActivityFromFragment(fragment, new Intent(activity, SelectDietActivity.class)
                                        .putExtra(ArgumentKeys.SELECTED_DATE, selectedDate)
                                        .putExtra(ArgumentKeys.MEAL_TYPE, dietListAdapterModelList.get(position).getTitle()),
                                RequestID.REQ_SELECT_DIET);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dietListAdapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dietListAdapterModelList.get(position).getType();
    }

    public void setData(List<DietListAdapterModel> dietListAdapterModelList, String selectedDate) {
        this.dietListAdapterModelList = dietListAdapterModelList;
        this.selectedDate = selectedDate;

        notifyDataSetChanged();
    }

    public void clearData() {
        this.dietListAdapterModelList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView headerTv;
        private View bottomView;
        private ImageView foodIv;
        private TextView foodTitleTv;
        private TextView foodSubtitleTv;
        private CardView btAddCardView;
        private CardView mDietItemCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
            foodIv = (ImageView) itemView.findViewById(R.id.food_iv);
            foodTitleTv = (TextView) itemView.findViewById(R.id.food_title_tv);
            foodSubtitleTv = (TextView) itemView.findViewById(R.id.food_subtitle_tv);
            btAddCardView = (CardView) itemView.findViewById(R.id.bt_add_card_view);
            mDietItemCv = (CardView) itemView.findViewById(R.id.diet_item_cv);
        }
    }
}
