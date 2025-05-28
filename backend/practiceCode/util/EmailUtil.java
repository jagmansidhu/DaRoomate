package com.roomate.app.util;

public class EmailUtil {
    // EFFECT: Email message for registration.
    public static String getEmailMessage(String name, String host, String code) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to verify your account:\n" +
                getVerificationLink(host, code) + "\nSupport";
    }

    // EFFECT: Email message for password reset.
    public static String getPasswordMessage(String name, String host, String code) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to reset your password:\n" +
                getPasswordLink(host, code) + "\nSupport";
    }

    // EFFECT: Verification link for registration.
    public static String getVerificationLink(String host, String code) {
        return host + "/user/verify/account?code=" + code;
    }

    // EFFECT: Password reset link.
    public static String getPasswordLink(String host, String code) {
        return host + "/user/verify/password?code=" + code;
    }

}
