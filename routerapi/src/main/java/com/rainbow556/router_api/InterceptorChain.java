package com.rainbow556.router_api;

/**
 * Created by lixiang on 2017/8/13.
 */
public interface InterceptorChain{
    void continueChain(RouteEntry entry);

    void interruptChain(InterruptionInfo info);
}
