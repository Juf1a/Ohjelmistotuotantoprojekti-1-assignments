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
}
