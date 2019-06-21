package com.example.caresses_pepper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import com.example.caresses_pepper.utlis.StickerUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var proximityObserver: ProximityObserver
    private var proximityObservationHandler: ProximityObserver.Handler? = null
    private lateinit var myStickerService :StickerService
    private var boundService = false
    private var foundObject:List<String>? = null
    //lateinit var foundObject: List<String>

    // Could credentials found from https://cloud.estimote.com/
    private val cloudCredentials =
        EstimoteCloudCredentials("laboratorium-dibris-gmail--kfg", "90e1b9d8344624e9c2cd42b9f5fd6392")


    private val ip = "130.251.13.109"
    private val port = 8080
    lateinit var currentZone: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // bind sticker service
        Intent(this, StickerService::class.java).also { intent ->
            bindService(intent, stickerConnection, Context.BIND_AUTO_CREATE)}


        //Requirements check for estimote
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons","onRequirementsFulfilled")
                startProximityObservation()
            },
            onRequirementsMissing = {},
            onError = {}
        )
    }

    override fun onDestroy() {
        proximityObservationHandler?.stop()
        super.onDestroy()
        //if implement the onStartCommand() callback method, must explicitly stop the service,
        val stickerServiceIntent = Intent(this, StickerService::class.java)
        stopService(stickerServiceIntent)
    }

        private fun startProximityObservation() {
        proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withTelemetryReportingDisabled() //Added this to reduce the bluetooth call back traffic which was giving an error " Closed Bluetooth Low Energy scan failed with error code: 2"
            .withAnalyticsReportingDisabled() //Similarly this
            .withBalancedPowerMode() //Similarly this
            .withEstimoteSecureMonitoringDisabled()
            .build()


        // start proximity
            val chairZone = ProximityZoneBuilder()
                .forTag("lemon")
                .inCustomRange(1.0)
                .onEnter {
                    currentZone = "chair"
                    val client = ClientSocket(ip, port, currentZone)
                    //get sticker result and start client socket
                    //foundObject = myStickerService.objectName
                    //val sendMeg2 = "Person: chair, Object: $foundObject "

                    // send person and object location
                    client.openClient()
                    //tv_location.text = sendMeg
                    Log.d("main","chair")



                }
                .onExit {
                    Log.d("main","leftchair")

                }
                .build()

            val tableZone = ProximityZoneBuilder()
                .forTag("candy")
                .inCustomRange(1.0)
                .onEnter {
                    currentZone = "table"
                    val client = ClientSocket(ip, port, currentZone)
                    client.openClient()
                    // get sticker result and start client socket
                    //foundObject = myStickerService.objectName
                    //val sendMeg = "Person: table, Object: $foundObject "
                    Log.d("main","table")
                    //tv_location.text = sendMeg


                }
                .onExit {
                    Log.d("main","lefttable")

                }
                .build()






            //proximityObservationHandler = proximityObserver.startObserving(pinkBeacon, purpleBeacon, yellowBeacon)
        proximityObservationHandler = proximityObserver.startObserving(chairZone,tableZone)

    }
    private val stickerConnection = object : ServiceConnection {
        // the client use IBinder to communicate with the bound service.
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to TryServerService, cast the IBinder and get TryServerService instance
            val socketBinder = service as StickerService.StickerBinder
            myStickerService = socketBinder.getService()

            Log.d("bindservice","myStickerService")

            boundService = true

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = false
        }
    }


}
