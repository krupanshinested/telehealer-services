package com.thealer.telehealer.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BaseAdapter extends RecyclerView.Adapter {

    public static final int headerType = 1;
    public static final int bodyType = 2;

    protected ArrayList<BaseAdapterModel> items = new ArrayList<>();


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        BaseAdapterModel baseAdapterModel = items.get(position);

        return baseAdapterModel.getType();
    }

    protected void generateModel(ArrayList<BaseAdapterObjectModel> modelList) {
        HashMap<String,ArrayList<BaseAdapterObjectModel>> map = new HashMap<>();

        for (BaseAdapterObjectModel model : modelList) {
            if (map.get(model.getAdapterTitle()) != null) {
                map.get(model.getAdapterTitle()).add(model);
            } else {
                ArrayList<BaseAdapterObjectModel> models = new ArrayList<>();
                models.add(model);
                map.put(model.getAdapterTitle(), models);
            }
        }

        items = new ArrayList<>();

        for (String key : map.keySet()) {
            items.add(new BaseAdapterModel(key));

            if ( map.get(key) != null) {
                for (BaseAdapterObjectModel model : map.get(key)) {
                    items.add(new BaseAdapterModel(model));
                }
            }
        }

    }


    public class TitleHolder extends RecyclerView.ViewHolder {

        public TextView titleTv;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
        }
    }


    public class BaseAdapterModel {

        @Nullable
        public BaseAdapterObjectModel actualValue;

        @Nullable
        public String title;

        public int getType() {
            if (title != null) {
                return BaseAdapter.headerType;
            } else {
                return BaseAdapter.bodyType;
            }
        }

        BaseAdapterModel(String title) {
            this.title = title;
        }

        BaseAdapterModel(BaseAdapterObjectModel actualValue) {
            this.actualValue = actualValue;
        }


    }
}
