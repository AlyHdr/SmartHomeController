package com.example.smarthome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity implements RoomContextStateListener {
    HttpManager manager;
    Room room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null)
            room = bundle.getParcelable("room");
        else
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        init();
    }

    private void init() {
        TextView textViewRoomName=findViewById(R.id.textViewRoomName);
        textViewRoomName.setText("Room "+room.getName());
        manager=new HttpManager(this);
        manager.addListener(this);
        manager.retrieveRoomContextState(room.getId());
    }

    @Override
    public void updateView(ArrayList<Room> rooms) {
        Room room=rooms.get(0);
        ListView listView=findViewById(R.id.sensorsListView);
        ArrayList<Light> list=room.getLights();
        RoomAdapter roomAdapter=new RoomAdapter(this,list,manager);
        listView.setAdapter(roomAdapter);
    }

    @Override
    public void updateView(Room rooms) {

    }

    public void addLight(View view) {
        Light light = new Light("4", 100, true, 5000, room);
        manager.addLight(light);
    }

    @Override
    protected void onDestroy() {
        MqttAndroidClient client =manager.getMqttConnection().getAndroidClient();
        client.unregisterResources();
        client.close();
        super.onDestroy();
    }
}
