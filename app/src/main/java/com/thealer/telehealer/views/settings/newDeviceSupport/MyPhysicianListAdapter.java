package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.home.DoctorPatientListAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPhysicianListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    private Context context;
    private List<DoctorPatientListAdapter.AssociationAdapterListModel> adapterListModels;

    public MyPhysicianListAdapter(Context context) {
        this.context = context;
        adapterListModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sms_physician, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ItemHolder itemHolder = (ItemHolder) holder;
        CommonUserApiResponseModel userModel = adapterListModels.get(position).getCommonUserApiResponseModel();

        itemHolder.titleTv.setText(userModel.getDisplayName());
        loadAvatar(itemHolder.avatarCiv, userModel.getUser_avatar());
        if(adapterListModels.get(position).isSelectedFlag())
            itemHolder.checkbox.setSelected(true);
        else
            itemHolder.checkbox.setSelected(true);

        itemHolder.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                adapterListModels.get(position).setSelectedFlag(true);
            }else{
                adapterListModels.get(position).setSelectedFlag(false);
            }
        });
    }

    private void loadAvatar(ImageView imageView, String user_avatar) {
        Utils.setImageWithGlide(context, imageView, user_avatar, context.getDrawable(R.drawable.profile_placeholder), true, true);
    }

    @Override
    public int getItemCount() {
        return adapterListModels.size();
    }

    public void setData(List<DoctorPatientListAdapter.AssociationAdapterListModel> associationApiResponseModelResult) {
        adapterListModels.clear();
        adapterListModels.addAll(associationApiResponseModelResult);
        notifyDataSetChanged();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private AppCompatTextView titleTv;
        private AppCompatCheckBox checkbox;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            avatarCiv = itemView.findViewById(R.id.avatar_civ);
            titleTv = itemView.findViewById(R.id.list_title_tv);
            checkbox = itemView.findViewById(R.id.checkbox);

        }
    }

}
