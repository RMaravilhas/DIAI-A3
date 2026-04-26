package pt.unl.fct.iadi.novaevents.client

data class WeatherResponse(
    val weather: List<WeatherDescription>
)

data class WeatherDescription(
    val main: String
)
