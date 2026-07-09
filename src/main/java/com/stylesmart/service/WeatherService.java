package com.stylesmart.service;

import com.stylesmart.dto.weather.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * WeatherService handles fetching weather data from OpenWeatherMap API
 * 
 * How it works:
 * 1. Receives latitude and longitude from the controller
 * 2. Makes HTTP request to OpenWeatherMap API with coordinates
 * 3. Parses the JSON response
 * 4. Extracts relevant weather data (temperature, condition, location)
 * 5. Returns a clean WeatherResponse DTO to the controller
 */
@Service
public class WeatherService {

    // Inject OpenWeatherMap API key from application.properties
    @Value("${weather.api.key}")
    private String apiKey;

    // Inject OpenWeatherMap base URL from application.properties
    @Value("${weather.api.base-url}")
    private String baseUrl;

    // RestTemplate for making HTTP requests to external APIs
    private final RestTemplate restTemplate = new RestTemplate();

    // ObjectMapper for parsing JSON responses
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetch current weather data for a given location (latitude and longitude)
     * 
     * @param latitude - The latitude coordinate (e.g., 51.5074 for London)
     * @param longitude - The longitude coordinate (e.g., -0.1278 for London)
     * @return WeatherResponse containing temperature, condition, location, etc.
     */
    public WeatherResponse getCurrentWeather(double latitude, double longitude) {
        try {
            // Step 1: Build the API URL with coordinates and API key
            // OpenWeatherMap endpoint: /data/2.5/weather?lat={lat}&lon={lon}&appid={key}&units=metric
            String url = String.format("%s?lat=%.6f&lon=%.6f&appid=%s&units=metric", 
                    baseUrl, latitude, longitude, apiKey);

            // Step 2: Make HTTP GET request to OpenWeatherMap API
            String response = restTemplate.getForObject(url, String.class);

            // Step 3: Parse the JSON response
            JsonNode root = objectMapper.readTree(response);

            // Step 4: Extract weather data from the JSON response
            // OpenWeatherMap response structure:
            // {
            //   "weather": [{ "main": "Clouds", "description": "partly cloudy", "icon": "03d" }],
            //   "main": { "temp": 29.0, "feels_like": 28.5, "humidity": 65 },
            //   "wind": { "speed": 3.5 },
            //   "name": "London",
            //   "sys": { "country": "GB" }
            // }

            // Extract temperature (in Celsius)
            double temperature = root.path("main").path("temp").asDouble();

            // Extract "feels like" temperature
            double feelsLike = root.path("main").path("feels_like").asDouble();

            // Extract humidity percentage
            int humidity = root.path("main").path("humidity").asInt();

            // Extract wind speed (m/s)
            double windSpeed = root.path("wind").path("speed").asDouble();

            // Extract weather condition (main category: Clouds, Rain, Clear, etc.)
            String mainCondition = root.path("weather").get(0).path("main").asText();

            // Extract weather description (partly cloudy, light rain, etc.)
            String condition = root.path("weather").get(0).path("description").asText();

            // Extract weather icon code (for frontend display)
            String icon = root.path("weather").get(0).path("icon").asText();

            // Extract location name (city)
            String city = root.path("name").asText();

            // Extract country code
            String country = root.path("sys").path("country").asText();

            // Combine city and country for full location name
            String location = city + ", " + country;

            // Step 5: Determine if location is verified
            // We consider it verified if we successfully got data from the API
            boolean verifiedLocation = true;

            // Step 6: Generate a simple forecast based on current conditions
            // This can be enhanced later with the forecast API
            String forecast = generateForecast(mainCondition);

            // Step 7: Build and return the WeatherResponse DTO
            WeatherResponse weatherResponse = new WeatherResponse();
            weatherResponse.setTemperature(temperature);
            weatherResponse.setCondition(condition);
            weatherResponse.setMainCondition(mainCondition);
            weatherResponse.setLocation(location);
            weatherResponse.setVerifiedLocation(verifiedLocation);
            weatherResponse.setIcon(icon);
            weatherResponse.setFeelsLike(feelsLike);
            weatherResponse.setHumidity(humidity);
            weatherResponse.setWindSpeed(windSpeed);
            weatherResponse.setForecast(forecast);

            // Log the weather data for debugging
            System.out.println("✅ Weather fetched for " + location + ": " + temperature + "°C, " + condition);

            return weatherResponse;

        } catch (Exception e) {
            // Log error and throw runtime exception
            System.err.println("❌ Error fetching weather data: " + e.getMessage());
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage());
        }
    }

    /**
     * Generate a simple forecast based on current weather conditions
     * 
     * This is a basic implementation. For accurate forecasts, you would need to
     * use OpenWeatherMap's forecast API (One Call API or 5-day forecast).
     * 
     * @param mainCondition - The main weather condition (Clouds, Rain, Clear, etc.)
     * @return A forecast description string
     */
    private String generateForecast(String mainCondition) {
        switch (mainCondition.toLowerCase()) {
            case "rain":
            case "drizzle":
            case "thunderstorm":
                return "Rain expected to continue";
            case "clouds":
                return "Cloudy conditions expected";
            case "clear":
                return "Clear skies expected";
            case "snow":
                return "Snow expected to continue";
            case "mist":
            case "fog":
            case "haze":
                return "Reduced visibility expected";
            default:
                return "Weather conditions stable";
        }
    }
}
