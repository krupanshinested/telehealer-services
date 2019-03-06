package com.thealer.telehealer.views.home.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.home.VitalsOrdersListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aswin on 21,November,2018
 */
public class OrdersListFragment extends BaseFragment {
    private RecyclerView listRv;
    private List<String> titleList = new ArrayList<>();
    private List<Integer> imageList = new ArrayList<>();
    private RecyclerView vitalsOrdersListRv;
    private FloatingActionButton addFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vitals_orders_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        addFab.hide();
        listRv = (RecyclerView) view.findViewById(R.id.vitals_orders_list_rv);
        listRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getArguments() != null) {

            titleList.clear();
            imageList.clear();

            String[] ordersTitles = {OrderConstant.ORDER_PRESCRIPTIONS, OrderConstant.ORDER_REFERRALS, OrderConstant.ORDER_LABS, OrderConstant.ORDER_RADIOLOGY, OrderConstant.ORDER_FORM, OrderConstant.ORDER_MISC};
            Integer[] ordersImages = {R.drawable.ic_orders_prescriptions, R.drawable.ic_orders_referrals, R.drawable.ic_orders_labs, R.drawable.ic_orders_radiology, R.drawable.ic_orders_forms, R.drawable.ic_orders_documents};

            titleList.addAll(Arrays.asList(ordersTitles));
            imageList.addAll(Arrays.asList(ordersImages));

            boolean isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome && UserType.isUserPatient()) {
                titleList.remove(OrderConstant.ORDER_DOCUMENTS);
            }

            if (isFromHome && !UserType.isUserPatient()) {
                titleList.remove(OrderConstant.ORDER_DOCUMENTS);
            }

            VitalsOrdersListAdapter ordersListAdapter = new VitalsOrdersListAdapter(getActivity(), titleList, imageList, Constants.VIEW_ORDERS, getArguments());
            listRv.setAdapter(ordersListAdapter);
        }
    }
}
