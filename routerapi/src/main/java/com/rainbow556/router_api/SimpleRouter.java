package com.rainbow556.router_api;

import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Created by lixiang on 2017/8/12.
 */
public final class SimpleRouter{
    private static SimpleRouter sInstance;
    private List<RouteEntry> mRouteEntries;

    private SimpleRouter(){
        try{
            Class clazz = Class.forName("com.rainbow556.RouteEntryLoader");
            List<RouteEntry> list = (List<RouteEntry>) clazz.getMethod("getRouteEntries").invoke(null);
            mRouteEntries = Collections.unmodifiableList(list);
            printAllRoute();
        }catch(Exception e){
            Log.e("lx", "init err!!!");
            e.printStackTrace();
        }
    }

    public static SimpleRouter getInstance(){
        if(sInstance == null){
            synchronized(SimpleRouter.class){
                if(sInstance == null){
                    sInstance = new SimpleRouter();
                }
            }
        }
        return sInstance;
    }

    /***
     * @param url
     * @param callback 可能在子线程中回调
     */
    public void queryRouteEntry(String url, OnQueryRouteEntryCallback callback){
        if(TextUtils.isEmpty(url)){
            callback.onQueryRouteEntryFail(new InterruptionInfo("url is null"));
            return;
        }
        for(RouteEntry entry : mRouteEntries){
            if(entry.match(url)){
                entry.setInterruptionInfo(null);
                entry.setTriggerUrl(url);
                RouteInterceptorService.getInstance().doInterceptions(entry, callback);
                return;
            }
        }
        callback.onQueryRouteEntryFail(new InterruptionInfo("route not found"));
    }

    public void addRouteInterceptor(RouteInterceptor interceptor){
        RouteInterceptorService.getInstance().addInterceptor(interceptor);
    }

    public void printAllRoute(){
        Log.d("lx", "------------");
        for(RouteEntry entry : mRouteEntries){
            Log.e("lx", entry.toString());
        }
        Log.d("lx", "------------");
    }
}
