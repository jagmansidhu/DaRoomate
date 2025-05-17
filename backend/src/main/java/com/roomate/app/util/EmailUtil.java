package com.roomate.app.util;

public class EmailUtil {
    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to verify your account:\n" +
                getVerificationLink(host, token) + "\nSupport";
    }

    public static String getPasswordMessage(String name, String host, String token) {
        return "Hello " + name + ",\n" +
                "Please click on the link below to reset your password:\n" +
                getPasswordLink(host, token) + "\nSupport";
    }

    public static String getVerificationLink(String host, String token) {
        return host + "/verify/account?token=" + token;
    }

    public static String getPasswordLink(String host, String token) {
        return host + "/verify/password?token=" + token;
    }

}
