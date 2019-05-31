package com.example.androiddatadatabinding.Activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.androiddatadatabinding.R
import kotlinx.android.synthetic.main.activity_kotlin_experience.*

class KotlinExperience : AppCompatActivity(), SensorEventListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_experience)
        sensormanager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        listview = findViewById<ListView>(R.id.listView)
//        var dropwnValues: Array<String> = getResources().getStringArray(R.array.dropdown_fastadapter)
//        var arrt: ArrayAdapter<String> = ArrayAdapter(this,
//                android.R.layout.simple_spinner_item, android.R.id.text1, dropwnValues)
//        arrt.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
//        listview!!.adapter = arrt
//        listview!!.setOnItemClickListener() { parent, view, position, id ->
//        Toast.makeText(this, "Clicked item :" + " " + position, Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        var stepSensor = sensormanager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensormanager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_mlkit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_scan -> {
                Toast.makeText(this, "Steps is null", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensormanager?.unregisterListener(this)
    }

    companion object {
        //        private var listview: ListView? = null
//        private var textView: TextView? = null
        var running = false
        var sensormanager: SensorManager? = null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (running) {
            stepValue.setText("" + event.values[0])
            button2.setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
