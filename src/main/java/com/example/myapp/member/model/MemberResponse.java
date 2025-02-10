package com.example.myapp.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

	private final Long memberId;
	private final String phone;
	private final String email;
	private final String role;
	private final String name;
}
