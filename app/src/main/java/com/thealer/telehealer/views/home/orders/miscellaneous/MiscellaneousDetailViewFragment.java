package com.thealer.telehealer.views.home.orders.miscellaneous;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OnCloseActionInterface;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;

/**
 * Created by Aswin on 13,March,2019
 */
public class MiscellaneousDetailViewFragment extends BaseFragment implements View.OnClickListener {
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
    private OnCloseActionInterface onCloseActionInterface;
    private OrdersApiViewModel ordersApiViewModel;
    private AttachObserverInterface attachObserverInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
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
            resultBean = (MiscellaneousApiResponseModel.ResultBean) getArguments().getSerializable(Constants.USER_DETAIL);

            if (resultBean != null) {
                if (resultBean.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
                    cancelLl.setVisibility(View.GONE);
                    cancelWatermarkTv.setVisibility(View.VISIBLE);
                    toolbar.getMenu().clear();
                }
                notesOcv.setTitleTv(resultBean.getDetail().getNotes());

                CommonUserApiResponseModel patientDetail = resultBean.getUserDetailMap().get(resultBean.getPatient().getUser_guid());
                if (patientDetail != null) {
                    patientOcv.setTitleTv(patientDetail.getDisplayName());
                    patientOcv.setSubtitleTv(patientDetail.getDisplayInfo());
                }

                CommonUserApiResponseModel doctorDetail = resultBean.getUserDetailMap().get(resultBean.getDoctor().getUser_guid());
                if (doctorDetail != null) {
                    doctorOcv.setTitleTv(doctorDetail.getDisplayName());
                    doctorOcv.setSubtitleTv(doctorDetail.getDisplayInfo());
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
                        getString(R.string.cancel_miscellaneous_order),
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
