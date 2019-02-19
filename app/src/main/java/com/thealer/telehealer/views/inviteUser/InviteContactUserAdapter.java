package com.thealer.telehealer.views.inviteUser;

import android.arch.lifecycle.Observer;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thealer.telehealer.R;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CustomDialogs.ItemPickerDialog;
import com.thealer.telehealer.views.common.CustomDialogs.PickerListener;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aswin on 19,February,2019
 */
class InviteContactUserAdapter extends RecyclerView.Adapter<InviteContactUserAdapter.ViewHolder> {
    private FragmentActivity context;
    private List<ContactModel> contactModelList;
    private InviteContactViewModel inviteContactViewModel;

    public InviteContactUserAdapter(FragmentActivity context, List<ContactModel> contactModelList, InviteContactViewModel inviteContactViewModel) {
        this.contactModelList = contactModelList;
        this.context = context;
        this.inviteContactViewModel = inviteContactViewModel;

        inviteContactViewModel.selectedContactList.observe(context, new Observer<List<SelectedContactModel>>() {
            @Override
            public void onChanged(@Nullable List<SelectedContactModel> contactModelList) {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_contact_invite, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.contactNameTv.setText(contactModelList.get(i).getName());
        if (contactModelList.get(i).getPhoto() != null && !contactModelList.get(i).getPhoto().isEmpty()) {
            Bitmap bitmap = (Bitmap) openPhoto(Long.parseLong(contactModelList.get(i).getId()));
            Glide.with(context).load(bitmap).into(viewHolder.avatarCiv);
        } else {
            viewHolder.avatarCiv.setImageResource(R.drawable.profile_placeholder);
        }
//        String selectedContact = contactModelList.get(i).getSelectedContact();
//        if (selectedContact != null && !selectedContact.isEmpty()) {
//            viewHolder.contactTv.setText(selectedContact);
//            viewHolder.contactTv.setVisibility(View.VISIBLE);
//            viewHolder.checkbox.setChecked(true);
//        } else {
//            viewHolder.contactTv.setVisibility(View.GONE);
//            viewHolder.checkbox.setChecked(false);
//        }

        if (inviteContactViewModel.selectedIdList.containsKey(contactModelList.get(i).getId())) {
            viewHolder.checkbox.setChecked(true);
            viewHolder.contactTv.setText(contactModelList.get(i).getSelectedContact());
            viewHolder.contactTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkbox.setChecked(false);
            viewHolder.contactTv.setVisibility(View.GONE);
        }

        viewHolder.itemViewCl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<String> listOptions = new ArrayList<>();

                if (!viewHolder.checkbox.isChecked()) {
                    if (!contactModelList.get(i).getNumberList().isEmpty() && !contactModelList.get(i).getEmailList().isEmpty()) {
                        listOptions.addAll(contactModelList.get(i).getNumberList());
                        listOptions.addAll(contactModelList.get(i).getEmailList());
                        showOptionsAlert(listOptions, i, String.format(context.getString(R.string.choose_mode_of_invite), contactModelList.get(i).getName()));
                    } else {
                        if (contactModelList.get(i).getEmailList().isEmpty()) {
                            if (contactModelList.get(i).getNumberList().size() == 1) {
                                addContact(i, contactModelList.get(i).getNumberList().get(0), null);
                            } else {
                                listOptions.addAll(contactModelList.get(i).getNumberList());
                                showOptionsAlert(listOptions, i, context.getString(R.string.pick_number_to_invite));
                            }
                        }
                        if (contactModelList.get(i).getNumberList().isEmpty()) {
                            if (contactModelList.get(i).getEmailList().size() == 1) {
                                addContact(i, null, contactModelList.get(i).getEmailList().get(0));
                            } else {
                                listOptions.addAll(contactModelList.get(i).getEmailList());
                                showOptionsAlert(listOptions, i, context.getString(R.string.pick_email_to_invite));
                            }
                        }
                    }
                } else {
                    removeItem(i);
                }
            }
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

    private void showOptionsAlert(List<String> listOptions, int position, String title) {

        ItemPickerDialog itemPickerDialog = new ItemPickerDialog(context, title,
                (ArrayList<String>) listOptions, new PickerListener() {
            @Override
            public void didSelectedItem(int selectedPosition) {
                if (Utils.isEmailValid(listOptions.get(selectedPosition))) {
                    addContact(position, null, listOptions.get(selectedPosition));
                } else {
                    addContact(position, listOptions.get(selectedPosition), null);
                }
            }

            @Override
            public void didCancelled() {

            }
        });
        itemPickerDialog.show();
    }

    private void removeItem(int position) {
        for (int i = 0; i < inviteContactViewModel.getSelectedContactList().getValue().size(); i++) {
            if (inviteContactViewModel.getSelectedContactList().getValue().get(i).getId().equals(contactModelList.get(position).getId())) {
                inviteContactViewModel.getSelectedContactList().getValue().remove(i);
                inviteContactViewModel.selectedContactList.setValue(inviteContactViewModel.getSelectedContactList().getValue());
                break;
            }
        }
        inviteContactViewModel.selectedIdList.remove(contactModelList.get(position).getId());
        contactModelList.get(position).setSelectedContact(null);
        notifyItemChanged(position);
    }

    private void addContact(int position, String number, String email) {

        if (!inviteContactViewModel.selectedIdList.containsKey(contactModelList.get(position).getId())) {

            if (number != null) {
                contactModelList.get(position).setSelectedContact(number);
            }
            if (email != null) {
                contactModelList.get(position).setSelectedContact(email);
            }

            List<SelectedContactModel> modelList = new ArrayList<>(inviteContactViewModel.getSelectedContactList().getValue());
            modelList.add(new SelectedContactModel(contactModelList.get(position).getId(), email, number));

            inviteContactViewModel.selectedIdList.put(contactModelList.get(position).getId(), contactModelList.get(position).getName());

            inviteContactViewModel.selectedContactList.setValue(modelList);

            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    public void setContactModelList(List<ContactModel> contactModelList) {
        this.contactModelList = contactModelList;
        Collections.sort(this.contactModelList,
                new Comparator<ContactModel>() {
                    @Override
                    public int compare(ContactModel o1, ContactModel o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarCiv;
        private ConstraintLayout itemViewCl;
        private TextView contactNameTv;
        private TextView contactTv;
        private CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemViewCl = (ConstraintLayout) itemView.findViewById(R.id.itemView);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.avatar_civ);
            contactNameTv = (TextView) itemView.findViewById(R.id.contact_name_tv);
            contactTv = (TextView) itemView.findViewById(R.id.contact_tv);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
