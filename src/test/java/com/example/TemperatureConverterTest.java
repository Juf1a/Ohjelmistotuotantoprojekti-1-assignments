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
}
