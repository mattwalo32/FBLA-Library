package com.walowtech.fblaapplication.Utils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates text fields
 *
 * Class contains various methods to validate
 * names, emails, passwords, and school IDs.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/16/17
public class ValidationUtils{


    /**
     * Validates a name to be between 5 and 50
     *
     * @param name The name to be validated
     * @return A pair is returned with a boolean signifying if
     * a name is valid, and a message with it
     */
    public static Pair<Boolean, String> validateName(String name){
        Pattern p = Pattern.compile("^[\\p{L} .'-]+$");
        Matcher m = p.matcher(name);

        if(name.length() > 50)
            return new Pair<>(false, "Name must be shorter than 50 characters");
        if(m.matches() && name.length() > 5)
            return new Pair<>(true, null);
        if(m.matches() && (name.length() <= 4))
            return new Pair<>(false, "Name must be longer than 4 characters");
        return new Pair<>(false, "Type your first and last name with no special characters");
    }

    /**
     * Validates email between 5 and 50 characters and must be valid.
     *
     * @param email The email to be validated
     * @return A pair is returned with a boolean signifying if
     * an email is valid, and a message with it
     */
    public static Pair<Boolean, String> validateEmail(String email){
        if(email.length() >= 50)
            return new Pair<>(false, "Email must be shorter than 50 characters");
        if(email.length() <= 5)
            return new Pair<>(false, "Email must be longer than 5 characters");
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return new Pair<>(true, null);
        return new Pair<>(false, "Invalid email");
    }

    /**
     * Validates password between 5 and 35 characters
     *
     * @param password The password to be validated
     * @return A pair is returned with a boolean signifying if
     * a password is valid, and a message with it
     */
    public static Pair<Boolean, String> validatePassword(String password){
        if(password.length() >= 50)
            return new Pair<>(false, "Password must be shorter than 50 characters");
        if(password.length() > 5)
            return new Pair<>(true, null);
        return new Pair<>(false, "Password must be longer than 5 characters");
    }

    /**
     * Validates a school ID
     *
     * @param ID The ID to be validated
     * @return A pair is returned with a boolean signifying if
     * an ID is valid, and a message with it
     */
    public static Pair<Boolean, String> validateSchoolID(String ID){
        if(ID.length() > 5 && ID.length() < 7)
            return new Pair<>(true, null);
        return new Pair<>(false, "ID should be 6 digits long");
    }
}
