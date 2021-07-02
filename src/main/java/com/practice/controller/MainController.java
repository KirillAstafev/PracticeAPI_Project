package com.practice.controller;

import com.practice.data.currency.CurrencyData;
import com.practice.data.currency.PrivatBankData;
import com.practice.data.social.*;
import com.practice.data.weather.DayWeatherData;
import com.practice.data.weather.WeatherData;
import com.practice.service.UrlService;
import com.practice.service.XmlDataSerializer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static javafx.scene.chart.XYChart.Data;
import static javafx.scene.chart.XYChart.Series;

public class MainController implements Initializable {
    private UrlService urlService;
    private XmlDataSerializer serializer;
    private ExecutorService executorService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private List<String> currentDates = new ArrayList<>();

    @FXML
    private Button usdSearchButton;
    @FXML
    private Button weatherSearchButton;
    @FXML
    public Button photoSearchButton;
    @FXML
    private DatePicker usdDatePicker;
    @FXML
    private LineChart<String, Double> currencyLineChart;
    @FXML
    private LineChart<String, Double> weatherLineChart;
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField usersNamesTextField;
    @FXML
    private VBox photoBox;
    @FXML
    private GridPane photoPane;

    public void exitMenuItemAction(ActionEvent actionEvent) {
        executorService.shutdown();
        Platform.exit();
    }

    public void usdSearchAction(ActionEvent actionEvent) {
        try {
            executorService.execute(() -> urlService.receiveUSDData(() -> {
                synchronized (currentDates) {
                    currentDates.clear();
                }
            }, currentDates));

        } catch (RuntimeException exc) {
            exc.printStackTrace();
            new Alert(Alert.AlertType.WARNING, "ВЫБЕРИТЕ ДАТУ(ДАТЫ) ДЛЯ ЗАПРОСА").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchButtonsSetup();
        serviceSetup();
        datePickerSetup();
    }

    private void datePickerSetup() {
        usdDatePicker.setOnAction(event -> {
            String date = usdDatePicker.getValue().format(formatter);
            currentDates.add(date);
        });
    }

    private void serviceSetup() {
        urlService = new UrlService();
        executorService = Executors.newFixedThreadPool(4);
        serializer = new XmlDataSerializer();
    }

    private void searchButtonsSetup() {
        File imageFile = new File("./src/main/resources/images/search.png");
        Image image = new Image(imageFile.toURI().toString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(20.0);
        imageView.setFitHeight(20.0);

        usdSearchButton.setGraphic(imageView);
        weatherSearchButton.setGraphic(imageView);
        photoSearchButton.setGraphic(imageView);
    }

    public void showCurrencyDataAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("ВЫБОР ФАЙЛА");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            PrivatBankData bankData = (PrivatBankData) serializer.deserialize(file.getAbsolutePath(), PrivatBankData.class);
            drawCurrencyData(bankData);
        }
    }

    private void drawCurrencyData(PrivatBankData bankData) {
        Series<String, Double> series = new Series<>();
        series.setName("Курс доллара");

        currencyLineChart.getData().clear();
        for (CurrencyData currencyData : bankData.getCurrencyDataList()) {
            series.getData().add(new Data<>(currencyData.getDate(), currencyData.getExchangeRates()
                    .stream()
                    .filter(exchangeRate -> exchangeRate.getCurrency() != null && exchangeRate.getCurrency().equals("USD"))
                    .findFirst()
                    .orElseThrow(RuntimeException::new).getSaleRateNB()));
        }

        currencyLineChart.getData().add(series);
    }

    public void showWeatherDataAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("ВЫБОР ФАЙЛА");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            WeatherData weatherData = (WeatherData) serializer.deserialize(file.getAbsolutePath(), WeatherData.class);
            drawWeatherData(weatherData);
        }
    }

    private void drawWeatherData(WeatherData weatherData) {
        Series<String, Double> minSeries = new Series<>();
        minSeries.setName("Минимальная температура");

        Series<String, Double> maxSeries = new Series<>();
        maxSeries.setName("Максимальная температура");

        weatherLineChart.getData().clear();
        for (DayWeatherData dayWeatherData : weatherData.getWeatherDataList()) {
            String instant = Instant.ofEpochSecond(dayWeatherData.getDt()).toString();
            String dateAndTime = instant.substring(0, instant.length() - 1).replace('T', ' ');

            minSeries.getData().add(new Data<>(dateAndTime, dayWeatherData.getMainData().getTempMin() - 273.15));
            maxSeries.getData().add(new Data<>(dateAndTime, dayWeatherData.getMainData().getTempMax() - 273.15));
        }

        weatherLineChart.getData().add(minSeries);
        weatherLineChart.getData().add(maxSeries);
    }

    public void searchWeatherAction(ActionEvent actionEvent) {
        String targetCity = cityTextField.getText();
        if (targetCity.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "ВЫБЕРИТЕ ГОРОД ДЛЯ ПРОГНОЗА").show();
            return;
        }

        executorService.execute(() -> urlService.receiveWeatherData(targetCity));
    }

    public void searchUsersPhotosAction(ActionEvent actionEvent) {
        String userNames = usersNamesTextField.getText();
        if (userNames.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "ВВЕДИТЕ ИМЯ ХОТЯ БЫ ОДНОГО ПОЛЬЗОВАТЕЛЯ").show();
            return;
        }

        executorService.execute(() -> urlService.receivePhotoData(userNames.split(";")));
    }

    public void showUsersPhotosAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("ВЫБОР ФАЙЛА");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            UsersPhotoData photoData = (UsersPhotoData) serializer.deserialize(file.getAbsolutePath(), UsersPhotoData.class);
            drawImages(getImages(photoData));
        }
    }

    private List<Image> getImages(UsersPhotoData photoData) {
        List<Image> imageList = new ArrayList<>();

        for (PhotoArray photoArray : photoData.getPhotoArrayList()) {
            for (Response response : photoArray.getResponseList()) {
                for (Item item : response.getItemList()) {
                    final Size size = item.getSizeList()
                            .stream()
                            .filter(s -> s.getType().equals("p"))
                            .findFirst()
                            .orElseThrow(RuntimeException::new);

                    try {
                        imageList.add(new Image(new URL(size.getUrl()).openStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return imageList;
    }

    private void drawImages(List<Image> imageList) {
        int xIndex = 0, yIndex = 0;
        final List<ImageView> imageViewList = imageList.stream().map(ImageView::new).collect(Collectors.toList());

        for (ImageView imageView : imageViewList) {
            if (yIndex == 6) {
                xIndex++;
                yIndex = 0;
            }

            photoPane.add(imageView, yIndex, xIndex);
            yIndex++;
        }
    }
}
