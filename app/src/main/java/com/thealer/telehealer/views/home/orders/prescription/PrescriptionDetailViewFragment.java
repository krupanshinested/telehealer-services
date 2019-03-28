package com.thealer.telehealer.views.home.orders.prescription;

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
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

/**
 * Created by Aswin on 22,November,2018
 */
public class PrescriptionDetailViewFragment extends BaseFragment implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private OrdersCustomView patientOcv;
    private OrdersCustomView pharmacyOcv;
    private OrdersCustomView orderStatusOcv;
    private OrdersCustomView prescriptionOcv;
    private OrdersCustomView formOcv;
    private OrdersCustomView strengthOcv;
    private OrdersCustomView directionOcv;
    private OrdersCustomView dispenseOcv;
    private OrdersCustomView refillOcv;
    private Switch dnsSwitch;
    private Switch labelSwitch;
    private TextView cancelTv;

    private OnCloseActionInterface onCloseActionInterface;
    private OrdersApiViewModel ordersApiViewModel;
    private OrdersPrescriptionApiResponseModel.OrdersResultBean ordersResultBean;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private TextView cancelWatermarkTv;
    private Toolbar toolbar;
    private String userName, doctorGuid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
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
        View view = inflater.inflate(R.layout.fragment_prescription_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        pharmacyOcv = (OrdersCustomView) view.findViewById(R.id.pharmacy_ocv);
        orderStatusOcv = (OrdersCustomView) view.findViewById(R.id.order_status_ocv);
        prescriptionOcv = (OrdersCustomView) view.findViewById(R.id.prescription_ocv);
        formOcv = (OrdersCustomView) view.findViewById(R.id.form_ocv);
        strengthOcv = (OrdersCustomView) view.findViewById(R.id.strength_ocv);
        directionOcv = (OrdersCustomView) view.findViewById(R.id.direction_ocv);
        dispenseOcv = (OrdersCustomView) view.findViewById(R.id.dispense_ocv);
        refillOcv = (OrdersCustomView) view.findViewById(R.id.refill_ocv);
        dnsSwitch = (Switch) view.findViewById(R.id.dns_switch);
        labelSwitch = (Switch) view.findViewById(R.id.label_switch);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        cancelWatermarkTv = (TextView) view.findViewById(R.id.cancel_watermark_tv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.orders_detail_menu);

        if (!UserType.isUserPatient()) {
            toolbar.getMenu().findItem(R.id.send_fax_menu).setVisible(true);
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Bundle bundle = new Bundle();
                switch (menuItem.getItemId()) {
                    case R.id.send_fax_menu:
                        bundle.putInt(ArgumentKeys.PRESCRIPTION_ID, ordersResultBean.getReferral_id());
                        bundle.putBoolean(ArgumentKeys.IS_FROM_PRESCRIPTION_DETAIL, true);
                        bundle.putString(ArgumentKeys.VIEW_TITLE, getString(R.string.choose_pharmacy));
                        bundle.putString(ArgumentKeys.USER_NAME, userName);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

                        SelectPharmacyFragment selectPharmacyFragment = new SelectPharmacyFragment();
                        selectPharmacyFragment.setArguments(bundle);
                        selectPharmacyFragment.setTargetFragment(PrescriptionDetailViewFragment.this, RequestID.REQ_SEND_FAX);

                        showSubFragmentInterface.onShowFragment(selectPharmacyFragment);

                        break;
                    case R.id.print_menu:

                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();

                        bundle.putString(ArgumentKeys.PDF_TITLE, ordersResultBean.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, ordersResultBean.getPath());
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

        if (getArguments() != null) {

            ordersResultBean = (OrdersPrescriptionApiResponseModel.OrdersResultBean) getArguments().getSerializable(Constants.USER_DETAIL);
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);

            pharmacyOcv.setTitleTv("-");

            if (ordersResultBean != null) {

                if (ordersResultBean.getUserDetailMap() != null && !ordersResultBean.getUserDetailMap().isEmpty()) {
                    if (UserType.isUserPatient()) {
                        patientOcv.setTitleTv(ordersResultBean.getUserDetailMap().get(ordersResultBean.getDoctor().getUser_guid()).getDoctorDisplayName());
                    } else {
                        userName = ordersResultBean.getUserDetailMap().get(ordersResultBean.getPatient().getUser_guid()).getUserDisplay_name();
                        patientOcv.setTitleTv(userName);
                    }
                }

                orderStatusOcv.setTitleTv(ordersResultBean.getStatus());

                if (ordersResultBean.getFaxes().size() > 0) {
                    if (ordersResultBean.getFaxes().get(0).getDetail() != null &&
                            ordersResultBean.getFaxes().get(0).getDetail().getPharmacy() != null) {
                        pharmacyOcv.setTitleTv(ordersResultBean.getFaxes().get(0).getDetail().getPharmacy().getCompany());

                        orderStatusOcv.setLabelTv(getString(R.string.fax_status));
                        orderStatusOcv.setTitleTv(ordersResultBean.getFaxes().get(0).getStatus());
                    }
                }


                if (UserType.isUserPatient()) {
                    cancelTv.setVisibility(View.GONE);
                }

                if (ordersResultBean.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
                    cancelTv.setVisibility(View.GONE);
                    cancelWatermarkTv.setVisibility(View.VISIBLE);
                    toolbar.getMenu().clear();
                }

                if (ordersResultBean.getStatus().equals(OrderStatus.STATUS_FAILED) || ordersResultBean.getFaxes().size() > 0) {
                    toolbar.getMenu().removeItem(R.id.send_fax_menu);
                }

                if (ordersResultBean.getDetail() != null) {
                    toolbarTitle.setText(ordersResultBean.getDetail().getRx_drug_name());
                    dnsSwitch.setChecked(ordersResultBean.getDetail().isDo_not_substitute());
                    labelSwitch.setChecked(ordersResultBean.getDetail().isLabel());
                    prescriptionOcv.setTitleTv(ordersResultBean.getDetail().getRx_drug_name());
                    formOcv.setTitleTv(ordersResultBean.getDetail().getRx_form());
                    strengthOcv.setTitleTv(ordersResultBean.getDetail().getStrength());
                    directionOcv.setTitleTv(ordersResultBean.getDetail().getDirection());
                    dispenseOcv.setTitleTv(ordersResultBean.getDetail().getDispense());
                    refillOcv.setTitleTv(ordersResultBean.getDetail().getRefil());
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
                Utils.showAlertDialog(getActivity(), getString(R.string.cancel_caps),
                        getString(R.string.cancel_prescription_order),
                        getString(R.string.yes),
                        getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ordersApiViewModel.cancelOrder(OrderConstant.ORDER_PRESCRIPTIONS, ordersResultBean.getReferral_id(), doctorGuid);
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
