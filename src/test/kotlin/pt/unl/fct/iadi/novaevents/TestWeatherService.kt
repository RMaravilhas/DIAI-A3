package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.unl.fct.iadi.novaevents.client.OpenWeatherMapApi
import pt.unl.fct.iadi.novaevents.client.WeatherDescription
import pt.unl.fct.iadi.novaevents.client.WeatherResponse
import pt.unl.fct.iadi.novaevents.service.WeatherService

class TestWeatherService {

    class FakeOpenWeatherMapApi : OpenWeatherMapApi {
        var simulateRain = false
        var simulateError = false

        override fun getWeather(location: String, appId: String): WeatherResponse {
            if (simulateError) throw RuntimeException("Error fetching weather")
            return if (simulateRain) {
                WeatherResponse(listOf(WeatherDescription("Rain")))
            } else {
                WeatherResponse(listOf(WeatherDescription("Clear")))
            }
        }
    }

    private val fakeApi = FakeOpenWeatherMapApi()
    private val weatherService = WeatherService(fakeApi, "dummy-key")

    @Test
    fun `isRaining returns true when it is raining`() {
        fakeApi.simulateRain = true
        assertTrue(weatherService.isRaining("Lisbon")!!)
    }

    @Test
    fun `isRaining returns false when it is clear`() {
        fakeApi.simulateRain = false
        assertFalse(weatherService.isRaining("Lisbon")!!)
    }

    @Test
    fun `isRaining returns null when API fails`() {
        fakeApi.simulateError = true
        assertNull(weatherService.isRaining("Lisbon"))
    }
}
