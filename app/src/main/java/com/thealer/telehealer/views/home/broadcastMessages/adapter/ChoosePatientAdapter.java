package com.thealer.telehealer.views.home.broadcastMessages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        if (userModel != null) {
            holder.tvName.setText(userModel.getUserDisplay_name());
            loadAvatar(holder.avatarCiv, userModel.getUser_avatar());
            holder.tvDob.setText(userModel.getDob());
        }
        holder.itemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedUserList.add(userModel);
                } else {
                    selectedUserList.remove(userModel);
                }
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

        public OnPatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDob = itemView.findViewById(R.id.tv_dob);
            avatarCiv = itemView.findViewById(R.id.avatar_civ);
            itemCb = itemView.findViewById(R.id.item_cb);
        }
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(context.getApplicationContext(), imageView, user_avatar, context.getDrawable(R.drawable.profile_placeholder), true, true);
    }

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

}
