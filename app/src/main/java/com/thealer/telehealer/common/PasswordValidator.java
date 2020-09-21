package com.thealer.telehealer.common;

import java.util.regex.Pattern;

/**
 * Created by Aswin on 16,October,2018
 */
public class PasswordValidator {

    public boolean isLengthMatch(String password){
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

    public boolean isContainSymbol(String password){
        Pattern symbolPattern = Pattern.compile("[A-Za-z0-9]*");
        return !symbolPattern.matcher(password).matches();
    }

    public String isValidPassword(String password){
        if (!isLengthMatch(password)) {
            return "Password length should be 8 to 20";
        } else if (!isContainLowerCase(password)) {
            return "Password must contain atleast 1 lower case character";
        } else if (!isContainUpperCase(password)) {
            return "Password must contain atleast 1 upper case character";
        } else if (!isContainNumber(password)) {
            return "Password must contain atleast 1 number";
        } else if (!isContainSymbol(password)) {
            return "Password must contain atleast 1 special character";
        } else if (isContainRepeatedCharacter(password)) {
            return "Password must not contain repeated character";
        } else
            return null;
    }
}
