package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideo;
import com.thealer.telehealer.apilayer.models.EducationalVideo.EducationalVideoOrder;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersCommonResultResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersPrescriptionApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.OrdersSpecialistApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.lab.OrdersLabApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.miscellaneous.MiscellaneousApiResponseModel;
import com.thealer.telehealer.apilayer.models.orders.radiology.GetRadiologyResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.EducationalVideo.EducationalVideoDetailFragment;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.orders.forms.EditableFormFragment;
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
        TextView listOptionTitleTv, listOptionSubTitleTv;


        itemCv = (CardView) convertView.findViewById(R.id.item_cv);
        itemCiv = (CircleImageView) convertView.findViewById(R.id.item_civ);
        itemTitleTv = (TextView) convertView.findViewById(R.id.list_title_tv);
        itemSubTitleTv = (TextView) convertView.findViewById(R.id.list_sub_title_tv);
        statusIv = (ImageView) convertView.findViewById(R.id.status_iv);

        Bundle bundle = new Bundle();
        bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorGuid);

        OrdersDetailListAdapterModel ordersDetailListAdapterModel = childList.get(headerList.get(groupPosition)).get(childPosition);
        String key = null;

        if (!ordersDetailListAdapterModel.isForm()) {

            OrdersCommonResultResponseModel ordersCommonResultResponseModel = ordersDetailListAdapterModel.getCommonResultResponseModel();

            if (userDetailHashMap.containsKey(ordersCommonResultResponseModel.getDoctor().getUser_guid()) &&
                    userDetailHashMap.containsKey(ordersCommonResultResponseModel.getPatient().getUser_guid())) {

                HashMap<String, CommonUserApiResponseModel> detailMap = new HashMap<>();

                CommonUserApiResponseModel commonUserApiResponseModel = null;

                if (ordersDetailListAdapterModel.getOtherImageUrl() != null) {
                    Utils.setImageWithGlide(context, itemCiv, ordersDetailListAdapterModel.getOtherImageUrl(), context.getDrawable(R.drawable.profile_placeholder), true, true);
                    itemCiv.setDisableCircularTransformation(true);
                    itemCiv.setBackgroundColor(Color.LTGRAY);
                } else if (UserType.isUserPatient()) {
                    itemCiv.setBackgroundColor(Color.TRANSPARENT);
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getDoctor().getUser_guid());
                    itemCiv.setDisableCircularTransformation(false);
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getDoctorDisplayName());
                        Utils.setImageWithGlide(context, itemCiv, commonUserApiResponseModel.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);
                    }
                } else {
                    itemCiv.setBackgroundColor(Color.TRANSPARENT);
                    itemCiv.setDisableCircularTransformation(false);
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getPatient().getUser_guid());
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getUserDisplay_name());
                        Utils.setImageWithGlide(context, itemCiv, commonUserApiResponseModel.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);
                    }
                }

                if (UserType.isUserPatient()) {
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getDoctor().getUser_guid());
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getDoctorDisplayName());
                    }
                } else {
                    commonUserApiResponseModel = userDetailHashMap.get(childList.get(headerList.get(groupPosition)).get(childPosition).getCommonResultResponseModel().getPatient().getUser_guid());
                    if (commonUserApiResponseModel != null) {
                        itemTitleTv.setText(commonUserApiResponseModel.getUserDisplay_name());
                    }
                }

                detailMap.put(ordersCommonResultResponseModel.getDoctor().getUser_guid(), userDetailHashMap.get(ordersCommonResultResponseModel.getDoctor().getUser_guid()));
                detailMap.put(ordersCommonResultResponseModel.getPatient().getUser_guid(), userDetailHashMap.get(ordersCommonResultResponseModel.getPatient().getUser_guid()));

                ordersCommonResultResponseModel.setUserDetailMap(detailMap);
            }

            bundle.putSerializable(ArgumentKeys.ORDER_DETAIL, ordersCommonResultResponseModel);

            int statusImage = 0;

            if (ordersCommonResultResponseModel instanceof EducationalVideoOrder) {
                if (((EducationalVideoOrder) ordersCommonResultResponseModel).isViewed() && !UserType.isUserPatient()) {
                    statusImage = R.drawable.ic_eye;
                }
            } else if (!TextUtils.isEmpty(ordersCommonResultResponseModel.getStatus()) && ordersCommonResultResponseModel.getStatus().equals(OrderStatus.STATUS_CANCELLED)) {

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

            } else if (ordersCommonResultResponseModel instanceof EducationalVideoOrder) {

                fragment = new EducationalVideoDetailFragment();
            }

            if (statusImage != 0) {
                statusIv.setImageDrawable(context.getDrawable(statusImage));
                if (statusImage == R.drawable.ic_status_pending) {
                    statusIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
                }
                statusIv.setVisibility(View.VISIBLE);
            } else {
                statusIv.setVisibility(View.GONE);
            }

            if (!UserType.isUserPatient()) {
                key = ordersCommonResultResponseModel.getPatient().getUser_guid();
            } else {
                if (ordersCommonResultResponseModel.getDoctor() != null) {
                    key = ordersCommonResultResponseModel.getDoctor().getUser_guid();
                }
            }

        } else {
            listOptionTitleTv = (TextView) convertView.findViewById(R.id.list_option_title_tv);
            listOptionSubTitleTv = (TextView) convertView.findViewById(R.id.list_option_sub_title_tv);


            if (ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().isCompleted()) {
                listOptionTitleTv.setVisibility(View.VISIBLE);
                listOptionSubTitleTv.setVisibility(View.VISIBLE);

                String score = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getData().getDisplayScore();
                listOptionTitleTv.setText(score.equals("0") ? context.getString(R.string.na) : score);
                listOptionSubTitleTv.setText(context.getString(R.string.score));

            }

            if (!UserType.isUserPatient()) {
                key = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getPatient().getUser_guid();
            } else {
                if (ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getDoctor() != null) {
                    key = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getDoctor().getUser_guid();
                }
                if (ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getMedical_assistant() != null) {
                    key = ordersDetailListAdapterModel.getOrdersFormsApiResponseModel().getMedical_assistant().getUser_guid();
                }
            }

            fragment = new EditableFormFragment();
            bundle.putSerializable(ArgumentKeys.FORM_DETAIL, ordersDetailListAdapterModel.getOrdersFormsApiResponseModel());
            itemCiv.setVisibility(View.GONE);
        }

        itemTitleTv.setText(Html.fromHtml(context.getString(R.string.str_with_htmltag,childList.get(headerList.get(groupPosition)).get(childPosition).getSubTitle().trim())));

        if (ordersDetailListAdapterModel.getOtherImageUrl() != null) {
            Utils.setImageWithGlideWithoutDefaultPlaceholder(context, itemCiv, ordersDetailListAdapterModel.getOtherImageUrl(), null, true, true);
            itemCiv.setDisableCircularTransformation(true);
            itemCiv.setBackgroundColor(Color.LTGRAY);
        } else if (key != null && userDetailHashMap.containsKey(key)) {
            itemCiv.setBackgroundColor(Color.TRANSPARENT);
            itemCiv.setDisableCircularTransformation(false);
            Utils.setImageWithGlide(context, itemCiv, userDetailHashMap.get(key).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);
        }

        if (key != null && userDetailHashMap.containsKey(key)) {
            itemSubTitleTv.setText(userDetailHashMap.get(key).getUserDisplay_name());
        }

        itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fragment != null) {
                    boolean isPermissionGranted = true;

                    if (ordersDetailListAdapterModel.isForm()) {
                        isPermissionGranted = PermissionChecker.with(context).checkPermission(PermissionConstants.PERMISSION_STORAGE);
                    }

                    if (ordersDetailListAdapterModel.getCommonResultResponseModel() instanceof EducationalVideoOrder) {
                        bundle.putSerializable(ArgumentKeys.EDUCATIONAL_VIDEO,(EducationalVideoOrder) ordersDetailListAdapterModel.getCommonResultResponseModel());
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
