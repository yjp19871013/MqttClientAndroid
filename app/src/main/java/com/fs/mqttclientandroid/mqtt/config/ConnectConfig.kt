package com.fs.mqttclientandroid.mqtt.config

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

data class ConnectConfig(
    var host: String? = null,
    var port: Int = 1883,
    var timeout: Int = 0,
    var clientId: String? = null
) {
    companion object {

        fun loadConfiguration(pathname: String): ConnectConfig? {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(
                        InputStreamReader(
                                FileInputStream(pathname)))
                val gson = Gson()
                val config = gson.fromJson(reader, ConnectConfig::class.java)
                reader.close()
                return config
            } catch (e: Exception) {
                e.printStackTrace()
                if (null != reader) {
                    try {
                        reader.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                return null
            }

        }
    }
}
