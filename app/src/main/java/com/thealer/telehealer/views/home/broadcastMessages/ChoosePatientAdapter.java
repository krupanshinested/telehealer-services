package com.thealer.telehealer.views.home.broadcastMessages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thealer.telehealer.common.Constants.ACTIVATION_PENDING;
import static com.thealer.telehealer.common.Constants.AVAILABLE;
import static com.thealer.telehealer.common.Constants.BUSY;
import static com.thealer.telehealer.common.Constants.NO_DATA;
import static com.thealer.telehealer.common.Constants.OFFLINE;

public class ChoosePatientAdapter extends RecyclerView.Adapter<ChoosePatientAdapter.OnPatientViewHolder> {
    private Context context;
    private List<CommonUserApiResponseModel> adapterListModels;
    private List<CommonUserApiResponseModel> selectedUserList;

    public ChoosePatientAdapter(Context context) {
        this.context = context;
        this.adapterListModels = new ArrayList<>();
        this.selectedUserList = new ArrayList<>();
    }

    @NonNull
    @Override
    public OnPatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View patientView = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_patient_raw, parent, false);
        return new OnPatientViewHolder(patientView);
    }


    @Override
    public void onBindViewHolder(@NonNull OnPatientViewHolder holder, int position) {
        CommonUserApiResponseModel userModel = adapterListModels.get(position);
        if (position == 0)
            holder.selectAllCb.setVisibility(View.VISIBLE);
        else
            holder.selectAllCb.setVisibility(View.GONE);

        if (selectedUserList.contains(userModel)) {
            holder.itemCb.setChecked(true);
            holder.clRoot.setBackgroundColor(context.getColor(R.color.bt_very_light_gray));
        } else {
            holder.itemCb.setChecked(false);
            holder.clRoot.setBackgroundColor(Color.TRANSPARENT);
        }
        if(selectedUserList.size()==adapterListModels.size()){
            holder.selectAllCb.setChecked(true);
        }else {
            holder.selectAllCb.setChecked(false);
        }
        if (userModel != null) {
            holder.tvName.setText(userModel.getUserDisplay_name());
            loadAvatar(holder.avatarCiv, userModel.getUser_avatar());
            holder.tvDob.setText(userModel.getDob());
            setStatus(holder, userModel.getStatus(), userModel.getLast_active());

        }
        holder.itemCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.itemCb.isChecked()) {
                    selectedUserList.add(userModel);
                    holder.clRoot.setBackgroundColor(context.getColor(R.color.bt_very_light_gray));
                } else {
                    selectedUserList.remove(userModel);
                    holder.selectAllCb.setChecked(false);
                    holder.clRoot.setBackgroundColor(Color.TRANSPARENT);
                }
                notifyDataSetChanged();
            }
        });

        holder.selectAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUserList.clear();
                if (holder.selectAllCb.isChecked()) {
                    selectedUserList.addAll(adapterListModels);
                } else {
                    holder.clRoot.setBackgroundColor(Color.TRANSPARENT);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return adapterListModels.size();
    }

    public List<CommonUserApiResponseModel> getSelectedUserList() {
        return selectedUserList;
    }

    public class OnPatientViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarCiv;
        TextView tvName, tvDob;
        CheckBox itemCb;
        ConstraintLayout clRoot;
        CircleImageView statusCiv;
        CheckBox selectAllCb;

        public OnPatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDob = itemView.findViewById(R.id.tv_dob);
            avatarCiv = itemView.findViewById(R.id.avatar_civ);
            itemCb = itemView.findViewById(R.id.item_cb);
            clRoot = itemView.findViewById(R.id.cl_root);
            statusCiv = itemView.findViewById(R.id.status_civ);
            selectAllCb = itemView.findViewById(R.id.select_all_cb);
        }
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(context.getApplicationContext(), imageView, user_avatar, context.getDrawable(R.drawable.profile_placeholder), true, true);
    }

    // update patient list
    public void setData(List<CommonUserApiResponseModel> associationApiResponseModelResult, int page) {
        if (page == 1) {
            adapterListModels.clear();
            adapterListModels = associationApiResponseModelResult;
            notifyDataSetChanged();
        } else {
            int prevPos = adapterListModels.size();
            adapterListModels.addAll(associationApiResponseModelResult);
            notifyItemChanged(prevPos - 1);
        }
    }

    // Display Status of patient
    public void setStatus(OnPatientViewHolder holder, String status, String last_active) {
        int color = R.color.status_offline;
        switch (status) {
            case AVAILABLE:
                color = R.color.status_available;
                if (Utils.isOneHourBefore(last_active))
                    color = R.color.status_away;
                break;
            case OFFLINE:
                color = R.color.status_offline;
                break;
            case BUSY:
                color = R.color.status_busy;
                break;
            case ACTIVATION_PENDING:
            case NO_DATA:
                color = R.color.colorBlack;
                break;
        }

        holder.statusCiv.setImageDrawable(new ColorDrawable(ContextCompat.getColor(context, color)));

        if (!status.equals(AVAILABLE)) {
            Utils.greyoutProfile(holder.avatarCiv);
        } else {
            Utils.removeGreyoutProfile(holder.avatarCiv);
        }
    }

}
