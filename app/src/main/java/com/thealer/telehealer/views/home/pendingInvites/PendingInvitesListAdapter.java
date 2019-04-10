package com.thealer.telehealer.views.home.pendingInvites;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiResponseModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationApiViewModel;
import com.thealer.telehealer.apilayer.models.notification.NotificationRequestUpdateResponseModel;
import com.thealer.telehealer.apilayer.models.pendingInvites.PendingInvitesNonRegisterdApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.notification.NotificationListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 04,March,2019
 */
public class PendingInvitesListAdapter extends RecyclerView.Adapter<PendingInvitesListAdapter.ViewHolder> {

    private final int TYPE_HEADER = 1;
    private final int TYPE_REGISTERED_USER = 2;
    private final int TYPE_UNREGISTERED_USER = 3;

    private FragmentActivity activity;
    private List<PendingInvitesAdapterModel> adapterModelList;
    private List<PendingInvitesAdapterModel> registeredUsers;
    private List<PendingInvitesAdapterModel> nonRegisteredUsers;
    private boolean isShowAction;
    private NotificationApiViewModel notificationApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private int selectedPosition = -1;

    public PendingInvitesListAdapter(FragmentActivity activity, boolean showAction) {
        this.activity = activity;
        adapterModelList = new ArrayList<>();
        registeredUsers = new ArrayList<>();
        nonRegisteredUsers = new ArrayList<>();
        this.isShowAction = showAction;

        attachObserverInterface = (AttachObserverInterface) activity;
        notificationApiViewModel = ViewModelProviders.of(activity).get(NotificationApiViewModel.class);
        attachObserverInterface.attachObserver(notificationApiViewModel);

        notificationApiViewModel.baseApiResponseModelMutableLiveData.observe(activity,
                new Observer<BaseApiResponseModel>() {
                    @Override
                    public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                        if (baseApiResponseModel != null) {
                            NotificationRequestUpdateResponseModel updateResponseModel = (NotificationRequestUpdateResponseModel) baseApiResponseModel;
                            if (updateResponseModel.isSuccess()) {
                                if (selectedPosition != -1) {
                                    adapterModelList.remove(selectedPosition);
                                    notifyItemChanged(selectedPosition);
                                    selectedPosition = -1;

                                    if (getItemCount() <= 1) {
                                        activity.getSupportFragmentManager().getFragments().get(0).onResume();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(activity);
        switch (i) {
            case TYPE_HEADER:
                view = inflater.inflate(R.layout.adapter_expandable_list_header_view, viewGroup, false);
                break;
            case TYPE_REGISTERED_USER:
            case TYPE_UNREGISTERED_USER:
                view = inflater.inflate(R.layout.adapter_pending_invites, viewGroup, false);
                break;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (adapterModelList.get(position).getType()) {
            case TYPE_HEADER:
                viewHolder.recentHeaderTv.setText(adapterModelList.get(position).getHeaderString());
                break;
            case TYPE_REGISTERED_USER:
                String avatarUrl, title, subTitle;

                if (isShowAction) {
                    viewHolder.actionCl.setVisibility(View.VISIBLE);
                    avatarUrl = adapterModelList.get(position).getInvitesResponseModel().getRequestor().getUser_avatar();
                    title = adapterModelList.get(position).getInvitesResponseModel().getRequestor().getDisplayName();
                    subTitle = adapterModelList.get(position).getInvitesResponseModel().getRequestor().getDisplayInfo();

                    viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPosition = position;
                            Utils.vibrate(activity);

                            notificationApiViewModel.updateNotification(adapterModelList.get(position).getInvitesResponseModel().getType(), true,
                                    adapterModelList.get(position).getInvitesResponseModel().getRequestor().getUser_guid(),
                                    adapterModelList.get(position).getInvitesResponseModel().getRequest_id(),
                                    NotificationListAdapter.ACCEPTED.toLowerCase(), null, null,
                                    null, true,adapterModelList.get(position).getInvitesResponseModel().getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
                        }
                    });

                    viewHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPosition = position;
                            notificationApiViewModel.updateNotification(adapterModelList.get(position).getInvitesResponseModel().getType(), true,
                                    adapterModelList.get(position).getInvitesResponseModel().getRequestor().getUser_guid(),
                                    adapterModelList.get(position).getInvitesResponseModel().getRequest_id(),
                                    NotificationListAdapter.REJECTED.toLowerCase(), null, null,
                                    null, true,adapterModelList.get(position).getInvitesResponseModel().getRequestor().getRole().equals(Constants.ROLE_ASSISTANT));
                        }
                    });

                } else {
                    viewHolder.actionCl.setVisibility(View.GONE);
                    avatarUrl = adapterModelList.get(position).getInvitesResponseModel().getRequestee().getUser_avatar();
                    title = adapterModelList.get(position).getInvitesResponseModel().getRequestee().getDisplayName();
                    subTitle = adapterModelList.get(position).getInvitesResponseModel().getRequestee().getDisplayInfo();
                }

                Utils.setImageWithGlide(activity.getApplicationContext(), viewHolder.avatarCiv, avatarUrl, activity.getDrawable(R.drawable.profile_placeholder), true);
                viewHolder.titleTv.setText(title);
                viewHolder.subTitleTv.setText(subTitle);

                break;
            case TYPE_UNREGISTERED_USER:
                String mode = null, date;
                if (adapterModelList.get(position).getNonRegisterdApiResponseModel().getEmail() != null) {
                    mode = adapterModelList.get(position).getNonRegisterdApiResponseModel().getEmail();
                }
                if (adapterModelList.get(position).getNonRegisterdApiResponseModel().getPhone() != null) {
                    mode = adapterModelList.get(position).getNonRegisterdApiResponseModel().getPhone();
                }

                date = Utils.getDayMonthYear(adapterModelList.get(position).getNonRegisterdApiResponseModel().getCreated_at());

                viewHolder.titleTv.setText(mode);
                viewHolder.subTitleTv.setText(date);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return adapterModelList.get(position).getType();
    }

    public void setData(NotificationApiResponseModel.ResultBean invitesResponseModel, int page) {

        PendingInvitesAdapterModel adapterModel;

        if (page == 1) {
            registeredUsers.clear();

            adapterModel = new PendingInvitesAdapterModel();
            adapterModel.setType(TYPE_HEADER);
            adapterModel.setHeaderString(activity.getString(R.string.registered_users));

            registeredUsers.add(adapterModel);

        }

        List<NotificationApiResponseModel.ResultBean.RequestsBean> requestsBeanList = invitesResponseModel.getRequests();

        for (int i = 0; i < requestsBeanList.size(); i++) {
            adapterModel = new PendingInvitesAdapterModel();
            adapterModel.setType(TYPE_REGISTERED_USER);
            adapterModel.setInvitesResponseModel(requestsBeanList.get(i));
            registeredUsers.add(adapterModel);
        }
        adapterModelList.clear();
        adapterModelList.addAll(registeredUsers);

        notifyDataSetChanged();
    }

    public void setData(List<PendingInvitesNonRegisterdApiResponseModel.ResultBean> result, int page) {
        PendingInvitesAdapterModel adapterModel;

        if (page == 1) {

            nonRegisteredUsers.clear();

            adapterModel = new PendingInvitesAdapterModel();
            adapterModel.setType(TYPE_HEADER);
            adapterModel.setHeaderString(activity.getString(R.string.non_registered_users));

            nonRegisteredUsers.add(adapterModel);
        }

        for (int i = 0; i < result.size(); i++) {
            adapterModel = new PendingInvitesAdapterModel();
            adapterModel.setType(TYPE_UNREGISTERED_USER);
            adapterModel.setNonRegisterdApiResponseModel(result.get(i));
            nonRegisteredUsers.add(adapterModel);
        }

        adapterModelList.clear();
        adapterModelList.addAll(registeredUsers);
        adapterModelList.addAll(nonRegisteredUsers);

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recentHeaderTv;
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ConstraintLayout actionCl;
        private CustomButton acceptBtn;
        private Button rejectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recentHeaderTv = (TextView) itemView.findViewById(R.id.recent_header_tv);

            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            titleTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            actionCl = (ConstraintLayout) itemView.findViewById(R.id.action_cl);
            acceptBtn = (CustomButton) itemView.findViewById(R.id.accept_btn);
            rejectBtn = (Button) itemView.findViewById(R.id.reject_btn);
        }
    }


    public class PendingInvitesAdapterModel {
        private int type;
        private String headerString;
        private NotificationApiResponseModel.ResultBean.RequestsBean invitesResponseModel;
        private PendingInvitesNonRegisterdApiResponseModel.ResultBean nonRegisterdApiResponseModel;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getHeaderString() {
            return headerString;
        }

        public void setHeaderString(String headerString) {
            this.headerString = headerString;
        }

        public NotificationApiResponseModel.ResultBean.RequestsBean getInvitesResponseModel() {
            return invitesResponseModel;
        }

        public void setInvitesResponseModel(NotificationApiResponseModel.ResultBean.RequestsBean invitesResponseModel) {
            this.invitesResponseModel = invitesResponseModel;
        }

        public PendingInvitesNonRegisterdApiResponseModel.ResultBean getNonRegisterdApiResponseModel() {
            return nonRegisterdApiResponseModel;
        }

        public void setNonRegisterdApiResponseModel(PendingInvitesNonRegisterdApiResponseModel.ResultBean nonRegisterdApiResponseModel) {
            this.nonRegisterdApiResponseModel = nonRegisterdApiResponseModel;
        }
    }
}
