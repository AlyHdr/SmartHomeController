  function dialogAddRoom(){
    (async () => {
        const { value: formValues } = await Swal.fire({
          title: 'Add Room',
          html:
            '<p>Name:  <input id="room_name" class="swal2-input"></p>' +
            '<p>Floor: <input id="floor_num" class="swal2-input "></p>',
          focusConfirm: false,
          preConfirm: () => {
            return {
              "room_name": document.getElementById('room_name').value,
              "floor_num" :document.getElementById('floor_num').value
            }
          }
        })

        if (formValues) {
           var request = new XMLHttpRequest()
            // Open a new connection, using the GET request on the URL endpoint
            request.open('POST', 'https://alyhdr.cleverapps.io/api/rooms/', true)
            request.setRequestHeader("Content-type", "application/json");
            
            request.onload = function(){
               var data = JSON.parse(this.response)

              if (request.status >= 200 && request.status < 400) {
                console.log(this.response)
                  getRooms()
                 console.log('done !');
                 const Toast = Swal.mixin({
                          toast: true,
                          position: 'top-end',
                          showConfirmButton: false,
                          timer: 2000,
                          timerProgressBar: true,
                          onOpen: (toast) => {
                            toast.addEventListener('mouseenter', Swal.stopTimer)
                            toast.addEventListener('mouseleave', Swal.resumeTimer)
                          }
                        })

                        Toast.fire({
                          icon: 'success',
                          title: 'Changed successfully'
                        })

              } else {
              console.log('error')
              }
            }
            // Send request
            var payload='{"floor": '+formValues.floor_num+',"name": "'+formValues.room_name+'"}';
            console.log(payload)
            request.send(payload)
            
            
        }
    })()
  }

  function loadInterface(rooms){

    var container = document.getElementById("div_all")
    if(container != null){
      console.log('not nulllll')
      document.body.removeChild(container)
    }
    var a_elements = []
      var count = 0

    var room_controllers = []

    const div_all = document.createElement("div");
    div_all.id = "div_all";
    
    rooms.forEach(room => {
      
      const div_container = document.createElement("div");
      div_container.className = "container";
      div_container.id = "container_rooms";
      const div_row = document.createElement("div");
      div_row.className = "row";
      div_row.id = "#div_"+room.id;
      const room_heading = document.createElement("h2");
      const room_text = document.createTextNode(room.name);
      room_heading.appendChild(room_text);
      div_row.appendChild(room_heading);

      div_row.appendChild(document.createElement("hr"))
      

      const controller_heading = document.createElement("h2");
      const controller_text = document.createTextNode("Controller");
      controller_heading.appendChild(controller_text);
      div_row.appendChild(controller_heading)


      const btn_add_light = document.createElement("a");
      btn_add_light.className="button success-button toggle-button";
      btn_add_light.appendChild(document.createTextNode("Add Light"));
      btn_add_light.id = 'btn_add_light_'+room.id
      btn_add_light.href = "#div_"+room.id;
      div_row.appendChild(btn_add_light)

      const btn_switch_all_on = document.createElement("a");
      btn_switch_all_on.className="button success-button toggle-button";
      btn_switch_all_on.appendChild(document.createTextNode("Switch All ON"));
      btn_switch_all_on.id = 'btn_switch_all_on_'+room.id
      btn_switch_all_on.href = "#div_"+room.id;
      div_row.appendChild(btn_switch_all_on)

      const btn_switch_all_off = document.createElement("a");
      btn_switch_all_off.className="button success-button toggle-button";
      btn_switch_all_off.appendChild(document.createTextNode("Switch All off"));
      btn_switch_all_off.id = 'btn_switch_all_off_'+room.id
      btn_switch_all_off.href = "#div_"+room.id;
      div_row.appendChild(btn_switch_all_off)


      div_row.appendChild(document.createElement("hr"))
      
      room_controllers.push(new RoomController(btn_switch_all_off,btn_switch_all_on,btn_add_light,room.id))
      //replace with thing name when API is ready ..

      var things = room.things
      things.forEach(thing => {
        
        const div_col = document.createElement("div");
        div_col.className = "col-md-3";

        const div_content = document.createElement("div");
        div_content.className = "content";
        const header_name = document.createElement("h3");

        var name = "id: "+thing.id;
        const header_value = document.createTextNode(name);
        header_name.className = "room-title";
        header_name.appendChild(header_value);

        const div_icon = document.createElement("div");
        div_icon.className = "device-icon";
        const icon = document.createElement("img");
        icon.id = "icon_"+count
        icon.setAttribute("width", "120")
        icon.setAttribute("height", "120")
        
        if(thing instanceof Light){
          //icon.className = "fas fa-lightbulb"; 
          
          
        }else{
          icon.className = "fa fa-plug"
        }

      
        div_icon.appendChild(icon);

        // button initializiation ..
        const ahref = document.createElement("a");
        var id = "btn_"+count;
        ahref.id = id;

        ahref.href = "#div_"+room.id;
        
        if(thing.status == "ON"){
          ahref.appendChild(document.createTextNode("Turn off device"));
          ahref.className="button success-button toggle-button enabled";
          icon.setAttribute("src", "./images/light_on.png")
        }
        else{

          ahref.appendChild(document.createTextNode("Turn on device"));
          ahref.className="button success-button toggle-button";
          icon.setAttribute("src", "./images/light_off.png")
        }
        
        const btn_delete = document.createElement("a");
        
        btn_delete.className="button";
        btn_delete.appendChild(document.createTextNode("Delete Light"));
        btn_delete.id = 'btn_delete_light_'+room.id
        btn_delete.href = "#div_"+room.id;
        
        const btn_change_color = document.createElement("a");
        btn_change_color.className="button success-button toggle-button enabled";
        btn_change_color.appendChild(document.createTextNode("Change color"));
        btn_change_color.href = "#div_"+room.id;
        

        const brightness_name  = document.createElement("h3");
        var name = "Brightness";
        const brightness_value = document.createTextNode(name);
        brightness_name.className = "room-title";
        brightness_name.appendChild(brightness_value);

        var slider = document.createElement("INPUT");
        
        slider.setAttribute("type","range");
        slider.setAttribute("min","1");
        slider.setAttribute("max","254");
        slider.setAttribute("value",thing.level);
        slider.setAttribute("class","slider");

        a_elements.push(new LightController(ahref,slider,btn_delete,icon,btn_change_color,thing.id,thing))
        
        div_content.appendChild(header_name);
        div_content.appendChild(document.createElement("hr"))
        div_content.appendChild(div_icon);
        div_content.appendChild(ahref);
        div_content.appendChild(document.createElement("br"))
        
        div_content.appendChild(btn_delete);
        div_content.appendChild(document.createElement("br"))

        div_content.appendChild(document.createElement("hr"));
        
        div_content.appendChild(brightness_name);
        div_content.appendChild(document.createElement("br"));
        div_content.appendChild(slider);
        div_content.appendChild(document.createElement("br"));
        div_content.appendChild(btn_change_color)
        div_col.appendChild(div_content);
        div_row.appendChild(div_col);
        count++;
        })
        
    div_container.appendChild(div_row);
  
    div_all.appendChild(div_container)
    })

    document.body.appendChild(div_all);
    

  a_elements.forEach(function(a){
    a.ahref.addEventListener('click', function (){
      var request = new XMLHttpRequest()
    // Open a new connection, using the GET request on the URL endpoint
    request.open('PUT', 'https://alyhdr.cleverapps.io/api/lights/'+a.light_id+'/switch/', true)

    request.onload = function(){
       var data = JSON.parse(this.response)

      if (request.status >= 200 && request.status < 400) {
        
        if(document.getElementById(a.ahref.id).className == "button success-button toggle-button enabled"){
            document.getElementById(a.ahref.id).className = "button success-button toggle-button";
            document.getElementById(a.ahref.id).innerHTML = "Turn on Device";
            document.getElementById(a.ligth_icon.id).src = "./images/light_off.png"
        }else{
          document.getElementById(a.ahref.id).className = "button success-button toggle-button enabled";
          document.getElementById(a.ahref.id).innerHTML = "Turn off Device";
          document.getElementById(a.ligth_icon.id).src = "./images/light_on.png"
        }
      } else {
      console.log('error')
      }
    }
    // Send request
    request.send()
    });

    a.brightness_slider.addEventListener('mouseup',function(){
      console.log('brightness_value: '+a.brightness_slider.value+" --- "+a.light_id)
      var request = new XMLHttpRequest()
      // Open a new connection, using the GET request on the URL endpoint
      request.open('POST', 'https://alyhdr.cleverapps.io/api/lights/'+a.light_id+'/bri/', true)
      request.setRequestHeader("Content-type", "application/json");
      
      request.onload = function(){
         var data = JSON.parse(this.response)

        if (request.status >= 200 && request.status < 400) {
          console.log(this.response)
           console.log('done !');
           const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true,
                    onOpen: (toast) => {
                      toast.addEventListener('mouseenter', Swal.stopTimer)
                      toast.addEventListener('mouseleave', Swal.resumeTimer)
                    }
                  })

                  Toast.fire({
                    icon: 'success',
                    title: 'Changed successfully'
                  })

        } else {
        console.log('error')
        }
      }
      // Send request
      var payload='{"level": '+a.brightness_slider.value+' }';
      request.send(payload)
    });
    a.delete_btn.addEventListener('click',function(){
      var request = new XMLHttpRequest()
      // Open a new connection, using the GET request on the URL endpoint
      request.open('DELETE', 'https://alyhdr.cleverapps.io/api/lights/'+a.light_id, true)
      request.setRequestHeader("Content-type", "application/json");
      
      request.onload = function(){

        if (request.status >= 200 && request.status < 400) {
          console.log(this.response)
          getRooms() // init rooms interface again..
           console.log('Deleted !');
           const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000,
                    timerProgressBar: true,
                    onOpen: (toast) => {
                      toast.addEventListener('mouseenter', Swal.stopTimer)
                      toast.addEventListener('mouseleave', Swal.resumeTimer)
                    }
                  })

                  Toast.fire({
                    icon: 'success',
                    title: 'Changed successfully'
                  })

        } else {
        console.log('error')
        }
      }
      request.send()
    })
    a.btn_change_color.addEventListener('click',function(){
      console.log('Light color: '+a.light.color)
      Swal.fire({
            title: '<strong>Choose color</strong>',
            icon: 'info',
            html:
              '<div id="app">\
                    <color-picker v-bind="color" @input="onInput"></color-picker>\
                    <h1 v-text="msg"></h1>\
                </div>'
          }).then( result =>{
              if(result.value){
                app.changeColor()      
              }else{
                return
              }
          })
        var ColorPicker = VueColorPicker;
        var app = new Vue({
            el: '#app',
            components: {
                ColorPicker: ColorPicker
            },
            data: {
                msg: 'Radial Color Picker - Vue',
                color: {
                    hue:a.light.color/182 ,
                    saturation: 100,
                    luminosity: 50,
                    alpha: 1

                },
                  lights:[],
              selectLight:0
            },
            mounted () {
                axios
                .get('https://alyhdr.cleverapps.io/api/lights')
              .then(response => {
                this.lights = response.data
               
              },
              )
        },
            methods: {
                onInput: function(hue) {
                    this.color.hue = hue;
                    this.hue=hue*182;
                },
                changeColor:function () {
                  a.light.color = this.hue
                    let post_url= "https://alyhdr.cleverapps.io/api/lights/"+a.light_id+"/hue";
                      axios.post(post_url,{
                        color:this.hue
                      })
                    .then(response => {}) 
                    .catch(function (error) {
                                    console.log(error);
                    
                          })

            }
        }
        });

    })
  })
  initListenerRoomController(room_controllers)
} //end of function load .. 

