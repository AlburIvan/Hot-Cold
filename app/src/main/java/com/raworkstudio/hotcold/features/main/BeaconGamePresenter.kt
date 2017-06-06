package com.raworkstudio.hotcold.features.main

import android.content.res.Resources
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.common.KontaktSDK
import com.raworkstudio.hotcold.R

/**
 * Created by Ivan on 6/4/2017.
 */
class BeaconGamePresenter(val view: BeaconGameContract.View) : BeaconGameContract.Presenter {

    private var proximityManager: ProximityManager

    init {

        KontaktSDK.initialize(Resources.getSystem().getString(R.string.Kontakt_api_key))
        proximityManager = ProximityManagerFactory.create(view.getActivity())
    }


    override fun startScanning() {
        proximityManager.connect {
            OnServiceReadyListener {
                proximityManager.startScanning()
            }
        }
    }

    override fun onStop() {
        proximityManager?.let { it ->
            it.stopScanning()
        }
    }

    override fun askPermissions() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        proximityManager?.let { it -> {
            it.disconnect()
        } }
    }
}