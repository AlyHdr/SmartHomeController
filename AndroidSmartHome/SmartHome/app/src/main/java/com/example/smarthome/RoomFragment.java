package com.example.smarthome;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    public void updateLight(Light light) {
        RoomAdapter roomAdapter=(RoomAdapter)((ListView)getActivity().findViewById(R.id.sensorsListView)).getAdapter();
        ListView listView=getActivity().findViewById(R.id.sensorsListView);
        for(int i=0;i<roomAdapter.getCount();i++)
        {
            Light lightCurrent=roomAdapter.getItem(i);
            if(light.getId().equals(lightCurrent.getId()))
            {
                View view=getViewByPosition(i,listView);

                LinearLayout linearLayoutSensorOptions = view.findViewById(R.id.linearLayoutSensorOptions);
                LinearLayout linearLayoutLamp=view.findViewById(R.id.linearLayoutLamp);

                SeekBar seekBarBrightness = linearLayoutLamp.findViewById(R.id.seekBarBrightness);

                RadioGroup radioGroupOnOff=linearLayoutSensorOptions.findViewById(R.id.radioGroupOnOff);
                RadioButton radioButtonOn=radioGroupOnOff.findViewById(R.id.radioButtonOn);
                RadioButton radioButtonOff=radioGroupOnOff.findViewById(R.id.radioButtonOff);

                View rectangle=linearLayoutSensorOptions.findViewById(R.id.rectangleColor);
                float hsv[]=new float[3];
                hsv[0]=(light.getColor()*360)/65535;
                hsv[1]=1;
                hsv[2]=1;

                int color = Color.HSVToColor(hsv);
                GradientDrawable bgShape = (GradientDrawable)rectangle.getBackground();
                bgShape.setColor(color);
                seekBarBrightness.setProgress(light.getLevel());
                ImageView image = linearLayoutLamp.findViewById(R.id.imageSensor);
                if(light.isOn())
                {
                    image.setImageResource(R.drawable.light_bulb_on);
                    radioButtonOn.setChecked(true);
                }
                else
                {
                    image.setImageResource(R.drawable.light_bulb_off);
                    radioButtonOff.setChecked(true);
                }
            }

        }
    }
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
