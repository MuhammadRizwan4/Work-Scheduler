package soa.work.scheduler.Supportclasses;


import soa.work.scheduler.R;

public class WeatherIcon {
    public static int getWeatherIcon(String iconId) {
        int resource;
        String weather_text;
        switch (iconId) {
            case "11d":
                resource = R.drawable.thunder_day;
                weather_text = "it's thunder today";
                break;
            case "11n":
                resource = R.drawable.thunder_night;
                weather_text = "it's thunder today";
                break;
            case "09d":
                resource = R.drawable.rainy_weather;
                weather_text = "it's rainy today";
                break;
            case "09n":
            case "10n":
                resource = R.drawable.rainy_night;
                weather_text = "it's rainy today";
                break;
            case "10d":
                resource = R.drawable.rainy_day;
                weather_text = "it's rainy today";
                break;
            case "13d":
                resource = R.drawable.rain_snow;
                weather_text ="it's shower today";
                break;
            case "13n":
                resource = R.drawable.rain_snow_night;
                weather_text = "it's shower today";
                break;
            case "50d":
                resource = R.drawable.haze_day;
                weather_text = "it's hazy today";
                break;
            case "50n":
                resource = R.drawable.haze_night;
                weather_text = "it's hazy today";
                break;
            case "01d":
                resource = R.drawable.clear_day;
                weather_text = "it's clear today";
                break;
            case "01n":
                resource = R.drawable.clear_night;
                weather_text = "it's clear today";
                break;
            case "02d":
                resource = R.drawable.partly_cloudy;
                weather_text = "it's cloudy today";
                break;
            case "02n":
                resource = R.drawable.partly_cloudy_night;
                weather_text = "it's cloudy today";
                break;
            case "03d":
            case "03n":
                resource = R.drawable.cloudy_weather;
                weather_text = "it's cloudy today";
                break;
            case "04d":
                resource = R.drawable.mostly_cloudy;
                weather_text = "it's cloudy today";
                break;
            case "04n":
                resource = R.drawable.mostly_cloudy_night;
                weather_text = "it's cloudy today";
                break;
            default:
                resource = R.drawable.unknown;
        }
        return resource;
    }
}
