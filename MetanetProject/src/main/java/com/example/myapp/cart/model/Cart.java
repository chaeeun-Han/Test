package com.example.myapp.cart.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class Cart {
	private final Long cartId;
	private final Long lectureId;
	private final Long memberId;
	
	private final String title;
	private final String profile;
	private final int price;
	private final boolean status;
	private final int limitStudent;
	private final Date deadLineTime;
	private final int remaining;
	
}
