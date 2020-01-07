package com.example.smarthome;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import org.eclipse.paho.android.service.MqttAndroidClient;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements RoomContextStateListener{

    HttpManager httpManager;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    public void init()
    {
        httpManager=new HttpManager(this);
        httpManager.addListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainFragment mainFragment=MainFragment.newInstance();
        mainFragment.setHttpManager(httpManager);
        transaction.replace(R.id.main_activity_layout,mainFragment,"MainFragment");
        transaction.commit();

    }
    public void enterRoom (View view)
    {
        CardView cardView=(CardView)view;
        Room room=(Room)cardView.getTag();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RoomFragment roomFragment=RoomFragment.newInstance();
        roomFragment.setManager(httpManager);
        roomFragment.setRoom(room);
        currentFragment=roomFragment;
        transaction.replace(R.id.main_activity_layout,roomFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void updateView(ArrayList<Room> rooms) {
        MainFragment mainFragment= (MainFragment)getSupportFragmentManager().findFragmentByTag("MainFragment");
        mainFragment.showRooms(rooms);
    }

    @Override
    public void updateView(Room room) {
        ((RoomFragment)currentFragment).updateView(room);
    }

    @Override
    public void updateLight(Light light) {
        ((RoomFragment)currentFragment).updateLight(light);
    }

    @Override
    protected void onDestroy() {
        MqttAndroidClient client =httpManager.getMqttConnection().getAndroidClient();
        client.unregisterResources();
        client.close();
        super.onDestroy();
    }
}
