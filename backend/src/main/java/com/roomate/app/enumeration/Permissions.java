package com.roomate.app.enumeration;

import static com.roomate.app.constant.Constant.*;

public enum Permissions {
    USER(USER_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES),
    LANDLORD(LANDLORD_AUTHORITIES),
    ROOMMATE(ROOMMATE_AUTHORITIES),
    HEADROOMMATE(HEADROOMMATE_AUTHORITIES),
    SUPERADMIN(SUPERADMIN_AUTHORITIES);

    private final String value;

    Permissions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
