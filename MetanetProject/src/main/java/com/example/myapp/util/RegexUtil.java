package com.example.myapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    // 전화번호 검증 (예: 010-1234-5678 형식)
    public boolean telNumber(String phone) {
        String regEx = "^\\d{3}-\\d{3,4}-\\d{4}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    // 이메일 검증
    public boolean checkEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 비밀번호 검증 (숫자, 특수문자 포함, 8~16자리)
    public boolean checkPassword(String password) {
        String regEx = "^(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\":{}|<>]{8,16}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // 날짜 인증
    public boolean checkDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = date.format(formatter);

        // 정규식 패턴 정의
        String regEx = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(formattedDate);

        return matcher.matches();
    }
}
