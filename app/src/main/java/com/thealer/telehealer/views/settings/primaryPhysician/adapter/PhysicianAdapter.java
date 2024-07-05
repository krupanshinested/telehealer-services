package com.thealer.telehealer.views.settings.primaryPhysician.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.models.accessLog.AccessLogApiResponseModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.UserDetailPreferenceManager;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.settings.primaryPhysician.interfaces.OnPhysicianClick;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhysicianAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<CommonUserApiResponseModel> adapterModelList;
    private OnPhysicianClick onPhysicianClick;

    public PhysicianAdapter(Context context, ArrayList<CommonUserApiResponseModel> adapterModelList, OnPhysicianClick onPhysicianClick) {
        this.context = context;
        this.adapterModelList = adapterModelList;
        this.onPhysicianClick = onPhysicianClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.cell_physician_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        CommonUserApiResponseModel commonUserApiResponseModel = adapterModelList.get(position);

        String title, specialization = "-";

        title = commonUserApiResponseModel.getFirst_name() + " " + commonUserApiResponseModel.getLast_name() + ", " + commonUserApiResponseModel.getUser_detail().getData().getTitle().toUpperCase();
        if (!(commonUserApiResponseModel.getUser_detail().getData().getSpecialties().isEmpty())) {
            specialization = commonUserApiResponseModel.getUser_detail().getData().getSpecialties().get(0).getName();
        }


        itemHolder.userNameTv.setText(title);
        itemHolder.userDesTv.setText(specialization);

        itemHolder.radioBtn.setChecked(commonUserApiResponseModel.getSelected());

        Glide.with(context.getApplicationContext())
                .load(commonUserApiResponseModel.getUser_avatar())
                .placeholder(R.drawable.profile_placeholder) // Replace with your placeholder image resource
                .into(itemHolder.userAvatarCiv);

        itemHolder.itemView.setOnClickListener(view -> {
            for (CommonUserApiResponseModel currentItem : adapterModelList) {
                if (currentItem.getUser_id() == commonUserApiResponseModel.getUser_id()) {
                    currentItem.setSelection(true);
                } else {
                    currentItem.setSelection(false);
                }
            }
            onPhysicianClick.onClick(commonUserApiResponseModel.getUser_id());
            notifyDataSetChanged();
        });

    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    @Override
    public int getItemCount() {
        return adapterModelList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private CircleImageView userAvatarCiv;
        private TextView userNameTv;
        private TextView userDesTv;
        private RadioButton radioBtn;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            userNameTv = (TextView) itemView.findViewById(R.id.list_title_tv);
            userDesTv = (TextView) itemView.findViewById(R.id.list_sub_title_tv);
            radioBtn = (RadioButton) itemView.findViewById(R.id.radioBtn);
        }
    }

    private class AccessLogAdapterModel {
        private int type;
        private String date;
        private AccessLogApiResponseModel.ResultBean item;

        public AccessLogAdapterModel(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public AccessLogAdapterModel(int type, AccessLogApiResponseModel.ResultBean item) {
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

        public AccessLogApiResponseModel.ResultBean getItem() {
            return item;
        }

        public void setItem(AccessLogApiResponseModel.ResultBean item) {
            this.item = item;
        }
    }

}
