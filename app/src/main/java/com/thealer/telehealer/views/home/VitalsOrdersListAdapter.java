package com.thealer.telehealer.views.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
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
    private List<String> vitalMeasurementTypes;
    private String viewType;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private Bundle bundle;
    private CommonUserApiResponseModel doctorModel;

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
    public VitalsOrdersListAdapter(FragmentActivity fragmentActivity, List<String> items, String viewType, Bundle bundle) {
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
                if (UserType.isUserAssistant() && doctorModel != null && doctorModel.getPermissions() != null && doctorModel.getPermissions().size() > 0) {
                    String permissionCode = "";
                    String vitalType="";
                    if (viewType.equals(Constants.VIEW_VITALS)) {
                        vitalType=vitalMeasurementTypes.get(i);
                    }else if(viewType.equals(Constants.VIEW_ORDERS)){
                        vitalType=typeList.get(i);
                    }
                    switch (vitalType) {
                        case OrderConstant.ORDER_FORM:
                            permissionCode=ArgumentKeys.FORMS_CODE;
                            break;
                        case OrderConstant.ORDER_PRESCRIPTIONS:
                            permissionCode=ArgumentKeys.PRESCRIPTION_CODE;
                            break;
                        case OrderConstant.ORDER_REFERRALS:
                            permissionCode=ArgumentKeys.REFERRALS_CODE;
                            break;
                        case OrderConstant.ORDER_LABS:
                            permissionCode=ArgumentKeys.LABS_CODE;
                            break;
                        case OrderConstant.ORDER_RADIOLOGY:
                            permissionCode=ArgumentKeys.RADIOLOGY_CODE;
                            break;
                        case OrderConstant.ORDER_MISC:
                            permissionCode=ArgumentKeys.MEDICAL_DOCUMENTS_CODE;
                            break;
                        case OrderConstant.ORDER_EDUCATIONAL_VIDEO:
                            permissionCode=ArgumentKeys.EDUCATIONAL_VIDEOS_CODE;
                            break;
                    }
                    if(!permissionCode.isEmpty()) {
                        boolean isPermissionAllowed = Utils.checkPermissionStatus(doctorModel.getPermissions(), permissionCode);
                        if (!isPermissionAllowed) {
                            Utils.displayPermissionMsg(fragmentActivity);
                            return;
                        }
                    }
                }
                Fragment fragment = null;

                if (viewType.equals(Constants.VIEW_VITALS)) {
                    bundle.putString(ArgumentKeys.MEASUREMENT_TYPE, vitalMeasurementTypes.get(i));
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

    public void setDoctorModel(CommonUserApiResponseModel doctorModel) {
        this.doctorModel = doctorModel;
        notifyDataSetChanged();
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
