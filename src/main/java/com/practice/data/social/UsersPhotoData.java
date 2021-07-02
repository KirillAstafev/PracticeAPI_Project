package com.practice.data.social;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "usersphotodata")
public class UsersPhotoData {
    @XmlElement(name = "photoarray")
    private List<PhotoArray> photoArrayList;

    public List<PhotoArray> getPhotoArrayList() {
        return photoArrayList;
    }
}
