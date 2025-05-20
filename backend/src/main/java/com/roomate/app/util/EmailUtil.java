package com.roomate.app.util;

public class EmailUtil {
    public static String getEmailMessage(String name, String host, String code) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to verify your account:\n" +
                getVerificationLink(host, code) + "\nSupport";
    }

    public static String getPasswordMessage(String name, String host, String code) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to reset your password:\n" +
                getPasswordLink(host, code) + "\nSupport";
    }

    public static String getVerificationLink(String host, String code) {
        return host + "/verify/account?code=" + code;
    }

    public static String getPasswordLink(String host, String code) {
        return host + "/verify/password?code=" + code;
    }

}
