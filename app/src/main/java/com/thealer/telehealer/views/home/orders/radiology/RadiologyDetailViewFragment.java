package com.thealer.telehealer.views.home.orders.radiology;

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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersIdListApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.IcdCodeApiViewModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrderStatus;
import com.thealer.telehealer.views.home.orders.OrdersBaseFragment;
import com.thealer.telehealer.views.home.orders.OrdersCustomView;
import com.thealer.telehealer.views.home.orders.SendFaxByNumberFragment;
import com.thealer.telehealer.views.home.orders.labs.IcdCodeListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import config.AppConfig;

import static com.thealer.telehealer.TeleHealerApplication.appConfig;

/**
 * Created by Aswin on 12,December,2018
 */
public class RadiologyDetailViewFragment extends OrdersBaseFragment implements View.OnClickListener {
    private OrdersCustomView patientOcv;
    private OrdersCustomView copyResultOcv;
    private TextView radiologyLabel;
    private RecyclerView radiologyListRv;
    private TextView icdLabel;
    private RecyclerView icdListRv;
    private Switch statSwitch;
    private OrdersCustomView dateOcv;
    private OrdersCustomView commentsOcv;
    private LinearLayout cancelLl;
    private TextView cancelTv;
    private TextView cancelWatermarkTv;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;

    private GetRadiologyResponseModel.ResultBean getRadiologyResponseModel;
    private HashMap<String, String> icdCodeMap = new HashMap<>();
    private IcdCodeListAdapter icdCodeListAdapter;

