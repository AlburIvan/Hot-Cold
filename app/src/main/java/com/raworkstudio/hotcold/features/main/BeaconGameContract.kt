package com.raworkstudio.hotcold.features.main

import android.app.Activity

/**
 * Created by Ivan on 6/2/2017.
 */

interface BeaconGameContract {

    interface View {

        fun showProgress()

        fun hideProgress()

        fun showMessage(reason: String)

        fun getActivity(): Activity
    }

    interface Presenter {

        fun askPermissions()

        fun startScanning()

        fun onStop()

        fun onDestroy()

        fun start()
    }
}
