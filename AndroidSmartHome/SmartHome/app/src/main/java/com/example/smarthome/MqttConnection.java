package com.example.smarthome;

import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttConnection {
    String clientId;
    MqttAndroidClient androidClient;
    Context context;
    HttpManager httpManager;
    public MqttConnection(Context context, HttpManager httpManager)
    {
        this.httpManager=httpManager;
        this.context=context;
        this.clientId= MqttClient.generateClientId();
        this.androidClient= new MqttAndroidClient(context, "tcp://max.isasecret.com:1723", clientId);
        connect();
    }

    private void connect() {
        try {
            MqttConnectOptions options=new MqttConnectOptions();
            options.setUserName("majinfo2019");
            options.setPassword("Y@_oK2".toCharArray());
            IMqttToken token = androidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribe("a_a_m_light");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
//                    Toast.makeText(context, "Error in connection: Timeout? Firewall?", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publish(String topic,String message)
    {
        byte[] encodedPayload = new byte[0];
        try
        {
            encodedPayload = message.getBytes("UTF-8");
            MqttMessage mqttMessage = new MqttMessage(encodedPayload);
            androidClient.publish(topic, mqttMessage);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Can't publish", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void subscribe(String topic)
    {
        int qos = 1;
        try
        {
            IMqttToken subToken = androidClient.subscribe(topic, qos);
            androidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(context, "Connection Lost! Try Again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    parseMqttMessage(topic,message);
//                    httpManager.retrieveRoomContextState(message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(context, "Can't subscribe! User authorization?", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void parseMqttMessage(String topic, MqttMessage mqttMessage) {
        try
        {
            String message=mqttMessage.toString();
            JSONObject jsonObject=new JSONObject(message);
            String id=jsonObject.getString("id");
            String room=jsonObject.getString("room");
            Toast.makeText(context, "Recieved", Toast.LENGTH_SHORT).show();
            httpManager.retrieveRoomContextState(room);
//            JSONObject properties=jsonObject.getJSONObject("properties");
//            Iterator<String> iterator=properties.keys();
//            while (iterator.hasNext())
//            {
//                String key=iterator.next();
//                String value=properties.getString(key);
//
//                switch (key)
//                {
//                    case "on":
//                        break;
//                    case "hue":
//                        break;
//                    case "bri":
//                        break;
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MqttAndroidClient getAndroidClient() {
        return androidClient;
    }
}
