package com.example.myapp.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {

    private String code;
    private String message;
    private T data;

    public ResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 200
    public static ResponseEntity<ResponseDto> success() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);

    }    

    // HTTP Status 400
    public static ResponseEntity<ResponseDto> notExistUser() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NOT_EXISTED_USER, ResponseMessage.NOT_EXISTED_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> notExistEmail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NOT_EXISTED_EMAIL, ResponseMessage.NOT_EXISTED_EMAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> duplicateEmail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DUPLICATE_EMAIL, ResponseMessage.DUPLICATE_EMAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> duplicatedId() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DUPLICATE_ID, ResponseMessage.DUPLICATE_ID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> validationFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.VALIDATION_FAILED, ResponseMessage.VALIDATION_FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> notSamePassword() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NOT_SAME_PW, ResponseMessage.NOT_SAME_PW);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> nullInputValue() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NULL_INTPUT_VALUE, ResponseMessage.NULL_INTPUT_VALUE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
    
    public static ResponseEntity<ResponseDto> zoomBadRequest() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ZOOM_BAD_REQUEST, ResponseMessage.ZOOM_BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    // HTTP Status 401
    public static ResponseEntity<ResponseDto> signInFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SIGN_IN_FAILED, ResponseMessage.SIGN_IN_FAILED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> certificateFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.CERTIFICATE_FAIL, ResponseMessage.CERTIFICATE_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> noAuthentication() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.AUTHORIZATION_FAIL, ResponseMessage.AUTHORIZATION_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> invalidGrant() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.INVALID_GRANT, ResponseMessage.INVALID_GRANT);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> expiredToken() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.EXPIRED_TOKEN, ResponseMessage.EXPIRED_TOKEN);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    // HTTP Status 403
    public static ResponseEntity<ResponseDto> noPermission() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.NO_PERMISSION, ResponseMessage.NO_PERMISSION);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
    }
    
    // HTTP Status 404
    public static ResponseEntity<ResponseDto> zoomNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ZOOM_NOT_FOUND, ResponseMessage.ZOOM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }
    
    // HTTP Status 429
    public static ResponseEntity<ResponseDto> zoomTooManyRequests() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ZOOM_TOO_MANY_REQUESTS, ResponseMessage.ZOOM_TOO_MANY_REQUESTS);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(responseBody);
    }

    // HTTP Status 500
    public static ResponseEntity<ResponseDto> mailSendFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MAIL_FAIL, ResponseMessage.MAIL_FAIL);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> databaseError() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DATABASE_ERROR, ResponseMessage.DATABASE_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> redisError() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.REDIS_ERROR, ResponseMessage.REDIS_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> serverError() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SERVER_ERROR, ResponseMessage.SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> alreadyBuyed() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ALREADY_BUYED, ResponseMessage.ALREADY_BUYED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> cantRefund() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DENY_REFUND, ResponseMessage.DENY_REFUND);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<byte[]> successPdf(byte[] pdfContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/pdf");
        headers.add("Content-Disposition", "attachment; filename=certificate.pdf");

        // PDF 콘텐츠를 body로 직접 반환
        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
