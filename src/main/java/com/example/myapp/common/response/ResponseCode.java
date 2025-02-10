package com.example.myapp.common.response;

public interface ResponseCode {
    // HTTP Status 200
    String SUCCESS = "SU";

    // HTTP Status 400
    String NOT_EXISTED_USER = "NEU";
    String NOT_EXISTED_EMAIL = "NEE";
    String DUPLICATE_EMAIL = "DE";
    String DUPLICATE_ID = "DI";
    String VALIDATION_FAILED = "VF";
    String NOT_SAME_PW = "NSP";
    String NULL_INTPUT_VALUE = "NIV";
    String ZOOM_BAD_REQUEST = "ZBR";

    // HTTP Status 401
    String SIGN_IN_FAILED = "SF";
    String CERTIFICATE_FAIL = "CF";
    String AUTHORIZATION_FAIL = "AF";
    String INVALID_GRANT = "IG";
    String EXPIRED_TOKEN = "EXT";

    // HTTP Status 403
    String NO_PERMISSION = "NP";
    
    //HTTP Status 404
    String ZOOM_NOT_FOUND = "ZNF";
    
    //HTTP Status 429
    String ZOOM_TOO_MANY_REQUESTS = "ZTMR";

    // HTTP Status 500
    String MAIL_FAIL = "MF";
    String REDIS_ERROR = "RE";
    String DATABASE_ERROR = "DBE";
    String SERVER_ERROR = "SE";

    //// lecture buy 기능
    String ALREADY_BUYED = "NB";
    String DENY_REFUND = "DR";
}