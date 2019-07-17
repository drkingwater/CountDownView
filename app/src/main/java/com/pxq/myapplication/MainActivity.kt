package com.pxq.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pxq.myapplication.widget.CountDownView

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val countDownView = findViewById<CountDownView>(R.id.count_down)
        countDownView.setCount(10)
    }

}