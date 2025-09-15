package com.imfine.ngs.cart.entity;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<Game> items = new ArrayList<>();

    public void add(Game game){
        items.add(game);
    }

    public void remove(Game game) {
        items.remove(game);
    }

    public int getItemCount(){
        return items.size();
    }
}
