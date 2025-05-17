package com.roomate.app.constant;

public class Constant {
    public static String ROLE_PREFIX = "ROLE_";
    public static String AUTHORITY_DELIMITER = ",";
    public static String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static String ADMIN_AUTHORITIES = "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";
    public static String LANDLORD_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static String ROOMMATE_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static String HEADROOMMATE_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static String SUPERADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete, document:create,document:read,document:update,document:delete";


}
