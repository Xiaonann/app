package com.example.caresses_pepper.utlis

import android.util.Log
import com.estimote.proximity_sdk.api.ProximityZone
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import com.example.caresses_pepper.data.Beacon
import java.util.ArrayList


object BeaconUtils {
    interface BeaconListener{
        fun onEnterZone(definedZone: String)
        fun onExitZone(definedZone: String)

    }
    private val defaultRange = 1.0
    val beaconZones = ArrayList<ProximityZone>()
    var listener: BeaconListener? = null
    val beacons = arrayListOf(
        Beacon("purple2","table"),
        Beacon("candy","door"),
        Beacon("lemon","sofa")

    )

    //private var lastBeaconDate: Date? = null

    init {
        for (beacon in beacons) {
            beaconZones.add(
                ProximityZoneBuilder()
                    .forTag(beacon.tag)
                    .inCustomRange(2.0)
                    .onEnter {
                        Log.d("Beacons", "Enter")
                        /*if(lastBeaconDate == null || Date().time - lastBeaconDate!!.time < 5000){
                            lastBeaconDate = Date()
                            listener?.onEnterZone(zone)
                        }*/
                        listener?.onEnterZone(beacon.definedZone)
                    }
                    .onExit {
                        Log.d("Beacons", "Exit")
                        listener?.onExitZone(beacon.definedZone)
                    }
                    .build())

        }
    }
}