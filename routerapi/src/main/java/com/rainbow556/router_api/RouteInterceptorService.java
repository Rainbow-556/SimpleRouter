package com.rainbow556.router_api;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixiang on 2017/8/12.
 */
public final class RouteInterceptorService{
    private static RouteInterceptorService sInstance;
    private List<RouteInterceptor> mInterceptors = new ArrayList<>();
    private boolean runOnThreadPool = true;

    private RouteInterceptorService(){}

    public static RouteInterceptorService getInstance(){
        if(sInstance == null){
            synchronized(RouteInterceptorService.class){
                if(sInstance == null){
                    sInstance = new RouteInterceptorService();
                }
            }
        }
        return sInstance;
    }

    public void addInterceptor(RouteInterceptor interceptor){
        if(interceptor == null){
            return;
        }
        if(!mInterceptors.contains(interceptor)){
            mInterceptors.add(interceptor);
        }
    }

    public void doInterceptions(final RouteEntry entry, OnQueryRouteEntryCallback callback){
        if(mInterceptors.isEmpty()){
            callback.onQueryRouteEntrySuccess(entry);
            return;
        }
        if(runOnThreadPool){
            doInterceptionsOnThreadPool(entry, callback);
        }else{
            doInterceptionsOnCurrentThread(entry, callback);
        }
    }

    private void doInterceptionsOnThreadPool(final RouteEntry entry, final OnQueryRouteEntryCallback callback){
        Runnable runnable = new Runnable(){
            @Override
            public void run(){
                CancelableCountDownLatch latch = new CancelableCountDownLatch(mInterceptors.size());
                try{
                    doInterceptionsInner(0, entry, latch);
                    //防止interceptor中没调用continueChain() or interruptChain()
                    latch.await(200, TimeUnit.MILLISECONDS);
                    if(latch.getCount() > 0){
                        String msg = "interceptor not call InterceptorChain.continueChain() or interruptChain()";
                        callback.onQueryRouteEntryFail(new InterruptionInfo(msg));
                    }else if(entry.getInterruptionInfo() != null){
                        callback.onQueryRouteEntryFail(entry.getInterruptionInfo());
                    }else{
                        callback.onQueryRouteEntrySuccess(entry);
                    }
                }catch(Exception e){
                    callback.onQueryRouteEntryFail(new InterruptionInfo(e.getMessage()));
                }
            }
        };
        AsyncTask.THREAD_POOL_EXECUTOR.execute(runnable);
    }

    private void doInterceptionsOnCurrentThread(final RouteEntry entry, OnQueryRouteEntryCallback callback){
        try{
            doInterceptionsInner(0, entry, null);
        }catch(Exception e){
            entry.setInterruptionInfo(new InterruptionInfo(e.getMessage()));
        }
        if(entry.getInterruptionInfo() == null){
            callback.onQueryRouteEntrySuccess(entry);
        }else{
            callback.onQueryRouteEntryFail(entry.getInterruptionInfo());
        }
    }

    private void doInterceptionsInner(final int index, final RouteEntry entry, final CancelableCountDownLatch latch){
        if(index > mInterceptors.size() - 1){
            return;
        }
        RouteInterceptor interceptor = mInterceptors.get(index);
        if(!interceptor.isInit()){
            interceptor.init();
        }
        interceptor.onIntercept(entry, new InterceptorChain(){
            @Override
            public void continueChain(RouteEntry entry){
                if(latch != null){
                    latch.countDown();
                }
                doInterceptionsInner(index + 1, entry, latch);
            }

            @Override
            public void interruptChain(InterruptionInfo info){
                if(latch != null){
                    latch.cancel();
                }
                entry.setInterruptionInfo(info);
            }
        });
    }
}
