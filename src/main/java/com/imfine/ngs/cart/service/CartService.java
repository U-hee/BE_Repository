package com.imfine.ngs.cart.service;


import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;

public class CartService {

    public void addGame(Cart cart, Game game){
        cart.add(game);
    }
}
