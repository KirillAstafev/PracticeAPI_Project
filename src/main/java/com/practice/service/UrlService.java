package com.practice.service;

import com.practice.Callback;
import com.practice.data.currency.PrivatBankData;
import com.practice.data.social.UsersPhotoData;
import com.practice.data.weather.WeatherData;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UrlService {
    private static final String USD_RESOURCE = "https://api.privatbank.ua/p24api/exchange_rates?json";
    private static final String WEATHER_RESOURCE = "http://api.openweathermap.org/data/2.5/forecast/";
    private static final String VK_RESOURCE = "https://api.vk.com/method/photos.getAll?";

    private static final String WEATHER_API_KEY = "1116f3e52185cd62cc6323a25f0a340d";
    private static final String VK_ACCESS_TOKEN = "7ec9ce4710a15240a3ebdf3f73bc276e591386d61c83b2566fd207121e4047ad71ea2d5c80916cea99849";

    private final JsonToXmlConverter converter;
    private final XmlDataSerializer serializer;

    public UrlService() {
        converter = new JsonToXmlConverter();
        serializer = new XmlDataSerializer();
    }

    public void receiveUSDData(Callback callback, List<String> dates) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);

        AsyncWebResource resource = client.asyncResource(USD_RESOURCE);
        List<Future<String>> futures = Collections.synchronizedList(new ArrayList<>());

        dates.parallelStream().forEach(date -> {
            Future<String> jsonResult = resource.queryParam("date", date).accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            futures.add(jsonResult);
        });

        callback.onDataReceived();

        List<String> results = futures.parallelStream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("ERROR!");
        }).collect(Collectors.toList());

        serializer.serialize(converter.convert(results, "privatbankdata", "currencydata"), PrivatBankData.class);
    }

    public void receiveWeatherData(String cityName) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);

        WebResource resource = client
                .resource(WEATHER_RESOURCE)
                .queryParam("q", cityName)
                .queryParam("appid", WEATHER_API_KEY);

        String jsonResult = resource.get(String.class).replaceAll("3h", "h");
        serializer.serialize(converter.convert(jsonResult, "weatherdata"), WeatherData.class);
    }

    public void receivePhotoData(String[] userNames) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);

        List<Long> ownerIdList = getOwnerIDList(userNames, client);

        List<Future<String>> photoResults = Collections.synchronizedList(new ArrayList<>());
        AsyncWebResource photoAsyncWebResource = client.asyncResource(VK_RESOURCE);

        ownerIdList.parallelStream().forEach(ownerId -> {
            final Future<String> future = photoAsyncWebResource
                    .queryParam("owner_id", ownerId.toString())
                    .queryParam("access_token", VK_ACCESS_TOKEN)
                    .queryParam("v", "5.131")
                    .get(String.class);

            photoResults.add(future);
        });

        List<String> resultData = photoResults.stream().map(photoResult -> {
            try {
                return photoResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            throw new RuntimeException("ERROR!");
        }).collect(Collectors.toList());

        int i = 10;
        serializer.serialize(converter.convert(resultData, "usersphotodata", "photoarray"), UsersPhotoData.class);
    }

    private List<Long> getOwnerIDList(String[] userNames, Client client) {
        List<Future<String>> futureList = Collections.synchronizedList(new ArrayList<>());
        List<Long> ownerIdList = Collections.synchronizedList(new ArrayList<>());

        AsyncWebResource asyncResource = client.asyncResource("https://api.vk.com/method/utils.resolveScreenName?");
        for (String userName : userNames) {
            final Future<String> future = asyncResource
                    .queryParam("screen_name", userName)
                    .queryParam("access_token", VK_ACCESS_TOKEN)
                    .queryParam("v", "5.131")
                    .get(String.class);
            futureList.add(future);
        }


        futureList.parallelStream().forEach(ownerIdInfoFuture -> {
            try {
                JSONObject object = new JSONObject(ownerIdInfoFuture.get());
                final long ownerId = object.getJSONObject("response").getLong("object_id");

                ownerIdList.add(ownerId);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return ownerIdList;
    }
}
