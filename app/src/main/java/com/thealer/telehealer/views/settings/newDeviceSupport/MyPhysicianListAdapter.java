package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.DoctorGroupedAssociations;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.common.Animation.CustomUserListItemView;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserType;
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

        itemHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemHolder.checkbox.isSelected()){
                    adapterListModels.get(position).setSelectedFlag(false);
                }else{
                    adapterListModels.get(position).setSelectedFlag(true);
                }
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

    public void setData(List<DoctorGroupedAssociations> associationApiResponseModelResult) {
        adapterListModels.clear();

        for (int i = 0; i < associationApiResponseModelResult.size(); i++) {
            DoctorGroupedAssociations associations = associationApiResponseModelResult.get(i);

            for (int j = 0; j < associations.getDoctors().size(); j++) {
                adapterListModels.add(new DoctorPatientListAdapter.AssociationAdapterListModel(TYPE_ITEM, associations.getDoctors().get(j)));
            }
        }

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
