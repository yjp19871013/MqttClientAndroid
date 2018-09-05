package com.fs.mqttclientandroid.mqtt.client

import org.eclipse.paho.client.mqttv3.MqttMessage

interface MessageDispatcher {
    fun dispatch(client: MqttClient, topic: String, message: MqttMessage)
}
