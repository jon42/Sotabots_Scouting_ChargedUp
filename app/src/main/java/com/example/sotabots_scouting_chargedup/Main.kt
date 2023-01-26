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
        configureButtonBindings()
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.start)
        configureButtonBindings()
    }


    override fun onResume() {
        super.onResume()
    }

    var data = mutableMapOf<String, Int>()



    fun initializeData(team: Int, match:Int){
        data["Team"] = team
        data["match"] = match
    }
    fun setData(view: View){

        var button: Button = findViewById(view.id)
        data.putIfAbsent(view.id.toString(), 0)
        data.compute(view.id.toString()) { k , v ->
            return@compute if(v != null) v + 1 else 1
        }
        var text = button.hint.toString() + data[view.id.toString()].toString()
        button.text = text

    }

    fun configureButtonBindings(){
        val start = findViewById<Button>(R.id.start)
        start.setOnClickListener(){x: View -> setPoseView()}

    }

    fun setPoseView(){
        setContentView(R.layout.setpose)
    }

}