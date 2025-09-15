package com.imfine.ngs.cart.entity;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<Game> items = new ArrayList<>();

    public void add(Game game){
        if (items.contains(game)) {
            throw new IllegalArgumentException("이미 장바구니에 추가된 게임입니다.");
        }
        items.add(game);
    }

    public void remove(Game game) {
        items.remove(game);
    }

    public int getItemCount(){
        return items.size();
    }
}
