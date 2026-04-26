package pt.unl.fct.iadi.novaevents.client

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface OpenWeatherMapApi {
    @GetExchange("/data/2.5/weather")
    fun getWeather(
        @RequestParam("q") location: String,
        @RequestParam("appid") appId: String
    ): WeatherResponse
}
