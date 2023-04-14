package MVC;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import server.ServerLauncher;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Model model = new Model();
    private ArrayList<Course> cours = new ArrayList<>();
    private VBox selectedVBox;
    @FXML
    ComboBox<String> sessionBox;
    @FXML
    VBox displayCours;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        model.connectServer();
    }

    /**
     * Gere les clic sur les VBox corresondant aux cours
     * @param event Source du clic
     */
    @FXML
    public void handleCoursClicked(MouseEvent event){
        VBox clickedCours = (VBox) event.getSource();

        if (selectedVBox != null) {
            // Reinitialize la couleur de fond du VBox a blanc
            selectedVBox.setStyle("-fx-background-color: #FFFFFF");
        }

        // Met la couleur du VBox clique a une variante de bleu
        clickedCours.setStyle("-fx-background-color: #AEC4FF");
        selectedVBox = clickedCours; // Update the selected VBox
    }

    /**
     * Loads and displays the courses in the GUI
     */
    @FXML
    public void loadCours(){
        cours = model.loadCours(sessionBox.getValue());
        displayCours.getChildren().clear(); // Vide les enfants du vbox existant
        for (Course course: cours){
            Text text = new Text(course.getCode() +"\t\t\t" + course.getName());
            VBox vBox = new VBox(text);
            vBox.setOnMouseClicked(this::handleCoursClicked); // Met le handle click event
            vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");
            displayCours.getChildren().add(vBox);
        }
    }

}
