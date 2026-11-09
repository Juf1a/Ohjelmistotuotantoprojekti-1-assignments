package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TemperatureConverterTest {
    
    private  TemperatureConverter converter = new TemperatureConverter();

    @Test
    public void testFahrenheitToCelsius1() {
        double result1 = converter.fahrenheitToCelsius(32);
        assertEquals(0, result1);
    }

    @Test
    public void testFahrenheitToCelsius2(){
        double result2 = converter.fahrenheitToCelsius(212);
        assertEquals(100, result2);
    }

    @Test 
    public void testCelsiusToFahrenheit1(){
        double result3 = converter.celsiusToFahrenheit(0);
        assertEquals(32, result3);
    }

    @Test 
    public void testCelsiusToFahrenheit2(){
        double result4 = converter.celsiusToFahrenheit(100);
        assertEquals(212, result4);
    }

    @Test
    public void testIsExtremeTemperature(){
        boolean result5 = converter.isExtremeTemperature(-50);
        assertTrue(result5);
    }

    @Test
    public void testIsExtremeTemperature2(){
        boolean result6 = converter.isExtremeTemperature(25);
        assertFalse(result6);
    }

    @Test
    public void testKelvinToCelsius(){
        double result7 = converter.kelvinToCelsius(300);
        assertEquals(26.85, result7, 0.0001);
    }

    @Test
    public void testIsExtremeTemperature3(){
        boolean result8 = converter.isExtremeTemperature(60);
        assertTrue(result8);
    }

    @Test
    public void testFahrenheitToCelsius3(){
        double result9 = converter.fahrenheitToCelsius(-40);
        assertEquals(-40, result9, 0.0001);
    }

    @Test
    public void testFahrenheitToCelsius4(){
        double result10 = converter.fahrenheitToCelsius(98.6);
        assertEquals(37, result10, 0.0001);
    }

    @Test
    public void testCelsiusToFahrenheit3(){
        double result11 = converter.celsiusToFahrenheit(-40);
        assertEquals(-40, result11, 0.0001);
    }

    @Test
    public void testCelsiusToFahrenheit4(){
        double result12 = converter.celsiusToFahrenheit(37);
        assertEquals(98.6, result12, 0.0001);
    }

    @Test
    public void testIsExtremeTemperatureLowerBoundary(){
        // -40 itself is not extreme, only below it
        assertFalse(converter.isExtremeTemperature(-40));
    }

    @Test
    public void testIsExtremeTemperatureUpperBoundary(){
        // 50 itself is not extreme, only above it
        assertFalse(converter.isExtremeTemperature(50));
    }

    @Test
    public void testIsExtremeTemperatureJustBelowLower(){
        assertTrue(converter.isExtremeTemperature(-41));
    }

    @Test
    public void testIsExtremeTemperatureJustAboveUpper(){
        assertTrue(converter.isExtremeTemperature(51));
    }

    // --- generic conversion between unit keys / units ---

    private static Unit unit(String key, String category, double toBase) {
        return new Unit(0, key, category, "", toBase, key, "");
    }

    private final Unit celsius = unit("celsius", "temperature", 0);
    private final Unit fahrenheit = unit("fahrenheit", "temperature", 0);
    private final Unit kelvin = unit("kelvin", "temperature", 0);

    @Test
    public void testCelsiusToKelvin() {
        assertEquals(273.15, converter.celsiusToKelvin(0), 0.0001);
    }

    @Test
    public void testConvertTemperatureAllPairs() {
        assertEquals(212, converter.convertTemperature(100, "celsius", "fahrenheit"), 0.0001);
        assertEquals(373.15, converter.convertTemperature(100, "celsius", "kelvin"), 0.0001);
        assertEquals(0, converter.convertTemperature(32, "fahrenheit", "celsius"), 0.0001);
        assertEquals(273.15, converter.convertTemperature(32, "fahrenheit", "kelvin"), 0.0001);
        assertEquals(-273.15, converter.convertTemperature(0, "kelvin", "celsius"), 0.0001);
        assertEquals(-459.67, converter.convertTemperature(0, "kelvin", "fahrenheit"), 0.0001);
        assertEquals(42, converter.convertTemperature(42, "kelvin", "kelvin"), 0.0001);
    }

    @Test
    public void testUnknownTemperatureUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> converter.toCelsius(1, "rankine"));
        assertThrows(IllegalArgumentException.class, () -> converter.fromCelsius(1, "rankine"));
    }

    @Test
    public void testConvertTemperatureUnits() {
        assertEquals(212, converter.convert(100, celsius, fahrenheit), 0.0001);
        assertEquals(0, converter.convert(273.15, kelvin, celsius), 0.0001);
    }

    @Test
    public void testConvertSpeedDistanceTime() {
        Unit ms = unit("metre_per_second", "speed", 1);
        Unit kmh = unit("kilometre_per_hour", "speed", 1 / 3.6);
        Unit mph = unit("mile_per_hour", "speed", 0.44704);
        assertEquals(36, converter.convert(10, ms, kmh), 0.0001);
        assertEquals(62.1371, converter.convert(100, kmh, mph), 0.0001);

        Unit km = unit("kilometre", "distance", 1000);
        Unit mile = unit("mile", "distance", 1609.344);
        assertEquals(1, converter.convert(1.609344, km, mile), 0.0001);

        Unit minute = unit("minute", "time", 60);
        Unit hour = unit("hour", "time", 3600);
        assertEquals(90, converter.convert(1.5, hour, minute), 0.0001);
    }

    @Test
    public void testConvertAcrossCategoriesThrows() {
        Unit km = unit("kilometre", "distance", 1000);
        assertThrows(IllegalArgumentException.class, () -> converter.convert(1, celsius, km));
    }

    @Test
    public void testIsExtremeUsesSourceUnit() {
        assertTrue(converter.isExtreme(130, fahrenheit));   // 54.4 °C
        assertFalse(converter.isExtreme(300, kelvin));      // 26.85 °C
        assertTrue(converter.isExtreme(-45, celsius));
        assertFalse(converter.isExtreme(1000, unit("kilometre", "distance", 1000)));
    }
}
