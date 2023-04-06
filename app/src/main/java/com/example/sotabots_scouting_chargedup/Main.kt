package com.example.sotabots_scouting_chargedup

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sotabots_scouting_chargedup.R.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Stack


class Main : Activity() {
    lateinit var data: MutableMap<String, Any>
    var pose: Int = 0
    lateinit var poseTXT: String
    lateinit var color: Drawable
    lateinit var prevChange: Stack<View>
    var dataBase: FirebaseDatabase = Firebase.database("https://sotabots-scouting-2023-default-rtdb.firebaseio.com/")
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        data = mutableMapOf()
        prevChange = Stack()
        dataBase = Firebase.database("https://sotabots-scouting-2023-default-rtdb.firebaseio.com/")
        dataBase.setPersistenceEnabled(true)
    hideSystemUI()
    }
    // Function to hide NavigationBar
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(R.id.content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    fun onNewStart(){
        setContentView(layout.start)
        hideSystemUI()
        findViewById<Button>(id.start).setOnClickListener() {x -> setPoseView()}
        data.clear()
        prevChange = Stack()

    }
    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }




    fun initializeData(){
        if(findViewById<TextView>(id.setTeamNumber).text.toString() == "" ||
            findViewById<TextView>(id.setMatchNumber).text.toString() == "") return
        data.clear()
        data["Pose"] = this.pose
        data["Team"] = findViewById<TextView>(id.setTeamNumber).text.toString().toInt()
        data["match"] = findViewById<TextView>(id.setMatchNumber).text.toString().toInt()

        setAuto()
    }
    fun setData(view: View) {

        var button: Button = findViewById(view.id)
        data.putIfAbsent(view.tag.toString(), 0)
        data.compute(view.tag.toString()) { k , v ->
            return@compute if(v != null) v.toString().toInt() + 1 else 1
        }
        var text = button.hint.toString() + data[view.tag.toString()].toString()
        button.text = text
        prevChange.add(view)

    }
    fun chargeStation(view: View){
        var x = view.tag.toString()
        var y = if (x == "auto") "autoChargeStation" else "teleChargeStation"
        if (x == "auto") {
            when (view.id) {
                id.autoChargeOff -> {
                    data.putIfAbsent(y, 0)
                    data[y] = "Off" }
                id.autoChargeEngaged -> {
                    data.putIfAbsent(y, 1)
                    data[y] = "Engaged" }
                id.autoChargeDocked -> {
                    data.putIfAbsent(y, 2)
                    data[y] = "Docked" } }
        }else{
            when (view.id) {
                id.teleChargeOff -> {
                    data.putIfAbsent(y, 0)
                    data[y] = "Off" }
                id.teleChargeEngaged -> {
                    data.putIfAbsent(y, 1)
                    data[y] = "Engaged" }
                id.teleChargeDocked -> {
                    data.putIfAbsent(y, 2)
                    data[y] = "Docked" } }
        }
    }

    fun undo(){
        if(prevChange.empty()) {
            initializeView()
            return
        }
        var view = prevChange.pop()
        data.compute(view.tag.toString()) {k, v ->
            return@compute if (v != null) {v.toString().toInt() - 1 } else 0
        }
        try{
            setTele()
            val x = findViewById<Button>(view.id)
            x.text = x.hint.toString() + data[x.tag.toString()].toString()
        }catch(e: NullPointerException){
            setAuto()
        }

    }

    fun setPoseView(){
        setContentView(layout.setpose)
        hideSystemUI()
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
        hideSystemUI()
        var showPose = findViewById<TextView>(id.displayPose)
        showPose.background = color
        showPose.text = poseTXT

        findViewById<Button>(id.setPose).setOnClickListener() {setPoseView()}

        findViewById<Button>(id.startDataBT).setOnClickListener() {initializeData()}
    }

    fun setAuto(){
        setContentView(layout.auto)
        hideSystemUI()
        findViewById<Button>(id.teleopbt).setOnClickListener() {setTele()}
        findViewById<Button>(id.autoUndobt).setOnClickListener() {undo()}
        findViewById<CheckBox>(id.checkBox).setOnClickListener() {
            data.putIfAbsent(it.tag.toString(), 0)
            data.compute(it.tag.toString()) { k, v -> return@compute if (v == 1) 0 else 1 }
        }
        val z = findViewById<CheckBox>(id.checkBox)
        if(data.get(z.tag.toString()) == 1){
            z.setChecked(true)
        }

        var touchables = listOf<Button>(
            findViewById<Button>(id.autoConeLowbt),
            findViewById(id.autoConeHighbt),
            findViewById(id.autoCubelowbt),
            findViewById(id.autoConeMiddlebt),
            findViewById(id.autoCubeHighbt),
            findViewById(id.autoCubeMiddle),
            findViewById(id.autoFoul)
        )

        touchables.forEach(){
            val x = findViewById<Button>(it.id)
            x.text = x.hint.toString() + if(data[x.tag.toString()] == null) 0 else data[x.tag.toString()]
            x.setOnClickListener(){
                setData(x)
                x.text = x.hint.toString() + data[x.tag.toString()]
            }
        }
        var radio = listOf<View>(
            findViewById<RadioButton>(id.autoChargeOff),
            findViewById<RadioButton>(id.autoChargeDocked),
            findViewById<RadioButton>(id.autoChargeEngaged)
        )

        radio.forEach() {
            it.setOnClickListener() {
                val x = findViewById<RadioButton>(it.id)
                chargeStation(x)
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun setTele(){
        setContentView(layout.teleop)
        hideSystemUI()
        findViewById<Button>(id.teleUndoBt).setOnClickListener() {undo()}
        findViewById<Button>(id.teleSetAutobt).setOnClickListener() {setAuto()}
        findViewById<Button>(id.Finished).setOnClickListener() {
            finalDataView()
        }
        var touchables = listOf<View>(
            findViewById<Button>(id.teleConeHighBt),
            findViewById<Button>(id.teleConeLowBt),
            findViewById<Button>(id.teleConeMiddlebt),
            findViewById<Button>(id.teleCubeMiddlebt),
            findViewById<Button>(id.teleCubeLowbt),
            findViewById<Button>(id.teleCubeHighbt),
            findViewById<Button>(id.teleFoul)
        )

        touchables.forEach(){
            val x = findViewById<Button>(it.id)
            x.text = x.hint.toString() + if(data[x.tag.toString()] == null) 0 else data[x.tag.toString()]
            x.setOnClickListener(){
                setData(x)
                x.text = x.hint.toString() + data[x.tag.toString()]
            }
        }
        var radio = listOf<View>(
            findViewById<RadioButton>(id.teleChargeOff),
            findViewById<RadioButton>(id.teleChargeDocked),
            findViewById<RadioButton>(id.teleChargeEngaged)
        )
        radio.forEach(){
            it.setOnClickListener() {
                val x = findViewById<RadioButton>(it.id)
                chargeStation(x)
            }
        }
    }

    fun finalData(){
        data["Notes"] = findViewById<EditText>(id.notes).text.toString()
        data["Strategy"] = findViewById<SeekBar>(id.strategyRateBar).progress
        data["Defense"] = findViewById<SeekBar>(id.defenseRateBar).progress
        data["Scoring"] = findViewById<SeekBar>(id.scoringRateBar).progress
        gameStatusView()
    }
    fun gameStatusView(){
        setContentView(layout.gamestatus)
        hideSystemUI()
        findViewById<Button>(id.win).setOnClickListener() { finish(it) }
        findViewById<Button>(id.tie).setOnClickListener() { finish(it) }
        findViewById<Button>(id.lose).setOnClickListener() { finish(it) }
    }
    private fun finalDataView(){
        setContentView(layout.finaldata)
        hideSystemUI()
        findViewById<Button>(id.FinalBackButton).setOnClickListener() {setTele()}
        findViewById<Button>(id.finalNextButton).setOnClickListener() { finalData() }
    }
    fun finish(view: View){
        var status = view.tag.toString()
        when (status) {
            "win" -> data["gameStatus"] = "win"
            "tie" -> data["gameStatus"] = "tie"
            "lose" -> data["gameStatus"] = "lose"
        }
        publishData()
        onNewStart()
    }

    fun publishData(){
        var allValues = listOf<String>(
            "Pose",
            "Team",
            "match",
            "autoChargeStation",
            "autoConeHigh",
            "autoConeMiddle",
            "autoConeLow",
            "autoCubeHigh",
            "autoCubeMiddle",
            "autoCubeLow" ,
            "autoFoul",
            "Mobility",
            "teleChargeStation",
            "teleConeHigh",
            "teleConeMiddle",
            "teleConeLow",
            "teleCubeHigh",
            "teleCubeMiddle",
            "teleCubeLow",
            "teleFoul",
            "Notes",
            "Strategy",
            "Defense",
            "Scoring",
            "gameStatus"
        )
        for (term in allValues) {
            data.putIfAbsent(term, 0)
        }
        var ref = dataBase.getReference(
            "Teams/" + data["Team"].toString()
                    + "/" + "Matches/"  + data["match"])
        ref.setValue(data)

    }


}