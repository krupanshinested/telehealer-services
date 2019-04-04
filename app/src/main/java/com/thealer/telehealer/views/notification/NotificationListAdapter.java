package com.thealer.telehealer.views.notification;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.RoundCornerConstraintLayout;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 08,January,2019
 */
public class NotificationListAdapter extends BaseExpandableListAdapter {

    public static final String REQUEST_TYPE_CONNECTION = "connection";
    public static final String REQUEST_TYPE_APPOINTMENT = "appointment";
    private final String REQUEST_TYPE_MISSED_CALL = "missed_call";

    public static final String REQUEST_STATUS_OPEN = "open";
    public static final String REQUEST_STATUS_ACCEPTED = "accepted";
    public static final String REQUEST_STATUS_REJECTED = "rejected";
    public static final String REQUEST_STATUS_CANCELED = "cancel";

    private final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    private final String CANCELED = "CANCELED";


    private FragmentActivity activity;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private List<String> headerList = new ArrayList<>();
    private Map<String, List<NotificationApiResponseModel.ResultBean.RequestsBean>> childList = new HashMap<>();
    private int selectedSlot = 0;
    private NotificationApiViewModel notificationApiViewModel;

    public NotificationListAdapter(FragmentActivity activity) {
        this.activity = activity;
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        notificationApiViewModel = ViewModelProviders.of(activity).get(NotificationApiViewModel.class);
    }

    @Override
    public int getGroupCount() {
        return headerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(headerList.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return headerList.get(groupPosition);
    }

    @Override
    public NotificationApiResponseModel.ResultBean.RequestsBean getChild(int groupPosition, int childPosition) {
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

        convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_expandable_list_header_view, parent, false);

        TextView recentHeaderTv = (TextView) convertView.findViewById(R.id.recent_header_tv);
        recentHeaderTv.setText(getGroup(groupPosition));

        return convertView;
    }

    String doctorGuid = null;

    public static class ChildViewHolder {
        ConstraintLayout userDetailCl, slotCl, actionCl, doctorDetailCl;
        CircleImageView avatarCiv;
        TextView titleTv, nameTv, userDetailTv, descriptionTv, slotLabel, doctorNameTv,
                slotTime1Tv, slotDate1Tv, slotTime2Tv, slotDate2Tv, slotTime3Tv, slotDate3Tv;
        ImageView infoIv;
        CustomButton acceptBtn;
        RoundCornerConstraintLayout slot1Btn, slot2Btn, slot3Btn;
        Button rejectBtn;
        View bottomView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CardView notificationCv;
        ChildViewHolder childViewHolder;
        CommonUserApiResponseModel doctorModel = null, patientModel = null, MaModel = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_notification_list, parent, false);
            childViewHolder = new ChildViewHolder();
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        notificationCv = (CardView) convertView.findViewById(R.id.notification_cv);
        childViewHolder.bottomView = (View) convertView.findViewById(R.id.bottom_view);
        childViewHolder.doctorDetailCl = (ConstraintLayout) convertView.findViewById(R.id.doctor_detail_cl);
        childViewHolder.doctorNameTv = (TextView) convertView.findViewById(R.id.doctor_name_tv);
        childViewHolder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
        childViewHolder.userDetailCl = (ConstraintLayout) convertView.findViewById(R.id.user_detail_cl);
        childViewHolder.avatarCiv = (CircleImageView) convertView.findViewById(R.id.avatar_civ);
        childViewHolder.nameTv = (TextView) convertView.findViewById(R.id.list_title_tv);
        childViewHolder.userDetailTv = (TextView) convertView.findViewById(R.id.list_sub_title_tv);
        childViewHolder.infoIv = (ImageView) convertView.findViewById(R.id.info_iv);
        childViewHolder.descriptionTv = (TextView) convertView.findViewById(R.id.description_tv);
        childViewHolder.slotCl = (ConstraintLayout) convertView.findViewById(R.id.slot_cl);
        childViewHolder.slotLabel = (TextView) convertView.findViewById(R.id.slot_label);
        childViewHolder.slot1Btn = (RoundCornerConstraintLayout) convertView.findViewById(R.id.slot1_btn);
        childViewHolder.slot2Btn = (RoundCornerConstraintLayout) convertView.findViewById(R.id.slot2_btn);
        childViewHolder.slot3Btn = (RoundCornerConstraintLayout) convertView.findViewById(R.id.slot3_btn);
        childViewHolder.slotTime1Tv = (TextView) convertView.findViewById(R.id.slot_time1_tv);
        childViewHolder.slotDate1Tv = (TextView) convertView.findViewById(R.id.slot_date1_tv);
        childViewHolder.slotTime2Tv = (TextView) convertView.findViewById(R.id.slot_time2_tv);
        childViewHolder.slotDate2Tv = (TextView) convertView.findViewById(R.id.slot_date2_tv);
        childViewHolder.slotTime3Tv = (TextView) convertView.findViewById(R.id.slot_time3_tv);
        childViewHolder.slotDate3Tv = (TextView) convertView.findViewById(R.id.slot_date3_tv);

