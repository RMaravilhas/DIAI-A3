package pt.unl.fct.iadi.novaevents.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.client.OpenWeatherMapApi
import org.springframework.web.client.RestClientException

@Service
class WeatherService(
    private val openWeatherMapApi: OpenWeatherMapApi,
    @Value("\${weather.api.key}") private val apiKey: String
) {
    fun isRaining(location: String): Boolean? {
        return try {
            val response = openWeatherMapApi.getWeather(location, apiKey)
            response.weather.any { it.main.equals("Rain", ignoreCase = true) }
        } catch (e: Exception) {
            null
        }
    }
}
