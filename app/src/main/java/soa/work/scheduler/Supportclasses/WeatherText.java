package soa.work.scheduler.Supportclasses;


import soa.work.scheduler.R;

public class WeatherText {
    public static String getWeatherText(String iconId) {
        String weather_text;
        switch (iconId) {
            case "11d":
                weather_text = "it's thunder today";
                break;
            case "11n":
                weather_text = "it's thunder today";
                break;
            case "09d":
                weather_text = "it's rainy today";
                break;
            case "09n":
            case "10n":
                weather_text = "it's rainy today";
                break;
            case "10d":
                weather_text = "it's rainy today";
                break;
            case "13d":
                weather_text ="it's shower today";
                break;
            case "13n":
                weather_text = "it's shower today";
                break;
            case "50d":
                weather_text = "it's hazy today";
                break;
            case "50n":
                weather_text = "it's hazy today";
                break;
            case "01d":
                weather_text = "it's clear today";
                break;
            case "01n":
                weather_text = "it's clear today";
                break;
            case "02d":
                weather_text = "it's cloudy today";
                break;
            case "02n":
                weather_text = "it's cloudy today";
                break;
            case "03d":
            case "03n":
                weather_text = "it's cloudy today";
                break;
            case "04d":
                weather_text = "it's cloudy today";
                break;
            case "04n":
                weather_text = "it's cloudy today";
                break;
            default:
                weather_text = "not fetched";
        }
        return weather_text;
    }
}
