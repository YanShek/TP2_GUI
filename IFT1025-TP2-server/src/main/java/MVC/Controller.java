package MVC;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
    private ArrayList<VBox> coursContent = new ArrayList<>();
    @FXML
    ComboBox<String> sessionBox;
    @FXML
    VBox displayCours;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        model.connectServer();
    }

    /**
     * Ceci permet de saisir le cours et son sigle  qu'on a selectionne
     * @param event Source du clic, soit l'element clicke
     */
    @FXML
    public void handleCoursClicked(MouseEvent event){

        VBox clickedCours = (VBox) event.getSource();
        for (VBox vBox: coursContent){
            vBox.setStyle("-fx-background-color: #FFFFFF");
        }
        // On met un arriere plan bleu pour l'element selectionne
        clickedCours.setStyle("-fx-background-color: #AEC4FF;");
    }

    /**
     * Ceci permet l'affichage des cours dans le GUI et cree egalement les elements pour
     */
    @FXML
    public void loadCours(){
        cours = model.loadCours(sessionBox.getValue());
        for (Course course: cours){
            Label label = new Label(course.getCode() +"\t\t\t" + course.getName());
            System.out.println(course.getCode());
            VBox vBox = new VBox(label);
            // Ajoute l'attribut on clicked pour chaque VBox
            vBox.setOnMouseClicked(event -> {
                handleCoursClicked((MouseEvent) event.getTarget());
            });
            vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 1px;");
            coursContent.add(vBox);
            displayCours.getChildren().add(vBox);
        }
    }

}
