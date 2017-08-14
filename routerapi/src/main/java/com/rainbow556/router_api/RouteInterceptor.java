package com.rainbow556.router_api;

/**
 * Created by lixiang on 2017/8/12.
 */
public abstract class RouteInterceptor{
    private boolean init;

    public abstract void onIntercept(RouteEntry entry, InterceptorChain chain);

    public void init(){
        init = true;
    }

    public boolean isInit(){
        return init;
    }
}
