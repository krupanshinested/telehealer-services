package com.thealer.telehealer.views.home.orders.specialist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.ChangeTitleInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;

/**
 * Created by Aswin on 26,November,2018
 */
public class SpecialistDetailViewFragment extends BaseFragment implements View.OnClickListener {

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

    private OnCloseActionInterface onCloseActionInterface;
    private OrdersApiViewModel ordersApiViewModel;
    private OrdersSpecialistApiResponseModel.ResultBean resultBean;
    private ChangeTitleInterface changeTitleInterface;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private OrdersCustomView faxNumberOcv;
    private OrdersCustomView faxStatusOcv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        changeTitleInterface = (ChangeTitleInterface) getActivity();
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        ordersApiViewModel = ViewModelProviders.of(this).get(OrdersApiViewModel.class);
        ordersApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        onCloseActionInterface.onClose(false);
                    }
                }
            }
        });
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
        if (UserType.isUserDoctor()) {
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

                        pdfViewerFragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                        break;
                    case R.id.send_fax_menu:
                        SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                        bundle = new Bundle();
                        bundle.putInt(ArgumentKeys.ORDER_ID, resultBean.getReferral_id());
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
            resultBean = (OrdersSpecialistApiResponseModel.ResultBean) getArguments().getSerializable(Constants.USER_DETAIL);

            if (resultBean != null) {
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.cancel_tv:
                showAlertDialog(getActivity(), getString(R.string.cancel_caps),
                        getString(R.string.cancel_prescription_order),
                        getString(R.string.yes),
                        getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ordersApiViewModel.cancelSpecialistOrder(resultBean.getReferral_id());
                                dialog.dismiss();

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
    }
}
