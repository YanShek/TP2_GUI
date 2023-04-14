package server;

import server.models.Course;
import server.models.RegistrationForm;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = ServerLauncher.PORT;
        Scanner sc = new Scanner(System.in);

        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) {
                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste de cours ");
                String session = "";
                System.out.println("Entrez la session");
                System.out.println("1-Automne");
                System.out.println("2- Hiver");
                System.out.println("3- Ete");
                String sessionChoix = sc.nextLine();

                switch (sessionChoix) {
                    case "1" -> session = "Automne";
                    case "2" -> session = "Hiver";
                    case "3" -> session = "Ete";
                    default -> {
                        System.out.println("Choix invalide");
                        continue;
                    }
                }
                try {
                    objectOutputStream.writeObject(Server.LOAD_COMMAND + " " + session);
                    objectOutputStream.flush();

                    //Affichage des cours
                    ArrayList<Course> cours = (ArrayList<Course>) objectInputStream.readObject();
                    System.out.println("Voici la liste de cours de la session " + session);
                    for (Course courses : cours) {

                        System.out.println(courses.getCode() + " - " + courses.getName());
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


                System.out.println("Faites un choix");
                System.out.println("1. Reconsulter les cours d'une session");
                System.out.println("2. S'inscrire a un cours");
                String choix = sc.nextLine();

                switch (choix) {
                    case "1" -> {

                    }
                    case "2" -> {

                        System.out.println("Entrez le code du cours :");
                        String code = sc.nextLine();
                        System.out.println("Entrez votre matricule :");
                        String matricule = sc.nextLine();
                        System.out.println("Entrez votre prénom :");
                        String prenom = sc.nextLine();
                        System.out.println("Entrez votre nom :");
                        String nom = sc.nextLine();
                        System.out.println("Entrez votre email :");
                        String email = sc.nextLine();

                        RegistrationForm form = new RegistrationForm(prenom, nom, email, matricule, new Course("", code, session));
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                        outputStream.writeObject(Server.REGISTER_COMMAND);
                        outputStream.flush();
                        outputStream.writeObject(form);
                        outputStream.flush();
                        //Message de confirmation
                        String message = (String) objectInputStream.readObject();
                        System.out.println(message);
                    }
                    default -> {
                        System.out.println("Commande invalide");

                    }
                }
            }
        }
        catch (ClassNotFoundException | IOException e){
                e.printStackTrace();
        }
    }
}
