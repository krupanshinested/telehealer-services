package com.thealer.telehealer.views.home.orders;

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
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;
import com.thealer.telehealer.views.home.recents.adapterModels.VisitOrdersAdapterModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aswin on 01,May,2019
 */
public class OrderSelectionListAdapter extends RecyclerView.Adapter<OrderSelectionListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private int mode;
    private OnListItemSelectInterface onListItemSelectInterface;
    private List<VisitOrdersAdapterModel> visitOrdersAdapterModels = new ArrayList<>();
    private CommonUserApiResponseModel userModel;
    private List<Integer> positionList = new ArrayList<>();

    public OrderSelectionListAdapter(FragmentActivity activity, CommonUserApiResponseModel userModel, int mode, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.userModel = userModel;
        this.mode = mode;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_editable_order_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        VisitOrdersAdapterModel visitOrdersAdapterModel = visitOrdersAdapterModels.get(i);

        if (i == 0) {
            viewHolder.headerTv.setText(Utils.getDayMonthYear(visitOrdersAdapterModel.getDate()));
            viewHolder.headerTv.setVisibility(View.VISIBLE);
        } else {
            if (Utils.getDayMonthYear(visitOrdersAdapterModel.getDate()).equals(Utils.getDayMonthYear(visitOrdersAdapterModels.get(i - 1).getDate()))) {
                viewHolder.headerTv.setVisibility(View.GONE);
            } else {
                viewHolder.headerTv.setText(Utils.getDayMonthYear(visitOrdersAdapterModel.getDate()));
                viewHolder.headerTv.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.orderListIv.setImageResource(visitOrdersAdapterModel.getDisplayImage());
        viewHolder.orderListTitleTv.setText(visitOrdersAdapterModel.getDisplayTitle());
        viewHolder.orderListSubTitleTv.setText(userModel.getUserDisplay_name());

        switch (mode) {
            case Constants.VIEW_MODE:
                viewHolder.visitCb.setVisibility(View.GONE);
                break;
            case Constants.EDIT_MODE:
                if (visitOrdersAdapterModel.getOrderId() == null) {
                    viewHolder.visitCb.setVisibility(View.VISIBLE);
                    viewHolder.orderListItemCv.setCardBackgroundColor(activity.getColor(R.color.colorWhite));
                    viewHolder.orderListIv.setImageTintList(ColorStateList.valueOf(activity.getColor(R.color.app_gradient_start)));
                } else {
                    viewHolder.visitCb.setVisibility(View.GONE);
                    viewHolder.orderListItemCv.setCardBackgroundColor(activity.getColor(R.color.colorGrey_light));
                    viewHolder.orderListIv.setImageTintList(ColorStateList.valueOf(activity.getColor(R.color.colorGrey)));
                }
                break;
        }

        viewHolder.orderRootCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ArgumentKeys.ORDER_DATA, visitOrdersAdapterModel);

                if (visitOrdersAdapterModel.getOrderId() == null) {
                    onListItemSelectInterface.onListItemSelected(i, bundle);
                    if (positionList.contains(i)) {
                        positionList.remove((Object) i);
                    } else {
                        positionList.add(i);
                    }
                }
                notifyItemChanged(i);
            }
        });

        if (positionList.contains(i)) {
            viewHolder.visitCb.setChecked(true);
        } else {
            viewHolder.visitCb.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return visitOrdersAdapterModels.size();
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public void setData(OrdersIdListApiResponseModel ordersIdListApiResponseModel) {
        visitOrdersAdapterModels = generateAdapterModel(ordersIdListApiResponseModel);
        notifyDataSetChanged();
    }

    private List<VisitOrdersAdapterModel> generateAdapterModel(OrdersIdListApiResponseModel ordersIdListApiResponseModel) {

        List<VisitOrdersAdapterModel> visitOrdersAdapterModels = new ArrayList<>();

        if (ordersIdListApiResponseModel.getPrescriptions() != null &&
                !ordersIdListApiResponseModel.getPrescriptions().isEmpty()) {
            for (int i = 0; i < ordersIdListApiResponseModel.getPrescriptions().size(); i++) {
                visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(ordersIdListApiResponseModel.getPrescriptions().get(i).getCreated_at(),
                        ordersIdListApiResponseModel.getPrescriptions().get(i)));
            }
        }
        if (ordersIdListApiResponseModel.getSpecialists() != null &&
                !ordersIdListApiResponseModel.getSpecialists().isEmpty()) {
            for (int i = 0; i < ordersIdListApiResponseModel.getSpecialists().size(); i++) {
                visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(ordersIdListApiResponseModel.getSpecialists().get(i).getCreated_at(),
                        ordersIdListApiResponseModel.getSpecialists().get(i)));
            }
        }
        if (ordersIdListApiResponseModel.getLabs() != null &&
                !ordersIdListApiResponseModel.getLabs().isEmpty()) {
            for (int i = 0; i < ordersIdListApiResponseModel.getLabs().size(); i++) {
                visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(ordersIdListApiResponseModel.getLabs().get(i).getCreated_at(),
                        ordersIdListApiResponseModel.getLabs().get(i)));
            }
        }
        if (ordersIdListApiResponseModel.getXrays() != null &&
                !ordersIdListApiResponseModel.getXrays().isEmpty()) {
            for (int i = 0; i < ordersIdListApiResponseModel.getXrays().size(); i++) {
                visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(ordersIdListApiResponseModel.getXrays().get(i).getCreated_at(),
                        ordersIdListApiResponseModel.getXrays().get(i)));
            }
        }
        if (ordersIdListApiResponseModel.getMiscellaneous() != null &&
                !ordersIdListApiResponseModel.getMiscellaneous().isEmpty()) {
            for (int i = 0; i < ordersIdListApiResponseModel.getMiscellaneous().size(); i++) {
                visitOrdersAdapterModels.add(new VisitOrdersAdapterModel(ordersIdListApiResponseModel.getMiscellaneous().get(i).getCreated_at(),
                        ordersIdListApiResponseModel.getMiscellaneous().get(i)));
            }
        }

        Collections.sort(visitOrdersAdapterModels, new Comparator<VisitOrdersAdapterModel>() {
            @Override
            public int compare(VisitOrdersAdapterModel o1, VisitOrdersAdapterModel o2) {
                return Utils.getDateFromString(o2.getDate()).compareTo(Utils.getDateFromString(o1.getDate()));
            }
        });
        return visitOrdersAdapterModels;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout orderRootCl;
        private TextView headerTv;
        private CardView orderListItemCv;
        private ImageView orderListIv;
        private TextView orderListTitleTv;
        private TextView orderListSubTitleTv;
        private CheckBox visitCb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderRootCl = (ConstraintLayout) itemView.findViewById(R.id.order_root_cl);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
            headerTv = (TextView) itemView.findViewById(R.id.header_date_tv);
            orderListItemCv = (CardView) itemView.findViewById(R.id.order_list_item_cv);
            orderListIv = (ImageView) itemView.findViewById(R.id.order_list_iv);
            orderListTitleTv = (TextView) itemView.findViewById(R.id.order_list_title_tv);
            orderListSubTitleTv = (TextView) itemView.findViewById(R.id.order_list_sub_title_tv);
        }
    }
}
