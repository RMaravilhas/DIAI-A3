package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pt.unl.fct.iadi.novaevents.service.WeatherService

@SpringBootTest
@AutoConfigureMockMvc
class TestWeatherController {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var weatherService: WeatherService

    @Test
    @WithMockUser
    fun `getWeatherJson returns true when raining`() {
        `when`(weatherService.isRaining("Lisbon")).thenReturn(true)
        
        mockMvc.perform(get("/api/weather?location=Lisbon").accept("application/json"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.raining").value(true))
    }

    @Test
    @WithMockUser
    fun `getWeatherJson returns false when clear`() {
        `when`(weatherService.isRaining("Lisbon")).thenReturn(false)
        
        mockMvc.perform(get("/api/weather?location=Lisbon").accept("application/json"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.raining").value(false))
    }

    @Test
    @WithMockUser
    fun `getWeatherHtml returns warning badge when raining`() {
        `when`(weatherService.isRaining("Lisbon")).thenReturn(true)
        
        mockMvc.perform(get("/api/weather?location=Lisbon").accept("text/html"))
            .andExpect(status().isOk)
            .andExpect(view().name("fragments/weather"))
            .andExpect(model().attribute("raining", true))
    }

    @Test
    @WithMockUser
    fun `getWeatherHtml returns success badge when clear`() {
        `when`(weatherService.isRaining("Lisbon")).thenReturn(false)
        
        mockMvc.perform(get("/api/weather?location=Lisbon").accept("text/html"))
            .andExpect(status().isOk)
            .andExpect(view().name("fragments/weather"))
            .andExpect(model().attribute("raining", false))
    }

    @Test
    @WithMockUser
    fun `getWeatherHtml returns unavailable message when API fails`() {
        `when`(weatherService.isRaining("Lisbon")).thenReturn(null)
        
        mockMvc.perform(get("/api/weather?location=Lisbon").accept("text/html"))
            .andExpect(status().isOk)
            .andExpect(view().name("fragments/weather"))
            .andExpect(model().attribute("raining", null as Boolean?))
    }

    @Test
    fun `unauthenticated access is denied`() {
        mockMvc.perform(get("/api/weather?location=Lisbon"))
            .andExpect(status().isUnauthorized)
    }
}
