package com.rainbow556.router_api;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 90326 on 2017/8/13.
 */
public class CancelableCountDownLatch extends CountDownLatch{
    public CancelableCountDownLatch(int count){
        super(count);
    }

    public void cancel(){
        while(getCount() > 0){
            countDown();
        }
    }
}
