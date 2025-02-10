package com.example.myapp.cart.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.myapp.cart.model.Cart;

@Repository
@Mapper
public interface ICartRepository {

	List<Cart> getCarts(Long memberUID);
	void addCart(Long memberUID, String lectureId);
	void deleteCart(Long memberUID, Long cartId);
	String getMemberIdbyCartId(Long cartId);
}
