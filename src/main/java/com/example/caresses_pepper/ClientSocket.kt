package com.example.caresses_pepper

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.UnknownHostException

class ClientSocket (var ip:String,var port:Int,var sendMsg:String){
    private var clientSocket: Socket? = null
    private var isClient: Boolean = false
    private var input: InputStream? = null
    private var output: OutputStream? = null
    var send: String? = null
    var receive: String? = null

    fun openClient(){
        Thread(Runnable {
            try {
                // build a server with port
                clientSocket = Socket(ip, port)
                //Log.d("Sign for Socket","$clientSocket")
                if (clientSocket!= null) {
                    isClient = true
                    sendMsg()
                    val r = receiveMsg()
                } else {
                    isClient = false

                }

            } catch (e: UnknownHostException) {
                e.printStackTrace()
                //Log.i("socket", "6")
            }finally {
                input?.close()
                output?.close()
                clientSocket?.close()

            }

        }).start()
    }


    //message to server
    private fun sendMsg(){
        try {
            output = clientSocket?.getOutputStream()
            output?.let {
                val outStream = DataOutputStream(output)
                //test text
                send = sendMsg
                outStream.writeUTF(send)
                outStream.flush()
            }
        }catch (e: Exception){
            e.printStackTrace ()

        }

    }
    // message from server
    fun receiveMsg(): String?{
        try {
            input = clientSocket?.getInputStream()
            input?.let {
                val inputStream = DataInputStream(input)
                receive = inputStream.readUTF()
            }
            //Log.d("Msg from server", "$receive")
        }catch (e: Exception){
            e.printStackTrace ()
        }
        return receive
    }
}
