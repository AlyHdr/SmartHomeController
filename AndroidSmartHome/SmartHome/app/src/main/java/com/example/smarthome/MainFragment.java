package com.example.smarthome;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    HttpManager httpManager;
    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public void init()
    {
        httpManager.retrieveRooms();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setHttpManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void showRooms(ArrayList<Room> roomStates)
    {
        TableLayout tableRooms=getActivity().findViewById(R.id.tableOfRooms);
        tableRooms.removeAllViews();
        TableRow tableRow=new TableRow(getActivity());
        for(int i=0;i<roomStates.size();i++)
        {
            if(i%3==0)
            {
                tableRooms.addView(tableRow);
                tableRow=new TableRow(getActivity());
            }


            final View cardViewRoom = getLayoutInflater().inflate(R.layout.room,null);
            cardViewRoom.setTag(roomStates.get(i));
            cardViewRoom.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    new AlertDialog.Builder(getActivity())
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
}
