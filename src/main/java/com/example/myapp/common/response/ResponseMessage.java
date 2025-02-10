package com.example.myapp.common.response;

public interface ResponseMessage {
    // HTTP Status 200
    String SUCCESS = "success";

    // HTTP Status 400
    String NOT_EXISTED_USER = "This user does not exist.";
    String NOT_EXISTED_EMAIL = "This email does not exist.";
    String DUPLICATE_EMAIL = "duplicate email";
    String DUPLICATE_ID = "duplicate id";
    String VALIDATION_FAILED = "validation failed";
    String NOT_SAME_PW = "not same password and password1";
    String NULL_INTPUT_VALUE = "null input value";
    String ZOOM_BAD_REQUEST = "Zoom bad request";

    // HTTP Status 401
    String SIGN_IN_FAILED = "Login information mismatch.";
    String CERTIFICATE_FAIL = "certification failed";
    String AUTHORIZATION_FAIL = "Authorization Failed.";
    String INVALID_GRANT = "The provided access grant is invalid, expired, or revoked";
    String EXPIRED_TOKEN = "This token is expired";

    // HTTP Status 403
    String NO_PERMISSION = "Do not have permission.";
    
    //HTTP Status 404
    String ZOOM_NOT_FOUND = "Not found user";
    
    //HTTP Status 429
    String ZOOM_TOO_MANY_REQUESTS = "Too many requests";

    // HTTP Status 500
    String MAIL_FAIL = "mail send failed";
    String REDIS_ERROR = "An error occurred while processing Redis.";
    String DATABASE_ERROR = "database error";
    String SERVER_ERROR = "server error";

    // lecture buy 기능
    String ALREADY_BUYED = "already buyed lecture, no need to buy";
    String DENY_REFUND = "You cannot refund. Please contact us if you did not download the file.";
}