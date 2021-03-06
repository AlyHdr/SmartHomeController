#include <string.h>
#include <SPI.h>
#include <WiFiNINA.h>
#include <ArduinoJson.h>
//#include <HttpClient.h>
#include <MQTT.h>

WiFiClient client;
MQTTClient mqtt_client ;

#define POTENTIOMETER_PIN A2 // TODO: uncomment this line if the potentiometer is connected to analogic pin 4, or adapt...
#define BUTTON_PIN 2 // TODO: uncomment this line if the potentiometer is connected to digital pin 3, or adapt...

String ID = "91229" ;
String LIGHT_ID = "18";
// TODO: you probably need to add a line here for the button pin.... 


const char* wifi_ssid     = "lab-iot-1";// TODO: change this
const char* wifi_password = "lab-iot-1"; // TODO: change this

// mqtt configuration change here
const char* mqtt_host = "max.isasecret.com" ;
const char* mqtt_user     = "majinfo2019";
const char* mqtt_password = "Y@_oK2";
const uint16_t mqtt_port =  1723;

/* Some variables to handle measurements. */
int potentiometerValue;
int buttonState = 0;         // variable for reading the pushbutton status


uint32_t t0, t ;

/* Time between displays. */
#define DELTA_T 500
//StaticJsonBuffer<200> jsonBuffer;
int status = WL_IDLE_STATUS; 

void setup() {
  // monitoring via Serial is always nice when possible
  Serial.begin(9600) ;
//  delay(100) ;
  Serial.println("Initializing...\n") ;

  connect();
  // initialize the Potentiometer and button pin as inputs:
  pinMode(POTENTIOMETER_PIN, INPUT);
  pinMode(BUTTON_PIN, INPUT);

  // Time begins now!
  t0 = t = millis() ;
}
boolean isOn = false;
int oldValue = 0;
void loop() {
   mqtt_client.loop() ;
  if(status != WL_CONNECTED){
    connect();
  }
  while ( ! mqtt_client.connected() ) {
    Serial.print(mqtt_client.connected());
    Serial.print("======================");
    connect();
  }
  t = millis() ;
  if ( (t - t0) >= DELTA_T ) {
    t0 = t ;
      
    // read the states:
    buttonState = digitalRead(BUTTON_PIN);
     potentiometerValue = analogRead(POTENTIOMETER_PIN); // TODO; uncomment this line if it is analog

    if(oldValue <= (potentiometerValue - 10) || oldValue >= (potentiometerValue + 10)){
      String briSt = String(potentiometerValue);
      changeBrightness(briSt);
      oldValue = potentiometerValue;
    }
    if(buttonState == 1){
   
        httpRequest();
    }else{
      
    }
    
  }
}
char server[] = "192.168.1.131";

void turnOn(){
  Serial.println("Trying to turn ligh on");
  if(client.connect(server, 80)){
    Serial.println("connected !");
     client.println("PUT /api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/"+LIGHT_ID+"/state HTTP/1.1");
    client.println("Host: 192.168.1.131");                          
     client.println("Connection: close");
     client.println("Content-Type: application/x-www-form-urlencoded");
     client.println("Content-Length: 12\r\n");
     String body = "{\"on\":true}";
  client.print(body);
  }
//  delay(100);
  while (client.available()) {
    String line = client.readStringUntil('\r');
      Serial.print(line);
   }
}
void turnOff(){
  Serial.println("Trying to turn ligh off");
  if(client.connect(server, 80)){
    Serial.println("connected !");
     client.println("PUT /api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/"+LIGHT_ID+"/state HTTP/1.1");
    client.println("Host: 192.168.1.131");                          
     client.println("Connection: close");
     client.println("Content-Type: application/x-www-form-urlencoded");
     client.println("Content-Length: 12\r\n");
     client.print("{\"on\":false}");
  }
}

