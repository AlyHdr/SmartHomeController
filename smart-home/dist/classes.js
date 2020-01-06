class LightController{
  constructor(ahref,brightness_slider,delete_btn,ligth_icon,btn_change_color,light_id,light){
    this.ahref = ahref
    this.brightness_slider = brightness_slider
    this.delete_btn = delete_btn
    this.light_id = light_id
    this.ligth_icon = ligth_icon
    this.btn_change_color = btn_change_color
    this.light=light
  }
}

class RoomController{
  constructor(btn_switch_all_off,btn_switch_all_on,btn_add_light,room_id){
    this.btn_switch_all_off = btn_switch_all_off
    this.btn_switch_all_on = btn_switch_all_on
    this.room_id = room_id
    this.btn_add_light = btn_add_light

  }
}

class Room {
  constructor(id,name,things){
    this.id = id;
    this.name = name;
    this.things = things
  }
  addThing(thing){
    this.things.push(thing)
  }
}
class Thing {
  constructor(){

  }
}
class Light extends Thing{
  constructor(id,level,status,color){
    super()
    this.id = id
    this.level = level
    this.color=color
    this.status = status
  }
}
class Noise extends Thing{
  constructor(id,level,status){
    super()
    this.id = id
    this.level = level
    this.status = status
  }
}