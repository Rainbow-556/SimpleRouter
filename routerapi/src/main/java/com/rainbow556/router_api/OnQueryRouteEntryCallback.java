package com.rainbow556.router_api;

/**
 * Created by lixiang on 2017/8/13.
 */
public interface OnQueryRouteEntryCallback{
    void onQueryRouteEntrySuccess(RouteEntry entry);

    void onQueryRouteEntryFail(InterruptionInfo info);
}
