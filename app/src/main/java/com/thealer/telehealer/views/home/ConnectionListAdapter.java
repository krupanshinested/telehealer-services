package com.thealer.telehealer.views.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.addConnection.AddConnectionApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnActionCompleteInterface;
import com.thealer.telehealer.views.common.OnListItemSelectInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 19,November,2018
 */
public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.ViewHolder> {
    private Context context;
    private FragmentActivity fragmentActivity;
    private List<CommonUserApiResponseModel> apiResponseModelList = new ArrayList<>();
    private OnListItemSelectInterface onListItemSelectInterface;
    private OnActionCompleteInterface onActionCompleteInterface;
    private AddConnectionApiViewModel addConnectionApiViewModel;
    private int selected_position = -1;

    public ConnectionListAdapter(Context context) {
        this.context = context;
        this.fragmentActivity = (FragmentActivity) context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        addConnectionApiViewModel = new ViewModelProvider(fragmentActivity).get(AddConnectionApiViewModel.class);onListItemSelectInterface = (OnListItemSelectInterface) fragmentActivity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        Log.e("aswin", "onChanged: " + selected_position);
                        if (selected_position >= 0) {
                            apiResponseModelList.get(selected_position).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
                            notifyItemChanged(selected_position);
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_add_connection_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CommonUserApiResponseModel user = apiResponseModelList.get(i);

        if (user.getRole().equals(Constants.ROLE_DOCTOR) && !user.getConnection_requests()) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_info_32dp));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
        } else if (user.getConnection_status() == null ||
                user.getConnection_status().equals(Constants.CONNECTION_STATUS_REJECTED)) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_connect_user));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.app_gradient_start)));
        } else if (user.getConnection_status().equals(Constants.CONNECTION_STATUS_OPEN) ||
                user.getConnection_status().equals(Constants.CONNECTION_STATUS_PENDING)) {
            Log.e("aswin", "onBindViewHolder: " + i);
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_status_pending));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.color_green_light)));
        } else {
            viewHolder.actionIv.setVisibility(View.GONE);
        }

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, user.getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_position = i;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });

        viewHolder.actionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getRole().equals(Constants.ROLE_DOCTOR) && !user.getConnection_requests()) {
                    selected_position = i;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.USER_DETAIL, user);
                    bundle.putBoolean(ArgumentKeys.SHOW_CONNECTION_REQUEST_ALERT,true);
                    onListItemSelectInterface.onListItemSelected(i, bundle);
                } else if (apiResponseModelList.get(i).getConnection_status() == null ||
                        apiResponseModelList.get(i).getConnection_status().equals(Constants.CONNECTION_STATUS_REJECTED)) {
                    Utils.vibrate(fragmentActivity);
                    selected_position = i;
                    onListItemSelectInterface.onListItemSelected(i, null);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ADD_CONNECTION_ID, apiResponseModelList.get(i).getUser_id());
                    bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));

                    onActionCompleteInterface.onCompletionResult(null, true, bundle);
                }
            }
        });

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, apiResponseModelList.get(i).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true, true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

    }

    @Override
    public int getItemCount() {
        if (apiResponseModelList != null) {
            return apiResponseModelList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<CommonUserApiResponseModel> commonUserApiResponseModelList, int selectedPosition) {
        apiResponseModelList = commonUserApiResponseModelList;
        if (selectedPosition == -1)
            notifyDataSetChanged();
        else {
            apiResponseModelList.get(selectedPosition).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
            notifyItemChanged(selected_position);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private TextView titleTv;
        private TextView subTitleTv;
        private ImageView actionIv;
        private CardView itemCv;
        private CustomUserListItemView userListIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userListIv = (CustomUserListItemView) itemView.findViewById(R.id.user_list_iv);
            itemCv = userListIv.getListItemCv();
            avatarCiv = userListIv.getAvatarCiv();
            titleTv = userListIv.getListTitleTv();
            subTitleTv = userListIv.getListSubTitleTv();
            actionIv = userListIv.getActionIv();
        }
    }
}
