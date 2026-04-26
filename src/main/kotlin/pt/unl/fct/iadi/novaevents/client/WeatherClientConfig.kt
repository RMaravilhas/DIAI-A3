package pt.unl.fct.iadi.novaevents.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class WeatherClientConfig {

    @Bean
    fun openWeatherMapApi(builder: RestClient.Builder): OpenWeatherMapApi {
        val restClient = builder.baseUrl("https://api.openweathermap.org").build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(OpenWeatherMapApi::class.java)
    }
}
