package com.weblab.baragregator;

public class CommonParameters {
    static String INTENT_FLAG = "FLAG";
    static String RESTORE_PASSWORD_FLAG = "RESTORE_PASS";
    static String RESTORE_TOKEN_FLAG = "RESTORE_TOKEN";
    static String REGISTRATION_FLAG = "REGISTRATION";

    static String SHARED_PREF_NAME = "TOKENS_SPREF";

    static String SERVER_IP = "http://192.168.43.208:8080";

    static String TOKEN_TAG = "user_token";
    static String SESSION_TOKEN_TAG = "session_token";
    static String PASSHASH_TAG = "pass_hash";
    static String EMAIL_TAG = "email";
    static String STATUS_TAG = "status";
    static String USER_ROLE_ID_TAG = "user_role_id";
    static String OTP_TAG = "otp";
    static String BAR_NAMES_TAG = "bar_names";
    static String BAR_IDS_TAG = "bar_ids";
    static String BAR_NAME_TAG = "bar_name";
    static String WAITER_NAME_TAG = "waiter_name";
    static String BAR_ID_TAG = "bar_id";
    static String BAR_RECEIPT_ID_TAG = "bar_receipt_id";
    static String PRODUCTS_LIST_TAG = "products_list";
    static String PRODUCTS_ID_TAG = "products_id";
    static String PRODUCTS_NAMES = "products_names";
    static String UNIQUE_RECEIPT_ID_TAG = "unique_receipt_id";

    static String REGISTRATION_REQ = "/registration";
    static String AUTHORIZATION_REQ = "/authorization";
    static String ADD_BAR_REQ = "/add_bar";
    static String GET_BAR_LIST_REQ = "/get_bar_list";
    static String OTP_CONFIRMATION_REQ = "/otp_confirmation";
    static String OTP_REQ = "/otp_req";
    static String INIT_REQ = "/init";
    static String ADD_WAITER_REQ = "/add_waiter";
    static String DELETE_WAIER_REQ = "/delete_waiter";
    static String DELETE_BAR_REQ = "/delete_bar";
    static String WAITER_ROLE_REQ = "/waiter";
    static String GET_BAR_PRODUCTS_REQ = "/get_bar_products";
    static String ADD_RECEIPT_REQ = "/add_receipt";
    static String GET_BAR_RECEIPT = "/get_bar_receipt";
    static String PAYMENT_REQ = "/payment_req";


    static String STATUS_USER_EXISTS = "USER_EXIST";
    static String STATUS_USER_NOT_EXISTS = "USER_NOT_EXISTS";
    static String STATUS_WRONG_TOKEN = "WRONG_TOKEN";
    static String STATUS_WRONG_PASSWORD = "WRONG_PASSWORD";
    static String STATUS_OK = "OK";
    static String STATUS_ERROR = "ERROR";
    static String STATUS_WRONG_OTP = "WRONG_OTP";
    static String STATUS_SESSION_NOT_EXISTS = "SESSION_TIMEOUT";

    static String ROLE_ADMIN = "admin";
    static String ROLE_USER = "user";
    static String ROLE_BAR = "bar_admin";
    static String ROLE_WAITER = "waiter";


    static String FromBytesToHexString(byte[] str) {
        String buff = new String();
        for (byte b : str)
            buff += String.format("%02X", b);

        return buff;
    }
}

