package com.thealer.telehealer.views.home.vitals;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.vitals.StethBean;
import com.thealer.telehealer.apilayer.models.vitals.VitalsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aswin on 02,July,2019
 */
public class NewVitalsDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 1;
    private final int TYPE_ITEM = 2;

    private List<VitalAdapterModel> adapterModelList = new ArrayList<>();
    private FragmentActivity activity;
    private List<VitalsApiResponseModel> vitalsApiResponseModelList;
    private boolean imageVisible;
    private int mode;
    private OnListItemSelectInterface onListItemSelectInterface;

    public NewVitalsDetailListAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public NewVitalsDetailListAdapter(FragmentActivity activity, boolean imageVisible, int mode, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.imageVisible = imageVisible;
        this.mode = mode;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case TYPE_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_list_header_view, viewGroup, false);
                return new HeaderHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_vitals_list_view, viewGroup, false);
                return new ItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        switch (adapterModelList.get(i).getType()) {
            case TYPE_HEADER:
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.headerTv.setText(adapterModelList.get(i).getDate());
                break;
            case TYPE_ITEM:
                ItemHolder itemHolder = (ItemHolder) holder;


                VitalsApiResponseModel vitalsApiResponseModel = adapterModelList.get(i).getVitalsApiResponseModel();

                itemHolder.timeTv.setText(Utils.getFormatedTime(vitalsApiResponseModel.getCreated_at()));
                itemHolder.descriptionTv.setText(vitalsApiResponseModel.getCapturedBy(activity));

                if (!vitalsApiResponseModel.getType().equals(SupportedMeasurementType.stethoscope)) {
                    itemHolder.valueTv.setText(vitalsApiResponseModel.getValue().toString());
                    itemHolder.unitTv.setText(SupportedMeasurementType.getVitalUnit(vitalsApiResponseModel.getType()));
                    itemHolder.itemCv.setCardElevation(0);
                    itemHolder.itemCv.setRadius(0);
                } else {

                    StethBean stethBean = vitalsApiResponseModel.getStethBean();

                    itemHolder.unitTv.setText(stethBean.getSegments().size() + " - " + activity.getString(R.string.segment));

                    itemHolder.vitalIv.setImageDrawable(activity.getDrawable(vitalsApiResponseModel.getStethIoImage()));
                    itemHolder.vitalIv.setVisibility(View.VISIBLE);

                    ShowSubFragmentInterface showSubFragmentInterface = (ShowSubFragmentInterface) activity;

                    itemHolder.itemCv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StethoscopeDetailViewFragment stethoscopeDetailViewFragment = new StethoscopeDetailViewFragment();

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ArgumentKeys.VITAL_DETAIL, vitalsApiResponseModel);
                            stethoscopeDetailViewFragment.setArguments(bundle);

                            showSubFragmentInterface.onShowFragment(stethoscopeDetailViewFragment);
                        }
                    });
                }

                if (vitalsApiResponseModel.isAbnormal()) {
                    itemHolder.abnormalIndicatorCl.setVisibility(View.VISIBLE);
                }

                if (imageVisible) {
                    itemHolder.vitalIv.setVisibility(View.VISIBLE);
                    int drawable = SupportedMeasurementType.getDrawable(vitalsApiResponseModel.getType());
                    if (drawable != 0) {
                        itemHolder.vitalIv.setImageDrawable(activity.getDrawable(drawable));
                    }
                }

                switch (mode) {
                    case Constants.VIEW_MODE:
                        itemHolder.visitCb.setVisibility(View.GONE);
                        break;
                    case Constants.EDIT_MODE:
                        if (vitalsApiResponseModel.getOrder_id() == null) {
                            itemHolder.visitCb.setVisibility(View.VISIBLE);
                            itemHolder.vitalRootCl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    itemHolder.visitCb.setChecked(!itemHolder.visitCb.isChecked());

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(ArgumentKeys.SELECTED_VITAL, vitalsApiResponseModel);

                                    onListItemSelectInterface.onListItemSelected(-1, bundle);
                                }
                            });
                            itemHolder.itemCv.setCardBackgroundColor(activity.getColor(R.color.colorWhite));
                            itemHolder.vitalIv.setImageTintList(ColorStateList.valueOf(activity.getColor(R.color.app_gradient_start)));
                        } else {
                            itemHolder.visitCb.setVisibility(View.GONE);
                            itemHolder.itemCv.setCardBackgroundColor(activity.getColor(R.color.colorGrey_light));
                            itemHolder.vitalIv.setImageTintList(ColorStateList.valueOf(activity.getColor(R.color.colorGrey)));
                        }
                        break;
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return adapterModelList.get(position).getType();
    }

    public void setData(List<VitalsApiResponseModel> vitalsApiResponseModelArrayList, int page) {
        if (page == 1) {
            this.vitalsApiResponseModelList = vitalsApiResponseModelArrayList;
        } else {
            this.vitalsApiResponseModelList.addAll(vitalsApiResponseModelArrayList);
        }

        Collections.sort(vitalsApiResponseModelList, new Comparator<VitalsApiResponseModel>() {
            @Override
            public int compare(VitalsApiResponseModel o1, VitalsApiResponseModel o2) {
                return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
            }
        });

        generateAdapterList();
    }

    private void generateAdapterList() {
        adapterModelList.clear();

        for (int i = 0; i < vitalsApiResponseModelList.size(); i++) {
            if (i == 0 || !Utils.getDayMonthYear(vitalsApiResponseModelList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(vitalsApiResponseModelList.get(i - 1).getCreated_at()))) {
                adapterModelList.add(new VitalAdapterModel(TYPE_HEADER, Utils.getDayMonthYear(vitalsApiResponseModelList.get(i).getCreated_at())));
            }

            adapterModelList.add(new VitalAdapterModel(TYPE_ITEM, vitalsApiResponseModelList.get(i)));
        }

        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout vitalRootCl;
        private LinearLayout checkboxLl;
        private CheckBox visitCb;
        private CardView itemCv;
        private ImageView vitalIv;
        private TextView valueTv;
        private TextView unitTv;
        private TextView descriptionTv;
        private ConstraintLayout abnormalIndicatorCl;
        private TextView timeTv;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            vitalRootCl = (ConstraintLayout) itemView.findViewById(R.id.vital_root_cl);
            checkboxLl = (LinearLayout) itemView.findViewById(R.id.checkbox_ll);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            vitalIv = (ImageView) itemView.findViewById(R.id.vital_iv);
            valueTv = (TextView) itemView.findViewById(R.id.value_tv);
            unitTv = (TextView) itemView.findViewById(R.id.unit_tv);
            descriptionTv = (TextView) itemView.findViewById(R.id.description_tv);
            abnormalIndicatorCl = (ConstraintLayout) itemView.findViewById(R.id.abnormal_indicator_cl);
            timeTv = (TextView) itemView.findViewById(R.id.time_tv);
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
        }
    }

    public class VitalAdapterModel {
        private int type;
        private String date;
        private VitalsApiResponseModel vitalsApiResponseModel;

        public VitalAdapterModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public VitalAdapterModel(int type, VitalsApiResponseModel vitalsApiResponseModel) {
            this.type = type;
            this.vitalsApiResponseModel = vitalsApiResponseModel;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public VitalsApiResponseModel getVitalsApiResponseModel() {
            return vitalsApiResponseModel;
        }

        public void setVitalsApiResponseModel(VitalsApiResponseModel vitalsApiResponseModel) {
            this.vitalsApiResponseModel = vitalsApiResponseModel;
        }
    }
}
