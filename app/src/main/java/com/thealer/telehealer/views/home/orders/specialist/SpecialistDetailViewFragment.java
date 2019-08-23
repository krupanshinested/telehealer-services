package com.thealer.telehealer.views.home.orders.specialist;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 26,November,2018
 */
public class SpecialistDetailViewFragment extends OrdersBaseFragment implements View.OnClickListener {

    private ImageView backIv;
    private TextView toolbarTitle;
    private OrdersCustomView patientOcv;
    private OrdersCustomView referredOcv;
    private OrdersCustomView referralOcv;
    private OrdersCustomView reasonOcv;
    private LinearLayout cancelView;
    private TextView cancelTv;
    private TextView cancelWatermarkTv;
    private Toolbar toolbar;

    private OrdersSpecialistApiResponseModel.ResultBean resultBean;
    private ChangeTitleInterface changeTitleInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private OrdersCustomView faxNumberOcv;
    private OrdersCustomView faxStatusOcv;
    private String doctorGuid, userGuid;
    private HashMap<String, CommonUserApiResponseModel> userDetailMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specialist_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        referredOcv = (OrdersCustomView) view.findViewById(R.id.referred_ocv);
        referralOcv = (OrdersCustomView) view.findViewById(R.id.referral_ocv);
        reasonOcv = (OrdersCustomView) view.findViewById(R.id.reason_ocv);
        cancelView = (LinearLayout) view.findViewById(R.id.cancel_view);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        cancelWatermarkTv = (TextView) view.findViewById(R.id.cancel_watermark_tv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        faxNumberOcv = (OrdersCustomView) view.findViewById(R.id.fax_number_ocv);
        faxStatusOcv = (OrdersCustomView) view.findViewById(R.id.fax_status_ocv);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        if (!UserType.isUserPatient()) {
            toolbar.getMenu().findItem(R.id.send_fax_menu).setVisible(true);
        }
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
                    case R.id.send_fax_menu:
                        SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                        bundle = new Bundle();
                        bundle.putInt(ArgumentKeys.ORDER_ID, resultBean.getReferral_id());
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                        sendFaxByNumberFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
                        break;

                }
                return true;
            }
        });
        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);

        if (UserType.isUserPatient()) {
            cancelView.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            resultBean = (OrdersSpecialistApiResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.ORDER_DETAIL);

            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            userGuid = getArguments().getString(ArgumentKeys.USER_GUID);

            CommonUserApiResponseModel patientDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

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
                setUserDetail();
            } else {
                int id = getArguments().getInt(ArgumentKeys.ORDER_ID);

                getOrdersDetail(userGuid, doctorGuid, new ArrayList<>(Arrays.asList(id)), true);
            }
        }
    }

    private void setUserDetail() {
        if (!userDetailMap.isEmpty()) {
            resultBean.setUserDetailMap(userDetailMap);
        }
    }

    @Override
    public void onDetailReceived(@Nullable OrdersIdListApiResponseModel idListApiResponseModel) {
        if (idListApiResponseModel != null && idListApiResponseModel.getSpecialists() != null && !idListApiResponseModel.getSpecialists().isEmpty()) {
            resultBean = idListApiResponseModel.getSpecialists().get(0);
            setUserDetail();
            setData(resultBean);
        }
    }

    private void setData(OrdersSpecialistApiResponseModel.ResultBean resultBean) {
        if (resultBean.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
            cancelView.setVisibility(View.GONE);
            cancelWatermarkTv.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
        }
        if (resultBean.getStatus().equals(OrderStatus.STATUS_FAILED) || resultBean.getFaxes().size() > 0) {
            toolbar.getMenu().removeItem(R.id.send_fax_menu);
        }

        if (resultBean.getFaxes() != null &&
                resultBean.getFaxes().size() > 0) {
            faxNumberOcv.setTitleTv(resultBean.getFaxes().get(0).getFax_number());
            faxStatusOcv.setTitleTv(resultBean.getFaxes().get(0).getStatus());

            faxNumberOcv.setVisibility(View.VISIBLE);
            faxStatusOcv.setVisibility(View.VISIBLE);
        } else {
            faxNumberOcv.setVisibility(View.GONE);
            faxStatusOcv.setVisibility(View.GONE);
        }

        if (resultBean.getDetail() != null) {
            reasonOcv.setTitleTv(resultBean.getDetail().getDescription());

            referralOcv.setTitleTv(resultBean.getDetail().getCopy_to().getName());
            referralOcv.setSubtitleTv(resultBean.getDetail().getCopy_to().getAddress());

            if (resultBean.getUserDetailMap() != null && resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid()) != null) {
                referredOcv.setTitleTv(resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid()).getDoctorDisplayName());
                referredOcv.setSubtitleTv(resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid()).getDoctorSpecialist());

                toolbarTitle.setText(resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid()).getDoctorDisplayName());

                patientOcv.setTitleTv(resultBean.getUserDetailMap().get(resultBean.getPatient().getUser_guid()).getUserDisplay_name());
                patientOcv.setSubtitleTv(resultBean.getUserDetailMap().get(resultBean.getPatient().getUser_guid()).getDob());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.cancel_tv:
                cancelOrder(OrderConstant.ORDER_REFERRALS, resultBean.getReferral_id(), doctorGuid);
                break;
        }
    }
}
