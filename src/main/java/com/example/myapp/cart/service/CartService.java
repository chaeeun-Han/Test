package com.example.myapp.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.myapp.cart.dao.ICartRepository;
import com.example.myapp.cart.model.Cart;
import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.member.dao.IMemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CartService implements ICartService{

	@Autowired
	ICartRepository cartRepository;
	
	@Autowired
	IMemberRepository memberRepository;
	
	// 장바구니 전체 조회 - 채은
	@Override
	public ResponseEntity<ResponseDto> getCarts(String memberId) {
		List<Cart> carts = null;
		try {
			Long memberUID = memberRepository.getMemberIdById(memberId);
			carts = cartRepository.getCarts(memberUID);
		} catch (Exception e){
			e.printStackTrace();
			return ResponseDto.databaseError();
		}
			ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, carts);
			return ResponseEntity.ok(responseBody);
	}
	
	// 장바구니 추가 - 채은
	@Override
	public ResponseEntity<ResponseDto> addCart(String memberId, String lectureId) {
		try {
			Long memberUID = memberRepository.getMemberIdById(memberId);
			cartRepository.addCart(memberUID, lectureId);
		} catch (Exception e){
			e.printStackTrace();
			return ResponseDto.databaseError();
		}
		return ResponseDto.success();
	}
	
	// 장바구니 삭제 - 채은
	@Override
	public ResponseEntity<ResponseDto> deleteCarts(String memberId, List<Long> cartIds) {
		try {
			Long memberUID = memberRepository.getMemberIdById(memberId);
			for (Long cartId : cartIds) {
				String dbMemberId = cartRepository.getMemberIdbyCartId(cartId);
			
				if (!memberUID.equals(dbMemberId)) {
					return ResponseDto.certificateFail();
				}
				
				cartRepository.deleteCart(memberUID, cartId);
			}
		} catch (Exception e){
			e.printStackTrace();
			return ResponseDto.databaseError();
		}
		return ResponseDto.success();
	}
	
}
