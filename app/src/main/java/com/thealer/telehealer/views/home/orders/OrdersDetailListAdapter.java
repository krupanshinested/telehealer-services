package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.PdfViewerFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.labs.LabsDetailViewFragment;
import com.thealer.telehealer.views.home.orders.miscellaneous.MiscellaneousDetailViewFragment;
import com.thealer.telehealer.views.home.orders.prescription.PrescriptionDetailViewFragment;
import com.thealer.telehealer.views.home.orders.radiology.RadiologyDetailViewFragment;
import com.thealer.telehealer.views.home.orders.specialist.SpecialistDetailViewFragment;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 21,November,2018
 */
public class OrdersDetailListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerList;
    private HashMap<String, List<OrdersDetailListAdapterModel>> childList;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private Fragment fragment = null;
    private HashMap<String, CommonUserApiResponseModel> userDetailHashMap;
    private String doctorGuid;

    public OrdersDetailListAdapter(FragmentActivity fragmentActivity, List<String> headerList, HashMap<String,
            List<OrdersDetailListAdapterModel>> childList, HashMap<String, CommonUserApiResponseModel> userDetailHashMap, String doctorGuid) {
        this.context = fragmentActivity;
        this.headerList = headerList;
        this.childList = childList;
        showSubFragmentInterface = (ShowSubFragmentInterface) fragmentActivity;
        this.userDetailHashMap = userDetailHashMap;
        this.doctorGuid = doctorGuid;
    }

    @Override
    public int getGroupCount() {
        return childList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(headerList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(headerList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);

        recentHeaderTv.setText(headerList.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_order_detail_list_view, parent, false);

        CardView itemCv;
        CircleImageView itemCiv;
        TextView itemTitleTv, itemSubTitleTv;
        ImageView statusIv;


        itemCv = (CardView) convertView.findViewById(R.id.item_cv);
        itemCiv = (CircleImageView) convertView.findViewById(R.id.item_civ);
        itemTitleTv = (TextView) convertView.findViewById(R.id.list_title_tv);
        itemSubTitleTv = (TextView) convertView.findViewById(R.id.list_sub_title_tv);
        statusIv = (ImageView) convertView.findViewById(R.id.status_iv);

        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

        OrdersDetailListAdapterModel ordersDetailListAdapterModel = childList.get(headerList.get(groupPosition)).get(childPosition);

        if (!ordersDetailListAdapterModel.isForm()) {

            OrdersCommonResultResponseModel ordersCommonResultResponseModel = ordersDetailListAdapterModel.getCommonResultResponseModel();

            if (userDetailHashMap.containsKey(ordersCommonResultResponseModel.getDoctor().getUser_guid()) &&
                    userDetailHashMap.containsKey(ordersCommonResultResponseModel.getPatient().getUser_guid())) {

                HashMap<String, CommonUserApiResponseModel> detailMap = new HashMap<>();

                CommonUserApiResponseModel commonUserApiResponseModel = null;

                if (UserType.isUserPatient()) {
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getDoctor().getUser_guid());
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getDoctorDisplayName());
                        Utils.setImageWithGlide(context, itemCiv, commonUserApiResponseModel.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true);
                    }
                } else {
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getPatient().getUser_guid());
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getUserDisplay_name());
                        Utils.setImageWithGlide(context, itemCiv, commonUserApiResponseModel.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true);
                    }
                }

                detailMap.put(ordersCommonResultResponseModel.getDoctor().getUser_guid(), userDetailHashMap.get(ordersCommonResultResponseModel.getDoctor().getUser_guid()));
                detailMap.put(ordersCommonResultResponseModel.getPatient().getUser_guid(), userDetailHashMap.get(ordersCommonResultResponseModel.getPatient().getUser_guid()));

                ordersCommonResultResponseModel.setUserDetailMap(detailMap);
            }

            bundle.putSerializable(Constants.USER_DETAIL, ordersCommonResultResponseModel);

            int statusImage = 0;

            if (ordersCommonResultResponseModel.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {

                statusImage = OrderStatus.getStatusImage(ordersCommonResultResponseModel.getStatus());

            } else {
                if (ordersCommonResultResponseModel.getFaxes() != null &&
                        ordersCommonResultResponseModel.getFaxes().size() > 0) {
                    statusImage = OrderStatus.getStatusImage(ordersCommonResultResponseModel.getFaxes().get(0).getStatus());
                }
            }

            if (ordersCommonResultResponseModel instanceof OrdersPrescriptionApiResponseModel.OrdersResultBean) {

                fragment = new PrescriptionDetailViewFragment();

            } else if (ordersCommonResultResponseModel instanceof OrdersLabApiResponseModel.LabsResponseBean) {

                fragment = new LabsDetailViewFragment();

            } else if (ordersCommonResultResponseModel instanceof OrdersSpecialistApiResponseModel.ResultBean) {

                fragment = new SpecialistDetailViewFragment();

            } else if (ordersCommonResultResponseModel instanceof GetRadiologyResponseModel.ResultBean) {

                fragment = new RadiologyDetailViewFragment();

            } else if (ordersCommonResultResponseModel instanceof MiscellaneousApiResponseModel.ResultBean) {

                fragment = new MiscellaneousDetailViewFragment();

            }

            if (statusImage != 0) {
                statusIv.setImageDrawable(context.getDrawable(statusImage));
                if (statusImage == R.drawable.ic_status_pending) {
                    statusIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
                }
            }

        } else {

            String key;
            if (UserType.isUserAssistant()) {
                key = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getPatient().getUser_guid();
            } else {
                key = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getDoctor().getUser_guid();
            }
            if (userDetailHashMap.containsKey(key)) {
                itemTitleTv.setText(userDetailHashMap.get(key).getUserDisplay_name());
                Utils.setImageWithGlide(context, itemCiv, userDetailHashMap.get(key).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true);
            }

            fragment = new PdfViewerFragment();
            bundle.putBoolean(ArgumentKeys.IS_FROM_PRESCRIPTION_DETAIL, true);
            bundle.putString(ArgumentKeys.PDF_TITLE, ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getName());
            bundle.putString(ArgumentKeys.PDF_URL, ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getUrl());
            bundle.putBoolean(ArgumentKeys.IS_PDF_DECRYPT, false);
        }

        itemSubTitleTv.setText(childList.get(headerList.get(groupPosition)).get(childPosition).getSubTitle());

        itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fragment != null) {
                    boolean isPermissionGranted = true;

                    if (ordersDetailListAdapterModel.isForm()) {
                        isPermissionGranted = PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_STORAGE);
                    }

                    if (isPermissionGranted) {
                        fragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(fragment);
                    }
                }

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> headerList, HashMap<String, List<OrdersDetailListAdapterModel>> childList) {
        this.headerList = headerList;
        this.childList = childList;
        notifyDataSetChanged();
    }

    public void setUserDetailHashMap(HashMap<String, CommonUserApiResponseModel> userDetailHashMap) {
        this.userDetailHashMap = userDetailHashMap;
        notifyDataSetChanged();
    }
}
