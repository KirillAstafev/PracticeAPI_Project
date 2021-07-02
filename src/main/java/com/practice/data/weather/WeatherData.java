package com.practice.data.weather;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "weatherdata")
public class WeatherData {
    @XmlElement(name = "list")
    private List<DayWeatherData> weatherDataList;

    public List<DayWeatherData> getWeatherDataList() {
        return weatherDataList;
    }
}
