package com.practice.service;

import com.practice.data.currency.PrivatBankData;
import com.practice.data.social.UsersPhotoData;
import com.practice.data.weather.WeatherData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XmlDataSerializer {
    public void serialize(String xmlData, Class<?> cls) {
        StringBuilder builder = new StringBuilder().append("data/");

        if (cls.equals(PrivatBankData.class))
            builder.append("currency_data/").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).concat("_currency_data").replace(':', '-')).append(".xml");
        else if (cls.equals(WeatherData.class))
            builder.append("weather_data/").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).concat("_weather_data").replace(':', '-')).append(".xml");
        else if(cls.equals(UsersPhotoData.class))
            builder.append("social_data/").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).concat("_social_data").replace(':', '-')).append(".xml");

        try (FileWriter fileWriter = new FileWriter(builder.toString())) {
            fileWriter.write(xmlData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object deserialize(String fileName, Class<?>... classes) {
        try (final FileReader reader = new FileReader(fileName)) {
            JAXBContext context = JAXBContext.newInstance(classes);
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            return unmarshaller.unmarshal(reader);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }
}
