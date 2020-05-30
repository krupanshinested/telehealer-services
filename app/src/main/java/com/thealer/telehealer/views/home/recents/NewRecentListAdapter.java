package com.thealer.telehealer.views.home.recents;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.TeleHealerApplication;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;
import com.thealer.telehealer.views.home.DoctorPatientDetailViewFragment;
import com.thealer.telehealer.views.home.chat.ChatActivity;
import com.thealer.telehealer.views.home.orders.OrderConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 12,June,2019
 */
public class NewRecentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_TITLE = 1;
    private final int TYPE_SUB_TITLE = 2;
    private final int TYPE_ITEM = 3;

    private FragmentActivity activity;
    private List<RecentListAdapterModel> adapterModelList;
    private ShowSubFragmentInterface showSubFragmentInterface;
    private List<RecentsApiResponseModel.ResultBean> recentList;
    private boolean isCalls = false;

    public NewRecentListAdapter(FragmentActivity activity) {
        this.activity = activity;
        adapterModelList = new ArrayList<>();
        this.showSubFragmentInterface = (ShowSubFragmentInterface) activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case TYPE_TITLE:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_list_category_view, viewGroup, false);
                return new TitleHolder(view);
            case TYPE_SUB_TITLE:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_list_header_view, viewGroup, false);
                return new SubTitleHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(activity).inflate(R.layout.adapter_recent_list_child_view, viewGroup, false);
                return new ItemHolder(view);
        }
        return null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemHolder) {
            ItemHolder viewHolder = (ItemHolder) holder;
            viewHolder.userNameTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.colorBlack)));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        RecentListAdapterModel adapterModel = adapterModelList.get(i);

        switch (adapterModel.getType()) {
            case TYPE_TITLE:
                TitleHolder titleHolder = (TitleHolder) holder;
                titleHolder.categoryTv.setText(adapterModel.getTitle());
                break;
            case TYPE_SUB_TITLE:
                SubTitleHolder subTitleHolder = (SubTitleHolder) holder;
                subTitleHolder.headerTv.setText(adapterModel.getTitle());
                break;
            case TYPE_ITEM:
                ItemHolder itemHolder = (ItemHolder) holder;
                itemHolder.infoIv.setVisibility(View.GONE);
                RecentsApiResponseModel.ResultBean resultBean = adapterModel.getItemData();
                boolean isChat = resultBean.getCorr_type().equals(OrderConstant.RECENT_TYPE_CHAT);

                if (!isChat) {
                    if (!TextUtils.isEmpty(resultBean.getType()) && resultBean.getType().equals(OrderConstant.RECENT_TYPE_AUDIO)) {
                        if (UserType.isUserPatient()) {
                            itemHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.ic_call_incoming), null, null, null);
                        } else {
                            itemHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.ic_call_outgoing), null, null, null);
                        }
                    } else {
                        itemHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.ic_videocam_black_24dp), null, null, null);
                    }
                }


                int userType = TeleHealerApplication.appPreference.getInt(Constants.USER_TYPE);
                String userName = null, avatar = null, time, duration;

                if (resultBean.getCategory() != null) {
                    itemHolder.labelTv.setText(resultBean.getCategory());
                    itemHolder.labelTv.setVisibility(View.VISIBLE);
                    itemHolder.labelIv.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.labelTv.setVisibility(View.GONE);
                    itemHolder.labelIv.setVisibility(View.GONE);
                }

                itemHolder.itemCv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isChat){
                            activity.startActivity(new Intent(activity, ChatActivity.class).putExtra(ArgumentKeys.USER_GUID, UserType.isUserPatient() ? resultBean.getDoctor().getUser_guid() : resultBean.getPatient().getUser_guid()));
                        }
                        else if (resultBean.getDurationInSecs() > 0) {
                            VisitsDetailFragment visitsDetailFragment = new VisitsDetailFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL, resultBean);
                            visitsDetailFragment.setArguments(bundle);
                            showSubFragmentInterface.onShowFragment(visitsDetailFragment);
                        }
                    }
                });

                itemHolder.infoIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DoctorPatientDetailViewFragment doctorPatientDetailViewFragment = new DoctorPatientDetailViewFragment();
                        Bundle bundle = new Bundle();
                        String userGuid;
                        if (UserType.isUserPatient()) {
                            userGuid = resultBean.getDoctor().getUser_guid();

                            if (resultBean.getMedical_assistant() != null) {
                                userGuid = resultBean.getMedical_assistant().getUser_guid();
                            }
                        } else {
                            userGuid = resultBean.getPatient().getUser_guid();

                            if (UserType.isUserAssistant()) {
                                bundle.putString(ArgumentKeys.DOCTOR_GUID, resultBean.getDoctor().getUser_guid());
                                bundle.putBoolean(ArgumentKeys.CHECK_CONNECTION_STATUS, true);
                            }
                        }
                        bundle.putString(Constants.VIEW_TYPE, Constants.VIEW_ASSOCIATION_DETAIL);
                        bundle.putString(ArgumentKeys.USER_GUID, userGuid);

                        doctorPatientDetailViewFragment.setArguments(bundle);
                        showSubFragmentInterface.onShowFragment(doctorPatientDetailViewFragment);
                    }
                });

                if (userType == Constants.TYPE_PATIENT) {

                    if (resultBean.getDoctor() != null && resultBean.getCaller_id() == resultBean.getDoctor().getUser_id()) {
                        userName = resultBean.getDoctor().getFirst_name() + " " + resultBean.getDoctor().getLast_name();
                        avatar = resultBean.getDoctor().getUser_avatar();

                    } else if (resultBean.getMedical_assistant() != null && resultBean.getCaller_id() == resultBean.getMedical_assistant().getUser_id()) {
                        userName = resultBean.getMedical_assistant().getFirst_name() + " " + resultBean.getMedical_assistant().getLast_name();
                        avatar = resultBean.getMedical_assistant().getUser_avatar();
                    } else {
                        if (resultBean.getDoctor() != null) {
                            userName = resultBean.getDoctor().getFirst_name() + " " + resultBean.getDoctor().getLast_name();
                            avatar = resultBean.getDoctor().getUser_avatar();
                        } else if (resultBean.getMedical_assistant() != null) {
                            userName = resultBean.getMedical_assistant().getFirst_name() + " " + resultBean.getMedical_assistant().getLast_name();
                            avatar = resultBean.getMedical_assistant().getUser_avatar();
                        }
                    }

                } else {

                    userName = resultBean.getPatient().getFirst_name() + " " + resultBean.getPatient().getLast_name();
                    avatar = resultBean.getPatient().getUser_avatar();

                }

                Utils.setImageWithGlide(activity, itemHolder.userAvatarCiv, avatar, activity.getDrawable(R.drawable.profile_placeholder), true, true);
                itemHolder.userNameTv.setText(userName);

                time = resultBean.getStart_time();
                if (time == null)
                    time = resultBean.getUpdated_at();

                itemHolder.timeTv.setText(Utils.getFormatedTime(time));

                if (isChat) {
                    itemHolder.durationTv.setVisibility(View.GONE);
                    itemHolder.timeTv.setCompoundDrawablesWithIntrinsicBounds(activity.getDrawable(R.drawable.ic_chat_bubble_outline_black_24dp), null, null, null);
                } else {

                    itemHolder.durationTv.setText(Utils.getDisplayDuration(resultBean.getDurationInSecs()));

                    if (resultBean.getStatus().equals(OrderConstant.CALL_STATUS_NO_ANSWER) && UserType.isUserPatient()) {
                        itemHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.ic_call_missed_24dp), null, null, null);
                        itemHolder.durationTv.setText(activity.getString(R.string.missed));
                        itemHolder.userNameTv.setTextColor(ColorStateList.valueOf(activity.getColor(R.color.red)));
                    }
                }
                if (UserType.isUserPatient()) {
                    itemHolder.labelIv.setVisibility(View.GONE);
                    itemHolder.labelTv.setVisibility(View.GONE);
                }

                break;
        }

    }

    public void setData(List<RecentsApiResponseModel.ResultBean> recentList, int page, boolean isCalls) {
        if (page == 1) {
            this.recentList = recentList;
        } else {
            if (this.recentList == null) {
                this.recentList = new ArrayList<>();
            }
            this.recentList.addAll(recentList);
        }
        this.isCalls = isCalls;
        generateAdapterList();
    }

    private void generateAdapterList() {
        if (recentList != null && !recentList.isEmpty()) {

            adapterModelList.clear();

            List<RecentsApiResponseModel.ResultBean> chatList = new ArrayList<>();
            List<RecentsApiResponseModel.ResultBean> callList = new ArrayList<>();

            for (int i = 0; i < recentList.size(); i++) {
                if (recentList.get(i).getCorr_type().equals(Constants.CALL)) {
                    if (recentList.get(i).getCreated_at() != null && !recentList.get(i).getCreated_at().isEmpty()) {
                        callList.add(recentList.get(i));
                    }
                } else if (recentList.get(i).getCorr_type().equals(Constants.CHAT)) {
                    chatList.add(recentList.get(i));
                }
            }

            if (!chatList.isEmpty()) {
                Collections.sort(chatList, new Comparator<RecentsApiResponseModel.ResultBean>() {
                    @Override
                    public int compare(RecentsApiResponseModel.ResultBean o1, RecentsApiResponseModel.ResultBean o2) {
                        Date start = Utils.getDateFromString(o2.getCreated_at());
                        Date end = Utils.getDateFromString(o1.getCreated_at());

                        if (start != null && end != null) {
                            return start.compareTo(end);
                        } else {
                            return 1;
                        }

                    }
                });

                adapterModelList.add(new RecentListAdapterModel(TYPE_TITLE, activity.getString(R.string.chat)));

                for (int i = 0; i < chatList.size(); i++) {
                    boolean isShowDate = true;
                    if (i != 0 && Utils.getDayMonthYear(chatList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(chatList.get(i - 1).getCreated_at()))) {
                        isShowDate = false;
                    }
                    if (isShowDate) {
                        adapterModelList.add(new RecentListAdapterModel(TYPE_SUB_TITLE, Utils.getDayMonthYear(chatList.get(i).getUpdated_at())));
                    }
                    adapterModelList.add(new RecentListAdapterModel(TYPE_ITEM, chatList.get(i)));
                }
            }

            if (!callList.isEmpty()) {
                Collections.sort(callList, new Comparator<RecentsApiResponseModel.ResultBean>() {
                    @Override
                    public int compare(RecentsApiResponseModel.ResultBean o1, RecentsApiResponseModel.ResultBean o2) {
                        return Utils.getDateFromString(o2.getCreated_at()).compareTo(Utils.getDateFromString(o1.getCreated_at()));
                    }
                });

                if (!isCalls) {
                    adapterModelList.add(new RecentListAdapterModel(TYPE_TITLE, activity.getString(R.string.calls)));
                }

                for (int i = 0; i < callList.size(); i++) {
                    boolean isShowDate = true;
                    if (i != 0 && Utils.getDayMonthYear(callList.get(i).getCreated_at()).equals(Utils.getDayMonthYear(callList.get(i - 1).getCreated_at()))) {
                        isShowDate = false;
                    }
                    if (isShowDate) {
                        adapterModelList.add(new RecentListAdapterModel(TYPE_SUB_TITLE, Utils.getDayMonthYear(callList.get(i).getCreated_at())));
                    }
                    adapterModelList.add(new RecentListAdapterModel(TYPE_ITEM, callList.get(i)));
                }
            }

            notifyDataSetChanged();
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private CardView itemCv;
        private CircleImageView userAvatarCiv;
        private TextView userNameTv;
        private ImageView labelIv;
        private TextView labelTv;
        private TextView timeTv;
        private TextView durationTv;
        private ImageView infoIv;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            userAvatarCiv = (CircleImageView) itemView.findViewById(R.id.user_avatar_civ);
            userNameTv = (TextView) itemView.findViewById(R.id.user_name_tv);
            labelIv = (ImageView) itemView.findViewById(R.id.label_iv);
            labelTv = (TextView) itemView.findViewById(R.id.label_tv);
            timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            durationTv = (TextView) itemView.findViewById(R.id.duration_tv);
            infoIv = (ImageView) itemView.findViewById(R.id.info_iv);
        }
    }

    public class TitleHolder extends RecyclerView.ViewHolder {
        private TextView categoryTv;

        public TitleHolder(@NonNull View itemView) {
            super(itemView);
            categoryTv = (TextView) itemView.findViewById(R.id.category_tv);
        }
    }

    public class SubTitleHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public SubTitleHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
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

    public class RecentListAdapterModel {
        private int type;
        private String title;
        private RecentsApiResponseModel.ResultBean itemData;

        public RecentListAdapterModel(int type, String title) {
            this.type = type;
            this.title = title;
        }

        public RecentListAdapterModel(int type, RecentsApiResponseModel.ResultBean itemData) {
            this.type = type;
            this.itemData = itemData;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public RecentsApiResponseModel.ResultBean getItemData() {
            return itemData;
        }

        public void setItemData(RecentsApiResponseModel.ResultBean itemData) {
            this.itemData = itemData;
        }
    }
}
