package com.fs.mqttclientandroid.mqtt.client

import android.content.Context
import com.fs.mqttclientandroid.mqtt.config.ConnectConfig
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClient(var context: Context) {

    private var client: MqttAndroidClient? = null
    private var options: MqttConnectOptions? = null
    private var dispatcher: MessageDispatcher? = null

    val clientId: String
        get() = client!!.clientId

    @Throws(MqttSecurityException::class, MqttException::class)
    fun connect(config: ConnectConfig) {
        // host为主机名，clientid即连接MQTT的客户端ID，
        // 一般以唯一标识符表示 MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = MqttAndroidClient(context, config.host + ":" + config.port, config.clientId, MemoryPersistence())
        client!!.setCallback(PushCallBack())

        options = MqttConnectOptions()

        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
        // 这里设置为true表示每次连接到服务器都以新的身份连接
        options!!.isCleanSession = true

        // 设置超时时间 单位为秒
        options!!.connectionTimeout = config.timeout
        client!!.connect(options)
    }

    @Throws(MqttException::class)
    fun disconnect() {
        client!!.disconnect()
    }

    @Throws(MqttException::class)
    fun subscribe(topic: String, qos: Int) {
        if (null == dispatcher) {
            throw RuntimeException("Before subcribe some topics, you must set your dispatcher")
        }

        client!!.subscribe(topic, qos)
    }

    @Throws(MqttException::class, MqttPersistenceException::class)
    fun publish(topic: String, message: String, qos: Int, retained: Boolean) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = message.toByteArray()
        mqttMessage.qos = qos
        mqttMessage.isRetained = false

        client!!.publish(topic, mqttMessage)
    }

    fun setDispatcher(dispatcher: MessageDispatcher) {
        this.dispatcher = dispatcher
    }

    inner class PushCallBack : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            if (null == message || null == message.payload || 0 == message.payload.size) {
                return
            }

            dispatcher!!.dispatch(this@MqttClient, topic!!, message)
        }

        override fun connectionLost(cause: Throwable?) {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            println("connect complete")
        }

    }
}
