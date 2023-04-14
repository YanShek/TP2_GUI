package MVC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlLoc = getClass().getResource("/Vue.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLoc);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Inscription a un cours");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
