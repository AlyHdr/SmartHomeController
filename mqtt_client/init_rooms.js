const fetch = require('node-fetch');
fetch('http://192.168.1.131/api/LDd1NE7AhgJ6bJ-SZ-g1zCkydqssE2wd5Lwe7lMU/lights/')
     .then(res => res.json())
    .then(function(json){
        for (lamp_id in json){
            var state = json[lamp_id].state
            var status = "ON"
            if(state.on === false)
                status = "OFF"
            const props = { id: lamp_id, color: state.hue, level: state.bri, roomId: -10  ,status: status}
            console.log(JSON.stringify(props))
            fetch('https://alyhdr.cleverapps.io/api/lights/', {
                method: 'POST',
                body:    JSON.stringify(props),
                headers: { 'Content-Type': 'application/json' }    
            })
            .then(res => res.json()) // expecting a json response
            .then(json => console.log(json));
        }
    })

