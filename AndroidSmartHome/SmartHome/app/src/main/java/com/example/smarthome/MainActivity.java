package com.example.smarthome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.azeesoft.lib.colorpicker.HuePicker;
import com.azeesoft.lib.colorpicker.SatValPicker;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;

import java.util.ArrayList;


//import top.defaults.colorpicker.ColorPickerPopup;
//import top.defaults.colorpicker.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    HttpManager httpManager;
//    ArrayList<Room> roomStates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        showAlert();
//        showColor();
//       test();
    }


    public void test()
    {

        ColorPickerDialog colorPickerDialog= ColorPickerDialog.createColorPickerDialog(this);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                System.out.println("Got color: " + color);
                System.out.println("Got color in hex form: " + hexVal);
            }
        });
        colorPickerDialog.show();
    }

    public void addRoom(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name");        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.add_room, null);
        builder.setView(customLayout);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String roomName= ((EditText)customLayout.findViewById(R.id.editTextRoomName)).getText().toString();
                int roomFloor = Integer.valueOf(((EditText)customLayout.findViewById(R.id.editTextRoomFloor)).getText().toString());
                Room room=new Room(roomName,roomFloor);
                httpManager.addRoom(room);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void init()
    {
        httpManager=new HttpManager(this);
        httpManager.retrieveRooms();
    }
    public void showRooms(ArrayList<Room> roomStates)
    {
        TableLayout tableRooms=findViewById(R.id.tableOfRooms);
        tableRooms.removeAllViews();
        TableRow tableRow=new TableRow(this);
        for(int i=0;i<roomStates.size();i++)
        {
            if(i%3==0)
            {
                tableRooms.addView(tableRow);
                tableRow=new TableRow(this);
            }


            final View cardViewRoom = getLayoutInflater().inflate(R.layout.room,null);
            cardViewRoom.setTag(roomStates.get(i));
            cardViewRoom.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this room?")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    httpManager.deleteRoom((Room)cardViewRoom.getTag());
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return true;
                }
            });

            android.widget.TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
            p.rightMargin = 20;
            p.bottomMargin=20;
            cardViewRoom.setLayoutParams(p);

            LinearLayout linearLayout=cardViewRoom.findViewById(R.id.linearLayoutRoom);
            ImageView imageView=(ImageView)linearLayout.getChildAt(0);
            imageView.setImageResource(R.drawable.room);
            TextView textView=(TextView)linearLayout.getChildAt(1);

            textView.setText(roomStates.get(i).getName());
            tableRow.addView(cardViewRoom);
            if(i==roomStates.size()-1)
            {
                tableRooms.addView(tableRow);
                return;
            }
        }
    }

    public void enterRoom(View view)
    {
        Intent intent = new Intent(this, RoomActivity.class);
        CardView cardView=(CardView)view;
        Room room=(Room)cardView.getTag();
//        intent.putExtra("room",room);

        Bundle bundle = new Bundle();
        bundle.putParcelable("room", room);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
