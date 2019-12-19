package com.example.smarthome;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.ColorUtils;

import com.azeesoft.lib.colorpicker.HuePicker;
import com.azeesoft.lib.colorpicker.SatValPicker;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;

import java.util.List;

//import top.defaults.colorpicker.ColorPickerView;

public class RoomAdapter extends ArrayAdapter<Light> {
    Context context;
    List<Light> lights;
    HttpManager manager;
    public RoomAdapter(@NonNull Context context, @NonNull List<Light> objects, HttpManager manager) {
        super(context, 0, objects);
        this.context=context;
        lights=objects;
        this.manager=manager;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
        {
            listItem = LayoutInflater.from(context).inflate(R.layout.room_sensor,parent,false);
        }

        if(lights.size()!=0)
        {
//            Toast.makeText(context, "Updating room", Toast.LENGTH_SHORT).show();
            final Light currentLight = lights.get(position);
            listItem.setTag(currentLight);
            Button buttonDelete=listItem.findViewById(R.id.linearLayoutButtons).findViewById(R.id.buttonDelete);

//            Button buttonSetTheme=listItem.findViewById(R.id.linearLayoutButtons).findViewById(R.id.buttonSetTheme);

            ImageView image = listItem.findViewById(R.id.imageSensor);
            LinearLayout linearLayoutSensorOptions = listItem.findViewById(R.id.linearLayoutSensorOptions);
            LinearLayout linearLayoutLamp=listItem.findViewById(R.id.linearLayoutLamp);

            RadioGroup radioGroupOnOff=linearLayoutSensorOptions.findViewById(R.id.radioGroupOnOff);
            RadioButton radioButtonOn=radioGroupOnOff.findViewById(R.id.radioButtonOn);
            RadioButton radioButtonOff=radioGroupOnOff.findViewById(R.id.radioButtonOff);
            if(currentLight.isOn())
            {
                image.setImageResource(R.drawable.light_bulb_on);
//                radioGroupOnOff.check(radioGroupOnOff.getChildAt(0).getId());
                radioButtonOn.setChecked(true);
            }
            else
            {
                image.setImageResource(R.drawable.light_bulb_off);
//                radioGroupOnOff.check(radioGroupOnOff.getChildAt(1).getId());
                radioButtonOff.setChecked(true);
            }

            SeekBar seekBarBrightness = linearLayoutLamp.findViewById(R.id.seekBarBrightness);
            seekBarBrightness.setProgress(currentLight.getLevel());

//            SeekBar seekBarColor=linearLayoutSensorOptions.findViewById(R.id.seekBarHue);
//            seekBarColor.setProgress(currentLight.getColor());

            View rectangle=linearLayoutSensorOptions.findViewById(R.id.rectangleColor);
            float hsv[]=new float[3];
            hsv[0]=(currentLight.getColor()*360)/65535;
//            hsv[1]=(float)(currentLight.getLevel()/(254*1.0));
            hsv[1]=1;
            hsv[2]=1;

//            System.out.println("HSV RECTangle: {"+hsv[0]+","+hsv[1]+","+hsv[2]+"}");
//            Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();

            int color = Color.HSVToColor(hsv);
            GradientDrawable bgShape = (GradientDrawable)rectangle.getBackground();
            bgShape.setColor(color);
//            rectangle.setBackgroundColor(color);


            TextView lampId=linearLayoutLamp.findViewById(R.id.textViewLightId);
            lampId.setText(currentLight.getId());

            TextView textViewSeekBarBrightness=linearLayoutLamp.findViewById(R.id.textViewSeekBarBrightnessValue);
            textViewSeekBarBrightness.setText(currentLight.getLevel()+"");

            TextView textViewSeekBarColor=linearLayoutSensorOptions.findViewById(R.id.textViewSeekBarColorValue);
            textViewSeekBarColor.setText(currentLight.getColor()+"");


            defineListeners(listItem, currentLight,rectangle, textViewSeekBarColor, seekBarBrightness, textViewSeekBarBrightness, buttonDelete, radioGroupOnOff);
        }
        return listItem;
    }

    private void defineListeners(final View listItem, Light light, View rectangle, final TextView textViewSeekBarColor, SeekBar seekBarBrightness, final TextView textViewSeekBarBrightness, Button buttonDelete, RadioGroup radioGroup) {

        rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Light light=(Light)listItem.getTag();
                showAlert(light);
            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekBarBrightness.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Light light=(Light)listItem.getTag();
                manager.changeBrightness(light,seekBar.getProgress());

            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.deleteLight((Light)listItem.getTag());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isOn=false;
                if(checkedId==R.id.radioButtonOn)
                {
                    isOn=true;
                }
                else if(checkedId==R.id.radioButtonOff)
                {
                    isOn=false;
                }
                else
                {
                    Toast.makeText(context, "ERRORSSSSS", Toast.LENGTH_SHORT).show();
                }
                Light light=(Light)listItem.getTag();
                if(light.isOn()!=isOn)
                    manager.switchLight(light,isOn);
            }
        });
    }


    public void showAlert(final Light light)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pick hue");
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customLayout = layoutInflater.inflate(R.layout.color_picker, null);
        HuePicker huePicker=customLayout.findViewById(R.id.hueBar);
        huePicker.setProgress(light.getColor());
        builder.setView(customLayout);
        huePicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                manager.changeColor(light,progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
