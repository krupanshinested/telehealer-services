package com.thealer.telehealer.common;

import android.content.Context;

import com.thealer.telehealer.R;

import java.util.regex.Pattern;

/**
 * Created by Aswin on 16,October,2018
 */
public class PasswordValidator {

    public boolean isLengthMatch(String password) {
        return password.length() > 7 && password.length() < 21;
    }

    public boolean isContainUpperCase(String password) {
        Pattern upperCasePattern = Pattern.compile("(?s).*[A-Z].*");
        return upperCasePattern.matcher(password).matches();
    }

    public boolean isContainLowerCase(String password) {
        Pattern lowerCasePattern = Pattern.compile("(?s).*[a-z].*");
        return lowerCasePattern.matcher(password).matches();
    }

    public boolean isContainNumber(String password) {
        Pattern numberPattern = Pattern.compile("(?d).*[0-9].*");
        return numberPattern.matcher(password).matches();
    }

    public boolean isContainRepeatedCharacter(String password) {
        Pattern repeatedPattern = Pattern.compile("^.*(.)\\1\\1.*$");
        return repeatedPattern.matcher(password).matches();
    }

    public boolean isContainSymbol(String password) {
        Pattern symbolPattern = Pattern.compile("[A-Za-z0-9]*");
        return !symbolPattern.matcher(password).matches();
    }

    public String isValidPassword(Context context, String password) {
        if (!isLengthMatch(password)) {
            return context.getString(R.string.password_length_error);
        } else if (!isContainLowerCase(password)) {
            return context.getString(R.string.password_lower_case_error);
        } else if (!isContainUpperCase(password)) {
            return context.getString(R.string.password_upper_case_error);
        } else if (!isContainNumber(password)) {
            return context.getString(R.string.password_number_error);
        } else if (!isContainSymbol(password)) {
            return context.getString(R.string.password_special_char_error);
        } else if (isContainRepeatedCharacter(password)) {
            return context.getString(R.string.password_repeated_content_error);
        } else
            return null;
    }
}
