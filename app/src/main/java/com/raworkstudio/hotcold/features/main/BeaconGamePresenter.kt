package com.raworkstudio.hotcold.features.main

import android.content.res.Resources
import android.util.Log
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration
import com.kontakt.sdk.android.ble.configuration.ScanMode
import com.kontakt.sdk.android.ble.configuration.ScanMode.*
import com.kontakt.sdk.android.ble.configuration.ScanPeriod
import com.kontakt.sdk.android.ble.configuration.ScanPeriod.*
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener
import com.kontakt.sdk.android.common.KontaktSDK
import com.raworkstudio.hotcold.R
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace
import com.kontakt.sdk.android.common.profile.IEddystoneDevice
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener
import com.kontakt.sdk.android.ble.rssi.RssiCalculators
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType
import java.util.*
import java.util.concurrent.TimeUnit
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleScanStatusListener
import com.kontakt.sdk.android.ble.manager.listeners.ScanStatusListener
import android.speech.tts.TextToSpeech






/**
 * Created by Ivan on 6/4/2017.
 */
class BeaconGamePresenter(val view: BeaconGameContract.View) : BeaconGameContract.Presenter {

    private var proximityManager: ProximityManager
    private val ttobj: TextToSpeech

    init {
        KontaktSDK.initialize(Resources.getSystem().getString(R.string.Kontakt_api_key))

        ttobj = TextToSpeech(view.getActivity(), TextToSpeech.OnInitListener { status ->
            Log.d(BeaconGameActivity::class.java.simpleName,
                    "Status $status")
        })
        ttobj.language = Locale.US

        proximityManager = ProximityManagerFactory.create(view.getActivity())
        configureProximityManager()
        proximityManager.setEddystoneListener(createEddystoneListener())
        proximityManager.setScanStatusListener(createScanStatusListener())
    }

    private fun configureProximityManager() {
        proximityManager.configuration()
                .scanMode(BALANCED)
                .scanPeriod(RANGING)
                .activityCheckConfiguration(ActivityCheckConfiguration.DISABLED)
                .forceScanConfiguration(ForceScanConfiguration.DISABLED)
                .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(2))
                .rssiCalculator(RssiCalculators.DEFAULT)
                .cacheFileName("Example")
                .resolveShuffledInterval(3)
                .monitoringEnabled(true)
                .monitoringSyncInterval(10)
                .eddystoneFrameTypes(Arrays.asList(EddystoneFrameType.UID, EddystoneFrameType.URL))
    }

    private fun createEddystoneListener(): EddystoneListener {
        return object: EddystoneListener {
            override fun onEddystonesUpdated(eddystone: MutableList<IEddystoneDevice>?, p1: IEddystoneNamespace?) {
                eddystone
                        ?.filter { it -> it.uniqueId == "SpHa" }
                        ?.forEach { beacon ->
                            Log.d(BeaconGameActivity::class.java.simpleName,
                                    "${beacon.uniqueId} | distance: ${beacon.distance} | proximity: ${beacon.proximity}")

                            if(beacon.distance > 1.85) {
                                ttobj.speak("You're far away, cold as shit", TextToSpeech.QUEUE_FLUSH, null)
                            }
                            else if (beacon.distance < 1.85 && beacon.distance > 0.48 ) {
                                ttobj.speak("You're getting warmer", TextToSpeech.QUEUE_FLUSH, null)
                            }
                            else if( beacon.distance < 0.48) {
                                ttobj.speak("You're Burning!!!", TextToSpeech.QUEUE_FLUSH, null)
                            }


                        }
            }

            override fun onEddystoneDiscovered(eddystone: IEddystoneDevice?, p1: IEddystoneNamespace?) {
                Log.d(BeaconGameActivity::class.java.simpleName, "Eddystone discovered: " + eddystone!!.toString())
            }

            override fun onEddystoneLost(eddystone: IEddystoneDevice?, p1: IEddystoneNamespace?) {
                Log.d(BeaconGameActivity::class.java.simpleName, "${eddystone?.uniqueId} lost")
            }
        }

    }

    private fun createScanStatusListener(): ScanStatusListener {
        return object : SimpleScanStatusListener() {
            override fun onScanStart() {
                view.showMessage("Scanning started")
            }

            override fun onScanStop() {
                view.showMessage("Scanning stopped")
            }
        }
    }


    override fun startScanning() {
        proximityManager.connect {
            OnServiceReadyListener {
                proximityManager.startScanning()
                Log.d(BeaconGameActivity::class.java.simpleName, "Scanning started: " + proximityManager.isScanning)
            }
        }
    }


    override fun start() {

        if (proximityManager.isScanning) {
            proximityManager.stopScanning()
            view.showMessage("Finished")
            return
        }

        if (proximityManager.isConnected) {
            proximityManager.startScanning()
        }
    }

    override fun onStop() {
        proximityManager.let { it ->
            it.stopScanning()
        }
    }

    override fun askPermissions() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        proximityManager.let { it -> {
            it.disconnect()
        } }
    }
}
