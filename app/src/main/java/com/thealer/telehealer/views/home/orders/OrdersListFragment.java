package com.thealer.telehealer.views.home.orders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.thealer.telehealer.R;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.home.VitalsOrdersListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 21,November,2018
 */
public class OrdersListFragment extends BaseFragment {
    private RecyclerView listRv;
    private List<String> typesList = new ArrayList<>();
    private List<Integer> imageList = new ArrayList<>();
    private FloatingActionButton addFab;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

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
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        if (getArguments() != null) {

            typesList.clear();
            imageList.clear();

            typesList.addAll(Arrays.asList(OrderConstant.ORDER_PRESCRIPTIONS, OrderConstant.ORDER_REFERRALS, OrderConstant.ORDER_LABS, OrderConstant.ORDER_RADIOLOGY, OrderConstant.ORDER_FORM, OrderConstant.ORDER_MISC));
            imageList.addAll(Arrays.asList(R.drawable.ic_orders_prescriptions, R.drawable.ic_orders_referrals, R.drawable.ic_orders_labs, R.drawable.ic_orders_radiology, R.drawable.ic_orders_forms, R.drawable.ic_orders_misc));

            if (appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_SPECIALIST)) {
                typesList.remove(1);
                imageList.remove(1);
            }
            boolean isFromHome = getArguments().getBoolean(Constants.IS_FROM_HOME);

            if (!isFromHome && UserType.isUserPatient()) {
                typesList.remove(OrderConstant.ORDER_DOCUMENTS);
            }

            if (isFromHome && !UserType.isUserPatient()) {
                typesList.remove(OrderConstant.ORDER_DOCUMENTS);
            }

            if (getArguments().getBoolean(ArgumentKeys.SHOW_TOOLBAR)) {
                appbarLayout.setVisibility(View.VISIBLE);
                toolbarTitle.setText(getString(R.string.orders));
                backIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((OnCloseActionInterface) getActivity()).onClose(false);
                    }
                });
            }

            VitalsOrdersListAdapter ordersListAdapter = new VitalsOrdersListAdapter(getActivity(), typesList, imageList, Constants.VIEW_ORDERS, getArguments());
            listRv.setAdapter(ordersListAdapter);
        }
    }
}
