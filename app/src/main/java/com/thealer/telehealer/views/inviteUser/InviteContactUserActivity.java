package com.thealer.telehealer.views.inviteUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneApiResponseModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteByEmailPhoneRequestModel;
import com.thealer.telehealer.apilayer.models.inviteUser.InviteUserApiViewModel;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.PermissionChecker;
import com.thealer.telehealer.common.PermissionConstants;
import com.thealer.telehealer.common.RequestID;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.SuccessViewInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by Aswin on 19,February,2019
 */
public class InviteContactUserActivity extends BaseActivity implements View.OnClickListener, SuccessViewInterface {
    private AppBarLayout appbarLayout;
    private Toolbar toolbar;
    private ImageView backIv;
    private TextView toolbarTitle;
    private LinearLayout searchLl;
    private View topView;
    private CardView searchCv;
    private EditText searchEt;
    private ImageView searchClearIv;
    private View bottomView;
    private RecyclerView contactsRv;
    private Button inviteBtn;

    private List<ContactModel> contactModelList = new ArrayList<>();
    private List<ContactModel> searchList = new ArrayList<>();
    private InviteContactUserAdapter inviteContactUserAdapter;

    private InviteContactViewModel inviteContactViewModel;
    private InviteUserApiViewModel inviteUserApiViewModel;
    private InviteByEmailPhoneApiResponseModel apiResponseModel;
    private InviteByEmailPhoneRequestModel apiRequestModel;
    private RecyclerView selectedContactsRv;
    private TextView emptyTv;
    private String doctorGuid = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contact);
        initViewModels();
        initView();
    }

    private void initViewModels() {
        inviteUserApiViewModel = new ViewModelProvider(this).get(InviteUserApiViewModel.class);
        attachObserver(inviteUserApiViewModel);
        inviteUserApiViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null) {
                    apiResponseModel = (InviteByEmailPhoneApiResponseModel) baseApiResponseModel;
                    boolean status = true;
                    String title = getString(R.string.success);
                    String message = getString(R.string.invite_user_success_message);

                    if (apiResponseModel.getSuccessCount() == 0 && apiResponseModel.getFailureCount() == 1) {
                        status = false;
                        title = getString(R.string.failure);
                        message = apiResponseModel.getResultData().get(0).getMessage();

                    } else if (apiResponseModel.getSuccessCount() > 0 && apiResponseModel.getFailureCount() > 0) {

                        title = getString(R.string.partially_success);

                        Set<String> stringSet = new HashSet<>();
                        if (apiResponseModel.getFailureCount() > 0) {
                            for (int i = 0; i < apiResponseModel.getResultData().size(); i++) {
                                if (!apiResponseModel.getResultData().get(i).isSuccess()) {
                                    stringSet.add(apiResponseModel.getResultData().get(i).getMessage());
                                }
                            }

                            message = String.format(getString(R.string.invite_contact_error), apiResponseModel.getResultData().size(),
                                    apiResponseModel.getFailureCount(), stringSet.toString().substring(1, stringSet.toString().length() - 1));
                        }
                    }
                    inviteContactViewModel.selectedIdList.clear();
                    sendSuccessViewBroadCast(InviteContactUserActivity.this, status, title, message);
                    searchEt.setText("");
                }
            }
        });

        inviteUserApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    sendSuccessViewBroadCast(InviteContactUserActivity.this, false, getString(R.string.failure), errorModel.getMessage());
                }
            }
        });
    }

    private void initView() {

        appbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backIv = (ImageView) findViewById(R.id.back_iv);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        searchLl = (LinearLayout) findViewById(R.id.search_ll);
        topView = (View) findViewById(R.id.top_view);
        searchCv = (CardView) findViewById(R.id.search_cv);
        searchEt = (EditText) findViewById(R.id.search_et);
        searchClearIv = (ImageView) findViewById(R.id.search_clear_iv);
        bottomView = (View) findViewById(R.id.bottom_view);
        contactsRv = (RecyclerView) findViewById(R.id.contacts_rv);
        inviteBtn = (Button) findViewById(R.id.invite_btn);
        selectedContactsRv = (RecyclerView) findViewById(R.id.selected_contacts_rv);
        emptyTv = (TextView) findViewById(R.id.empty_tv);

        searchEt.setHint(getString(R.string.search_contact));

        inviteContactViewModel = new ViewModelProvider(this).get(InviteContactViewModel.class);
        inviteContactViewModel.getSelectedContactList().setValue(new ArrayList<>());
        inviteContactViewModel.getSelectedContactList().observe(this, new Observer<List<SelectedContactModel>>() {
            @Override
            public void onChanged(@Nullable List<SelectedContactModel> selectedContactModels) {
                if (selectedContactModels != null && !selectedContactModels.isEmpty()) {
                    inviteBtn.setEnabled(true);
                    emptyTv.setVisibility(View.GONE);
                } else {
                    emptyTv.setVisibility(View.VISIBLE);
                    inviteBtn.setEnabled(false);
                }
            }
        });

        toolbarTitle.setText(getString(R.string.invite_contacts));
        bottomView.setVisibility(View.VISIBLE);

        backIv.setOnClickListener(this);
        inviteBtn.setOnClickListener(this);
        searchClearIv.setOnClickListener(this);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchClearIv.setVisibility(View.VISIBLE);
                    getSearchList(s.toString());
                } else {
                    searchClearIv.setVisibility(View.GONE);
                    if (inviteContactUserAdapter != null) {
                        inviteContactUserAdapter.setContactModelList(contactModelList);
                    }
                }
            }
        });

        if (PermissionChecker.with(this).checkPermission(PermissionConstants.PERMISSION_CONTACTS)) {
            getAllContacts(true);
        }

        selectedContactsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SelectedContactListAdapter selectedContactListAdapter = new SelectedContactListAdapter(this);
        selectedContactsRv.setAdapter(selectedContactListAdapter);

        if (getIntent().getExtras() != null) {
            CommonUserApiResponseModel doctorModel = (CommonUserApiResponseModel) getIntent().getExtras().getSerializable(Constants.USER_DETAIL);
            if (doctorModel != null) {
                doctorGuid = doctorModel.getUser_guid();
            }
        }
    }

    private void getSearchList(String name) {
        searchList.clear();
        for (int i = 0; i < contactModelList.size(); i++) {
            if (contactModelList.get(i).getName().toLowerCase().contains(name.toLowerCase())) {
                searchList.add(contactModelList.get(i));
            }
        }
        if (inviteContactUserAdapter != null) {
            inviteContactUserAdapter.setContactModelList(searchList);
        }
    }

    private void setRecyclerView() {
        contactsRv.setLayoutManager(new LinearLayoutManager(this));
        inviteContactUserAdapter = new InviteContactUserAdapter(this, contactModelList, inviteContactViewModel);
        contactsRv.setAdapter(inviteContactUserAdapter);
    }

    private void updateContacts() {
        inviteContactUserAdapter.setContactModelList(contactModelList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.invite_btn:
                showSuccessView(RequestID.REQ_SHOW_SUCCESS_VIEW);
                inviteUsers();
                break;
            case R.id.search_clear_iv:
                searchEt.setText(null);
                break;
        }
    }

    private void inviteUsers() {
        List<InviteByEmailPhoneRequestModel.InvitationsBean> invitationsBeanList = new ArrayList<>();

        for (int i = 0; i < inviteContactViewModel.getSelectedContactList().getValue().size(); i++) {

            String phone = inviteContactViewModel.getSelectedContactList().getValue().get(i).getPhone();
            if (phone != null) {
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(this);
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(inviteContactViewModel.getSelectedContactList().getValue().get(i).getPhone(), getResources().getConfiguration().locale.getCountry());
                    phone = "+" + phoneNumber.getCountryCode() + "" + phoneNumber.getNationalNumber();
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
            }
            InviteByEmailPhoneRequestModel.InvitationsBean invitationsBean = new InviteByEmailPhoneRequestModel.InvitationsBean(inviteContactViewModel.getSelectedContactList().getValue().get(i).getEmail(), phone);
            invitationsBeanList.add(invitationsBean);
        }

        apiRequestModel = new InviteByEmailPhoneRequestModel();
        apiRequestModel.setInvitations(invitationsBeanList);

        inviteUserApiViewModel.inviteUserByEmailPhone(doctorGuid, apiRequestModel, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionConstants.PERMISSION_CONTACTS) {
            if (resultCode == Activity.RESULT_OK) {
                getAllContacts(true);
            } else {
                onBackPressed();
            }
        }
    }


    private void getAllContacts(boolean isFirst) {
        String[] CONTACT_PROJECTION = {
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                CONTACT_PROJECTION, null, null,
                ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor != null && cursor.getCount() > 0)
            new FetchContacts(cursor, isFirst).execute();
    }

    @Override
    public void onSuccessViewCompletion(boolean success) {
        if (success) {
            inviteContactViewModel.selectedContactList.setValue(new ArrayList<>());
            inviteContactUserAdapter.setContactModelList(contactModelList);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchContacts extends AsyncTask<Void, Void, List<ContactModel>> {

        boolean isFirst;
        int start = 0, end, size;
        Cursor cursor;

        public FetchContacts(Cursor cursor, boolean isFirst) {
            this.isFirst = isFirst;
            this.cursor = cursor;
            this.size = cursor.getCount();
            if (size > 100) {
                if (isFirst) {
                    this.start = 0;
                    this.end = 100;
                } else {
                    this.start = 101;
                    this.end = cursor.getCount();
                }
            } else {
                end = size;
            }
        }

        @Override
        protected void onPreExecute() {
            if (isFirst)
                showProgressDialog();
        }

        @Override
        protected List<ContactModel> doInBackground(Void... voids) {

            if (!isFirst) {
                cursor.moveToPosition(100);
            }
            while (cursor.moveToNext() && cursor.getPosition() <= end) {
                String contactId = cursor.getString(1);
                String contactName = cursor.getString(2);
                String contactPhotoUri = cursor.getString(3);
                List<String> contactEmailList = new ArrayList<>();
                List<String> contactNumberList = new ArrayList<>();

                Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contactId}, null);

                if (emailCursor != null) {
                    if (emailCursor.getCount() > 0 && emailCursor.moveToFirst()) {

                        for (int i = 0; i < emailCursor.getCount(); i++) {
                            String contactEmail = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                            if (!contactEmailList.contains(contactEmail))
                                contactEmailList.add(contactEmail);
                            emailCursor.moveToNext();
                        }
                    }
                    emailCursor.close();
                }

                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

                if (phoneCursor != null) {
                    if (phoneCursor.getCount() > 0 && phoneCursor.moveToFirst()) {

                        for (int i = 0; i < phoneCursor.getCount(); i++) {
                            String contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (!contactNumberList.contains(contactNumber))
                                contactNumberList.add(contactNumber);
                            phoneCursor.moveToNext();
                        }
                    }
                    phoneCursor.close();
                }

                if (!contactEmailList.isEmpty() || !contactNumberList.isEmpty()) {
                    ContactModel contactModel = new ContactModel(contactId, contactName, contactPhotoUri, contactEmailList, contactNumberList);
                    contactModelList.add(contactModel);
                }
            }

            cursor.close();

            return contactModelList;
        }

        @Override
        protected void onPostExecute(List<ContactModel> contactModels) {
            if (isFirst) {
                setRecyclerView();
            } else {
                updateContacts();
            }
            dismissProgressDialog();
            if (size > 100 && isFirst) {
                getAllContacts(false);
            }
        }
    }
}
