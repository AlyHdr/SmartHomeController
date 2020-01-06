package com.example.smarthome;
import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class HttpManager {
    final private String apiRoomsURL = "https://alyhdr.cleverapps.io/api/rooms/";
    final private String apiLightsURL = "https://alyhdr.cleverapps.io/api/lights/";
    MqttConnection mqttConnection;
    RequestQueue queue;
    ArrayList<RoomContextStateListener> listeners;
    Context context;


    public HttpManager(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        listeners = new ArrayList<>();
        mqttConnection=new MqttConnection(context, this);
    }

    public void addListener(RoomContextStateListener activity) {
        this.listeners.add(activity);
    }

    public void switchLight(final Light light,boolean isOn)
    {
        try
        {
            if(light.isOn()!=isOn) {
                String url1Room = apiLightsURL + light.getId() + "/switch";
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url1Room,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    retrieveRoomContextState(light.getRoom().getId());
                                    JSONObject object = new JSONObject();
                                    object.put("id", light.getId());
                                    object.put("room",light.getRoom().getId());
                                    String objectString=object.toString();
//                                    mqttConnection.publish("light", objectString);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(stringRequest);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void retrieveRoomContextState(String room) {
        final String url1Room = apiRoomsURL + room;
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url1Room, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String roomId = response.getString("id");
                            String roomName = response.getString("name");
                            int roomFloor = response.getInt("floor");
                            Room room = new Room(roomId, roomName, roomFloor);
                            JSONArray lightsArray = response.getJSONArray("lights");
//                            Toast.makeText(context, ""+lightsArray.length(), Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < lightsArray.length(); i++) {
                                JSONObject lightObject = lightsArray.getJSONObject(i);
                                String lightId = lightObject.getString("id");
                                int lightLevel = lightObject.getInt("level");
                                int color = lightObject.getInt("color");
                                String status = lightObject.getString("status");
                                boolean isOn = false;
                                if (status.equals("ON"))
                                    isOn = true;
                                else if (status.equals("OFF"))
                                    isOn = false;
                                Light light = new Light(lightId, lightLevel, isOn, color, room);
                                room.addLight(light);
                            }
                            for (RoomContextStateListener listener : listeners) {
                                listener.updateView(room);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        queue.add(request);
    }

    public void retrieveRooms() {
        final ArrayList<Room> listOfRooms = new ArrayList<>();
        String urlRooms = apiRoomsURL;
        JsonArrayRequest contextRequest = new JsonArrayRequest(Request.Method.GET, urlRooms, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        String id = response.getJSONObject(i).getString("id");
                        String name = response.getJSONObject(i).getString("name");
                        int floor = response.getJSONObject(i).getInt("floor");
                        Room room = new Room(id, name, floor);
                        listOfRooms.add(room);
                    }
//                    if (context instanceof MainActivity) {
//                        MainActivity mainActivity = (MainActivity) context;
//                        mainActivity.showRooms(listOfRooms);
//                    }
                    for (RoomContextStateListener listener : listeners) {
                        listener.updateView(listOfRooms);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(contextRequest);

    }

    public void deleteLight(final Light light) {
        final String url1Room = apiLightsURL + light.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url1Room,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        retrieveRoomContextState(light.getRoom().getId());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    public void changeColor(final Light light, final int colorValue)
    {
        try
        {
            String url1Room = apiLightsURL + light.getId() + "/hue";
            JSONObject obj = new JSONObject();
            obj.put("color",colorValue);
//            obj.put("level",brightnessValue);
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url1Room, obj,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            retrieveRoomContextState(light.getRoom().getId());
                            publishMQTT(light);
//                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(postRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void publishMQTT(Light light) {
        try {
            JSONObject object = new JSONObject();
            object.put("id", light.getId());
            object.put("room",light.getRoom().getId());
            String objectString=object.toString();
//            mqttConnection.publish("light", objectString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void changeBrightness(final Light light, final int brightnessValue)
    {
        try
        {
            String url1Room = apiLightsURL + light.getId() + "/bri";
            JSONObject obj = new JSONObject();
            obj.put("level",brightnessValue);
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url1Room, obj,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                            publishMQTT(light);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(postRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addRoom(Room room)
    {
        try
        {
            Thread.sleep(1000);
            JSONObject obj = new JSONObject();
            obj.put("floor",room.getFloor());
            obj.put("id",room.getId());
            obj.put("name",room.getName());
            String url1Room = apiRoomsURL;
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url1Room, obj,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Added Room", Toast.LENGTH_SHORT).show();
                            retrieveRooms();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "ERROR in adding room", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(postRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addLight(final Light light)
    {
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("id",light.getId());
            obj.put("roomId",light.getRoom().getId());
            obj.put("level",light.getLevel());
            obj.put("color",light.getColor());
            obj.put("status","ON");

            String url1Room = apiLightsURL;
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url1Room, obj,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Added Light", Toast.LENGTH_SHORT).show();
                            retrieveRoomContextState(light.getRoom().getId());
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "ERROR in adding light", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(postRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deleteRoom(Room room) {
        try
        {
            String url1Room = apiRoomsURL+room.getId();
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url1Room,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             retrieveRooms();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public MqttConnection getMqttConnection() {
        return mqttConnection;
    }
}
