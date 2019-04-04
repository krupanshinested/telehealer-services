package com.thealer.telehealer.views.home.orders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.getDoctorsModel.GetDoctorsApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 28,November,2018
 */
public class AssociationListAdapter extends RecyclerView.Adapter<AssociationListAdapter.ViewHolder> {
    private Context context;
    private List<CommonUserApiResponseModel> commonUserApiResponseModelList;
    private OnListItemSelectInterface onListItemSelectInterface;
    private String selectionType;
    private List<GetDoctorsApiResponseModel.DataBean> doctorsApiResponseModel;
    private int size = 0;

    public AssociationListAdapter(FragmentActivity activity, List<CommonUserApiResponseModel> commonUserApiResponseModelList,
                                  OnListItemSelectInterface onListItemSelectInterface,
                                  List<GetDoctorsApiResponseModel.DataBean> doctorsApiResponseModel,
                                  String selectionType) {
        this.context = activity;
        this.commonUserApiResponseModelList = commonUserApiResponseModelList;
        this.onListItemSelectInterface = onListItemSelectInterface;
        this.selectionType = selectionType;
        this.doctorsApiResponseModel = doctorsApiResponseModel;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_association_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Bundle bundle = new Bundle();

        if (selectionType.equals(ArgumentKeys.SEARCH_ASSOCIATION) || selectionType.equals(ArgumentKeys.SEARCH_ASSOCIATION_DOCTOR)) {

            viewHolder.titleTv.setText(commonUserApiResponseModelList.get(i).getUserDisplay_name());
            if (selectionType.equals(ArgumentKeys.SEARCH_ASSOCIATION)) {
                viewHolder.subTitleTv.setText(commonUserApiResponseModelList.get(i).getDob());
            } else {
                viewHolder.subTitleTv.setText(commonUserApiResponseModelList.get(i).getDoctorSpecialist());
            }
            Utils.setImageWithGlide(context, viewHolder.avatarCiv, commonUserApiResponseModelList.get(i).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);
            bundle.putSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL, commonUserApiResponseModelList.get(i));
        }

        if (selectionType.equals(ArgumentKeys.SEARCH_DOCTOR) || selectionType.equals(ArgumentKeys.SEARCH_COPY_TO)) {

            viewHolder.titleTv.setText(doctorsApiResponseModel.get(i).getDoctorDisplayName());
            viewHolder.subTitleTv.setText(doctorsApiResponseModel.get(i).getDoctorAddress());
            Utils.setImageWithGlide(context, viewHolder.avatarCiv, doctorsApiResponseModel.get(i).getProfile().getImage_url(), context.getDrawable(R.drawable.profile_placeholder), false, true);

            bundle.putSerializable(ArgumentKeys.SELECTED_ASSOCIATION_DETAIL, doctorsApiResponseModel.get(i));

        }

        viewHolder.viewItemCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
        private ConstraintLayout viewItemCl;
        private CustomUserListItemView customCircleIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customCircleIv = (CustomUserListItemView) itemView.findViewById(R.id.custom_circle_iv);

            avatarCiv = customCircleIv.getAvatarCiv();
            titleTv = customCircleIv.getListTitleTv();
            subTitleTv = customCircleIv.getListSubTitleTv();
            actionIv = customCircleIv.getActionIv();
            viewItemCl = customCircleIv.getListItemCl();

            customCircleIv.getListItemCv().setCardElevation(0);
            customCircleIv.getListItemCv().setRadius(0);
            customCircleIv.getListItemCv().setUseCompatPadding(false);

        }
    }

    public void setCommonUserApiResponseModelList(List<CommonUserApiResponseModel> commonUserApiResponseModelList, int page) {
        if (page == 1) {
            this.commonUserApiResponseModelList = commonUserApiResponseModelList;
        } else {
            this.commonUserApiResponseModelList.addAll(commonUserApiResponseModelList);
        }
        size = this.commonUserApiResponseModelList.size();
        notifyDataSetChanged();
    }

    public void setDoctorsApiResponseModel(List<GetDoctorsApiResponseModel.DataBean> doctorsApiResponseModel, int page) {
        if (page == 1) {
            this.doctorsApiResponseModel = doctorsApiResponseModel;
        } else {
            this.doctorsApiResponseModel.addAll(doctorsApiResponseModel);
        }
        size = this.doctorsApiResponseModel.size();
        notifyDataSetChanged();
    }
}
