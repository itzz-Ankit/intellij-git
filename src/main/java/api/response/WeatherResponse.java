package api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response returned by the Weatherstack current-weather API.
 */
@Getter
@Setter
public class WeatherResponse {


    public Current current;

    @Getter
    @Setter


    public static class Current {
        public int temperature;

        @JsonProperty("weather_descriptions")
        public List<String> weather_descriptions;


        public int feelslike;

    }

}