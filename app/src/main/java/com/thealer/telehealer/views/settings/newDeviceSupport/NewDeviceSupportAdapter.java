package com.thealer.telehealer.views.settings.newDeviceSupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewDeviceSupportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    private Context context;
    private List<NewDeviceApiResponseModel.Data> resultBean;
    private List<NewDeviceAdapterModel> adapterModelList;
    OnItemClickListener onItemClickListener;

    public NewDeviceSupportAdapter(Context context, List<NewDeviceApiResponseModel.Data> deviceList, OnItemClickListener onClickListener) {
        this.context = context;
        adapterModelList = new ArrayList<>();
        onItemClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_new_device_support, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItemHolder itemHolder = (ItemHolder) holder;
        NewDeviceApiResponseModel.Data resultBean = adapterModelList.get(position).getItem();

        Utils.setImageWithGlide(context, itemHolder.deviceTv, resultBean.getImage(), context.getDrawable(R.drawable.add_provider), true, true);

        itemHolder.deviceName.setText(""+resultBean.getName());
        itemHolder.deviceDescription.setText(""+resultBean.getCompany_name());
        itemHolder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(holder.getAdapterPosition(), null));
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }
//    public int getItemCount() {
//        return 5;
//    }

    public void setData(List<NewDeviceApiResponseModel.Data> result) {
        resultBean = result;
        adapterModelList.clear();
        for (int i = 0; i < resultBean.size(); i++) {
            adapterModelList.add(new NewDeviceAdapterModel(TYPE_ITEM, resultBean.get(i)));
        }
        notifyDataSetChanged();
    }

    public class DateHolder extends RecyclerView.ViewHolder {
        private TextView headerTv;

        public DateHolder(@NonNull View itemView) {
            super(itemView);
            headerTv = (TextView) itemView.findViewById(R.id.header_tv);
            headerTv.setTextColor(context.getColor(R.color.colorBlack));
            headerTv.setBackgroundColor(context.getColor(R.color.color_grey_light_background));
            headerTv.setPadding(16, 16, 16, 16);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView deviceTv;
        private AppCompatTextView deviceName, deviceDescription;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            deviceTv = (AppCompatImageView) itemView.findViewById(R.id.deviceTv);
            deviceName = (AppCompatTextView) itemView.findViewById(R.id.device_name);
            deviceDescription = (AppCompatTextView) itemView.findViewById(R.id.device_description);

        }
    }

    private class NewDeviceAdapterModel {
        private int type;
        private String date;
        private NewDeviceApiResponseModel.Data item;

        public NewDeviceAdapterModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public NewDeviceAdapterModel(int type, NewDeviceApiResponseModel.Data item) {
            this.type = type;
            this.item = item;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public NewDeviceApiResponseModel.Data getItem() {
            return item;
        }

        public void setItem(NewDeviceApiResponseModel.Data item) {
            this.item = item;
        }
    }

}
