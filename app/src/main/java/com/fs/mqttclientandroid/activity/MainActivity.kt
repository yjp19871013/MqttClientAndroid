package com.fs.mqttclientandroid.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fs.mqttclientandroid.R
import com.fs.mqttclientandroid.mqtt.client.MessageDispatcher
import com.fs.mqttclientandroid.mqtt.client.MqttClient
import com.fs.mqttclientandroid.mqtt.config.ConnectConfig
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var client: MqttClient? = null
    private val executorService = Executors.newCachedThreadPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.subscribeButton.setOnClickListener {
            client?.subscribe(subscribeTopicEditText.text.toString(), 2)
        }

        this.publishButton.setOnClickListener {
            val topic = publishTopicEditText.text.toString()
            val payload = payloadEditText.text.toString()
            executorService.execute {
                client?.publish(topic, payload, 2, false)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        client = MqttClient(this)
        client?.setDispatcher(Dispatcher())
    }

    override fun onResume() {
        super.onResume()

        val config = loadClientConfig()
        client?.connect(config)
    }

    override fun onPause() {
        super.onPause()

        client?.disconnect()
        client = null
    }

    private fun loadClientConfig(): ConnectConfig {
        val inputStream = resources.assets.open("client_config.json")
        val result = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int

        length = inputStream.read(buffer)
        while (length != -1) {
            result.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }

        return Gson().fromJson(result.toString("UTF-8"),
                ConnectConfig::class.java)
    }

    inner class Dispatcher: MessageDispatcher {
        override fun dispatch(client: MqttClient, topic: String, message: MqttMessage) {
            resultTextView.text = String(message.payload)
        }
    }
}
