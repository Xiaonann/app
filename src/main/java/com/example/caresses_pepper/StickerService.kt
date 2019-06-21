package com.example.caresses_pepper

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.estimote.internal_plugins_api.scanning.ScanHandler
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory
import com.example.caresses_pepper.utlis.StickerUtils

private const val tag = "sticker"
class StickerService : Service() {

    private var scanHandle: ScanHandler? = null
    var objectList = ArrayList<String>()
    var objectName: List<String>? =null
    private val ip = "130.251.13.109"
    private val port = 8080
    lateinit var currentobject: String
    //private var objectCount = 0

    override fun onCreate() {
        Log.d("stickerservice","create")
            startSticker()
        //objectName = objectList.distinct()
    }

    private val stickerBinder = StickerBinder()

    //Class used for the client Binder.
    inner class StickerBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): StickerService = this@StickerService

    }

    override fun onBind(intent: Intent?): IBinder = stickerBinder


    override fun onDestroy() {
        super.onDestroy()
        scanHandle?.stop()
    }
    // need to put in a thread
    fun startSticker() {
        // or initial with no object found

        Thread(Runnable {
            val scanner = EstimoteBluetoothScannerFactory(applicationContext).getSimpleScanner()
// first test to see if can detect 2 stickers at the same time
            scanHandle = scanner
                .estimoteNearableScan()
                .withBalancedPowerMode()
                .withOnPacketFoundAction {
                    // need to extract certain length of data and value to client socket
                        for (sticker in StickerUtils.stickers)
                            if (sticker.stickerId == it.deviceId) {
                                objectList.add(sticker.attachedObject)
                                objectName = objectList.distinct()
                                if (objectList.size ==6){
                                    // open client once
                                    val clientSocket = ClientSocket(ip, port, objectName.toString())
                                    clientSocket.openClient()
                                    objectName = null
                                    objectList.clear()
                                }

                                Log.d("stickerlist","$objectList")
                                Log.d("stickerunique","$objectName")
                            }

                    //Log.d("stickerservice","$objectName")

                }
                .start()
        }).start()
        //Log.d("stickerservice","${objectList.distinct()}")


    }
}
