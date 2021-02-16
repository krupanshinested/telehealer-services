package com.thealer.telehealer.apilayer.models.transaction;

import android.text.Editable;
import android.text.TextWatcher;

public class TextFieldModel {

    private String value;

    public TextFieldModel() {
    }

    public TextFieldModel(String value) {
        this.value = value;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            value = s.toString();
        }
    };

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }
}
