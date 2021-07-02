package com.practice.data.social;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Response {
    @XmlElement(name = "items")
    private List<Item> itemList;

    public List<Item> getItemList() {
        return itemList;
    }
}
