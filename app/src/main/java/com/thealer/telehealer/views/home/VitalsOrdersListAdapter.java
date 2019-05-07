package com.thealer.telehealer.views.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.VitalCommon.SupportedMeasurementType;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersDetailListFragment;
import com.thealer.telehealer.views.home.orders.document.DocumentListFragment;
import com.thealer.telehealer.views.home.vitals.VitalsDetailListFragment;
import com.thealer.telehealer.views.settings.SignatureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 21,November,2018
 */
public class VitalsOrdersListAdapter extends RecyclerView.Adapter<VitalsOrdersListAdapter.ViewHolder> {

    private FragmentActivity fragmentActivity;
    private List<String> titleList, typeList;
    private List<Integer> imageList;
    private String[] vitalMeasurementTypes;
    private String viewType;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private Bundle bundle;

    public VitalsOrdersListAdapter(FragmentActivity fragmentActivity, List<String> typeList, List<Integer> imageList, String viewType, Bundle bundle) {
        this.fragmentActivity = fragmentActivity;
        this.typeList = typeList;
        this.imageList = imageList;
        this.viewType = viewType;
        this.bundle = bundle;
        titleList = new ArrayList<>();
        for (String type : typeList) {
            titleList.add(OrderConstant.getDislpayTitle(fragmentActivity, type));
        }
        showSubFragmentInterface = (ShowSubFragmentInterface) fragmentActivity;
    }

    /*
    For Vitals
    items - List of Supporting Measurement typ
     */
    public VitalsOrdersListAdapter(FragmentActivity fragmentActivity, String[] items, String viewType, Bundle bundle) {
        this.fragmentActivity = fragmentActivity;
        vitalMeasurementTypes = items;

        this.titleList = new ArrayList<>();
        this.imageList = new ArrayList<>();
        for (String type : vitalMeasurementTypes) {
            titleList.add(fragmentActivity.getString(SupportedMeasurementType.getTitle(type)));
            imageList.add(SupportedMeasurementType.getDrawable(type));
        }

        this.viewType = viewType;
        this.bundle = bundle;
        showSubFragmentInterface = (ShowSubFragmentInterface) fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_vitals_orders_list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.listTv.setText(titleList.get(i));
        viewHolder.listIv.setImageDrawable(fragmentActivity.getDrawable(imageList.get(i)));
        viewHolder.listCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = null;

                if (viewType.equals(Constants.VIEW_VITALS)) {
                    bundle.putString(ArgumentKeys.MEASUREMENT_TYPE, vitalMeasurementTypes[i]);
                    fragment = new VitalsDetailListFragment();
                } else if (viewType.equals(Constants.VIEW_ORDERS)) {
                    bundle.putString(Constants.SELECTED_ITEM, typeList.get(i));
                    if (typeList.get(i).equals(OrderConstant.ORDER_FORM)) {

                        if (UserType.isUserDoctor()) {
                            if (UserDetailPreferenceManager.getWhoAmIResponse().getUser_detail().getSignature() != null) {
                                fragment = new OrdersDetailListFragment();
                            } else {
                                bundle.putBoolean(ArgumentKeys.SHOW_SIGNATURE_PROPOSER, true);
                                fragmentActivity.startActivity(new Intent(fragmentActivity, SignatureActivity.class).putExtras(bundle));
                            }
                        } else {
                            fragment = new OrdersDetailListFragment();
                        }

                    } else if (typeList.get(i).equals(OrderConstant.ORDER_DOCUMENTS)) {
                        fragment = new DocumentListFragment();
                    } else {
                        fragment = new OrdersDetailListFragment();
                    }
                }

                if (fragment != null) {
                    fragment.setArguments(bundle);
                    showSubFragmentInterface.onShowFragment(fragment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView listIv;
        private TextView listTv;
        private CardView listCv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listIv = (ImageView) itemView.findViewById(R.id.list_iv);
            listTv = (TextView) itemView.findViewById(R.id.list_tv);
            listCv = (CardView) itemView.findViewById(R.id.list_cv);
        }
    }
}
