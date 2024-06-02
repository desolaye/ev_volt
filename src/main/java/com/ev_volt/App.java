package com.ev_volt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 *
 * @description Отправная точка приложения. Инициализация сцены отображения, подгрузка ассетов
 */
public class App extends Application {
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Image getImageResource(String imageName) {
        return new Image(Objects.requireNonNull(App.class.getResourceAsStream("/assets/" + imageName + ".png")));
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFXML("root-view"));
        Image icon = getImageResource("app-icon");

        stage.getIcons().add(icon);
        stage.setTitle("EV Volt");
        stage.setScene(scene);
        stage.show();
    }
}
