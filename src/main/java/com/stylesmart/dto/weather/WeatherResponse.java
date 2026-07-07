package com.stylesmart.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeatherResponse DTO - Represents the weather data sent to the frontend
 * 
 * This DTO contains all the weather information the frontend needs to display:
 * - Current temperature in Celsius
 * - Weather condition (sunny, cloudy, rainy, etc.)
 * - Location name (city, country)
 * - Whether the location is verified
 * - Weather icon code for UI display
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    
    // Current temperature in Celsius
    private double temperature;
    
    // Weather condition description (e.g., "partly cloudy", "light rain", "clear sky")
    private String condition;
    
    // Main weather category (e.g., "Clouds", "Rain", "Clear", "Snow")
    private String mainCondition;
    
    // Location name (e.g., "London, GB" or "New York, US")
    private String location;
    
    // Whether the location is verified (true if coordinates match the returned location)
    private boolean verifiedLocation;
    
    // Weather icon code from OpenWeatherMap (e.g., "01d", "10n")
    // Frontend can use this to display weather icons
    private String icon;
    
    // Feels like temperature in Celsius
    private double feelsLike;
    
    // Humidity percentage (0-100)
    private int humidity;
    
    // Wind speed in meters per second
    private double windSpeed;
    
    // Weather forecast prediction (e.g., "rain expected in 2 hours")
    // This can be enhanced with forecast API later
    private String forecast;
}
