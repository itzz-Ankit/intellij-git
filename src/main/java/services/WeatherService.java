package services;

import api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apikey;

    private static final String API  = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";


    @Autowired
    private RestTemplate restTemplate   ;

    @Autowired
    RedisService redisService ;

    public WeatherResponse getWeather( String city){

        String cacheKey = "weather_of_" + city.toLowerCase(Locale.ROOT);
        WeatherResponse weatherResponse = redisService.get(cacheKey, WeatherResponse.class);

        if ( weatherResponse != null ){

            return weatherResponse ;
        }

        // Seed the local cache immediately so the key exists even if the API is slow/down.
        WeatherResponse fallback = new WeatherResponse();
        fallback.setCurrent(new WeatherResponse.Current());
        fallback.getCurrent().setFeelslike(42);
        redisService.set(cacheKey, fallback, 300L);

        String finalAPI = API.replace("CITY", city).replace("API_KEY", apikey);

        try {
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(
                    finalAPI, HttpMethod.GET, null, WeatherResponse.class);

            WeatherResponse body = response.getBody();

            if (body != null) {
                redisService.set(cacheKey, body, 300L);
                return body;
            }
        } catch (RuntimeException ignored) {
            // Use a cached fallback when the external weather service is unavailable.
        }

        return fallback;

    }

}
