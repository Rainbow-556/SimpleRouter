package com.rainbow556.router_api;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 2017/8/12.
 */
public final class RouteEntry{
    private List<String> mUrls = new ArrayList<>(1);
    private String mTriggerUrl;
    private Class mTarget;
    private InterruptionInfo mInterruptionInfo;

    public void addUrl(String url){
        if(TextUtils.isEmpty(url) || mUrls.contains(url)){
            return;
        }
        mUrls.add(url);
    }

    public boolean match(String url){
        return mUrls.contains(url);
    }

    public void setTarget(Class target){
        mTarget = target;
    }

    public Class getTarget(){
        return mTarget;
    }

    public void setTriggerUrl(String url){
        mTriggerUrl = url;
    }

    public String getTriggerUrl(){
        return mTriggerUrl;
    }

    public InterruptionInfo getInterruptionInfo(){
        return mInterruptionInfo;
    }

    public void setInterruptionInfo(InterruptionInfo interruptionInfo){
        this.mInterruptionInfo = interruptionInfo;
    }

    @Override
    public String toString(){
        String target = mTarget != null ? mTarget.getName() : "mTarget is null";
        return target + " = " + mUrls;
    }
}
