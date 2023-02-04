package com.example.sotabots_scouting_chargedup

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.CalendarContract.Calendars
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.sotabots_scouting_chargedup.R.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Stack


class Main : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()

    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        start()
    }

    fun start(){
        setContentView(layout.start)
        findViewById<Button>(id.start).setOnClickListener() {x -> setPoseView()}
        dataBase.setPersistenceEnabled(true)
    }

    override fun onResume() {
        super.onResume()
    }

    var data = mutableMapOf<String, Int>()
    var pose = 0
    var poseTXT =""
    lateinit var color: Drawable
    var prevChange: Stack<View> = Stack()
    var dataBase = Firebase.database("https://sotabots-scouting-2023-default-rtdb.firebaseio.com/")


    fun initializeData(){
        if(findViewById<TextView>(id.setTeamNumber).text.toString().toInt() == null ||
            findViewById<TextView>(id.setMatchNumber).text.toString().toInt() == null) return
        data.clear()
        data["Pose"] = this.pose
        data["Team"] = findViewById<TextView>(id.setTeamNumber).text.toString().toInt()
        data["match"] = findViewById<TextView>(id.setMatchNumber).text.toString().toInt()

        setAuto()
    }
    fun setData(view: View){

        var button: Button = findViewById(view.id)
        data.putIfAbsent(view.tag.toString(), 0)
        data.compute(view.tag.toString()) { k , v ->
            return@compute if(v != null) v + 1 else 1
        }
        var text = button.hint.toString() + data[view.tag.toString()].toString()
        button.text = text
        prevChange.add(view)

    }

    fun undo(){
        if(prevChange.empty()) {
            initializeView()
            return
        }
        var view = prevChange.pop()
        data.compute(view.tag.toString()) {k, v ->
            if (v != null) {
                return@compute v - 1
            }
            return@compute 0
        }

        try{
            setTele()
            val x = findViewById<Button>(view.id)
            x.text = x.hint.toString() + data[x.id.toString()]
        }catch(e: NullPointerException){
            setAuto()
        }

    }



    fun setPoseView(){
        setContentView(layout.setpose)
        var buttonList =  listOf<View>(
        findViewById<Button>(id.red1),
        findViewById<Button>(id.red2),
        findViewById<Button>(id.red3),
        findViewById<Button>(id.blue1),
        findViewById<Button>(id.blue2),
        findViewById(id.blue3))

        buttonList.forEach() {z -> z.setOnClickListener() {x -> setPose(x)}}

    }

    fun setPose(view: View){
        var button = findViewById<Button>(view.id)
        this.pose = button.text.toString().toCharArray().lastIndex.toInt()
        color = button.background
        this.poseTXT = button.text.toString()
        initializeView()


    }

    fun initializeView(){
        setContentView(layout.initialize)
        var showPose = findViewById<TextView>(id.displayPose)
        showPose.background = color
        showPose.text = poseTXT

        findViewById<Button>(id.setPose).setOnClickListener() {setPoseView()}

        findViewById<Button>(id.startDataBT).setOnClickListener() {initializeData()}
    }
    fun setAuto(){
        setContentView(layout.auto)
        findViewById<Button>(id.teleopbt).setOnClickListener() {setTele()}
        findViewById<Button>(id.autoUndobt).setOnClickListener() {undo()}
        var touchables = listOf<Button>(
            findViewById<Button>(id.autoConeLowbt),
            findViewById(id.autoConeHighbt),
            findViewById(id.autoCubelowbt),
            findViewById(id.autoConeMiddlebt),
            findViewById(id.autoCubeHighbt),
            findViewById(id.autoCubeMiddle),
            findViewById(id.Foul)
        )

        touchables.forEach(){
            val x = findViewById<Button>(it.id)
            x.text = x.hint.toString() + if(data[x.tag.toString()] == null) 0 else data[x.tag.toString()]
            x.setOnClickListener(){
                setData(x)
                x.text = x.hint.toString() + data[x.tag.toString()]
        }
        }
    }
    fun setTele(){
        setContentView(layout.teleop)
        findViewById<Button>(id.teleUndoBt).setOnClickListener() {undo()}
        findViewById<Button>(id.teleSetAutobt).setOnClickListener() {setAuto()}
        findViewById<Button>(id.Finished).setOnClickListener() {
            publishData()
            initializeView()
        }
        var touchables = listOf<View>(
            findViewById<Button>(id.teleConeHighBt),
            findViewById(id.teleConeLowBt),
            findViewById(id.teleConeMiddlebt),
            findViewById(id.teleCubeMiddlebt),
            findViewById(id.teleCubeLowbt),
            findViewById(id.teleCubeHighbt)

        )

        touchables.forEach(){
            val x = findViewById<Button>(it.id)
            x.text = x.hint.toString() + if(data[x.tag.toString()] == null) 0 else data[x.tag.toString()]
            x.setOnClickListener(){
                setData(x)
                x.text = x.hint.toString() + data[x.tag.toString()]
            }
        }
    }

    fun publishData(){
        var ref = dataBase.getReference(
            "Teams/" + data["Team"].toString()
                    + "/" + "Match's/"  + data["match"])
        ref.setValue(data)

    }


}