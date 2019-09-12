package e.caioluis.testeinloco.json

data class Cities(
    val list : ArrayList<City>
)

data class City(

    val id : Int,
    val weather : ArrayList<Weather>,
    val temperature : Temperature,
    val name : String
)

data class Weather(

    val main: String,
    val description: String
)

data class Temperature (

    val temp_min : Double,
    val temp_max : Double
)