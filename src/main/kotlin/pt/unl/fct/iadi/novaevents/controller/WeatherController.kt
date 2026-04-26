package pt.unl.fct.iadi.novaevents.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import pt.unl.fct.iadi.novaevents.service.WeatherService

@Controller
class WeatherController(private val weatherService: WeatherService) {

    @GetMapping("/api/weather", produces = ["application/json"])
    @ResponseBody
    fun getWeatherJson(@RequestParam location: String): Map<String, Boolean?> {
        val isRaining = weatherService.isRaining(location)
        return mapOf("raining" to isRaining)
    }

    @GetMapping("/api/weather", produces = ["text/html"])
    fun getWeatherHtml(@RequestParam location: String, model: Model): String {
        val isRaining = weatherService.isRaining(location)
        model.addAttribute("raining", isRaining)
        return "fragments/weather"
    }
}