        childViewHolder.actionCl = (ConstraintLayout) convertView.findViewById(R.id.action_cl);
        childViewHolder.acceptBtn = (CustomButton) convertView.findViewById(R.id.accept_btn);
        childViewHolder.rejectBtn = (Button) convertView.findViewById(R.id.reject_btn);

        NotificationApiResponseModel.ResultBean.RequestsBean resultModel = getChild(groupPosition, childPosition);


        switch (resultModel.getRequestee().getRole()) {
            case Constants.ROLE_DOCTOR:
                doctorModel = resultModel.getRequestee();
                break;
            case Constants.ROLE_PATIENT:
            case Constants.ROLE_ASSISTANT:
                patientModel = resultModel.getRequestee();
                MaModel = resultModel.getRequestee();
                break;
        }

        switch (resultModel.getRequestor().getRole()) {
            case Constants.ROLE_DOCTOR:
                doctorModel = resultModel.getRequestor();
                break;
            case Constants.ROLE_PATIENT:
            case Constants.ROLE_ASSISTANT:
                patientModel = resultModel.getRequestor();
                MaModel = resultModel.getRequestor();
                break;
        }

        String title = "", description = null;

        boolean isMissedCall = false;

        childViewHolder.actionCl.setVisibility(View.GONE);
        childViewHolder.slotCl.setVisibility(View.GONE);
        childViewHolder.bottomView.setVisibility(View.GONE);

