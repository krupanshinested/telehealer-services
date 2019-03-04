package com.thealer.telehealer.views.home;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.thealer.telehealer.common.Constants;
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
    private int selected_position;

    public ConnectionListAdapter(Context context) {
        this.context = context;
        this.fragmentActivity = (FragmentActivity) context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        addConnectionApiViewModel = ViewModelProviders.of(fragmentActivity).get(AddConnectionApiViewModel.class);
        onListItemSelectInterface = (OnListItemSelectInterface) fragmentActivity;
        onActionCompleteInterface = (OnActionCompleteInterface) fragmentActivity;

        addConnectionApiViewModel.baseApiResponseModelMutableLiveData.observe(fragmentActivity, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    if (baseApiResponseModel.isSuccess()) {
                        apiResponseModelList.get(selected_position).setConnection_status(Constants.CONNECTION_STATUS_OPEN);
                        notifyDataSetChanged();
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
        Log.e("aswin", "onBindViewHolder: " + apiResponseModelList.get(i).getDisplayName() + " " + apiResponseModelList.get(i).getConnection_status());
        if (apiResponseModelList.get(i).getConnection_status() == null ||
                apiResponseModelList.get(i).getConnection_status().equals(Constants.CONNECTION_STATUS_REJECTED)) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_connect_user));
            viewHolder.actionIv.setImageTintList(null);
        } else if (apiResponseModelList.get(i).getConnection_status().equals(Constants.CONNECTION_STATUS_OPEN) ||
                apiResponseModelList.get(i).getConnection_status().equals(Constants.CONNECTION_STATUS_PENDING)) {
            viewHolder.actionIv.setImageDrawable(context.getDrawable(R.drawable.ic_access_time_24dp));
            viewHolder.actionIv.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.color_green_light)));
        } else {
            viewHolder.actionIv.setVisibility(View.GONE);
        }

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, apiResponseModelList.get(i).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });

        viewHolder.actionIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrate(fragmentActivity);
                if (apiResponseModelList.get(i).getConnection_status() == null) {
                    selected_position = i;
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.ADD_CONNECTION_ID, apiResponseModelList.get(i).getUser_id());
                    bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));

                    onActionCompleteInterface.onCompletionResult(null, true, bundle);
                }
            }
        });

        Utils.setImageWithGlide(context, viewHolder.avatarCiv, apiResponseModelList.get(i).getUser_avatar(), context.getDrawable(R.drawable.profile_placeholder), true);

        viewHolder.titleTv.setText(apiResponseModelList.get(i).getDisplayName());
        viewHolder.subTitleTv.setText(apiResponseModelList.get(i).getDisplayInfo());

        viewHolder.itemCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.USER_DETAIL, apiResponseModelList.get(i));
                onListItemSelectInterface.onListItemSelected(i, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apiResponseModelList.size();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCv = (CardView) itemView.findViewById(R.id.item_cv);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            titleTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            subTitleTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            actionIv = (ImageView) itemView.findViewById(R.id.action_iv);
        }
    }
}
