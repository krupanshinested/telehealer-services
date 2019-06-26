package com.thealer.telehealer.views.home.orders.labs;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
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
import java.util.Map;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 22,November,2018
 */
public class LabsDetailViewFragment extends OrdersBaseFragment implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private OrdersCustomView patientOcv;
    private OrdersCustomView doctorOcv;
    private OrdersCustomView dateOcv;
    private OrdersCustomView copyOcv;
    private RecyclerView labTestRv;
    private TextView cancelTv;
    private TextView cancelWatermarkTv;
    private Toolbar toolbar;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private OrdersLabApiResponseModel.LabsResponseBean labsResponseBean;
    private IcdCodeApiViewModel icdCodeApiViewModel;
    private LabTestListAdapter labTestListAdapter;
    private Map<String, String> icdCodeList = new HashMap<>();
    private OrdersCustomView faxStatusOcv;
    private OrdersCustomView faxNumberOcv;
    private String doctorGuid, userGuid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();
        icdCodeApiViewModel = ViewModelProviders.of(this).get(IcdCodeApiViewModel.class);
        icdCodeApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    IcdCodeApiResponseModel icdCodeApiResponseModel = (IcdCodeApiResponseModel) baseApiResponseModel;
                    if (icdCodeApiResponseModel.getResults().size() > 0) {
                        icdCodeList.clear();
                        icdCodeList.putAll(icdCodeApiResponseModel.getResultHashMap());
                        labTestListAdapter.setIcdCodeList(icdCodeList);
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
        View view = inflater.inflate(R.layout.fragment_lab_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        doctorOcv = (OrdersCustomView) view.findViewById(R.id.doctor_ocv);
        dateOcv = (OrdersCustomView) view.findViewById(R.id.date_ocv);
        copyOcv = (OrdersCustomView) view.findViewById(R.id.copy_ocv);
        labTestRv = (RecyclerView) view.findViewById(R.id.lab_test_rv);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        cancelWatermarkTv = (TextView) view.findViewById(R.id.cancel_watermark_tv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        faxStatusOcv = (OrdersCustomView) view.findViewById(R.id.fax_status_ocv);
        faxNumberOcv = (OrdersCustomView) view.findViewById(R.id.fax_number_ocv);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        if (!UserType.isUserPatient()) {
            toolbar.getMenu().findItem(R.id.send_fax_menu).setVisible(true);
        }
        if (appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PHARMACY_FAX)) {
            copyOcv.setVisibility(View.GONE);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Bundle bundle;
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:

                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                        bundle = new Bundle();
                        bundle.putString(ArgumentKeys.PDF_TITLE, labsResponseBean.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, labsResponseBean.getPath());
                        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

                        pdfViewerFragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);
                        break;
                    case R.id.send_fax_menu:
                        SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                        bundle = new Bundle();
                        bundle.putInt(ArgumentKeys.ORDER_ID, labsResponseBean.getReferral_id());
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

        patientOcv.setTitleTv("-");
        patientOcv.setSubtitleTv("-");

        doctorOcv.setTitleTv("-");
        doctorOcv.setSubtitleTv("-");

        if (!UserType.isUserPatient()) {
            cancelTv.setVisibility(View.VISIBLE);
        } else {
            cancelTv.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            labsResponseBean = (OrdersLabApiResponseModel.LabsResponseBean) getArguments().getSerializable(ArgumentKeys.ORDER_DETAIL);
            doctorGuid = getArguments().getString(ArgumentKeys.DOCTOR_GUID);
            userGuid = getArguments().getString(ArgumentKeys.USER_GUID);

            CommonUserApiResponseModel patientDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.USER_DETAIL);
            CommonUserApiResponseModel doctorDetail = (CommonUserApiResponseModel) getArguments().getSerializable(Constants.DOCTOR_DETAIL);

            HashMap<String, CommonUserApiResponseModel> userDetailMap = new HashMap<>();
            if (patientDetail != null) {
                userDetailMap.put(patientDetail.getUser_guid(), patientDetail);
            }

            if (doctorDetail != null) {
                doctorGuid = doctorDetail.getUser_guid();
                userDetailMap.put(doctorGuid, doctorDetail);
            }

            if (!userDetailMap.isEmpty()) {
                labsResponseBean.setUserDetailMap(userDetailMap);
            }

            if (labsResponseBean != null) {
                setData(labsResponseBean);
            } else {
                int id = getArguments().getInt(ArgumentKeys.ORDER_ID);
                getOrdersDetail(userGuid, doctorGuid, new ArrayList<>(Arrays.asList(id)), true);
            }
        }
    }

    @Override
    public void onDetailReceived(@Nullable OrdersIdListApiResponseModel idListApiResponseModel) {
        if (idListApiResponseModel != null && idListApiResponseModel.getLabs() != null && !idListApiResponseModel.getLabs().isEmpty()) {
            labsResponseBean = idListApiResponseModel.getLabs().get(0);
            setData(labsResponseBean);
        }
    }

    private void setData(OrdersLabApiResponseModel.LabsResponseBean labsResponseBean) {

        if (labsResponseBean.getUserDetailMap() != null) {

            patientOcv.setTitleTv(labsResponseBean.getUserDetailMap().get(labsResponseBean.getPatient().getUser_guid()).getUserDisplay_name());
            patientOcv.setSubtitleTv(labsResponseBean.getUserDetailMap().get(labsResponseBean.getPatient().getUser_guid()).getDob());

            doctorOcv.setTitleTv(labsResponseBean.getUserDetailMap().get(labsResponseBean.getDoctor().getUser_guid()).getDoctorDisplayName());
            doctorOcv.setSubtitleTv(labsResponseBean.getUserDetailMap().get(labsResponseBean.getDoctor().getUser_guid()).getDoctorSpecialist());
        }

        if (labsResponseBean.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
            cancelTv.setVisibility(View.GONE);
            cancelWatermarkTv.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
        }
        if (labsResponseBean.getStatus().equals(OrderStatus.STATUS_FAILED) || labsResponseBean.getFaxes().size() > 0) {
            toolbar.getMenu().removeItem(R.id.send_fax_menu);
        }

        if (labsResponseBean.getFaxes() != null &&
                labsResponseBean.getFaxes().size() > 0) {
            faxNumberOcv.setTitleTv(labsResponseBean.getFaxes().get(0).getFax_number());
            faxStatusOcv.setTitleTv(labsResponseBean.getFaxes().get(0).getStatus());

            faxNumberOcv.setVisibility(View.VISIBLE);
            faxStatusOcv.setVisibility(View.VISIBLE);
        } else {
            faxNumberOcv.setVisibility(View.GONE);
            faxStatusOcv.setVisibility(View.GONE);
        }

        dateOcv.setTitleTv(Utils.getDayMonthYear(labsResponseBean.getCreated_at()));

        copyOcv.setTitleTv("N/A");

        if (labsResponseBean.getDetail() != null) {
            toolbarTitle.setText(labsResponseBean.getDetail().getDisplayName());

            if (labsResponseBean.getDetail().getCopy_to() != null) {
                copyOcv.setTitleTv(labsResponseBean.getDetail().getCopy_to().getName());
                copyOcv.setSubtitleTv(labsResponseBean.getDetail().getCopy_to().getAddress());
                copyOcv.setSub_title_visible(true);
            }

            if (labsResponseBean.getDetail().getLabs().size() > 0) {
                labTestRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                labTestListAdapter = new LabTestListAdapter(getActivity(), labsResponseBean.getDetail().getLabs(), icdCodeList);
                labTestRv.setAdapter(labTestListAdapter);

                StringBuilder icdCode = new StringBuilder();
                for (int i = 0; i < labsResponseBean.getDetail().getLabs().size(); i++) {

                    for (int j = 0; j < labsResponseBean.getDetail().getLabs().get(i).getICD10_codes().size(); j++) {

                        icdCode.append(labsResponseBean.getDetail().getLabs().get(i).getICD10_codes().get(j));

                        if (j != labsResponseBean.getDetail().getLabs().get(i).getICD10_codes().size() - 1)
                            icdCode.append(",");
                    }
                    if (i != labsResponseBean.getDetail().getLabs().size() - 1)
                        icdCode.append(",");
                }

                icdCodeApiViewModel.getFilteredIcdCodes(icdCode.toString(), false);
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
                cancelOrder(OrderConstant.ORDER_LABS, labsResponseBean.getReferral_id(), doctorGuid);
                break;
        }
    }
}
