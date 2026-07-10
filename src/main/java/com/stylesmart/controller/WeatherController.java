package com.stylesmart.controller;

import com.stylesmart.dto.weather.WeatherResponse;
import com.stylesmart.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * WeatherController exposes HTTP REST endpoints for weather data.
 * 
 * This controller provides weather information based on geographic coordinates.
 * The frontend should send the user's current location (latitude and longitude)
 * and the backend will fetch weather data from OpenWeatherMap API.
 * 
 * Available endpoints:
 * 1. GET /api/weather/current - Get current weather for a location
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    // Inject WeatherService which handles the actual weather data fetching
    @Autowired
    private WeatherService weatherService;

    /**
     * Endpoint: Get Current Weather
     * 
     * This endpoint fetches current weather data for a given location.
     * The frontend should obtain the user's current location using browser
     * geolocation API and send the coordinates to this endpoint.
     *

     * How to use in Postman:
     *   Method:  GET
     *   URL:     http://localhost:8082/api/weather/current?lat=51.5074&lon=-0.1278
     *   Headers: (none required - this endpoint is public)

     * How to use from Frontend (JavaScript example):
     *   // Get user's location using browser geolocation API
     *   navigator.geolocation.getCurrentPosition((position) => {
     *     const lat = position.coords.latitude;
     *     const lon = position.coords.longitude;
     *     
     *     // Call the backend API
     *     fetch(`http://localhost:8082/api/weather/current?lat=${lat}&lon=${lon}`)
     *       .then(response => response.json())
     *       .then(data => {
     *         console.log('Temperature:', data.temperature);
     *         console.log('Condition:', data.condition);
     *         console.log('Location:', data.location);
     *       });
     *   });
     * 
     * Response example:
     *   {
     *     "temperature": 29.0,
     *     "condition": "partly cloudy",
     *     "mainCondition": "Clouds",
     *     "location": "London, GB",
     *     "verifiedLocation": true,
     *     "icon": "03d",
     *     "feelsLike": 28.5,
     *     "humidity": 65,
     *     "windSpeed": 3.5,
     *     "forecast": "Cloudy conditions expected"
     *   }
     * 
     * @param latitude - The latitude coordinate (required query parameter)
     * @param longitude - The longitude coordinate (required query parameter)
     * @return WeatherResponse containing all weather information
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(
            @RequestParam(name = "lat") Double latitude,
            @RequestParam(name = "lon") Double longitude
    ) {
        try {
            // Step 1: Validate that latitude and longitude are provided
            if (latitude == null || longitude == null) {
                return ResponseEntity.badRequest().body("Latitude and longitude are required parameters");
            }

            // Step 2: Validate coordinate ranges
            // Latitude must be between -90 and 90
            if (latitude < -90 || latitude > 90) {
                return ResponseEntity.badRequest().body("Invalid latitude. Must be between -90 and 90");
            }

            // Longitude must be between -180 and 180
            if (longitude < -180 || longitude > 180) {
                return ResponseEntity.badRequest().body("Invalid longitude. Must be between -180 and 180");
            }

            // Step 3: Call the service to fetch weather data from OpenWeatherMap
            WeatherResponse weatherResponse = weatherService.getCurrentWeather(latitude, longitude);

            // Step 4: Return 200 OK with the weather data
            return ResponseEntity.ok(weatherResponse);

        } catch (Exception e) {
            // Step 5: If something goes wrong, return a 500 error with the error message
            return ResponseEntity.internalServerError().body("Error fetching weather data: " + e.getMessage());
        }
    }

    /**
     * Endpoint: Get Weather by City Name (Alternative method)
     * 
     * This endpoint allows fetching weather by city name instead of coordinates.
     * This is useful for testing or when exact coordinates are not available.
     * 
     * How to use in Postman:
     *   Method:  GET
     *   URL:     http://localhost:8082/api/weather/by-city?city=London
     *   Headers: (none required)
     * 
     * @param city - The city name (required query parameter)
     * @return WeatherResponse containing all weather information
     */
    @GetMapping("/by-city")
    public ResponseEntity<?> getWeatherByCity(@RequestParam(name = "city") String city) {
        try {
            // Step 1: Validate that city name is provided
            if (city == null || city.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("City name is required");
            }

            // Note: This would require additional implementation in WeatherService
            // to support city-based lookups. For now, we return a message indicating
            // that only coordinate-based lookups are currently supported.
            return ResponseEntity.badRequest().body(
                "City-based weather lookup not yet implemented. " +
                "Please use /api/weather/current with latitude and longitude parameters."
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching weather data: " + e.getMessage());
        }
    }
}
