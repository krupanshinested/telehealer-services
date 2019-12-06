package com.thealer.telehealer.views.signup.doctor;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.createuser.CreateUserRequestModel;
import com.thealer.telehealer.apilayer.models.createuser.PhonesBean;
import com.thealer.telehealer.apilayer.models.createuser.PracticesBean;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.views.common.CurrentModeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aswin on 28,October,2018
 */
class OfficePhoneListAdapter extends RecyclerView.Adapter<OfficePhoneListAdapter.ViewHolder> {
    private FragmentActivity fragmentActivity;
    private int practiceId;
    private boolean isNewPractice;
    private CreateUserRequestModel createUserRequestModel;
    private List<PhonesBean> phonesBeanList = new ArrayList<>();
    private CurrentModeInterface currentModeInterface;

    public OfficePhoneListAdapter(FragmentActivity activity, int practiceId, boolean isNewPractice, CurrentModeInterface currentModeInterface) {
        this.fragmentActivity = activity;
        this.practiceId = practiceId;
        this.isNewPractice = isNewPractice;
        this.currentModeInterface = currentModeInterface;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        createUserRequestModel = new ViewModelProvider(fragmentActivity).get(CreateUserRequestModel.class);
        if (createUserRequestModel.getUser_detail().getData().getPractices().size() > practiceId) {
            this.phonesBeanList.addAll(createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).getPhones());
            addNewField();
            notifyDataSetChanged();
        } else {
            addNewField();
        }

    }

    private void addNewField() {
        phonesBeanList.add(new PhonesBean(null, fragmentActivity.getResources().getString(R.string.office_phone)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.adapter_doctor_phone_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String hint = phonesBeanList.get(i).getType();
        hint = hint.replace(hint.charAt(0), String.valueOf(hint.charAt(0)).toUpperCase().charAt(0));
        viewHolder.officePhoneTil.setHint(hint);
        viewHolder.officePhoneEt.setText(phonesBeanList.get(i).getNumber());

        viewHolder.officePhoneEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE &&
                        !viewHolder.officePhoneEt.getText().toString().isEmpty()) {

                    PhonesBean phonesBean = new PhonesBean(viewHolder.officePhoneEt.getText().toString(),
                            fragmentActivity.getResources().getString(R.string.office_phone));
                    phonesBeanList.set(i, phonesBean);

                    if (i == getItemCount() - 1) {
                        addNewField();
                    }

                    updateUserModel();
                }
                return false;
            }
        });

        viewHolder.officePhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateUserModel();
            }
        });

        if (currentModeInterface.getCurrentMode() == Constants.VIEW_MODE) {
            Utils.setEditable(viewHolder.officePhoneEt, false);
        } else {
            Utils.setEditable(viewHolder.officePhoneEt, true);
        }
    }

    private void updateUserModel() {
        if (!isNewPractice) {
            if (createUserRequestModel.getUser_detail().getData().getPractices().size() == 0) {
                createUserRequestModel.getUser_detail().getData().getPractices().add(new PracticesBean(null, null, null, new ArrayList<>()));
            }
            createUserRequestModel.getUser_detail().getData().getPractices().get(practiceId).setPhones(phonesBeanList.subList(0, phonesBeanList.size() - 1));
        } else {
            List<PracticesBean> practicesBeanList = createUserRequestModel.getUser_detail().getData().getPractices();
            if (practicesBeanList == null)
                practicesBeanList = new ArrayList<>();

            if (practicesBeanList.size() > 0) {
                practicesBeanList.get(0).setPhones(phonesBeanList.subList(0, phonesBeanList.size() - 1));
            } else {
                practicesBeanList.add(new PracticesBean(null,
                        null,
                        null,
                        phonesBeanList.subList(0, phonesBeanList.size() - 1)));
            }

            createUserRequestModel.getUser_detail().getData().setPractices(practicesBeanList);
            isNewPractice = false;
        }

    }

    @Override
    public int getItemCount() {
        return phonesBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout officePhoneTil;
        private EditText officePhoneEt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            officePhoneTil = (TextInputLayout) itemView.findViewById(R.id.office_phone_til);
            officePhoneEt = (EditText) itemView.findViewById(R.id.office_phone_et);
        }
    }
}
