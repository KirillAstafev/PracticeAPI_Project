package com.practice.data.social;

import javax.xml.bind.annotation.XmlElement;

public class Size {
    @XmlElement(name = "width")
    private Integer width;

    @XmlElement(name = "height")
    private Integer height;

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "type")
    private String type;

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
}