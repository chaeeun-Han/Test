package com.example.myapp.cart.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.myapp.cart.model.Cart;
import com.example.myapp.common.response.ResponseDto;

public interface ICartService {

	ResponseEntity<ResponseDto> getCarts(String memberId);
	ResponseEntity<ResponseDto> addCart(String memberId, String lecureId);
	ResponseEntity<ResponseDto> deleteCarts(String memberId, List<Long> cartId);

}
