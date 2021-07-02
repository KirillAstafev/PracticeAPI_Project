package com.practice.data.social;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Item {
    @XmlElement(name = "sizes")
    private List<Size> sizeList;

    public List<Size> getSizeList() {
        return sizeList;
    }
}
