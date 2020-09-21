package com.thealer.telehealer.views.settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.models.DeleteAccount.DeleteAccountViewModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiResponseModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.Logs;
import com.thealer.telehealer.views.base.BaseActivity;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.signin.SigninActivity;

import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by rsekar on 11/20/18.
 */

public class DeleteAccountActivity extends BaseActivity implements TextWatcher {

    private EditText dEditText,e1EditText,e2EditText,e3EditText,lEditText,tEdiText;
    private ImageView closeImageView;

    private EditText[] editTexts;
    private Boolean isDeleteCalled = false;
    private DeleteAccountViewModel deleteAccountViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreenMode();
        setContentView(R.layout.activity_delete_account);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*
    *
    *Private methods
    *
     */
    private void initView() {
        dEditText = findViewById(R.id.d_editText);
        e1EditText = findViewById(R.id.e1_editText);
        e2EditText = findViewById(R.id.e2_editText);
        e3EditText = findViewById(R.id.e3_editText);
        lEditText = findViewById(R.id.l_editText);
        tEdiText = findViewById(R.id.t_editText);

        dEditText.requestFocus();

        editTexts = new EditText[]{dEditText,e1EditText,lEditText,e2EditText,tEdiText,e3EditText};

        for (EditText editText : editTexts) {
            editText.addTextChangedListener(this);
            addFocusLisetener(editText);
        }

        closeImageView = findViewById(R.id.close_iv);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteAccountViewModel = ViewModelProviders.of(DeleteAccountActivity.this).get(DeleteAccountViewModel.class);

        attachObserver(deleteAccountViewModel);

        deleteAccountViewModel.baseApiResponseModelMutableLiveData.observe(this, new Observer<BaseApiResponseModel>() {
            @Override
            public void onChanged(@Nullable BaseApiResponseModel baseApiResponseModel) {
                if (baseApiResponseModel != null){
                    appPreference.deletePreference();
                    startActivity(new Intent(DeleteAccountActivity.this, SigninActivity.class));
                    finish();
                }
            }
        });
    }

    private void makeTextFieldResponder(int forIndex) {
        if (forIndex >= 0 && forIndex < editTexts.length) {
            EditText editText = editTexts[forIndex];
            for (EditText et : editTexts) {
                if (et != editText)
                    et.clearFocus();
            }

            editText.requestFocus();
        }

    }

    private Boolean isValidToDelete()  {
        String string = "";

        for (EditText editText : editTexts) {
            string += editText.getText().toString();
        }

        return string.equals("DELETE");
    }

    private int getIndex(EditText editText)  {
        if (editText == dEditText) {
            return 0;
        } else if (editText == e1EditText) {
            return 1;
        } else if (editText == lEditText) {
            return 2;
        } else if (editText == e2EditText) {
            return 3;
        } else if (editText == tEdiText) {
            return 4;
        } else if (editText == e3EditText) {
            return 5;
        } else {
            return 0;
        }
    }

    private void addFocusLisetener(EditText editText) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText et = (EditText) view;
                if (b && et != dEditText) {
                    et.setText("");
                }
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {

                    EditText et = (EditText) v;
                    if (et.getText().toString().isEmpty()) {
                        int index = getIndex(et);
                        if (index > 0) {
                            makeTextFieldResponder(index - 1);
                        }
                    }

                }
                return false;
            }
        });
    }

    private void deleteAccount() {
        if (!isDeleteCalled)   {
            isDeleteCalled = true;
            deleteAccountViewModel.deleteAccount();
        }
    }

    /*
    *
    * Listeners
    *
    * */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().isEmpty()) {

        } else {
            EditText nextEditText = null;

            for (EditText editText : editTexts) {
                if (editText.getText().toString().isEmpty()) {
                    nextEditText = editText;
                    break;
                }
            }

            if (nextEditText != null) {
                int index = getIndex(nextEditText);
                makeTextFieldResponder(index);
            } else if (isValidToDelete()) {
                deleteAccount();
            }

        }
    }
}
