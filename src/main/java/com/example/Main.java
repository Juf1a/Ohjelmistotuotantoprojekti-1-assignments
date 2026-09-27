package com.example;

public class Main {
    public static void main(String[] args) {
        TemperatureConverter converter = new TemperatureConverter();
        System.out.println("32F to C: " + converter.fahrenheitToCelsius(32));
        System.out.println("0C to F: " + converter.celsiusToFahrenheit(0));
        System.out.println("Is -50C extreme? " + converter.isExtremeTemperature(-50));
        System.out.println("300K to C: " + converter.kelvinToCelsius(300));
    }
}