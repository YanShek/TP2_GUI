package MVC;

import server.Server;
import server.ServerLauncher;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Model {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Connecte le GUI à notre serveur ce qui permet l'échange d'informations, notamment la reception de cours disponibles
     * ainsi que l'inscription au cours
     */
    public void connectServer(){
        try {

            Socket socket = new Socket("localhost", ServerLauncher.PORT);
             outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction sert à charger les cours enregistrés dans le server selon la session spécifiée.
     * @param session La session pour laquelle on veut afficher les cours
     * @return ArrayList des cours
     */
    public ArrayList<Course> loadCours(String session){
        ArrayList<Course> cours = new ArrayList<>();
        try {
            outputStream.writeObject(Server.LOAD_COMMAND + " " + session);
            outputStream.flush();

            cours = (ArrayList<Course>) inputStream.readObject();

        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        finally {
            return cours;
        }
    }

    /**
     * Cette fonction dit au serveur de faire une inscription avec les paramètres entrées
     * @param nom Nom saisi par le clavier
     * @param prenom Prenom saisi par le clavier
     * @param email Courriel saisi par le clavier
     * @param matricule Numero de matricule saisi en entree
     * @param code Code du cours
     * @param session   La session dans laquelle l'inscription a lieu
     * @return Message de succes ou d'erreur
     */
    public String inscription(String nom, String prenom,String email, String matricule, String code, String session){
        RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, new Course("", code, session));
        String message = "";
        try {
            outputStream.writeObject(Server.REGISTER_COMMAND);
            outputStream.flush();
            outputStream.writeObject(form);
            outputStream.flush();

            message = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            message = e.getMessage();
        }
        finally {
            return message;
        }
    }

    /**
     * Vérification de la validité du format des informations entrées par l'usager.
     * @param entree Information saisie au clavier
     * @param type Type d'entree: nom, prenom, courriel ou matricule
     * @return Retourne une erreur ou rien
     */
    public String verifEntrees(String entree, String type){
        String message = "";

        switch (type){
            case "nom" -> {
                if (!entree.matches("^[A-Za-z]+$")){
                    message = "Erreur/Nom invalide/Le nom doit contenir uniquement des lettres.";
                }
            }
            case "prenom" -> {
                if (!entree.matches("^[A-Za-z]+$")){
                    message = "Erreur/Prenom invalide/Le prenom doit contenir uniquement des lettres.";
                }
            }
            case "email"->{
                if (!entree.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
                    message = "Erreur/Courriel invalide/Veuillez entrer un courriel valide";
                }
            }
            case "matricule"-> {
                if (!entree.matches("^\\d{6}$")) {
                    message = "Erreur/Matricule invalide/Le matricule doit être un entier de 6 chiffres.";
                }
            }


        }
        return message;
    }
}
