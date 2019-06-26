package com.thealer.telehealer.views.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.recents.RecentsApiResponseModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.home.orders.OrderConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 12,April,2019
 */
public class RecentsSelectionAdapter extends RecyclerView.Adapter<RecentsSelectionAdapter.ViewHolder> {

    private Context context;
    private List<RecentsApiResponseModel.ResultBean> resultBeanList = new ArrayList<>();
    private OnListItemSelectInterface onListItemSelectInterface;
    private int selectedTranscriptionId;

    public RecentsSelectionAdapter(Context context, int selectedTranscriptionId, OnListItemSelectInterface onListItemSelectInterface) {
        this.context = context;
        this.selectedTranscriptionId = selectedTranscriptionId;
        this.onListItemSelectInterface = onListItemSelectInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_recent_selection, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.headerTv.setText(Utils.getDayMonthYear(resultBeanList.get(i).getUpdated_at()));

        if (resultBeanList.get(i).getDurationInSecs() > 0) {

            if (i > 0 && (Utils.getDayMonthYear(resultBeanList.get(i).getUpdated_at()).equals(Utils.getDayMonthYear(resultBeanList.get(i - 1).getUpdated_at())))) {
                viewHolder.headerTv.setVisibility(View.GONE);
            }

            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            viewHolder.visitCb.setVisibility(View.VISIBLE);
            if (selectedTranscriptionId >= 0 && resultBeanList.get(i).getTranscription_id() == selectedTranscriptionId) {
                viewHolder.visitCb.setChecked(true);
            }
            viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.visitCb.setChecked(!viewHolder.visitCb.isChecked());
                    Bundle bundle = new Bundle();
                    RecentsApiResponseModel.ResultBean resultBean = null;
                    if (viewHolder.visitCb.isChecked()) {
                        resultBean = resultBeanList.get(i);
                    }
                    bundle.putSerializable(ArgumentKeys.SELECTED_RECENT_DETAIL, resultBean);
                    onListItemSelectInterface.onListItemSelected(i, bundle);
                }
            });
        } else {
            viewHolder.rootLayout.setVisibility(View.GONE);
            viewHolder.headerTv.setVisibility(View.GONE);
        }

        boolean isChat = resultBeanList.get(i).getCorr_type().equals(OrderConstant.RECENT_TYPE_CHAT);

        if (!isChat) {
            if (resultBeanList.get(i).getType().equals(OrderConstant.RECENT_TYPE_AUDIO)) {
                if (UserType.isUserPatient()) {
                    viewHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.ic_call_incoming), null, null, null);
                } else {
                    viewHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.ic_call_outgoing), null, null, null);
                }
            } else {
                viewHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.ic_videocam_black_24dp), null, null, null);
            }
        }

        if (isChat) {
            viewHolder.durationTv.setVisibility(View.GONE);
            viewHolder.timeTv.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_chat_bubble_outline_black_24dp), null, null, null);
        } else {
            int seconds = resultBeanList.get(i).getDurationInSecs();
            if (seconds < 60) {
                viewHolder.durationTv.setText(resultBeanList.get(i).getDurationInSecs() + " sec");
            } else {
                viewHolder.durationTv.setText((seconds / 60) + " min " + (seconds % 60) + " sec");
            }

            if (resultBeanList.get(i).getStatus().equals(OrderConstant.CALL_STATUS_NO_ANSWER) && UserType.isUserPatient()) {
                viewHolder.timeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.ic_call_missed_24dp), null, null, null);
                viewHolder.durationTv.setText(context.getString(R.string.missed));
                viewHolder.userNameTv.setTextColor(ColorStateList.valueOf(context.getColor(R.color.red)));
            }
        }


        if (UserType.isUserPatient()) {
            viewHolder.labelIv.setVisibility(View.GONE);
            viewHolder.labelTv.setVisibility(View.GONE);
        }

        String name, avatar;

        if (UserType.isUserPatient()) {
            name = resultBeanList.get(i).getDoctor().getDisplayName();
            avatar = resultBeanList.get(i).getDoctor().getUser_avatar();
        } else {
            name = resultBeanList.get(i).getPatient().getDisplayName();
            avatar = resultBeanList.get(i).getPatient().getUser_avatar();
        }

        Utils.setImageWithGlide(context, viewHolder.userAvatarCiv, avatar, context.getDrawable(R.drawable.profile_placeholder), true, true);
        viewHolder.userNameTv.setText(name);
        viewHolder.timeTv.setText(Utils.getFormatedTime(resultBeanList.get(i).getUpdated_at()));

    }

    @Override
    public int getItemCount() {
        return resultBeanList.size();
    }

    public void setData(List<RecentsApiResponseModel.ResultBean> result, int page) {

        if (page == 1) {
            resultBeanList = result;
        } else {
            resultBeanList.addAll(result);
        }

        Collections.sort(resultBeanList, new Comparator<RecentsApiResponseModel.ResultBean>() {
            @Override
            public int compare(RecentsApiResponseModel.ResultBean o1, RecentsApiResponseModel.ResultBean o2) {
                return Utils.getDateFromString(o2.getUpdated_at()).compareTo(Utils.getDateFromString(o1.getUpdated_at()));
            }
        });

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout rootLayout;
        private CardView itemCv;
        private CircleImageView userAvatarCiv;
        private TextView userNameTv;
        private ImageView labelIv;
        private TextView labelTv;
        private TextView timeTv;
        private TextView durationTv;
        private ImageView infoIv;
        private TextView headerTv;
        private CheckBox visitCb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            rootLayout = (ConstraintLayout) itemView.findViewById(R.id.rootLayout);
            visitCb = (CheckBox) itemView.findViewById(R.id.visit_cb);
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
}