function initListenerRoomController(arr){
  console.log(arr)
  arr.forEach(function(controller){

      controller.btn_switch_all_off.addEventListener('click', function (){
          var request = new XMLHttpRequest()
        // Open a new connection, using the GET request on the URL endpoint
        request.open('PUT', 'https://alyhdr.cleverapps.io/api/rooms/'+controller.room_id+'/switch-off/', true)

        request.onload = function(){
           var data = JSON.parse(this.response)

          if (request.status >= 200 && request.status < 400) {
              console.log(this.response)
              getRooms()
          } else {
          console.log('error')
          }
        }
        // Send request
        request.send()
        })
      controller.btn_switch_all_on.addEventListener('click', function (){
          var request = new XMLHttpRequest()
        // Open a new connection, using the GET request on the URL endpoint
        request.open('PUT', 'https://alyhdr.cleverapps.io/api/rooms/'+controller.room_id+'/switch-on/', true)

        request.onload = function(){
           var data = JSON.parse(this.response)

          if (request.status >= 200 && request.status < 400) {
              console.log(this.response)
              getRooms()
          } else {
          console.log('error')
          }
        }
        // Send request
        request.send()
        })
      controller.btn_add_light.addEventListener('click',function(){
             (async () => {
            const { value: formValues } = await Swal.fire({
              title: 'Add Light Form',
              html:
                'level:  <input id="light_level" class="swal2-input">' +
                '<p>color: <input id="color_value" class="swal2-input "></p>',
              focusConfirm: false,
              preConfirm: () => {
                return {
                  "color_value": document.getElementById('color_value').value,
                  "light_level" :document.getElementById('light_level').value,
                }
              }
            })

            if (formValues) {
               var request = new XMLHttpRequest()
                // Open a new connection, using the GET request on the URL endpoint
                request.open('POST', 'https://alyhdr.cleverapps.io/api/lights/', true)
                request.setRequestHeader("Content-type", "application/json");
                
                request.onload = function(){
                   var data = JSON.parse(this.response)

                  if (request.status >= 200 && request.status < 400) {
                    console.log(this.response)
                      getRooms()
                     console.log('done !');
                     const Toast = Swal.mixin({
                              toast: true,
                              position: 'top-end',
                              showConfirmButton: false,
                              timer: 2000,
                              timerProgressBar: true,
                              onOpen: (toast) => {
                                toast.addEventListener('mouseenter', Swal.stopTimer)
                                toast.addEventListener('mouseleave', Swal.resumeTimer)
                              }
                            })

                            Toast.fire({
                              icon: 'success',
                              title: 'Changed successfully'
                            })

                  } else {
                  console.log('error')
                  }
                }
                // Send request
                if(formValues.light_level < 1){
                  alert('not allowed')
                }
                var payload='{"color": '+formValues.color_value+',"level": '+formValues.light_level+', "roomId": '+controller.room_id+', "status": "OFF"}';
                console.log(payload)
                request.send(payload)
                
                
            }
        })()
      })
  })
}

function printRooms(rooms){
   rooms.forEach(room =>{

      console.log(room)
    })
}
function getRooms(){

   var rooms = []
    var request = new XMLHttpRequest()
    // Open a new connection, using the GET request on the URL endpoint
    request.open('GET', 'https://alyhdr.cleverapps.io/api/rooms', true)

    request.onload = function(){
       var data = JSON.parse(this.response)

      if (request.status >= 200 && request.status < 400) {
        
        data.forEach(room => {
            //console.log(room)
            var things = []
            
            var lights = room.lights
            var room = new Room(room.id,room.name,[])
            lights.forEach(light => {
              // console.log(light)
              var light_obj = new Light(light.id,light.level,light.status,light.color)
              room.addThing(light_obj)
            })
            //var noise = new Noise(room.noise.id,room.noise.level,room.noise.status)
            //room.addThing(noise)


           rooms.push(room)

        })
        //printRooms(rooms)
        loadInterface(rooms)
      } else {
      console.log('error')
      }
    }

    // Send request
    request.send()


}