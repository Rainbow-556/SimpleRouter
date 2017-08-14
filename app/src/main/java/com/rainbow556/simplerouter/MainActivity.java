package com.rainbow556.simplerouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rainbow556.annotation.Route;
import com.rainbow556.router_api.InterceptorChain;
import com.rainbow556.router_api.InterruptionInfo;
import com.rainbow556.router_api.OnQueryRouteEntryCallback;
import com.rainbow556.router_api.RouteEntry;
import com.rainbow556.router_api.RouteInterceptor;
import com.rainbow556.router_api.SimpleRouter;

/**
 * Created by lixiang on 2017/8/12.
 */
@Route({"/MainActivity", "/Home"})
public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleRouter.getInstance().addRouteInterceptor(new RouteInterceptor(){
            @Override
            public void onIntercept(RouteEntry entry, InterceptorChain chain){
                String threadName = Thread.currentThread().getName();
                Log.e("lx", threadName + "-> Interceptor_log: " + entry.getTriggerUrl() + " = " + entry.getTarget().getName());
                if(false){
                    chain.interruptChain(new InterruptionInfo("not login"));
                }else{
                    chain.continueChain(entry);
                }
            }
        });
    }

    public void open(View view){
        String url = "route://com.simplerouter.app/SecondActivity";
        SimpleRouter.getInstance().queryRouteEntry(url, new OnQueryRouteEntryCallback(){
            @Override
            public void onQueryRouteEntrySuccess(RouteEntry entry){
                Log.e("lx", "query success: " + entry.getTriggerUrl() + " = " + entry.getTarget().getName());
                startActivity(new Intent(MainActivity.this, entry.getTarget()));
            }

            @Override
            public void onQueryRouteEntryFail(InterruptionInfo info){
                Log.e("lx", "query fail: " + info.getMsg());
            }
        });
    }
}
