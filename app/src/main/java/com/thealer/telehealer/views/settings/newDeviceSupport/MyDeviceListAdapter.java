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
import com.thealer.telehealer.apilayer.models.newDeviceSetup.MyDeviceListApiResponseModel;
import com.thealer.telehealer.apilayer.models.newDeviceSetup.NewDeviceApiResponseModel;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.OnDeviceItemClickListener;
import com.thealer.telehealer.views.common.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MyDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    private Context context;
    private List<MyDeviceListApiResponseModel.Data> resultBean;
    private List<MyDeviceAdapterModel> adapterModelList;
    OnDeviceItemClickListener onItemClickListener;

    public MyDeviceListAdapter(Context context, List<MyDeviceListApiResponseModel.Data> deviceList, OnDeviceItemClickListener onClickListener) {
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
        MyDeviceListApiResponseModel.Data resultBean = adapterModelList.get(position).getItem();

        Utils.setImageWithGlide(context, itemHolder.deviceTv, resultBean.getHealthcare_device().getImage(), context.getDrawable(R.drawable.add_provider), true, true);
        itemHolder.deviceDelete.setVisibility(View.VISIBLE);
        itemHolder.deviceName.setText(""+resultBean.getHealthcare_device().getName());
        itemHolder.deviceDescription.setText(""+resultBean.getHealthcare_device().getDescription());
        itemHolder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(holder.getAdapterPosition(), null));

        itemHolder.deviceDelete.setOnClickListener(view -> onItemClickListener.onItemDeleteClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    public void setData(ArrayList<MyDeviceListApiResponseModel.Data> result) {
        resultBean = result;
        adapterModelList.clear();
        for (int i = 0; i < resultBean.size(); i++) {
            adapterModelList.add(new MyDeviceAdapterModel(TYPE_ITEM, resultBean.get(i)));
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
        private AppCompatImageView deviceTv, deviceDelete;
        private AppCompatTextView deviceName, deviceDescription;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            deviceTv = (AppCompatImageView) itemView.findViewById(R.id.deviceTv);
            deviceDelete = (AppCompatImageView) itemView.findViewById(R.id.deviceDelete);
            deviceName = (AppCompatTextView) itemView.findViewById(R.id.device_name);
            deviceDescription = (AppCompatTextView) itemView.findViewById(R.id.device_description);
        }
    }

    private class MyDeviceAdapterModel {
        private int type;
        private String date;
        private MyDeviceListApiResponseModel.Data item;

        public MyDeviceAdapterModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public MyDeviceAdapterModel(int type, MyDeviceListApiResponseModel.Data item) {
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

        public MyDeviceListApiResponseModel.Data getItem() {
            return item;
        }

        public void setItem(MyDeviceListApiResponseModel.Data item) {
            this.item = item;
        }
    }

}
