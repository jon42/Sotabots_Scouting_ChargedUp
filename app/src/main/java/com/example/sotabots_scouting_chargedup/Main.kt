package com.example.sotabots_scouting_chargedup

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button


class Main : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.start)
    }

    override fun onResume() {
        super.onResume()
    }
    var data = mutableMapOf<String, Int>()

    fun setData(view: View){
        data.putIfAbsent(view.id.toString(), 0)
        data.compute(view.id.toString()) { k , v ->
            return@compute if(v != null) v + 1 else 1
        }
    }

    fun configureButtonBindings(){
        Start.setOnClickListener(){x -> setData(x)}
    }

}