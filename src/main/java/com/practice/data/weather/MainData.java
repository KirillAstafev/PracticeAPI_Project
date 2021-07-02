package com.practice.data.weather;

import javax.xml.bind.annotation.XmlElement;

public class MainData {
    @XmlElement(name = "temp_min")
    private double tempMin;
    @XmlElement(name = "temp_max")
    private double tempMax;

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }
}
