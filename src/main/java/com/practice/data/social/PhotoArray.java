package com.practice.data.social;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class PhotoArray {
    @XmlElement(name = "response")
    private List<Response> responseList;

    public List<Response> getResponseList() {
        return responseList;
    }
}