        switch (resultModel.getType()) {
            case REQUEST_TYPE_APPOINTMENT:
                title = activity.getString(R.string.appointment_request);
                childViewHolder.descriptionTv.setVisibility(View.VISIBLE);
                if (resultModel.getDetail() != null) {
                    description = resultModel.getDetail().getReason();
                }
                childViewHolder.bottomView.setVisibility(View.VISIBLE);
                switch (resultModel.getStatus()) {
                    case REQUEST_STATUS_OPEN:
                        childViewHolder.slotCl.setVisibility(View.VISIBLE);
                        childViewHolder.actionCl.setVisibility(View.VISIBLE);
                        break;
                    case REQUEST_STATUS_ACCEPTED:
                        break;
                    case REQUEST_STATUS_CANCELED:
                        break;
                    case REQUEST_STATUS_REJECTED:
                        break;
                    default:
                        childViewHolder.slotCl.setVisibility(View.GONE);
                        childViewHolder.actionCl.setVisibility(View.GONE);
                        break;
                }
                break;
            case REQUEST_TYPE_CONNECTION:
                title = activity.getString(R.string.connection_request);
                childViewHolder.descriptionTv.setVisibility(View.VISIBLE);
                if (resultModel.getDetail() != null) {
                    description = resultModel.getDetail().getReason();
                }
                switch (resultModel.getStatus()) {
                    case REQUEST_STATUS_OPEN:
                        childViewHolder.actionCl.setVisibility(View.VISIBLE);
                        if (resultModel.isIs_requestee()) {
                            childViewHolder.bottomView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case REQUEST_STATUS_ACCEPTED:
                        break;
                    case REQUEST_STATUS_CANCELED:
                        break;
                    case REQUEST_STATUS_REJECTED:
                        break;
                    default:
                        childViewHolder.actionCl.setVisibility(View.GONE);
                        childViewHolder.slotCl.setVisibility(View.GONE);
                        break;
                }
                break;
            case REQUEST_TYPE_MISSED_CALL:
                if (UserType.isUserPatient()) {
                    title = activity.getString(R.string.missed_call);
                } else {
                    title = activity.getString(R.string.not_answered);
                }
                childViewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                childViewHolder.bottomView.setVisibility(View.GONE);
                isMissedCall = true;
                break;
        }

        if (!isMissedCall) {
            switch (resultModel.getStatus()) {
                case REQUEST_STATUS_OPEN:
                    childViewHolder.titleTv.setTextColor(activity.getColor(android.R.color.holo_orange_dark));
                    title = title.concat(" ").concat(activity.getString(R.string.pending).toUpperCase());
                    break;
                case REQUEST_STATUS_ACCEPTED:
                    childViewHolder.titleTv.setTextColor(activity.getColor(R.color.color_green_light));
                    title = title.concat(" ").concat(activity.getString(R.string.accepted).toUpperCase());
                    break;
                case REQUEST_STATUS_CANCELED:
                    childViewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                    title = title.concat(" ").concat(activity.getString(R.string.canceled).toUpperCase());
                    break;
                case REQUEST_STATUS_REJECTED:
                    childViewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                    title = title.concat(" ").concat(activity.getString(R.string.rejected).toUpperCase());
                    break;
            }
        }

        if (UserType.isUserAssistant() && doctorModel != null) {
            childViewHolder.doctorDetailCl.setVisibility(View.VISIBLE);
            title = title.concat(" " + activity.getString(R.string.For));
            childViewHolder.doctorNameTv.setText(doctorModel.getUserDisplay_name());

            if (resultModel.getType().equals(REQUEST_TYPE_CONNECTION) &&
                    MaModel != null && MaModel.getUser_id() == UserDetailPreferenceManager.getWhoAmIResponse().getUser_id()) {
                childViewHolder.doctorDetailCl.setVisibility(View.GONE);
            }
        } else {
            childViewHolder.doctorDetailCl.setVisibility(View.GONE);
        }

        childViewHolder.titleTv.setText(title);

        if (UserType.isUserDoctor()) {
            if (patientModel != null) {
                Utils.setImageWithGlide(activity.getApplicationContext(), childViewHolder.avatarCiv, patientModel.getUser_avatar(), null, true, true);
                childViewHolder.nameTv.setText(patientModel.getUserDisplay_name());
                if (patientModel.getRole().equals(Constants.ROLE_PATIENT)) {
                    childViewHolder.userDetailTv.setText(patientModel.getDob());
                } else {
                    childViewHolder.userDetailTv.setText(patientModel.getAssistantTitle());
                }
            }
        } else if (UserType.isUserPatient()) {
            String userAvatar = null, name = null, info = null;
            if (doctorModel != null) {
                userAvatar = doctorModel.getUser_avatar();
                name = doctorModel.getUserDisplay_name();
                info = doctorModel.getDisplayInfo();
            } else if (MaModel != null) {
                userAvatar = MaModel.getUser_avatar();
                name = MaModel.getUserDisplay_name();
                info = MaModel.getDisplayInfo();
            }
            Utils.setImageWithGlide(activity.getApplicationContext(), childViewHolder.avatarCiv, userAvatar, activity.getDrawable(R.drawable.profile_placeholder), true, true);
            childViewHolder.nameTv.setText(name);
            childViewHolder.userDetailTv.setText(info);
        } else if (UserType.isUserAssistant()) {

            if (resultModel.isOwnNotification() && resultModel.getType().equals(REQUEST_TYPE_CONNECTION) && patientModel != null) {
                if (doctorModel != null) {
                    Utils.setImageWithGlide(activity.getApplicationContext(), childViewHolder.avatarCiv, doctorModel.getUser_avatar(), null, true, true);
                    childViewHolder.nameTv.setText(doctorModel.getUserDisplay_name());
                    childViewHolder.userDetailTv.setText(doctorModel.getDisplayInfo());
                }
            } else {
                if (patientModel != null) {
                    Utils.setImageWithGlide(activity.getApplicationContext(), childViewHolder.avatarCiv, patientModel.getUser_avatar(), null, true, true);
                    childViewHolder.nameTv.setText(patientModel.getUserDisplay_name());
                    if (patientModel.getRole().equals(Constants.ROLE_PATIENT)) {
                        childViewHolder.userDetailTv.setText(patientModel.getDob());
                    } else {
                        childViewHolder.userDetailTv.setText(patientModel.getAssistantTitle());
                    }
                }
            }
        }


        if (description != null && !description.isEmpty()) {
            childViewHolder.descriptionTv.setText(description);
        } else {
            childViewHolder.descriptionTv.setVisibility(View.GONE);
        }

        if (resultModel.getDetail() != null &&
                resultModel.getDetail().getDates() != null) {
            for (int i = 0; i < resultModel.getDetail().getDates().size(); i++) {
                String[] date = Utils.getNotificationSlotTime(resultModel.getDetail().getDates().get(i).getStart());
                switch (i) {
                    case 0:
                        childViewHolder.slotTime1Tv.setText(date[0]);
                        childViewHolder.slotDate1Tv.setText(date[1]);
                        childViewHolder.slot1Btn.setVisibility(View.VISIBLE);

                        childViewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                        childViewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        childViewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        break;
                    case 1:
                        childViewHolder.slotTime2Tv.setText(date[0]);
                        childViewHolder.slotDate2Tv.setText(date[1]);
                        childViewHolder.slot2Btn.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        childViewHolder.slotTime3Tv.setText(date[0]);
                        childViewHolder.slotDate3Tv.setText(date[1]);
                        childViewHolder.slot3Btn.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        CommonUserApiResponseModel finalPatientModel = patientModel;
        if (UserType.isUserPatient() && doctorModel == null && MaModel != null) {
            doctorModel = MaModel;
        }
        CommonUserApiResponseModel finalDoctorModel = doctorModel;

        childViewHolder.infoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                Bundle bundle = new Bundle();
                if (UserType.isUserDoctor()) {
                    bundle.putSerializable(Constants.USER_DETAIL, finalPatientModel);
                } else if (UserType.isUserPatient()) {
                    bundle.putSerializable(Constants.USER_DETAIL, finalDoctorModel);
                } else {

                    if (resultModel.getType().equals(REQUEST_TYPE_CONNECTION) && resultModel.isOwnNotification()) {
                        bundle.putSerializable(Constants.USER_DETAIL, finalDoctorModel);
                    } else {
                        bundle.putSerializable(Constants.USER_DETAIL, finalPatientModel);
                        bundle.putSerializable(Constants.DOCTOR_DETAIL, finalDoctorModel);
                    }
                }

                if (resultModel.getType().equals(REQUEST_TYPE_CONNECTION)) {
                    if (resultModel.getStatus().equals(REQUEST_STATUS_ACCEPTED)) {
                        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                    } else {
                        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_CONNECTION);
                    }
                } else {
                    bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                }

                bundle.putBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, true);

                doctorPatientDetailViewFragment.setArguments(bundle);
                showSubFragmentInterface.onShowFragment(doctorPatientDetailViewFragment);
            }
        });

        childViewHolder.slot1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSlot = 0;

                childViewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                childViewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                childViewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
            }
        });
        childViewHolder.slot2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSlot = 1;

                childViewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                childViewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                childViewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
            }
        });
        childViewHolder.slot3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSlot = 2;

                childViewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                childViewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                childViewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
            }
        });

        childViewHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = null, endDate = null;
                switch (resultModel.getType()) {
                    case REQUEST_TYPE_APPOINTMENT:
                        if (resultModel.getDetail() != null &&
                                resultModel.getDetail().getDates() != null) {
                            startDate = resultModel.getDetail().getDates().get(selectedSlot).getStart();
                            endDate = resultModel.getDetail().getDates().get(selectedSlot).getEnd();
                        }
                        break;
                }
                if (!resultModel.isOwnNotification()) {
                    if (UserType.isUserAssistant()) {
                        doctorGuid = resultModel.getDoctorModel().getUser_guid();
                    }
                }
                updateRequest(resultModel.getType(), false, resultModel.getRequestor().getUser_guid(), resultModel.getRequest_id(), REJECTED.toLowerCase(), startDate, endDate, doctorGuid, true,resultModel.getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
            }
        });

        childViewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrate(activity);
                String startDate = null, endDate = null;
                switch (resultModel.getType()) {
                    case REQUEST_TYPE_APPOINTMENT:
                        if (resultModel.getDetail() != null &&
                                resultModel.getDetail().getDates() != null) {
                            startDate = resultModel.getDetail().getDates().get(selectedSlot).getStart();
                            endDate = resultModel.getDetail().getDates().get(selectedSlot).getEnd();
                        }
                        break;
                }
                if (!resultModel.isOwnNotification()) {
                    if (UserType.isUserAssistant()) {
                        doctorGuid = resultModel.getDoctorModel().getUser_guid();
                    }
                }
                updateRequest(resultModel.getType(), true, resultModel.getRequestor().getUser_guid(), resultModel.getRequest_id(), ACCEPTED.toLowerCase(), startDate, endDate, doctorGuid, true,resultModel.getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
            }
        });

        if (!resultModel.isIs_requestee()) {
            childViewHolder.actionCl.setVisibility(View.GONE);
            if (resultModel.isRequestor_read_status()) {
                notificationCv.setCardBackgroundColor(activity.getColor(R.color.card_background_grey));
            }
        } else {
            if (resultModel.isRequestee_read_status()) {
                notificationCv.setCardBackgroundColor(activity.getColor(R.color.card_background_grey));
            }
        }

        return convertView;
    }

    private void updateRequest(String type, boolean isAccept, String toGuid, @NonNull int id, @NonNull String status, @Nullable String startDate, @Nullable String endDate,
                               @Nullable String doctorGuid, boolean isShowProgress,boolean isRequestorMA) {
        notificationApiViewModel.updateNotification(type, isAccept, toGuid, id, status, startDate, endDate, doctorGuid, isShowProgress,isRequestorMA);
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<String> headerList, Map<String, List<NotificationApiResponseModel.ResultBean.RequestsBean>> childList, int page) {

        this.headerList = headerList;
        this.childList = childList;

        notifyDataSetChanged();
    }
}
