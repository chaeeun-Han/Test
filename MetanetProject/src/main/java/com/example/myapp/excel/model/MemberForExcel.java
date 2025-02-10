package com.example.myapp.excel.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberForExcel {
    private String title;
    private String name;
    private String phone;
    private String email;
}
