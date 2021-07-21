package com.thealer.telehealer.apilayer.models.transaction;

import java.util.ArrayList;

public class TextFieldReasonOption extends ReasonOption {

    private ArrayList<TextFieldModel> textFieldValues;

    public TextFieldReasonOption(int value, String title, ArrayList<TextFieldModel> textFieldValues) {
        super(value, title, 0);
        this.textFieldValues = textFieldValues;
    }


    public ArrayList<TextFieldModel> getTextFieldValues() {
        return textFieldValues;
    }

    public void setTextFieldValues(ArrayList<TextFieldModel> textFieldValues) {
        this.textFieldValues = textFieldValues;
    }
}
