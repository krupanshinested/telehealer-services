package com.thealer.telehealer.views.home.orders.miscellaneous;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Aswin on 13,March,2019
 */
public class MiscellaneousDetailViewFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv;
    private OrdersCustomView doctorOcv;
    private OrdersCustomView notesOcv;
    private TextView cancelWatermarkTv;
    private LinearLayout cancelLl;
    private View topView;
    private TextView cancelTv;
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private MiscellaneousApiResponseModel.ResultBean resultBean;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private AttachObserverInterface attachObserverInterface;
    private String doctorGuid, userGuid;
    private CommonUserApiResponseModel patientDetail, doctorDetail;
    private HashMap<String, CommonUserApiResponseModel> userDetailMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miscellaneous_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        doctorOcv = (OrdersCustomView) view.findViewById(R.id.doctor_ocv);
        notesOcv = (OrdersCustomView) view.findViewById(R.id.notes_ocv);
        cancelWatermarkTv = (TextView) view.findViewById(R.id.cancel_watermark_tv);
        cancelLl = (LinearLayout) view.findViewById(R.id.cancel_ll);
        topView = (View) view.findViewById(R.id.top_view);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        appbarLayout = (AppBarLayout) view.findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getString(R.string.miscellaneous_referral));
        cancelWatermarkTv.setVisibility(View.GONE);

        toolbar.inflateMenu(R.menu.orders_detail_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Bundle bundle = new Bundle();
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:

                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();

                        bundle.putString(ArgumentKeys.PDF_TITLE, resultBean.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, resultBean.getPath());
                        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                        pdfViewerFragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                        break;

                }
                return true;
            }
        });
        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);

        if (UserType.isUserPatient()) {
            cancelLl.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            resultBean = (MiscellaneousApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.ORDER_DETAIL);
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            userGuid = getArguments().getString(ArgumentKeys.USER_GUID);

            patientDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

            userDetailMap = new HashMap<>();
            if (patientDetail != null) {
                userDetailMap.put(patientDetail.getUser_guid(), patientDetail);
            }

            if (doctorDetail != null) {
                doctorGuid = doctorDetail.getUser_guid();
                userDetailMap.put(doctorGuid, doctorDetail);
            }

            if (resultBean != null) {
                setData(resultBean);

                setUserDetails();
            } else {
                int id = getArguments().getInt(ArgumentKeys.ORDER_ID);
                getOrdersDetail(userGuid, doctorGuid, new ArrayList<>(Arrays.asList(id)), true);
            }
        }
    }

    private void setUserDetails() {
        if (!userDetailMap.isEmpty()) {
            resultBean.setUserDetailMap(userDetailMap);
        }
    }

    @Override
    public void onDetailReceived(@Nullable OrdersIdListApiResponseModel idListApiResponseModel) {
        if (idListApiResponseModel != null && idListApiResponseModel.getMiscellaneous() != null && !idListApiResponseModel.getMiscellaneous().isEmpty()) {
            resultBean = idListApiResponseModel.getMiscellaneous().get(0);
            setUserDetails();
            setData(resultBean);
        }
    }

    private void setData(MiscellaneousApiResponseModel.ResultBean resultBean) {
        if (resultBean.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
            cancelLl.setVisibility(View.GONE);
            cancelWatermarkTv.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
        }
        notesOcv.setTitleTv(resultBean.getDetail().getNotes());

        patientDetail = resultBean.getUserDetailMap().get(resultBean.getPatient().getUser_guid());
        if (patientDetail != null) {
            patientOcv.setTitleTv(patientDetail.getDisplayName());
            patientOcv.setSubtitleTv(patientDetail.getDisplayInfo());
        }

        doctorDetail = resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid());
        if (doctorDetail != null) {
            doctorOcv.setTitleTv(doctorDetail.getDisplayName());
            doctorOcv.setSubtitleTv(doctorDetail.getDisplayInfo());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.cancel_tv:
                cancelOrder(OrderConstant.ORDER_MISC, resultBean.getReferral_id(), doctorGuid);
                break;
        }
    }
}
