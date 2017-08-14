package com.rainbow556.simplerouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rainbow556.annotation.Route;
/**
 * Created by lixiang on 2017/8/7.
 */
@Route("/SecondActivity")
public class SecondActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}