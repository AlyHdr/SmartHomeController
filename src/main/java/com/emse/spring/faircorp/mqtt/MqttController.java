package com.emse.spring.faircorp.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

    IMqttClient mqttClient;

    public MqttController(@Value("${mqtt.clientId}") String clientId, @Value("${mqtt.hostname}") String hostname,
                          @Value("${mqtt.port}") int port) {
        try {
            mqttClient = new MqttClient("tcp://" + hostname + ":" + port, clientId);
            mqttClient.connect(mqttConnectOptions());
            subscribe("ahmad");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "mqtt")
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("majinfo2019");
        mqttConnectOptions.setPassword("Y@_oK2".toCharArray());
        return mqttConnectOptions;
    }

    public void publish(String topic, String payload) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(payload.getBytes());
//            mqttMessage.setQos(qos);
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(final String topic) {
        try {
            System.out.println("Messages received:");
            mqttClient.subscribeWithResponse(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println(message.getId() + " -> " + new String(message.getPayload()));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
