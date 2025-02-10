package com.example.myapp.member.model;

import java.util.List;

import lombok.Data;

@Data
public class DeleteMemberRequest {
    private List<Long> memberIds;
}

