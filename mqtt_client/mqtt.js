var mqtt = require('mqtt')

var options = {
  port: 1723,
  username: 'majinfo2019',
  password: 'Y@_oK2'
}

const fetch = require('node-fetch');

console.log('MQTT client started...')
var client  = mqtt.connect('mqtt://max.isasecret.com',options)
 
client.on('connect', function () {
  client.subscribe('getAllRooms')
  client.subscribe('switchState')
  client.subscribe('changeColor')
  client.subscribe('changeBri')
  client.subscribe('a_a_m_light')
  
  console.log('Client subsrcribed to topics')
})
 
client.on('message', function (topic, message) {
  // message is Buffer
  if(topic == 'getAllRooms'){
    console.log('recieved: '+message)
    // fetch('https://alyhdr.cleverapps.io/api/rooms')
    // .then(res => res.json())
    // .then(json => console.log(json));
  }else if(topic == 'switchState'){
    var obj = JSON.parse(message);
    var light_id = obj.id
    fetch('https://alyhdr.cleverapps.io/api/lights/'+light_id+'/switch', { method: 'PUT'})
    .then(res => res.json()) // expecting a json response
    .then(json => console.log('logging result ---- :'+json));
  }else if(topic == 'changeBri'){
    
    var obj = JSON.parse(message);
    var light_id = obj.id
    fetch('https://alyhdr.cleverapps.io/api/lights/'+light_id+'/bri', { method: 'POST'})
    .then(res => res.json()) // expecting a json response
    .then(json => console.log('logging result ---- :'+json));

  }else if(topic == 'changeColor'){
    
    var obj = JSON.parse(message);
    var light_id = obj.id
    
    const body = { level: "'"+obj.color+"'" }
    fetch('https://alyhdr.cleverapps.io/api/lights/'+light_id+'/hue', {
         method: 'POST',
         body:    JSON.stringify(body),
        headers: { 'Content-Type': 'application/json' }    
    })
    .then(res => res.json()) // expecting a json response
    .then(json => console.log('logging result ---- :'+json));
    
  }else if(topic == 'a_a_m_light'){

    var obj = JSON.parse(message);
    var light_id = obj.id
    
    const props = obj.properties
    console.log(props)
    fetch('http://192.168.1.131/api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/'+light_id+'/state', {
         method: 'PUT',
         body:    JSON.stringify(props),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }    
    })
    .then(res => console.log(res));
  }
  console.log('logging -----: recived: '+topic+' with payload: '+message.toString())
  //client.end()
})