void changeBrightness(String bri){
  Serial.print("Changing brightness...");
  Serial.println(bri);
  if(client.connect(server, 80)){
    Serial.println("connected !");
     client.println("PUT /api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/"+LIGHT_ID+"/state HTTP/1.1");
    client.println("Host: 192.168.1.131");                          
     client.println("Connection: close");
     client.println("Content-Type: application/x-www-form-urlencoded");
     int str_length = 19 + bri.length();
     String len = String(str_length);
     String content = "Content-Length: "+len+"\r\n";
     client.println(content);
     String body = "{\"on\":true,\"bri\":"+bri+"}";
     client.print(body);
  }
}

void httpRequest(){
   Serial.println("\nStarting connection to server...");
  // if you get a connection, report back via serial:
  if (client.connect(server, 80)) {
    Serial.println("connected to server");
    // Make a HTTP request:
    client.println("GET /api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/"+LIGHT_ID+" HTTP/1.1");
    client.println("Host: 192.168.1.131");
    //client.println("Content-type: application/json");
    
    client.println("Connection: keep-alive");
    client.println();
  
  }
//  delay(100);
  String line= "";
  while (client.available()) {
     line = client.readStringUntil('\n');
   }
   demoParse(line);
//  delay(100);
    Serial.print(line);


  
}
void demoParse(String input) {
   DynamicJsonDocument doc(1024);
   deserializeJson(doc,input);
  
  JsonObject myObject=doc.as<JsonObject>();

  // myObject.hasOwnProperty(key) checks if the object contains an entry for key
    JsonObject state_var = myObject["state"];
    if((bool)state_var["on"] == true){
      Serial.println("The lamp is on !");
      turnOff();
    }else{
      Serial.println("The lamp is off !");
      turnOn();
    }
  Serial.println();
}
void connect() {
 
  while ( status != WL_CONNECTED) 
  {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(wifi_ssid);
    // Connect to WPA/WPA2 network:
    status = WiFi.begin(wifi_ssid, wifi_password);

    // wait 5 seconds for connection:
    delay(5000);
  }
  if(status == WL_CONNECTED)
  {
     mqtt_client.begin(mqtt_host, mqtt_port, client) ;
    mqtt_client.onMessage(callback) ;
  }


  Serial.print("MQTT: trying to connect to host ") ;
  Serial.print(mqtt_host) ;
  Serial.print(" on port ") ;
  Serial.print(mqtt_port) ;
  Serial.println(" ...") ;

  while ( !mqtt_client.connect(ID.c_str(), mqtt_user, mqtt_password) ) 
  {
      Serial.println("not conn");
  }
  Serial.println("MQTT: connected") ;

  mqtt_client.subscribe("a_a_m_light");
  

  Serial.print("\n");
  Serial.print("WiFi connected\n");
  Serial.print("IP address: \n");
  Serial.print(WiFi.localIP());
  Serial.print("\n") ;
}

void forward(String LIGHT_ID,String  message)
{
  if(client.connect(server, 80))
  {
    Serial.println("connected !");
     client.println("PUT /api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/"+LIGHT_ID+"/state HTTP/1.1");
    client.println("Host: 192.168.1.131");                          
     client.println("Connection: close");
     client.println("Content-Type: application/x-www-form-urlencoded");
     int str_length = 19 + message.length();
     String len = String(str_length);
     String content = "Content-Length: "+len+"\r\n";
     client.println(content);
     client.print(message);
  }
//  delay(100);
  while (client.available()) {
    char c =client.read();
      Serial.print(c);
   }
  
}
void callback(String &intopic, String &payload)
{
  /* There's nothing to do here ... as long as the module
   *  cannot handle messages.
   */
   DynamicJsonDocument doc(1024);
    deserializeJson(doc,payload);
    JsonObject obj=doc.as<JsonObject>();

  // myObject.hasOwnProperty(key) checks if the object contains an entry for key
    String id=obj["id"];
    String room=obj["room"];
    JsonObject properties= obj["properties"];
    String message;
    serializeJson(properties, message);
    Serial.println(message);
    forward(id,message);
}





