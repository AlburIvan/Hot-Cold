package com.raworkstudio.hotcold.features.main

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.raworkstudio.hotcold.R

import kotlinx.android.synthetic.main.content_beacon_game.*
import android.webkit.PermissionRequest
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.READ_CONTACTS
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.raworkstudio.hotcold.utils.logTag
import kotlinx.android.synthetic.main.activity_beacon_game.*
import android.widget.Toast
import java.lang.Compiler.enable
import android.bluetooth.BluetoothAdapter
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.content.Intent
import android.provider.Settings


class BeaconGameActivity : AppCompatActivity(), BeaconGameContract.View {

    private lateinit var presenter: BeaconGamePresenter
    private val activity = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_game)

        setSupportActionBar(toolbar)

        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(logTag(), "Permissions granted!")
                    presenter = BeaconGamePresenter(activity)
                }
            }

            override fun onPermissionRationaleShouldBeShown(p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?, p1: PermissionToken?) {
                /* ... */
            }
        }).check()


        beacon_game_start_button.isEnabled = false
        beacon_game_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked){
                true -> turnOnHardware()
                false -> turnOffHardware()
            }
        }

        beacon_game_start_button.setOnClickListener { v: View ->
            presenter.start()
        }
    }



    private fun turnOnHardware(): Unit {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.enable()
        } else {
            Toast.makeText(applicationContext, "Bluetooth Already Enabled", Toast.LENGTH_LONG).show()
        }

        startActivity( Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        beacon_game_start_button.isEnabled = true
        beacon_game_start_button.setBackgroundColor( resources.getColor(android.R.color.holo_blue_light))
    }

    private fun turnOffHardware() {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.disable()
        }

        startActivity( Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        beacon_game_start_button.isEnabled = false
        beacon_game_start_button.setBackgroundColor( resources.getColor(android.R.color.darker_gray))
    }


    override fun onStart() {
        super.onStart()
        presenter.startScanning()
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(reason: String) {
        Toast.makeText(applicationContext, reason, Toast.LENGTH_LONG).show()
    }

    override fun getActivity(): Activity = this

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_beacon_game, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when(item.itemId) {
            R.id.action_settings -> return true
        }

        return super.onOptionsItemSelected(item)
    }
}