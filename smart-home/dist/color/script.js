var ColorPicker = VueColorPicker;

var app = new Vue({
    el: '#app',
    components: {
        ColorPicker: ColorPicker
    },
    data: {
        msg: 'Radial Color Picker - Vue',
        color: {
            hue: 50,
            saturation: 100,
            luminosity: 50,
            alpha: 1

        },
          lights:[],
      selectLight:0,
        hue:0
    },
    mounted () {
        axios
        .get('"https://alyhdr.cleverapps.io/api/lights')
      .then(response => {
        this.lights = response.data
       
      },
      )
},
    methods: {
        onInput: function(hue) {
            this.color.hue = hue;
this.hue=hue*360;
        },
        changeColor:function () {
            let post_url= "https://alyhdr.cleverapps.io/api/lights/1/hue";
              axios.post(post_url,{
                color:this.hue,
                level:this.luminosity,
              })
            .then(response => {}) 
            .catch(function (error) {
                            console.log(error);
            
})
    }
}
});