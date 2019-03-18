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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BaseAdapter extends RecyclerView.Adapter {

    public static final int headerType = 1;
    public static final int bodyType = 2;

    protected ArrayList<BaseAdapterModel> items = new ArrayList<>();
    protected boolean sortByAscending = false;

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

        items = new ArrayList<>();

        Collections.sort(modelList, new Comparator<BaseAdapterObjectModel>() {
            public int compare(BaseAdapterObjectModel obj1, BaseAdapterObjectModel obj2) {

                BaseAdapterObjectModel first,second;
                if (sortByAscending) {
                    first = obj1;
                    second = obj2;
                } else {
                    first = obj2;
                    second = obj1;
                }

                if (obj1 == null || obj2 == null) {
                    return 0;
                } else if ((obj1.getComparableObject() instanceof Date)) {
                    return (((Date) first.getComparableObject()).compareTo((Date) second.getComparableObject()));
                } else if ((obj1.getComparableObject() instanceof Integer)) {
                    return (((Integer) first.getComparableObject()).compareTo((Integer) second.getComparableObject()));
                }  else if ((obj1.getComparableObject() instanceof Double)) {
                    return (((Double) first.getComparableObject()).compareTo((Double) second.getComparableObject()));
                } else if ((obj1.getComparableObject() instanceof String)) {
                    return (((String) first.getComparableObject()).compareTo((String) second.getComparableObject()));
                } else {
                    return 0;
                }
            }
        });

        for (BaseAdapterObjectModel model : modelList) {

            if (map.get(model.getAdapterTitle()) == null) {
                items.add(new BaseAdapterModel(model.getAdapterTitle()));
            }

            ArrayList<BaseAdapterObjectModel> values = map.get(model.getAdapterTitle());
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(model);
            map.put(model.getAdapterTitle(),values);
            items.add(new BaseAdapterModel(model));
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
