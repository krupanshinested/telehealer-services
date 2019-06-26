package com.thealer.telehealer.views.inviteUser;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 08,March,2019
 */
public class SelectedContactListAdapter extends RecyclerView.Adapter<SelectedContactListAdapter.ViewHolder> {
    private FragmentActivity activity;
    private InviteContactViewModel inviteContactViewModel;
    private List<SelectedContactModel> selectedContactModels;

    public SelectedContactListAdapter(FragmentActivity activity) {
        this.activity = activity;
        selectedContactModels = new ArrayList<>();
        inviteContactViewModel = ViewModelProviders.of(activity).get(InviteContactViewModel.class);
        inviteContactViewModel.selectedContactList.observe(activity, new Observer<List<SelectedContactModel>>() {
            @Override
            public void onChanged(@Nullable List<SelectedContactModel> contactModelList) {
                if (contactModelList != null) {
                    selectedContactModels = contactModelList;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_selected_contact_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.contactNameTv.setText(inviteContactViewModel.selectedIdList.get(selectedContactModels.get(i).getId()));

        Bitmap bitmap = (Bitmap) openPhoto(Long.parseLong(selectedContactModels.get(i).getId()));

        if (bitmap != null) {
            Glide.with(activity.getApplicationContext()).load(bitmap).into(viewHolder.avatarCiv);
        } else {
            viewHolder.avatarCiv.setImageDrawable(activity.getDrawable(R.drawable.profile_placeholder));
        }

        viewHolder.closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteContactViewModel.selectedIdList.remove(selectedContactModels.get(i).getId());
                selectedContactModels.remove(i);
                notifyItemRemoved(i);
                inviteContactViewModel.selectedContactList.setValue(selectedContactModels);
            }
        });
    }

    public Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = activity.getContentResolver().query(photoUri,
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
        return selectedContactModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private CircleImageView closeIv;
        private TextView contactNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            closeIv = (CircleImageView) itemView.findViewById(R.id.close_iv);
            contactNameTv = (TextView) itemView.findViewById(R.id.contact_name_tv);
        }
    }
}
