package com.zzh.orderingsystem;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class MyViewModel extends ViewModel {
    private MutableLiveData<Map<String, Integer>> cart;
    public Map<String, Integer> raw_cart;
    public MyViewModel() {
        raw_cart = new HashMap<>();
        this.cart = new MutableLiveData<>();
        this.cart.postValue(raw_cart);
        Log.d("huashi", "rawcart is null:"+String.valueOf(raw_cart==null));
    }

    public MutableLiveData<Map<String, Integer>> getCart() {
        return cart;
    }

    public void addFood(foods food){
        if(this.raw_cart.containsKey(food.food_name)){
            Integer num = raw_cart.get(food.food_name);
            raw_cart.put(food.food_name, num+1);
        }
        else {
            raw_cart.put(food.food_name, 1);
        }
        this.cart.postValue(raw_cart);
    }
}
