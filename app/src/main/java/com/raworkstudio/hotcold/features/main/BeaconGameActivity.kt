package com.raworkstudio.hotcold.features.main

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

class BeaconGameActivity : AppCompatActivity(), BeaconGameContract.View {


    private lateinit var presenter: BeaconGamePresenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_game)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        presenter = BeaconGamePresenter(this)


        beacon_game_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked){
                true -> beacon_game_start_button.isEnabled = true
                false -> beacon_game_start_button.isEnabled = false
            }
        }

        beacon_game_start_button.setOnClickListener { v: View ->
            Snackbar.make(v, "Button clicked!", Snackbar.LENGTH_SHORT)
                    .show()
        }

    }



    override fun onStart() {
        super.onStart()
        presenter.startScanning()
    }


    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(reason: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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