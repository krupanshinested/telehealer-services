package com.thealer.telehealer.views.notification;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.transaction.AskToAddCardViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.pubNub.models.APNSPayload;
import com.thealer.telehealer.stripe.AppPaymentCardUtils;
import com.thealer.telehealer.stripe.PaymentContentActivity;
import com.thealer.telehealer.views.EducationalVideo.EducationalVideoDetailFragment;
import com.thealer.telehealer.views.common.RoundCornerConstraintLayout;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.chat.ChatActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;
import com.thealer.telehealer.views.home.orders.OrdersListFragment;
import com.thealer.telehealer.views.home.orders.document.ViewDocumentFragment;
import com.thealer.telehealer.views.home.orders.forms.EditableFormFragment;
import com.thealer.telehealer.views.home.orders.labs.LabsDetailViewFragment;
import com.thealer.telehealer.views.home.orders.miscellaneous.MiscellaneousDetailViewFragment;
import com.thealer.telehealer.views.home.orders.prescription.PrescriptionDetailViewFragment;
import com.thealer.telehealer.views.home.orders.radiology.RadiologyDetailViewFragment;
import com.thealer.telehealer.views.home.orders.specialist.SpecialistDetailViewFragment;
import com.thealer.telehealer.views.home.vitals.VitalsDetailListFragment;
import com.thealer.telehealer.views.home.vitals.VitalsListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 24,June,2019
 */
public class NewNotificationListAdapter extends RecyclerView.Adapter<NewNotificationListAdapter.ViewHolder> {
    private final int TYPE_HEADER = 1;
    private final int TYPE_DATA = 2;

    public static final String REQUEST_TYPE_CONNECTION = "connection";
    public static final String REQUEST_TYPE_APPOINTMENT = "appointment";
    private final String REQUEST_TYPE_MISSED_CALL = "missed_call";
    private final String REQUEST_TYPE_CALLS = "calls";
    private final String REQUEST_TYPE_ABNORMAL_VITAL = "vitals";
    private final String MESSAGES = "messages";

    public static final String REQUEST_STATUS_OPEN = "open";
    public static final String REQUEST_STATUS_ACCEPTED = "accepted";
    public static final String REQUEST_STATUS_REJECTED = "rejected";
    public static final String REQUEST_STATUS_CANCELED = "cancel";

    private final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
    private final String CANCELED = "CANCELED";


    private List<NotificationListModel> modelList;
    private FragmentActivity activity;
    private List<NotificationApiResponseModel.ResultBean.RequestsBean> notificationList;
    private int selectedSlot = 0;
    private NotificationApiViewModel notificationApiViewModel;
    private AskToAddCardViewModel askToAddCardViewModel;

    private ShowSubFragmentInterface showSubFragmentInterface;

