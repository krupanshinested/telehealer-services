package com.thealer.telehealer.views.home.monitoring.diet;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.UserBean;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 04,June,2019
 */
class DietUserListAdapter extends RecyclerView.Adapter<DietUserListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private List<UserBean> userBeanList = new ArrayList<>();
    private OnListItemSelectInterface onListItemSelectInterface;
    private Bundle bundle;

    public DietUserListAdapter(FragmentActivity activity, Bundle arguments, OnListItemSelectInterface onListItemSelectInterface) {
        this.activity = activity;
        this.onListItemSelectInterface = onListItemSelectInterface;
        this.bundle = arguments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_doctor_patient_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Utils.setImageWithGlide(activity, viewHolder.userListIv.getAvatarCiv(), userBeanList.get(i).getUser_avatar(), activity.getDrawable(R.drawable.profile_placeholder), true, true);
        viewHolder.userListIv.getListItemCl().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putSerializable(Constants.USER_DETAIL, userBeanList.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });


        viewHolder.userListIv.getListTitleTv().setText(userBeanList.get(i).getName());

        if (UserType.isUserDoctor()) {
            viewHolder.userListIv.getActionIv().setVisibility(View.VISIBLE);
            Utils.setGenderImage(activity, viewHolder.userListIv.getActionIv(), userBeanList.get(i).getGender());
        } else if (UserType.isUserPatient() || UserType.isUserAssistant()) {
            viewHolder.userListIv.getActionIv().setVisibility(View.GONE);
        }
        viewHolder.userListIv.getListSubTitleTv().setText(userBeanList.get(i).getDob());

    }

    @Override
    public int getItemCount() {
        return userBeanList.size();
    }

    public void setData(List<UserBean> userBeans) {
        this.userBeanList = userBeans;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomUserListItemView userListIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
        }
    }
}
