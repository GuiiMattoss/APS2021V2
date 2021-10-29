package com.example.aps2021v2;

public class WeatherRVModal {

    private String time;
    private String temperatura;
    private String icon;
    private String windSpeed;

    public WeatherRVModal(String time, String temperatura, String icon, String windSpeed) {
        this.time = time;
        this.temperatura = temperatura;
        this.icon = icon;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
