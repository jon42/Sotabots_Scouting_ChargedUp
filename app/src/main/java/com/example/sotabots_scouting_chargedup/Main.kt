package com.example.sotabots_scouting_chargedup

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.sotabots_scouting_chargedup.R.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Stack


class Main : Activity() {
    lateinit var data: MutableMap<String, Int>
    var pose: Int = 0
    lateinit var poseTXT: String
    lateinit var color: Drawable
    lateinit var prevChange: Stack<View>
    lateinit var dataBase: FirebaseDatabase // = Firebase.database("https://sotabots-scouting-2023-default-rtdb.firebaseio.com/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()

    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        start()
    }

    fun start(){
//        setContentView(layout.start)
//        findViewById<Button>(id.start).setOnClickListener() {x -> setPoseView()}
        data = mutableMapOf()
        prevChange = Stack()
        dataBase = Firebase.database("https://sotabots-scouting-2023-default-rtdb.firebaseio.com/")
        dataBase.setPersistenceEnabled(true)
        setAuto()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }




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
    fun autoChargeStation(view: View){
        when (view.id) {
            id.autoChargeOff -> {data.putIfAbsent("autoChargeStation", 0)
                data["autoChargeStation"] = 0
            }
            id.autoChargeEngaged -> {data.putIfAbsent("autoChargeStation", 1)
                data["autoChargeStation"] = 1
            }
            id.autoChargeDocked -> {data.putIfAbsent("autoChargeStation", 2)
                data["autoChargeStation"] = 2
            }
        }
    }
    fun teleChargeStation(view: View){
        when (view.id) {
            id.teleChargeOff -> {data.putIfAbsent("teleChargeStation", 0)
                data["teleChargeStation"] = 0
            }
            id.teleChargeEngaged -> {data.putIfAbsent("teleChargeStation", 1)
                data["teleChargeStation"] = 1
            }
            id.teleChargeDocked -> {data.putIfAbsent("teleChargeStation", 2)
                data["teleChargeStation"] = 2
            }
        }
    }
    fun undo(){
        if(prevChange.empty()) {
            initializeView()
            return
        }
        var view = prevChange.pop()
        data.compute(view.tag.toString()) {k, v ->
            return@compute if (v != null) {v - 1 } else 0
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
        findViewById<CheckBox>(id.checkBox).setOnClickListener() {
            data.putIfAbsent(it.tag.toString(), 0)
            data.compute(it.tag.toString()) { k, v -> return@compute if (v == 1) 0 else 1 }
        }
        findViewById<RadioButton>(id.autoChargeOff).setOnClickListener() {autoChargeStation(it)}
        findViewById<RadioButton>(id.autoChargeDocked).setOnClickListener() {autoChargeStation(it)}
        findViewById<RadioButton>(id.autoChargeEngaged).setOnClickListener() {autoChargeStation(it)}


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
//            publishData()
//            initializeView()
            finalDataView()
        }
        var touchables = listOf<View>(
            findViewById<Button>(id.teleConeHighBt),
            findViewById(id.teleConeLowBt),
            findViewById(id.teleConeMiddlebt),
            findViewById(id.teleCubeMiddlebt),
            findViewById(id.teleCubeLowbt),
            findViewById(id.teleCubeHighbt)

        )
        findViewById<RadioButton>(id.teleChargeOff).setOnClickListener() {teleChargeStation(it)}
        findViewById<RadioButton>(id.teleChargeDocked).setOnClickListener() {teleChargeStation(it)}
        findViewById<RadioButton>(id.teleChargeEngaged).setOnClickListener() {teleChargeStation(it)}

        touchables.forEach(){
            val x = findViewById<Button>(it.id)
            x.text = x.hint.toString() + if(data[x.tag.toString()] == null) 0 else data[x.tag.toString()]
            x.setOnClickListener(){
                setData(x)
                x.text = x.hint.toString() + data[x.tag.toString()]
            }
        }
    }
    private fun finalDataView(){
        setContentView(R.layout.finaldata)
        var buttonOn = Drawable.createFromPath("button_shape_on.xml")
        var buttonOff = Drawable.createFromPath("button_shape_off.xml")
        findViewById<Button>(R.id.FinalBackButton).setOnClickListener() {setContentView(layout.teleop)}

        var engagedCharge = findViewById<Button>(id.TeleOpEngangedCharge)
        var onCharge = findViewById<Button>(id.TeleOpOnCharge)
        var noCharge = findViewById<Button>(id.TeleopNoCharge)
        var strategySlider = findViewById<SeekBar>(id.Strategy)

        data["teleCharge"] = 0
        noCharge.background = buttonOff

        noCharge.setOnClickListener(){
            data["teleCharge"] = 0
            it.background = buttonOff
            onCharge.background = buttonOn
            engagedCharge.background = buttonOn
        }
        onCharge.setOnClickListener() {
            data["teleCharge"] = 1
            it.background = buttonOff
            noCharge.background = buttonOn
            engagedCharge.background = buttonOn
        }
        engagedCharge.setOnClickListener(){
            data["teleCharge"] = 2
            it.background = buttonOff
            onCharge.background = buttonOn
            noCharge.background = buttonOn
        }
    }

    fun publishData(){
        var ref = dataBase.getReference(
            "Teams/" + data["Team"].toString()
                    + "/" + "Match's/"  + data["match"])
        ref.setValue(data)

    }


}