package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CryptoPriceHistoryApp extends Application {

    private static final String API_KEY = "88DAEFC0-1AAF-4C6E-A2B8-46935AC05EA5"; // Replace with your CoinAPI key
    private static final String BASE_URL = "https://rest.coinapi.io/v1/exchangerate/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ChoiceBox<String> coinChoiceBox = new ChoiceBox<>();
        coinChoiceBox.getItems().add("BTC"); // Placeholder item
        coinChoiceBox.setValue("BTC"); // Initial selection

        Button getPriceHistoryBtn = new Button("Get Price History");
        getPriceHistoryBtn.getStyleClass().add("button"); // Apply the Button styles defined in CSS
        getPriceHistoryBtn.setOnAction(e -> {
            String selectedCoin = coinChoiceBox.getValue();
            try {
                String json = makeAPICall(BASE_URL + selectedCoin + "/USD");
                double price = parsePriceData(json);

                if (price != 0) {
                    showLineChart(selectedCoin, price);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10, coinChoiceBox, getPriceHistoryBtn);
        root.getStyleClass().add("vbox-container"); // Apply the VBox styles defined in CSS

        Scene scene = new Scene(root, 400, 300);

        // Link the styles.css file using the file path
        String cssFilePath = "file:///C:/Users/Jagermeister/IdeaProjects/Assignment2/src/main/java/org/example/styles.css";
        scene.getStylesheets().add(cssFilePath);

        primaryStage.setTitle("Cryptocurrency Price History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String makeAPICall(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader("X-CoinAPI-Key", API_KEY);

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity);
        httpClient.close();

        return json;
    }

    private double parsePriceData(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (jsonObject != null && jsonObject.has("rate")) {
            return jsonObject.get("rate").getAsDouble();
        } else {
            System.out.println("No price data available for this cryptocurrency.");
            return 0;
        }
    }

    private void showLineChart(String coinId, double price) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Price History for " + coinId);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, price));

        lineChart.getData().add(series);

        Stage chartStage = new Stage();
        chartStage.setScene(new Scene(lineChart, 800, 600));
        chartStage.setTitle("Price History Chart for " + coinId);
        chartStage.show();
    }
}
