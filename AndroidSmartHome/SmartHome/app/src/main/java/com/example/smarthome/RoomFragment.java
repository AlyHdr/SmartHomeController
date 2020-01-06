package com.example.smarthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class RoomFragment extends Fragment{

    Room room;
    HttpManager manager;
    public RoomFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static RoomFragment newInstance() {

        RoomFragment fragment = new RoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void init(View layout) {
        TextView textViewRoomName=layout.findViewById(R.id.textViewRoomName);
        textViewRoomName.setText(room.getName());
        manager.retrieveRoomContextState(room.getId());
        ImageView buttonAddLight=layout.findViewById(R.id.buttonAddLight);
        buttonAddLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLight(v);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.fragment_room, container, false);
        init(layout);
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateView(Room room) {
        ListView listView=getActivity().findViewById(R.id.sensorsListView);
        ArrayList<Light> list=room.getLights();
        RoomAdapter roomAdapter=new RoomAdapter(getActivity(),list,manager);
        listView.setAdapter(roomAdapter);

    }

    public void addLight(View view) {
        Light light = new Light("4", 100, true, 5000, room);
        manager.addLight(light);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setManager(HttpManager manager) {
        this.manager = manager;
    }
}
