package com.practice.data.weather;

import javax.xml.bind.annotation.XmlElement;

public class DayWeatherData {
    @XmlElement(name = "dt")
    private long dt;
    @XmlElement(name = "main")
    private MainData mainData;

    public long getDt() {
        return dt;
    }

    public MainData getMainData() {
        return mainData;
    }
}
