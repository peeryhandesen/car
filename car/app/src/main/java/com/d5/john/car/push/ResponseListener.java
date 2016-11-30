package com.d5.john.car.push;

/**
 * Created by zhouyi on 2015/11/19 14:35.
 */
public interface ResponseListener<T> {

     public void responseSuccess(T t);

     public void responseFalse(T t);

}
