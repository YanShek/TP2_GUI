package MVC;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import server.models.Course;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private Model model = new Model();
    private ArrayList<Course> cours = new ArrayList<>();
    private Course selectedCours;
    @FXML
    ComboBox<String> sessionBox;
    @FXML
    ListView<Course> displayCours; // Use ListView instead of VBox

    /**
     * Ajoute un element on clicked au listview des le chargement du fxml
     * @param url N/A
     * @param resourceBundle N/A
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Add listener permet de mettre a jour la variable displayCours lorsqu'il y a un changement, incluant les clics sur les cours
        displayCours.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != -1) {
                selectedCours = displayCours.getSelectionModel().getSelectedItem();
            }
        });
    }


    /**
     * Charge et affiche les cours dans le GUI
     */
    @FXML
    public void loadCours(){
        // On charge le serveur
        model.connectServer();
        // On prend la liste de cours
        cours = model.loadCours(sessionBox.getValue());

        // Mise a jour de ListView avec la liste de cours
        ObservableList<Course> courseList = FXCollections.observableArrayList(cours);
        displayCours.setItems(courseList);
        displayCours.setCellFactory(courseListView -> new CourseListCell()); // Mise en place d'un CellFactory pour afficher les cours
    }


    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTxtField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField matrTextField;

    /**
     * Cette methode prend les informations entrées dans les champs, vérifie et confirme ou crée une erreur apres avoir essayer de faire une inscription
     */
    @FXML
    public void inscriptionCours(){
        if (selectedCours != null) {
            String nom = nomTextField.getText();
            String prenom = prenomTxtField.getText();
            String email = emailTextField.getText();
            String matricule = matrTextField.getText();
            String code = selectedCours.getCode();
            String session = sessionBox.getValue();

            String[] verifNom = model.verifEntrees(nom, "nom").split("/");
            String[] verifPrenom = model.verifEntrees(prenom, "prenom").split("/");
            String[] verifEmail = model.verifEntrees(email, "email").split("/");
            String[] verifMatricule = model.verifEntrees(matricule, "matricule").split("/");

            if (verifNom[0].equals("Erreur")){
                showErrorAlert(verifNom[0], verifNom[1], verifNom[2]);
            }
            else if (verifPrenom[0].equals("Erreur")){
                showErrorAlert(verifPrenom[0], verifPrenom[1], verifPrenom[2]);
            }
            else if (verifEmail[0].equals("Erreur")){
                showErrorAlert(verifEmail[0], verifEmail[1], verifEmail[2]);
            }
            else if (verifMatricule[0].equals("Erreur")){
                showErrorAlert(verifMatricule[0], verifMatricule[1], verifMatricule[2]);
            }
            else{
                model.connectServer();
                String message = model.inscription(nom, prenom, email, matricule, code, session);
                if (message.split("\n")[0].equals("Erreur")){
                    showErrorAlert("Erreur", message.split("\n")[1], "Une erreur est survenu lors de l'enregistrement");
                } else {
                    showErrorAlert("Succes", message, "");
                }
            }

        }else {
            showErrorAlert("Echec", "Cours introuvable", "Veuillez slectionner un cours");

        }
    }

    //Creation d'une cell factory pour afficher dans listview.
    private class CourseListCell extends ListCell<Course> {
        @Override
        protected void updateItem(Course item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Text text = new Text(item.getCode() +"\t\t" + item.getName());
                text.setFont(Font.font("Calisto MT", 15));
                setGraphic(text);
            } else {
                Text text = new Text("Aucun cours a afficher");
                text.setStyle("-fx-text-fill: red;");
                setGraphic(null);
            }
        }
    }

    // Affiche une fenetre indiquant l'etat de l'inscription
    private void showErrorAlert(String titre, String entete, String contenu){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
