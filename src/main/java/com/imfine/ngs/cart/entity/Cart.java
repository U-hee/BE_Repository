package com.imfine.ngs.cart.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cart {

    List<Game> items = new ArrayList<>();

    public void add(Game game){
        if (items.contains(game)) {
            throw new IllegalArgumentException("이미 장바구니에 추가된 게임입니다.");
        }
        items.add(game);
    }

    public void remove(Game game) {
        if(!items.contains(game)){
            throw new IllegalArgumentException("장바구니에 없는 게임입니다.");
        }
        items.remove(game);
    }

    public int getItemCount(){
        return items.size();
    }
}