    public NewNotificationListAdapter(FragmentActivity activity, AskToAddCardViewModel askToAddCardViewModel) {
        this.activity = activity;
        modelList = new ArrayList<>();
        showSubFragmentInterface = (ShowSubFragmentInterface) activity;
        notificationApiViewModel = new ViewModelProvider(activity).get(NotificationApiViewModel.class);
        this.askToAddCardViewModel = askToAddCardViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (i) {
            case TYPE_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_list_header_view, viewGroup, false);
                break;
            case TYPE_DATA:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_notification_list, viewGroup, false);
                break;
        }
        return new ViewHolder(view);
    }

    private String doctorGuid = null;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        switch (modelList.get(i).getType()) {
            case TYPE_HEADER:
                viewHolder.headerTv.setText(modelList.get(i).getDate());
                break;
            case TYPE_DATA:
                CommonUserApiResponseModel doctorModel = null, patientModel = null, MaModel = null;
                NotificationApiResponseModel.ResultBean.RequestsBean resultModel = modelList.get(i).getDataBean();

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

                boolean isAddRequestStatus = false;

                viewHolder.actionCl.setVisibility(View.GONE);
                viewHolder.slotCl.setVisibility(View.GONE);
                viewHolder.bottomView.setVisibility(View.GONE);

                viewHolder.titleTv.setTextColor(activity.getColor(android.R.color.holo_orange_dark));

                switch (resultModel.getType()) {
                    case REQUEST_TYPE_APPOINTMENT:
                        isAddRequestStatus = true;
                        title = activity.getString(R.string.appointment_request);
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        if (resultModel.getDetail() != null) {
                            description = resultModel.getDetail().getReason();
                        }
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        switch (resultModel.getStatus()) {
                            case REQUEST_STATUS_OPEN:
                                viewHolder.slotCl.setVisibility(View.VISIBLE);
                                viewHolder.actionCl.setVisibility(View.VISIBLE);
                                if (!UserType.isUserPatient()) {
                                    viewHolder.hasCardIV.setVisibility(View.VISIBLE);
                                    AppPaymentCardUtils.setCardStatusImage(viewHolder.hasCardIV, patientModel.getPayment_account_info());
                                    if (!AppPaymentCardUtils.hasValidPaymentCard(patientModel.getPayment_account_info())) {
                                        viewHolder.askForCardBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;
                            case REQUEST_STATUS_ACCEPTED:
                                break;
                            case REQUEST_STATUS_CANCELED:
                                break;
                            case REQUEST_STATUS_REJECTED:
                                break;
                            default:
                                viewHolder.slotCl.setVisibility(View.GONE);
                                viewHolder.actionCl.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case REQUEST_TYPE_CONNECTION:
                        isAddRequestStatus = true;
                        title = activity.getString(R.string.connection_request);
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        if (resultModel.getDetail() != null) {
                            description = resultModel.getDetail().getReason();
                        }
                        switch (resultModel.getStatus()) {
                            case REQUEST_STATUS_OPEN:
                                viewHolder.actionCl.setVisibility(View.VISIBLE);
                                if (resultModel.isIs_requestee()) {
                                    viewHolder.bottomView.setVisibility(View.VISIBLE);
                                }
                                break;
                            case REQUEST_STATUS_ACCEPTED:
                                break;
                            case REQUEST_STATUS_CANCELED:
                                break;
                            case REQUEST_STATUS_REJECTED:
                                break;
                            default:
                                viewHolder.actionCl.setVisibility(View.GONE);
                                viewHolder.slotCl.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case REQUEST_TYPE_MISSED_CALL:
                        if (UserType.isUserPatient()) {
                            title = activity.getString(R.string.missed_call);
                        } else {
                            title = activity.getString(R.string.not_answered);
                        }
                        viewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                        viewHolder.bottomView.setVisibility(View.GONE);
                        break;
                    case REQUEST_TYPE_ABNORMAL_VITAL:
                        title = activity.getString(R.string.abnormal_vital).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_FORM:
                        title = activity.getString(R.string.form).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_PRESCRIPTIONS:
                        title = activity.getString(R.string.prescription).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_SPECIALIST:
                        title = activity.getString(R.string.specialist).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_LABS:
                        title = activity.getString(R.string.labs).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_X_RAY:
                        title = activity.getString(R.string.radiology).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_FILES:
                        title = activity.getString(R.string.documents).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case OrderConstant.ORDER_TYPE_MISC:
                        title = activity.getString(R.string.miscellaneous).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case NotificationConstants.ORDERS:
                        title = activity.getString(R.string.orders).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case MESSAGES:
                        title = activity.getString(R.string.messages).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        break;
                    case REQUEST_TYPE_CALLS:
                        if (UserType.isUserPatient())
                            title = activity.getString(R.string.missed_call).toUpperCase();
                        else
                            title = activity.getString(R.string.not_answered).toUpperCase();

                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        viewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                        break;
                    case NotificationConstants.EDUCATIONAL_VIDEO:
                        title = activity.getString(R.string.educational_video).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        viewHolder.titleTv.setTextColor(activity.getColor(R.color.app_gradient_start));
                        break;
                    case APNSPayload.creditCardRequested: {
                        title = activity.getString(R.string.lbl_add_card).toUpperCase();
                        description = resultModel.getMessage();
                        viewHolder.descriptionTv.setVisibility(View.VISIBLE);
                        viewHolder.bottomView.setVisibility(View.VISIBLE);
                        viewHolder.titleTv.setTextColor(activity.getColor(R.color.app_gradient_start));
                        viewHolder.askForCardBtn.setText(R.string.lbl_add_card);
                        viewHolder.acceptBtn.setVisibility(View.GONE);
                        viewHolder.rejectBtn.setVisibility(View.GONE);
                        viewHolder.askForCardBtn.setVisibility(View.VISIBLE);
                        viewHolder.actionCl.setVisibility(View.VISIBLE);
                        break;
                    }

                }

                if (isAddRequestStatus) {
                    switch (resultModel.getStatus()) {
                        case REQUEST_STATUS_OPEN:
                            viewHolder.titleTv.setTextColor(activity.getColor(android.R.color.holo_orange_dark));
                            title = title.concat(" ").concat(activity.getString(R.string.pending).toUpperCase());
                            break;
                        case REQUEST_STATUS_ACCEPTED:
                            viewHolder.titleTv.setTextColor(activity.getColor(R.color.color_green_light));
                            title = title.concat(" ").concat(activity.getString(R.string.accepted).toUpperCase());
                            break;
                        case REQUEST_STATUS_CANCELED:
                            viewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                            title = title.concat(" ").concat(activity.getString(R.string.canceled).toUpperCase());
                            break;
                        case REQUEST_STATUS_REJECTED:
                            viewHolder.titleTv.setTextColor(activity.getColor(R.color.red));
                            title = title.concat(" ").concat(activity.getString(R.string.rejected).toUpperCase());
                            break;
                    }
                }

                if (UserType.isUserAssistant() && doctorModel != null) {
                    viewHolder.doctorDetailCl.setVisibility(View.VISIBLE);
                    if (isAddRequestStatus)
                        title = title.concat(" " + activity.getString(R.string.For).toUpperCase());

                    viewHolder.doctorNameTv.setText(doctorModel.getUserDisplay_name());

                    if (resultModel.getType().equals(REQUEST_TYPE_CONNECTION) &&
                            MaModel != null && MaModel.getUser_id() == UserDetailPreferenceManager.getWhoAmIResponse().getUser_id()) {
                        viewHolder.doctorDetailCl.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.doctorDetailCl.setVisibility(View.GONE);
                }

                viewHolder.titleTv.setText(title);

                if (UserType.isUserDoctor()) {
                    if (patientModel != null) {
                        Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.avatarCiv, patientModel.getUser_avatar(), null, true, true);
                        viewHolder.listTitleTv.setText(patientModel.getUserDisplay_name());
                        if (patientModel.getRole().equals(Constants.ROLE_PATIENT)) {
                            viewHolder.listSubTitleTv.setText(patientModel.getDob());
                        } else {
                            viewHolder.listSubTitleTv.setText(patientModel.getAssistantTitle());
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
                    Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.avatarCiv, userAvatar, activity.getDrawable(R.drawable.profile_placeholder), true, true);
                    viewHolder.listTitleTv.setText(name);
                    viewHolder.listSubTitleTv.setText(info);
                } else if (UserType.isUserAssistant()) {

                    if (resultModel.isOwnNotification() && resultModel.getType().equals(REQUEST_TYPE_CONNECTION) && patientModel != null) {
                        if (doctorModel != null) {
                            Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.avatarCiv, doctorModel.getUser_avatar(), null, true, true);
                            viewHolder.listTitleTv.setText(doctorModel.getUserDisplay_name());
                            viewHolder.listSubTitleTv.setText(doctorModel.getDisplayInfo());
                        }
                    } else {
                        if (patientModel != null) {
                            Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.avatarCiv, patientModel.getUser_avatar(), null, true, true);
                            viewHolder.listTitleTv.setText(patientModel.getUserDisplay_name());
                            if (patientModel.getRole().equals(Constants.ROLE_PATIENT)) {
                                viewHolder.listSubTitleTv.setText(patientModel.getDob());
                            } else {
                                viewHolder.listSubTitleTv.setText(patientModel.getAssistantTitle());
                            }
                        }
                    }
                }


                if (description != null && !description.isEmpty()) {
                    viewHolder.descriptionTv.setText(description);
                } else {
                    viewHolder.descriptionTv.setVisibility(View.GONE);
                }

                if (resultModel.getDetail() != null &&
                        resultModel.getDetail().getDates() != null) {
                    for (int j = 0; j < resultModel.getDetail().getDates().size(); j++) {
                        String[] date = Utils.getNotificationSlotTime(resultModel.getDetail().getDates().get(j).getStart());
                        switch (j) {
                            case 0:
                                viewHolder.slotTime1Tv.setText(date[0]);
                                viewHolder.slotDate1Tv.setText(date[1]);
                                viewHolder.slot1Btn.setVisibility(View.VISIBLE);

                                viewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                                viewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                                viewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                                break;
                            case 1:
                                viewHolder.slotTime2Tv.setText(date[0]);
                                viewHolder.slotDate2Tv.setText(date[1]);
                                viewHolder.slot2Btn.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                viewHolder.slotTime3Tv.setText(date[0]);
                                viewHolder.slotDate3Tv.setText(date[1]);
                                viewHolder.slot3Btn.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }

                CommonUserApiResponseModel finalPatientModel = patientModel;
                if (UserType.isUserPatient() && doctorModel == null && MaModel != null) {
                    doctorModel = MaModel;
                }
                CommonUserApiResponseModel finalDoctorModel = doctorModel;

                viewHolder.notificationCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();

                        switch (resultModel.getType()) {
                            case REQUEST_TYPE_ABNORMAL_VITAL:
                                if (resultModel.getEntity_id() == null) {
                                    VitalsListFragment vitalsListFragment = new VitalsListFragment();
                                    bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
                                    vitalsListFragment.setArguments(bundle);
                                    showSubFragmentInterface.onShowFragment(vitalsListFragment);
                                } else {
                                    VitalsDetailListFragment vitalsDetailListFragment = new VitalsDetailListFragment();
                                    if (finalPatientModel != null) {
                                        bundle.putString(ArgumentKeys.USER_GUID, finalPatientModel.getUser_guid());
                                        bundle.putSerializable(Constants.USER_DETAIL, finalPatientModel);
                                    }
                                    if (finalDoctorModel != null) {
                                        bundle.putString(ArgumentKeys.DOCTOR_GUID, finalDoctorModel.getUser_guid());
                                        bundle.putSerializable(Constants.DOCTOR_DETAIL, finalDoctorModel);
                                    }
                                    bundle.putInt(ArgumentKeys.ORDER_ID, resultModel.getEntity_id());
                                    bundle.putBoolean(ArgumentKeys.IS_GET_TYPE, true);
                                    bundle.putBoolean(ArgumentKeys.VIEW_ABNORMAL_VITAL, true);

                                    vitalsDetailListFragment.setArguments(bundle);

                                    showSubFragmentInterface.onShowFragment(vitalsDetailListFragment);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_FORM:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new EditableFormFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_PRESCRIPTIONS:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new PrescriptionDetailViewFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_SPECIALIST:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new SpecialistDetailViewFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_LABS:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new LabsDetailViewFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_X_RAY:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new RadiologyDetailViewFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_FILES:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new ViewDocumentFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case OrderConstant.ORDER_TYPE_MISC:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    showOrderDetail(new MiscellaneousDetailViewFragment(), resultModel.getEntity_id(), finalPatientModel, finalDoctorModel);
                                }
                                break;
                            case MESSAGES:
                                Bundle bundle1 = new Bundle();
                                bundle1.putSerializable(Constants.USER_DETAIL, resultModel.getOtherUserModel());

                                if (UserType.isUserAssistant() && resultModel.getDoctorModel() != null) {
                                    bundle1.putSerializable(Constants.DOCTOR_DETAIL, resultModel.getDoctorModel());
                                }
                                activity.startActivity(new Intent(activity, ChatActivity.class).putExtras(bundle1));
                                break;
                            case NotificationConstants.ORDERS:
                                if (resultModel.getEntity_id() == null) {
                                    showOrdersListView();
                                } else {
                                    if (resultModel.getSub_type() != null) {
                                        if (finalPatientModel != null) {
                                            bundle.putString(ArgumentKeys.USER_GUID, finalPatientModel.getUser_guid());
                                            bundle.putSerializable(Constants.USER_DETAIL, finalPatientModel);
                                        }
                                        if (finalDoctorModel != null) {
                                            bundle.putString(ArgumentKeys.DOCTOR_GUID, finalDoctorModel.getUser_guid());
                                            bundle.putSerializable(Constants.DOCTOR_DETAIL, finalDoctorModel);
                                        }

                                        bundle.putInt(ArgumentKeys.ORDER_ID, resultModel.getEntity_id());
                                        Fragment fragment = null;

                                        switch (resultModel.getSub_type()) {
                                            case NotificationConstants.ORDER_SUB_TYPE_PRESCRIPTIONS:
                                                fragment = new PrescriptionDetailViewFragment();
                                                break;
                                            case NotificationConstants.ORDER_SUB_TYPE_SPECIALIST:
                                                fragment = new SpecialistDetailViewFragment();
                                                break;
                                            case NotificationConstants.ORDER_SUB_TYPE_X_RAY:
                                                fragment = new RadiologyDetailViewFragment();
                                                break;
                                            case NotificationConstants.ORDER_SUB_TYPE_LABS:
                                                fragment = new LabsDetailViewFragment();
                                                break;
                                            case NotificationConstants.ORDER_SUB_TYPE_MISC:
                                                fragment = new MiscellaneousDetailViewFragment();
                                                break;
                                        }
                                        if (fragment != null) {
                                            fragment.setArguments(bundle);
                                            showSubFragmentInterface.onShowFragment(fragment);
                                        }
                                    }
                                }
                                break;
                            case NotificationConstants.EDUCATIONAL_VIDEO:
                                if (resultModel.getEntity_id() != null) {
                                    EducationalVideoDetailFragment fragment = new EducationalVideoDetailFragment();
                                    Bundle detail = new Bundle();
                                    if (UserType.isUserAssistant()) {
                                        detail.putString(ArgumentKeys.DOCTOR_GUID, resultModel.getDoctorModel().getUser_guid());
                                    }
                                    detail.putString(ArgumentKeys.USER_GUID, resultModel.getPatientModel().getUser_guid());
                                    detail.putString(ArgumentKeys.EDUCATIONAL_VIDEO_ID, resultModel.getEntity_id() + "");
                                    fragment.setArguments(detail);
                                    showSubFragmentInterface.onShowFragment(fragment);
                                }
                                break;
                            default:
                                showUserDetailView(resultModel, finalDoctorModel, finalPatientModel);
                        }
                    }
                });

                viewHolder.slot1Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedSlot = 0;

                        viewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                        viewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        viewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                    }
                });
                viewHolder.slot2Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedSlot = 1;

                        viewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        viewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                        viewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                    }
                });
                viewHolder.slot3Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedSlot = 2;

                        viewHolder.slot1Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        viewHolder.slot2Btn.setStrokeColor(activity.getColor(R.color.colorGrey_light));
                        viewHolder.slot3Btn.setStrokeColor(activity.getColor(R.color.app_gradient_start));
                    }
                });

                viewHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.rejectBtn.setEnabled(false);
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
                        updateRequest(resultModel.getType(), false, resultModel.getRequestor().getUser_guid(), resultModel.getRequest_id(), REJECTED.toLowerCase(), startDate, endDate, doctorGuid, true, resultModel.getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
                    }
                });

                viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.acceptBtn.setEnabled(false);
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
                        updateRequest(resultModel.getType(), true, resultModel.getRequestor().getUser_guid(), resultModel.getRequest_id(), ACCEPTED.toLowerCase(), startDate, endDate, doctorGuid, true, resultModel.getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
                    }
                });

                viewHolder.infoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserDetailView(resultModel, finalDoctorModel, finalPatientModel);
                    }
                });

                viewHolder.askForCardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserType.isUserAssistant())
                            askToAddCardViewModel.askToAddCard(finalPatientModel.getUser_guid(), finalDoctorModel.getUser_guid());
                        else if (UserType.isUserDoctor()) {
                            askToAddCardViewModel.askToAddCard(finalPatientModel.getUser_guid(), null);
                        } else if (UserType.isUserPatient()) {
                            activity.startActivity(new Intent(activity, PaymentContentActivity.class).putExtra(ArgumentKeys.IS_HEAD_LESS, true));
                        }
                    }
                });

                if (!resultModel.isIs_requestee()) {
                    viewHolder.actionCl.setVisibility(View.GONE);
                    if (resultModel.isRequestor_read_status()) {
                        viewHolder.notificationCv.setCardBackgroundColor(activity.getColor(R.color.card_background_grey));
                    }
                } else {
                    if (resultModel.isRequestee_read_status()) {
                        viewHolder.notificationCv.setCardBackgroundColor(activity.getColor(R.color.card_background_grey));
                    }
                }
                break;
        }
    }

    private void showUserDetailView(NotificationApiResponseModel.ResultBean.RequestsBean resultModel, CommonUserApiResponseModel finalDoctorModel, CommonUserApiResponseModel finalPatientModel) {
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

    private void showOrderDetail(Fragment fragment, Integer entity_id, CommonUserApiResponseModel userModel, CommonUserApiResponseModel doctorModel) {
        Bundle bundle = new Bundle();
        if (userModel != null) {
            bundle.putString(ArgumentKeys.USER_GUID, userModel.getUser_guid());
            bundle.putSerializable(Constants.USER_DETAIL, userModel);
        }
        if (doctorModel != null) {
            bundle.putString(ArgumentKeys.DOCTOR_GUID, doctorModel.getUser_guid());
            bundle.putSerializable(Constants.DOCTOR_DETAIL, doctorModel);
        }

        bundle.putInt(ArgumentKeys.ORDER_ID, entity_id);
        fragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(fragment);

    }

    private void showOrdersListView() {
        OrdersListFragment ordersListFragment = new OrdersListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ArgumentKeys.SHOW_TOOLBAR, true);
        bundle.putBoolean(Constants.IS_FROM_HOME, false);
        ordersListFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(ordersListFragment);

    }

    private void updateRequest(String type, boolean isAccept, String toGuid, @NonNull int id, @NonNull String status, @Nullable String startDate, @Nullable String endDate,
                               @Nullable String doctorGuid, boolean isShowProgress, boolean isRequestorMA) {
        notificationApiViewModel.updateNotification(type, isAccept, toGuid, id, status, startDate, endDate, doctorGuid, isShowProgress, isRequestorMA);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return modelList.get(position).getType();
    }

    public void setData(List<NotificationApiResponseModel.ResultBean.RequestsBean> requests, int page) {
        if (page == 1) {
            notificationList = requests;
        } else {
            notificationList.addAll(requests);
        }
        generateList();
    }

    private void generateList() {
        Collections.sort(notificationList, new Comparator<NotificationApiResponseModel.ResultBean.RequestsBean>() {
            @Override
            public int compare(NotificationApiResponseModel.ResultBean.RequestsBean o1, NotificationApiResponseModel.ResultBean.RequestsBean o2) {
                return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
            }
        });

        modelList.clear();

        for (int i = 0; i < notificationList.size(); i++) {
            if (i == 0 || !Utils.getDayMonthYear(notificationList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(notificationList.get(i - 1).getCreated_at()))) {
                modelList.add(new NotificationListModel(TYPE_HEADER, Utils.getDayMonthYear(notificationList.get(i).getCreated_at())));
            }
            modelList.add(new NotificationListModel(TYPE_DATA, notificationList.get(i)));
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;
        private CardView notificationCv;
        private TextView titleTv;
        private ConstraintLayout doctorDetailCl;
        private TextView doctorNameTv;
        private ConstraintLayout userDetailCl;
        private CircleImageView avatarCiv;
        private TextView listTitleTv;
        private TextView listSubTitleTv;
        private ImageView infoIv, hasCardIV;
        private View bottomView;
        private TextView descriptionTv;
        private ConstraintLayout slotCl;
        private TextView slotLabel;
        private RoundCornerConstraintLayout slot1Btn;
        private TextView slotTime1Tv;
        private TextView slotDate1Tv;
        private RoundCornerConstraintLayout slot2Btn;
        private TextView slotTime2Tv;
        private TextView slotDate2Tv;
        private RoundCornerConstraintLayout slot3Btn;
        private TextView slotTime3Tv;
        private TextView slotDate3Tv;
        private ConstraintLayout actionCl;
        private CustomButton acceptBtn;
        private Button rejectBtn;
        private Button askForCardBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);

            notificationCv = (CardView) itemView.findViewById(R.id.notification_cv);
            titleTv = (TextView) itemView.findViewById(R.id.title_tv);
            doctorDetailCl = (ConstraintLayout) itemView.findViewById(R.id.doctor_detail_cl);
            doctorNameTv = (TextView) itemView.findViewById(R.id.doctor_name_tv);
            userDetailCl = (ConstraintLayout) itemView.findViewById(R.id.user_detail_cl);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            listTitleTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            listSubTitleTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            infoIv = (ImageView) itemView.findViewById(R.id.info_iv);
            hasCardIV = (ImageView) itemView.findViewById(R.id.card_iv);
            bottomView = (View) itemView.findViewById(R.id.bottom_view);
            descriptionTv = (TextView) itemView.findViewById(R.id.description_tv);
            slotCl = (ConstraintLayout) itemView.findViewById(R.id.slot_cl);
            slotLabel = (TextView) itemView.findViewById(R.id.slot_label);
            slot1Btn = (RoundCornerConstraintLayout) itemView.findViewById(R.id.slot1_btn);
            slotTime1Tv = (TextView) itemView.findViewById(R.id.slot_time1_tv);
            slotDate1Tv = (TextView) itemView.findViewById(R.id.slot_date1_tv);
            slot2Btn = (RoundCornerConstraintLayout) itemView.findViewById(R.id.slot2_btn);
            slotTime2Tv = (TextView) itemView.findViewById(R.id.slot_time2_tv);
            slotDate2Tv = (TextView) itemView.findViewById(R.id.slot_date2_tv);
            slot3Btn = (RoundCornerConstraintLayout) itemView.findViewById(R.id.slot3_btn);
            slotTime3Tv = (TextView) itemView.findViewById(R.id.slot_time3_tv);
            slotDate3Tv = (TextView) itemView.findViewById(R.id.slot_date3_tv);
            actionCl = (ConstraintLayout) itemView.findViewById(R.id.action_cl);
            acceptBtn = (CustomButton) itemView.findViewById(R.id.accept_btn);
            rejectBtn = (Button) itemView.findViewById(R.id.reject_btn);
            askForCardBtn = (Button) itemView.findViewById(R.id.ask_for_card_btn);

        }
    }

    public class NotificationListModel {
        private int type;
        private String date;
        private NotificationApiResponseModel.ResultBean.RequestsBean dataBean;

        public NotificationListModel(int type, NotificationApiResponseModel.ResultBean.RequestsBean dataBean) {
            this.type = type;
            this.dataBean = dataBean;
        }

        public NotificationListModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public NotificationApiResponseModel.ResultBean.RequestsBean getDataBean() {
            return dataBean;
        }

        public void setDataBean(NotificationApiResponseModel.ResultBean.RequestsBean dataBean) {
            this.dataBean = dataBean;
        }
    }
}