    private ShowSubFragmentInterface showSubFragmentInterface;
    private IcdCodeApiViewModel icdCodeApiViewModel;
    private OrdersCustomView faxNumberOcv;
    private OrdersCustomView faxStatusOcv;
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
                        icdCodeMap.clear();
                        icdCodeMap.putAll(icdCodeApiResponseModel.getResultHashMap());
                        icdCodeListAdapter.setMapData(icdCodeMap);
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
        View view = inflater.inflate(R.layout.fragment_radiology_detail_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        patientOcv = (OrdersCustomView) view.findViewById(R.id.patient_ocv);
        copyResultOcv = (OrdersCustomView) view.findViewById(R.id.copy_result_ocv);
        radiologyLabel = (TextView) view.findViewById(R.id.radiology_label);
        radiologyListRv = (RecyclerView) view.findViewById(R.id.radiology_list_rv);
        icdLabel = (TextView) view.findViewById(R.id.icd_label);
        icdListRv = (RecyclerView) view.findViewById(R.id.icd_list_rv);
        statSwitch = (Switch) view.findViewById(R.id.stat_switch);
        dateOcv = (OrdersCustomView) view.findViewById(R.id.date_ocv);
        commentsOcv = (OrdersCustomView) view.findViewById(R.id.comments_ocv);
        cancelLl = (LinearLayout) view.findViewById(R.id.cancel_ll);
        cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        cancelWatermarkTv = (TextView) view.findViewById(R.id.cancel_watermark_tv);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        faxNumberOcv = (OrdersCustomView) view.findViewById(R.id.fax_number_ocv);
        faxStatusOcv = (OrdersCustomView) view.findViewById(R.id.fax_status_ocv);

        toolbar.inflateMenu(R.menu.orders_detail_menu);
        if (!UserType.isUserPatient()) {
            toolbar.getMenu().findItem(R.id.send_fax_menu).setVisible(true);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.print_menu:
                        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
                        Bundle bundle = new Bundle();

                        bundle.putString(ArgumentKeys.PDF_TITLE, getRadiologyResponseModel.getName());
                        bundle.putString(ArgumentKeys.PDF_URL, getRadiologyResponseModel.getPath());
                        bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, true);
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

                        pdfViewerFragment.setArguments(bundle);

                        showSubFragmentInterface.onShowFragment(pdfViewerFragment);

                        break;
                    case R.id.send_fax_menu:
                        SendFaxByNumberFragment sendFaxByNumberFragment = new SendFaxByNumberFragment();
                        bundle = new Bundle();
                        bundle.putInt(ArgumentKeys.ORDER_ID, getRadiologyResponseModel.getReferral_id());
                        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);
                        sendFaxByNumberFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(sendFaxByNumberFragment);
                        break;
                }
                return true;
            }
        });
        if (appConfig.getRemovedFeatures().contains(AppConfig.FEATURE_PHARMACY_FAX)){
            copyResultOcv.setVisibility(View.GONE);
        }
        backIv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);

        if (!UserType.isUserPatient()) {
            cancelTv.setVisibility(View.VISIBLE);
        } else {
            cancelTv.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            getRadiologyResponseModel = (GetRadiologyResponseModel.ResultBean) getArguments().getSerializable(ArgumentKeys.ORDER_DETAIL);
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
                getRadiologyResponseModel.setUserDetailMap(userDetailMap);
            }

            if (getRadiologyResponseModel != null) {
                setData(getRadiologyResponseModel);
            } else {
                int id = getArguments().getInt(ArgumentKeys.ORDER_ID);
                getOrdersDetail(userGuid, doctorGuid, new ArrayList<>(Arrays.asList(id)), true);
            }
        }
    }

    @Override
    public void onDetailReceived(@Nullable OrdersIdListApiResponseModel idListApiResponseModel) {
        if (idListApiResponseModel != null && idListApiResponseModel.getXrays() != null && !idListApiResponseModel.getXrays().isEmpty()) {
            getRadiologyResponseModel = idListApiResponseModel.getXrays().get(0);
            setData(getRadiologyResponseModel);
        }
    }

    private void setData(GetRadiologyResponseModel.ResultBean getRadiologyResponseModel) {
        if (getRadiologyResponseModel.getDetail() != null) {

            if (getRadiologyResponseModel.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {
                cancelTv.setVisibility(View.GONE);
                cancelWatermarkTv.setVisibility(View.VISIBLE);
                toolbar.getMenu().clear();
            }
            if (getRadiologyResponseModel.getStatus().equals(OrderStatus.STATUS_FAILED) || getRadiologyResponseModel.getFaxes().size() > 0) {
                toolbar.getMenu().removeItem(R.id.send_fax_menu);
            }

            if (getRadiologyResponseModel.getFaxes() != null &&
                    getRadiologyResponseModel.getFaxes().size() > 0) {
                faxNumberOcv.setTitleTv(getRadiologyResponseModel.getFaxes().get(0).getFax_number());
                faxStatusOcv.setTitleTv(getRadiologyResponseModel.getFaxes().get(0).getStatus());

                faxNumberOcv.setVisibility(View.VISIBLE);
                faxStatusOcv.setVisibility(View.VISIBLE);
            } else {
                faxNumberOcv.setVisibility(View.GONE);
                faxStatusOcv.setVisibility(View.GONE);
            }

            if (getRadiologyResponseModel.getUserDetailMap() != null &&
                    !getRadiologyResponseModel.getUserDetailMap().isEmpty()) {

                patientOcv.setTitleTv(getRadiologyResponseModel.getUserDetailMap().get(getRadiologyResponseModel.getPatient().getUser_guid()).getUserDisplay_name());
                patientOcv.setSubtitleTv(getRadiologyResponseModel.getUserDetailMap().get(getRadiologyResponseModel.getPatient().getUser_guid()).getDob());
                patientOcv.setSub_title_visible(true);
            }

            commentsOcv.setTitleTv(getRadiologyResponseModel.getDetail().getComment().trim());
            dateOcv.setTitleTv(getRadiologyResponseModel.getDetail().getRequested_date());

            if (getRadiologyResponseModel.getDetail().getCopy_to() != null) {
                copyResultOcv.setTitleTv(getRadiologyResponseModel.getDetail().getCopy_to().getName());
                copyResultOcv.setSub_title_visible(true);
                copyResultOcv.setSubtitleTv(getRadiologyResponseModel.getDetail().getCopy_to().getAddress());
            }

            radiologyListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            icdListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            statSwitch.setChecked(getRadiologyResponseModel.getDetail().getLabs().get(0).isStat());

            RadiologyListPreviewAdapter radiologyListPreviewAdapter = new RadiologyListPreviewAdapter(getActivity(), getRadiologyResponseModel.getDetail().getLabs().get(0).getXRayTests());
            radiologyListRv.setAdapter(radiologyListPreviewAdapter);

            StringBuilder title = null;
            for (int i = 0; i < getRadiologyResponseModel.getDetail().getLabs().get(0).getXRayTests().size(); i++) {
                if (title == null) {
                    title = new StringBuilder(getRadiologyResponseModel.getDetail().getLabs().get(0).getXRayTests().get(i).getDisplayText());
                } else {
                    title.append(",").append(getRadiologyResponseModel.getDetail().getLabs().get(0).getXRayTests().get(i).getDisplayText());
                }
            }

            toolbarTitle.setText(title);

            if (getRadiologyResponseModel.getDetail().getLabs().get(0).getICD10_codes().size() > 0) {
                StringBuilder icdCode = new StringBuilder();
                for (int i = 0; i < getRadiologyResponseModel.getDetail().getLabs().size(); i++) {

                    for (int j = 0; j < getRadiologyResponseModel.getDetail().getLabs().get(i).getICD10_codes().size(); j++) {

                        icdCode.append(getRadiologyResponseModel.getDetail().getLabs().get(i).getICD10_codes().get(j));

                        if (j != getRadiologyResponseModel.getDetail().getLabs().get(i).getICD10_codes().size() - 1)
                            icdCode.append(",");
                    }
                    if (i != getRadiologyResponseModel.getDetail().getLabs().size() - 1)
                        icdCode.append(",");
                }

                icdCodeApiViewModel.getFilteredIcdCodes(icdCode.toString(), true);
            }

            icdCodeListAdapter = new IcdCodeListAdapter(getActivity(), getRadiologyResponseModel.getDetail().getLabs().get(0).getICD10_codes(), icdCodeMap);
            icdListRv.setAdapter(icdCodeListAdapter);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.cancel_tv:
                cancelOrder(OrderConstant.ORDER_RADIOLOGY, getRadiologyResponseModel.getReferral_id(), doctorGuid);
                break;
        }
    }
}
